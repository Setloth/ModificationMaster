package me.setloth.modificationMaster.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EndChest implements CommandExecutor {
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

    if (commandSender instanceof Player p) {
      p.openInventory(p.getEnderChest());
    }

    return true;
  }
}
