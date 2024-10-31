package me.setloth.modificationMaster.commands;

import me.setloth.modificationMaster.util.VeinSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VeinToggle implements CommandExecutor {
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player p)) return true;

    VeinSystem.toggleVeinPlayer(p);
    VeinSystem.statusMessage(p);

    return true;
  }
}
