package me.setloth.modificationMaster.commands;

import me.setloth.modificationMaster.systems.SortSystem;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Sort implements CommandExecutor, TabCompleter {

  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    if (!(commandSender instanceof Player p)) return true;

    Inventory inv = p.getInventory();

    if (strings.length > 0 && strings[0].equalsIgnoreCase("block")) {

      // Use getTargetBlockExact to avoid issues with deprecated methods
      Block b = p.getTargetBlockExact(100);

      if (b == null) return false;  // No block found within range

      BlockState bs = b.getState();

      if (bs instanceof InventoryHolder ih) {
        if (ih instanceof DoubleChest dc) {
          inv = dc.getInventory();
        } else inv = ih.getInventory();

      } else return false;
    }

    SortSystem.sort(inv);
    p.sendMessage(inv.getType().defaultTitle().appendSpace().append(Component.text("has been " +
            "sorted")));

    return true;
  }

  public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    if (!(commandSender instanceof Player)) return null;
    if (strings.length < 2) return List.of("block");
    return null;
  }
}
