# Modification Master

A simple modification plugin!

<!-- TOC -->
* [Modification Master](#modification-master)
  * [ShelfViewer](#shelfviewer)
  * [Sorting](#sorting)
  * [Vein Mining](#vein-mining)
  * [End Chest & Craft](#end-chest--craft)
<!-- TOC -->

> [!TIP]
> Use the `/reloadconfig` command to re-load the configuration into the plugin
> Also make sure to save your `config.yml` once you have configured it

## [ShelfViewer](https://github.com/Setloth/ShelfViewer)
The main functionality of this plugin is to provide a bridge between my Fabric MC mod [ShelfViewer](https://github.com/Setloth/ShelfViewer) and Bukkit MC Servers

By installing this plugin on a Paper/SpigotMC 1.21.1+ server, you can access the benefits of a client-side mod on non-fabric servers!

To ensure that this communication can happen, ensure that the packet system is enabled in the `config.yml`:

```yml
System:
  packets: true
# needed for Shelf Viewer integration
```

If you wish to disable the system, set `packets: false`

Once the system is enabled, and the plugin is running, the server will communicate with Shelf Viewer to make sure the mod gets the correct information!

> [!WARNING]
> The packet system will not automatically enable or disable if you use the `/reloadconfig` command, you must restart the server for the changes to take effect!

Check out my [other repository](https://github.com/Setloth/ShelfViewer) for more information and downloads!

## Sorting
Modification Master also provides functionality to sort inventories of players as well as blocks (like chests, barrels, etc.)

To access these features, a player needs to have some permissions:
- `modificationmaster.sort` to access the `/sort` command
- `modificationmaster.sort.block` to access `/sort block`

The `/sort` command consists of two parts which are the player inventory sorting and the block sorting.

To sort your own player inventory, simply run `/sort`
A message should be displayed in chat notifying the player that their inventory has been sorted

To sort the block you are looking at, run `/sort block`
Similarly, a message will be sent stating the type of the block and that it was sorted (if it was able to be sorted)

Make sure to have the sorting system enabled in the configuration:

```yml
System:
# ...
  sorting:
# for inventory and chest sorting
    chest: true
    inventory: true
```

If you want to disable them, again switch the fields to `chest: false` or `inventory: false` for block sorting and player sorting, respectively

## Vein Mining
Modification Master now features a customizable Vein Mining system that allows you to specify which blocks to include as valid veins.

First, to access the system, players will need to have the `modificationmaster.veinmining` permission

Additionally, configure the system to your liking in the config:

```yml
System:
# ...
  vein-mining:
    include: [.*_LOG, .*_ORE, .*_STEM] # edit at own risk!
    exclude: [ ] # disabled -> [*]
# what blocks to include or exclude in vein-mining, ReGex is allowed, case-insensitive!

# to disable vein-mining entirely, set the exclude set to [*]

    require-tool: true
# if the correct tool, i.e. a pickaxe (for stone) or axe (for wood) should be required for vein-mining
```

First, the `include` list is a group of strings (text) that says which blocks should be vein-mined.
You can include [regex](https://en.wikipedia.org/wiki/Regular_expression) in order to filter more complex combinations of text.

The default configuration of `[.*_LOG, .*_ORE, .*_STEM]` ensures that all logs, ores and stems (nether trees) will be able to be vein-mined. 

> [!CAUTION]
> If you include a block that is very common, like stone or dirt, you **WILL** crash your server if you attempt to vein-mine them

You can exclude specific blocks or selections of blocks by including them (with regex support, as well) in the `exclude` list.
The exclude list takes priority over the include list, so any block in that list will always be exlucded.

## End Chest & Craft
Modification Master also includes a command to allow players to open their End Chest and a Crafting Table from *anywhere*

To access their End Chest via the `/endchest` command, players must have the `modificationmaster.endchest` permission

And, to access a crafting table via the `/craft` command, players must have the `modificationmaster.craft` permission
