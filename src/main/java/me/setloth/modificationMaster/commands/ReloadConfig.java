package me.setloth.modificationMaster.commands;

import me.setloth.modificationMaster.ModificationMaster;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadConfig implements CommandExecutor {
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

    long start = System.currentTimeMillis();

    ModificationMaster.instance().reloadConfig();
    ModificationMaster.instance().saveConfig();

    commandSender.sendMessage("Config Reloaded - took: "+(System.currentTimeMillis()-start)+"ms");

    return true;
  }
}
