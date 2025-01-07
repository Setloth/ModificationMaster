package me.setloth.modificationMaster.commands;

import me.setloth.modificationMaster.systems.ConfigurationSystem;
import me.setloth.modificationMaster.systems.VeinSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VeinToggle implements CommandExecutor {
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player p)) return true;

    if (!p.hasPermission("modificationmaster.veinmining")) {
      p.sendMessage("Insufficient permissions! Needs: modificationmaster.veinmining");
      return true;
    }

    VeinSystem.toggleVeinPlayer(p);
    VeinSystem.statusMessage(p);

    return true;
  }
}
