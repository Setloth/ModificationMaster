package me.setloth.modificationMaster;

import me.setloth.modificationMaster.commands.Sort;
import me.setloth.modificationMaster.listeners.BlockBreaking;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public final class ModificationMaster extends JavaPlugin {

  static ModificationMaster INSTANCE;
  public static Plugin instance() {
    return INSTANCE;
  }

  @Override
  public void onEnable() {
    INSTANCE = this;
    // Plugin startup logic
    log("Registering Events");
    getServer().getPluginManager().registerEvents(new BlockBreaking(), this);
    log("Registering Commands");
    Objects.requireNonNull(getServer().getPluginCommand("sort")).setExecutor(new Sort());

  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
    log("Goodbye :(");
  }

  public static void log(String msg) {
    Logger.getLogger("MM").info(msg);
  }
}
