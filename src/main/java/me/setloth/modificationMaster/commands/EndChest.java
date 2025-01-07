package me.setloth.modificationMaster.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EndChest implements CommandExecutor {
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

    if (commandSender instanceof Player p) {
      if (!p.hasPermission("modificationmaster.endchest")) {
        p.sendMessage(Component.text("Insufficient permissions! Needs: modificationmaster.endchest"));
        return true;
      }

      p.openInventory(p.getEnderChest());
    }

    return true;
  }
}
