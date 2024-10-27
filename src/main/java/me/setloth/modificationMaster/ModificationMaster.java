package me.setloth.modificationMaster;

import me.setloth.modificationMaster.commands.Craft;
import me.setloth.modificationMaster.commands.EndChest;
import me.setloth.modificationMaster.commands.Sort;
import me.setloth.modificationMaster.commands.VeinToggle;
import me.setloth.modificationMaster.listeners.BlockBreaking;
import me.setloth.modificationMaster.util.VersionChecker;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

@SuppressWarnings("unused")
public final class ModificationMaster extends JavaPlugin {

  static ModificationMaster INSTANCE;
  public static Plugin instance() {
    return INSTANCE;
  }

  static HashMap<UUID, Boolean> veinToggled = new HashMap<>();
  public static HashMap<UUID, Boolean> getVeinToggled() {
    return veinToggled;
  }

  public static void toggleVeinPlayer(Player p) {
    toggleVeinPlayer(p.getUniqueId());
  }

  public static void toggleVeinPlayer(UUID uuid) {
    if (!veinToggled.containsKey(uuid)) {
      veinToggled.put(uuid, true);
    }

    veinToggled.compute(uuid, (k, b) -> Boolean.FALSE.equals(b));
  }

  public static boolean isVeinToggled(Player p) {
    return veinToggled.get(p.getUniqueId());
  }

  @Override
  @SuppressWarnings("all")
  public void onEnable() {
    INSTANCE = this;
    long start = System.currentTimeMillis();

    String ver = this.getPluginMeta().getVersion();
    String latestVer = VersionChecker.latestVersion();

    log("Initializing plugin version "+ver);
    if (!ver.equals(latestVer)) {
      log("\n\nPlugin is outdated!\nYour Version: "+ver+"\nLatest Version: "+latestVer+"\n\nFetch" +
              " " +
              "updates at https://github.com/Setloth/ModificationMaster\n\n", Level.WARNING);
    }


    // Plugin startup logic
    log("Registering Events");
    getServer().getPluginManager().registerEvents(new BlockBreaking(), this);

    log("Registering Commands");
    Objects.requireNonNull(getServer().getPluginCommand("sort")).setExecutor(new Sort());
    Objects.requireNonNull(getServer().getPluginCommand("sort")).setTabCompleter(new Sort());
    Objects.requireNonNull(getServer().getPluginCommand("endchest")).setExecutor(new EndChest());
    Objects.requireNonNull(getServer().getPluginCommand("craft")).setExecutor(new Craft());
    Objects.requireNonNull(getServer().getPluginCommand("veintoggle")).setExecutor(new VeinToggle());


    log("Done! Took: "+(System.currentTimeMillis()-start)+" ms");

  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
    log("Goodbye :(");
  }



  public static void log(String msg) {
    log(msg, Level.INFO);
  }

  public static void log(String msg, Level level) {
    INSTANCE.getLogger().log(level, msg);
  }
}
