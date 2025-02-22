package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import org.bukkit.GameEvent;
import org.bukkit.JukeboxSong;
import org.bukkit.Keyed;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockType;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.block.banner.CraftPatternType;
import org.bukkit.craftbukkit.damage.CraftDamageType;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.entity.CraftCat;
import org.bukkit.craftbukkit.entity.CraftFrog;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.craftbukkit.generator.structure.CraftStructure;
import org.bukkit.craftbukkit.generator.structure.CraftStructureType;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.inventory.CraftMenuType;
import org.bukkit.craftbukkit.inventory.trim.CraftTrimMaterial;
import org.bukkit.craftbukkit.inventory.trim.CraftTrimPattern;
import org.bukkit.craftbukkit.legacy.FieldRename;
import org.bukkit.craftbukkit.map.CraftMapCursor;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class CraftRegistry<B extends Keyed, M> implements Registry<B> {

    private static RegistryAccess registry;

    public static void setMinecraftRegistry(RegistryAccess registry) {
        Preconditions.checkState(CraftRegistry.registry == null, "Registry already set");
        CraftRegistry.registry = registry;
    }

    public static RegistryAccess getMinecraftRegistry() {
        return CraftRegistry.registry;
    }

    public static <E> net.minecraft.core.Registry<E> getMinecraftRegistry(ResourceKey<net.minecraft.core.Registry<E>> key) {
        return CraftRegistry.getMinecraftRegistry().registryOrThrow(key);
    }

    /**
     * Usage note: Only use this method to delegate the conversion methods from the individual Craft classes to here.
     * Do not use it in other parts of CraftBukkit, use the methods in the respective Craft classes instead.
     *
     * @param minecraft the minecraft representation
     * @param registryKey the registry key of the minecraft registry to use
     * @param bukkitRegistry the bukkit registry to use
     * @return the bukkit representation of the minecraft value
     */
    public static <B extends Keyed, M> B minecraftToBukkit(M minecraft, ResourceKey<net.minecraft.core.Registry<M>> registryKey, Registry<B> bukkitRegistry) {
        Preconditions.checkArgument(minecraft != null);

        net.minecraft.core.Registry<M> registry = CraftRegistry.getMinecraftRegistry(registryKey);
        B bukkit = bukkitRegistry.get(CraftNamespacedKey.fromMinecraft(registry.getResourceKey(minecraft)
                .orElseThrow(() -> new IllegalStateException(String.format("Cannot convert '%s' to bukkit representation, since it is not registered.", minecraft))).location()));

        Preconditions.checkArgument(bukkit != null);

        return bukkit;
    }

    /**
     * Usage note: Only use this method to delegate the conversion methods from the individual Craft classes to here.
     * Do not use it in other parts of CraftBukkit, use the methods in the respective Craft classes instead.
     *
     * @param bukkit the bukkit representation
     * @return the minecraft representation of the bukkit value
     */
    public static <B extends Keyed, M> M bukkitToMinecraft(B bukkit) {
        Preconditions.checkArgument(bukkit != null);

        return ((Handleable<M>) bukkit).getHandle();
    }

    public static <B extends Keyed, M> Holder<M> bukkitToMinecraftHolder(B bukkit, ResourceKey<net.minecraft.core.Registry<M>> registryKey) {
        Preconditions.checkArgument(bukkit != null);

        net.minecraft.core.Registry<M> registry = CraftRegistry.getMinecraftRegistry(registryKey);

        if (registry.wrapAsHolder(CraftRegistry.bukkitToMinecraft(bukkit)) instanceof Holder.Reference<M> holder) {
            return holder;
        }

        throw new IllegalArgumentException("No Reference holder found for " + bukkit
                + ", this can happen if a plugin creates its own registry entry with out properly registering it.");
    }

    // Paper start - fixup upstream being dum
    public static <T extends org.bukkit.Keyed, M> java.util.Optional<T> unwrapAndConvertHolder(final io.papermc.paper.registry.RegistryKey<T> registryKey, final Holder<M> value) {
        return unwrapAndConvertHolder(io.papermc.paper.registry.RegistryAccess.registryAccess().getRegistry(registryKey), value);
    }

    public static <T extends org.bukkit.Keyed, M> java.util.Optional<T> unwrapAndConvertHolder(final Registry<T> registry, final Holder<M> value) {
        return value.unwrapKey().map(key -> registry.get(CraftNamespacedKey.fromMinecraft(key.location())));
    }
    // Paper end - fixup upstream being dum

    // Paper - move to PaperRegistries

    // Paper - NOTE: As long as all uses of the method below relate to *serialization* via ConfigurationSerializable, it's fine
    public static <B extends Keyed> B get(Registry<B> bukkit, NamespacedKey namespacedKey, ApiVersion apiVersion) {
        if (bukkit instanceof CraftRegistry<B, ?> craft) {
            return craft.get(craft.serializationUpdater.apply(namespacedKey, apiVersion)); // Paper
        }

        if (bukkit instanceof Registry.SimpleRegistry<?> simple) {
            Class<?> bClass = simple.getType();

            if (bClass == Biome.class) {
                return bukkit.get(FieldRename.BIOME_RENAME.apply(namespacedKey, apiVersion));
            }

            if (bClass == EntityType.class) {
                return bukkit.get(FieldRename.ENTITY_TYPE_RENAME.apply(namespacedKey, apiVersion));
            }

            if (bClass == Particle.class) {
                return bukkit.get(FieldRename.PARTICLE_TYPE_RENAME.apply(namespacedKey, apiVersion));
            }

            if (bClass == Attribute.class) {
                return bukkit.get(FieldRename.ATTRIBUTE_RENAME.apply(namespacedKey, apiVersion));
            }
        }

        return bukkit.get(namespacedKey);
    }

    private final Class<?> bukkitClass; // Paper - relax preload class
    private final Map<NamespacedKey, B> cache = new HashMap<>();
    private final Map<B, NamespacedKey> byValue = new java.util.IdentityHashMap<>(); // Paper - improve Registry
    private final net.minecraft.core.Registry<M> minecraftRegistry;
    private final BiFunction<? super NamespacedKey, M, B> minecraftToBukkit; // Paper
    private final BiFunction<NamespacedKey, ApiVersion, NamespacedKey> serializationUpdater; // Paper - rename to make it *clear* what it is *only* for
    private boolean init;

    public CraftRegistry(Class<?> bukkitClass, net.minecraft.core.Registry<M> minecraftRegistry, BiFunction<? super NamespacedKey, M, B> minecraftToBukkit, BiFunction<NamespacedKey, ApiVersion, NamespacedKey> serializationUpdater) { // Paper - relax preload class
        this.bukkitClass = bukkitClass;
        this.minecraftRegistry = minecraftRegistry;
        this.minecraftToBukkit = minecraftToBukkit;
        this.serializationUpdater = serializationUpdater;
    }

    // Paper - inline into CraftRegistry#get(Registry, NamespacedKey, ApiVersion) above

    @Override
    public B get(NamespacedKey namespacedKey) {
        B cached = this.cache.get(namespacedKey);
        if (cached != null) {
            return cached;
        }

        // Make sure that the bukkit class is loaded before creating an instance.
        // This ensures that only one instance with a given key is created.
        //
        // Without this code (when bukkit class is not loaded):
        // Registry#get -> #createBukkit -> (load class -> create default) -> put in cache
        // Result: Registry#get != <bukkitClass>.<field> for possible one registry item
        //
        // With this code (when bukkit class is not loaded):
        // Registry#get -> (load class -> create default) -> Registry#get -> get from cache
        // Result: Registry#get == <bukkitClass>.<field>
        if (!this.init) {
            this.init = true;
            try {
                Class.forName(this.bukkitClass.getName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Could not load registry class " + this.bukkitClass, e);
            }

            return this.get(namespacedKey);
        }

        B bukkit = this.createBukkit(namespacedKey, this.minecraftRegistry.getOptional(CraftNamespacedKey.toMinecraft(namespacedKey)).orElse(null));
        if (bukkit == null) {
            return null;
        }

        this.cache.put(namespacedKey, bukkit);
        this.byValue.put(bukkit, namespacedKey); // Paper - improve Registry

        return bukkit;
    }

    @NotNull
    @Override
    public B getOrThrow(@NotNull NamespacedKey namespacedKey) {
        B object = this.get(namespacedKey);

        Preconditions.checkArgument(object != null, "No %s registry entry found for key %s.", this.minecraftRegistry.key(), namespacedKey);

        return object;
    }

    @NotNull
    @Override
    public Stream<B> stream() {
        return this.minecraftRegistry.keySet().stream().map(minecraftKey -> this.get(CraftNamespacedKey.fromMinecraft(minecraftKey)));
    }

    @Override
    public Iterator<B> iterator() {
        return this.stream().iterator();
    }

    public B createBukkit(NamespacedKey namespacedKey, M minecraft) {
        if (minecraft == null) {
            return null;
        }

        return this.minecraftToBukkit.apply(namespacedKey, minecraft);
    }

    // Paper start - improve Registry
    @Override
    public NamespacedKey getKey(final B value) {
        return this.byValue.get(value);
    }
    // Paper end - improve Registry

    // Paper start - RegistrySet API
    @Override
    public boolean hasTag(final io.papermc.paper.registry.tag.TagKey<B> key) {
        return this.minecraftRegistry.getTag(net.minecraft.tags.TagKey.create(this.minecraftRegistry.key(), io.papermc.paper.adventure.PaperAdventure.asVanilla(key.key()))).isPresent();
    }

    @Override
    public io.papermc.paper.registry.tag.Tag<B> getTag(final io.papermc.paper.registry.tag.TagKey<B> key) {
        final net.minecraft.core.HolderSet.Named<M> namedHolderSet = this.minecraftRegistry.getTag(io.papermc.paper.registry.PaperRegistries.toNms(key)).orElseThrow();
        return new io.papermc.paper.registry.set.NamedRegistryKeySetImpl<>(key, namedHolderSet);
    }
    // Paper end - RegistrySet API
}
