package me.setloth.modificationMaster;

import me.setloth.modificationMaster.commands.Craft;
import me.setloth.modificationMaster.commands.EndChest;
import me.setloth.modificationMaster.commands.Sort;
import me.setloth.modificationMaster.commands.VeinToggle;
import me.setloth.modificationMaster.listeners.BlockBreaking;
import me.setloth.modificationMaster.listeners.RightClickToggle;
import me.setloth.modificationMaster.util.VersionChecker;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

@SuppressWarnings("unused")
public final class ModificationMaster extends JavaPlugin {

  static ModificationMaster INSTANCE;

  public static Plugin instance() {
    return INSTANCE;
  }

  public static void log(String msg) {
    log(msg, Level.INFO);
  }

  public static void log(String msg, Level level) {
    INSTANCE.getLogger().log(level, msg);
  }

  @Override
  @SuppressWarnings("all")
  public void onEnable() {
    INSTANCE = this;
    long start = System.currentTimeMillis();

    String ver = this.getPluginMeta().getVersion();
    String latestVer = VersionChecker.latestVersion();

    log("Initializing plugin version " + ver);
    if (!ver.equals(latestVer)) {
      log("\n\nPlugin is outdated!\nYour Version: " + ver + "\nLatest Version: " + latestVer + "\n\nFetch" +
              " updates at https://github.com/Setloth/ModificationMaster\n\n", Level.WARNING);
    } else {
      log("\n\nPlugin is up to date at " + latestVer + "\n\nFetch updates at https://github.com/Setloth/ModificationMaster\n\n");
    }

    // Plugin startup logic
    log("Registering Events");
    getServer().getPluginManager().registerEvents(new BlockBreaking(), this);
    getServer().getPluginManager().registerEvents(new RightClickToggle(), this);

    log("Registering Commands");
    Objects.requireNonNull(getServer().getPluginCommand("sort")).setExecutor(new Sort());
    Objects.requireNonNull(getServer().getPluginCommand("sort")).setTabCompleter(new Sort());
    Objects.requireNonNull(getServer().getPluginCommand("endchest")).setExecutor(new EndChest());
    Objects.requireNonNull(getServer().getPluginCommand("craft")).setExecutor(new Craft());
    Objects.requireNonNull(getServer().getPluginCommand("veintoggle")).setExecutor(new VeinToggle());

    log("Done! Took: " + (System.currentTimeMillis() - start) + " ms");
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
    log("Goodbye :(");
  }
}
