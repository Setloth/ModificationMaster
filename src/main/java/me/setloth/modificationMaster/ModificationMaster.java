package me.setloth.modificationMaster;

import me.setloth.modificationMaster.commands.Sort;
import me.setloth.modificationMaster.listeners.BlockBreaking;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public final class ModificationMaster extends JavaPlugin {

  @Override
  public void onEnable() {
    // Plugin startup logic
    Logger.getLogger("MM").info("Registering Events");
    getServer().getPluginManager().registerEvents(new BlockBreaking(), this);
    Logger.getLogger("MM").info("Registering Commands");
    Objects.requireNonNull(getServer().getPluginCommand("sort")).setExecutor(new Sort());

  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
    Logger.getLogger("MM").info("Goodbye :(");

  }
}
