// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.tree;

// CHECKSTYLE:OFF
import com.mojang.brigadier.AmbiguityConsumer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;

public abstract class CommandNode<S> implements Comparable<CommandNode<S>> {
    private final Map<String, CommandNode<S>> children = new LinkedHashMap<>();
    private final Map<String, LiteralCommandNode<S>> literals = new LinkedHashMap<>();
    private final Map<String, ArgumentCommandNode<S, ?>> arguments = new LinkedHashMap<>();
    public Predicate<S> requirement;
    private final CommandNode<S> redirect;
    private final RedirectModifier<S> modifier;
    private final boolean forks;
    private Command<S> command;
    public CommandNode<CommandSourceStack> clientNode; // Paper - Brigadier API
    public CommandNode<io.papermc.paper.command.brigadier.CommandSourceStack> unwrappedCached = null; // Paper - Brigadier Command API
    public CommandNode<io.papermc.paper.command.brigadier.CommandSourceStack> wrappedCached = null; // Paper - Brigadier Command API
    // CraftBukkit start
    public void removeCommand(String name) {
        this.children.remove(name);
        this.literals.remove(name);
        this.arguments.remove(name);
    }
    // CraftBukkit end

    protected CommandNode(final Command<S> command, final Predicate<S> requirement, final CommandNode<S> redirect, final RedirectModifier<S> modifier, final boolean forks) {
        this.command = command;
        this.requirement = requirement;
        this.redirect = redirect;
        this.modifier = modifier;
        this.forks = forks;
    }

    public Command<S> getCommand() {
        return this.command;
    }

    public Collection<CommandNode<S>> getChildren() {
        return this.children.values();
    }

    public CommandNode<S> getChild(final String name) {
        return this.children.get(name);
    }

    public CommandNode<S> getRedirect() {
        return this.redirect;
    }

    public RedirectModifier<S> getRedirectModifier() {
        return this.modifier;
    }

    // CraftBukkit start
    public synchronized boolean canUse(final S source) {
        if (source instanceof CommandSourceStack) {
            try {
                ((CommandSourceStack) source).currentCommand.put(Thread.currentThread(), this); // Paper - Thread Safe Vanilla Command permission checking
                return this.requirement.test(source);
            } finally {
                ((CommandSourceStack) source).currentCommand.remove(Thread.currentThread()); // Paper - Thread Safe Vanilla Command permission checking
            }
        }
        // CraftBukkit end
        return this.requirement.test(source);
    }

    public void addChild(final CommandNode<S> node) {
        if (node instanceof RootCommandNode) {
            throw new UnsupportedOperationException("Cannot add a RootCommandNode as a child to any other CommandNode");
        }

        final CommandNode<S> child = this.children.get(node.getName());
        if (child != null) {
            // We've found something to merge onto
            if (node.getCommand() != null) {
                child.command = node.getCommand();
            }
            for (final CommandNode<S> grandchild : node.getChildren()) {
                child.addChild(grandchild);
            }
        } else {
            this.children.put(node.getName(), node);
            if (node instanceof LiteralCommandNode) {
                this.literals.put(node.getName(), (LiteralCommandNode<S>) node);
            } else if (node instanceof ArgumentCommandNode) {
                this.arguments.put(node.getName(), (ArgumentCommandNode<S, ?>) node);
            }
        }
    }

    public void findAmbiguities(final AmbiguityConsumer<S> consumer) {
        Set<String> matches = new HashSet<>();

        for (final CommandNode<S> child : this.children.values()) {
            for (final CommandNode<S> sibling : this.children.values()) {
                if (child == sibling) {
                    continue;
                }

                for (final String input : child.getExamples()) {
                    if (sibling.isValidInput(input)) {
                        matches.add(input);
                    }
                }

                if (matches.size() > 0) {
                    consumer.ambiguous(this, child, sibling, matches);
                    matches = new HashSet<>();
                }
            }

            child.findAmbiguities(consumer);
        }
    }

    protected abstract boolean isValidInput(final String input);

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandNode)) return false;

        final CommandNode<S> that = (CommandNode<S>) o;

        if (!this.children.equals(that.children)) return false;
        if (this.command != null ? !this.command.equals(that.command) : that.command != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 31 * this.children.hashCode() + (this.command != null ? this.command.hashCode() : 0);
    }

    public Predicate<S> getRequirement() {
        return this.requirement;
    }

    public abstract String getName();

    public abstract String getUsageText();

    public abstract void parse(StringReader reader, CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException;

    public abstract CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) throws CommandSyntaxException;

    public abstract ArgumentBuilder<S, ?> createBuilder();

    protected abstract String getSortedKey();

    public Collection<? extends CommandNode<S>> getRelevantNodes(final StringReader input) {
        // Paper start - prioritize mc commands in function parsing
        return this.getRelevantNodes(input, null);
    }
    @org.jetbrains.annotations.ApiStatus.Internal
    public Collection<? extends CommandNode<S>> getRelevantNodes(final StringReader input, final Object source) {
        // Paper end - prioritize mc commands in function parsing
        if (this.literals.size() > 0) {
            final int cursor = input.getCursor();
            while (input.canRead() && input.peek() != ' ') {
                input.skip();
            }
            final String text = input.getString().substring(cursor, input.getCursor());
            input.setCursor(cursor);
            // Paper start - prioritize mc commands in function parsing
            LiteralCommandNode<S> literal = null;
            if (source instanceof CommandSourceStack css && css.source == net.minecraft.commands.CommandSource.NULL) {
                if (!text.contains(":")) {
                    literal = this.literals.get("minecraft:" + text);
                }
            } else if (source instanceof CommandSourceStack css && css.source instanceof net.minecraft.world.level.BaseCommandBlock) {
                if (css.getServer().server.getCommandBlockOverride(text) && !text.contains(":")) {
                    literal = this.literals.get("minecraft:" + text);
                }
            }
            if (literal == null) {
                literal = this.literals.get(text);
            }
            // Paper end - prioritize mc commands in function parsing
            if (literal != null) {
                return Collections.singleton(literal);
            } else {
                return this.arguments.values();
            }
        } else {
            return this.arguments.values();
        }
    }

    @Override
    public int compareTo(final CommandNode<S> o) {
        if (this instanceof LiteralCommandNode == o instanceof LiteralCommandNode) {
            return this.getSortedKey().compareTo(o.getSortedKey());
        }

        return (o instanceof LiteralCommandNode) ? 1 : -1;
    }

    public boolean isFork() {
        return this.forks;
    }

    public abstract Collection<String> getExamples();
    // Paper start - Brigadier Command API
    public void clearAll() {
        this.children.clear();
        this.literals.clear();
        this.arguments.clear();
    }
    // Paper end - Brigadier Command API
}
