package me.setloth.modificationMaster.systems;

import me.setloth.modificationMaster.ModificationMaster;

import static me.setloth.modificationMaster.ModificationMaster.log;

public enum ConfigurationSystem {


    SYSTEM("System"),
    PACKETS("System.packets"),
    SYSTEM_SORTING("System.sorting"),
    CHEST_SORTING("System.sorting.chest"),
    INVENTORY_SORTING("System.sorting.inventory"),
    SYSTEM_VEIN_MININIG("System.vein-mining"),
    INCLUDE_BLOCKS("System.vein-mining.include"),
    EXCLUDE_BLOCKS("System.vein-mining.exclude"),
    REQUIRE_TOOL("System.vein-mining.require-tool");


    final String path;

    ConfigurationSystem(String path) {
        this.path = path;
    }

    public <T> T value(Class<T> type) {
        T result = ModificationMaster.instance().getConfig().getObject(path, type, null);
        if (result == null) {
            log("Unable to fetch configuration value for path: "+path+", with type: "+type.getName());
            return null;
        }
        return result;
    }

}
