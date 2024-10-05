package me.setloth.modificationMaster.commands;

import me.setloth.modificationMaster.util.Utility;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Sort implements CommandExecutor, TabCompleter {

  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    if (!(commandSender instanceof Player p)) return true;

    Inventory inv = p.getInventory();

    if (strings.length > 0 && strings[0].equalsIgnoreCase("block")) {
      Block b = p.getTargetBlock(null, 100);
      BlockState bs = b.getState();

      if (!(bs instanceof Container c)) return false;
      inv = c.getInventory();
    }

    Utility.sort(inv);

    return true;
  }

  public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    if (!(commandSender instanceof Player)) return null;
    if (strings.length < 2) return List.of("block");
    return null;
  }
}
