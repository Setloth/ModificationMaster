package me.setloth.modificationMaster.systems;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Pattern;

public class VeinSystem {

  static HashMap<UUID, Boolean> veinToggled = new HashMap<>();

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
    return isVeinToggled(p.getUniqueId());
  }

  public static boolean isVeinToggled(UUID u) {
    if (!veinToggled.containsKey(u)) {
      veinToggled.put(u, true);
    }
    return veinToggled.get(u);
  }

  public static void statusMessage(Player p) {
    boolean state = VeinSystem.isVeinToggled(p);

    Component c = Component.text("Vein mining is now")
            .appendSpace()
            .append(
                    (state ?
                            Component.text("enabled").color(TextColor.color(115, 189, 121))
                            :
                            Component.text("disabled").color(TextColor.color(188, 63, 60))
                    )
                            .decorate(TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.runCommand("/vt"))
                            .hoverEvent(HoverEvent.showText(Component.text("Click To " +
                                    "Toggle " + (state ? "Off" : "On"))))
            );

    p.sendMessage(c);
  }

  public static boolean isVeinBlock(Material m) {
    ArrayList<?> includeList = ConfigurationSystem.INCLUDE_BLOCKS.value(ArrayList.class);
    ArrayList<?> excludeList = ConfigurationSystem.EXCLUDE_BLOCKS.value(ArrayList.class);
  
    String materialStr = m.toString();
  
    // Check exclude patterns
    if (excludeList != null) {
      for (Object excludePattern : excludeList) {
        if (excludePattern instanceof String && Pattern.compile((String) excludePattern, Pattern.CASE_INSENSITIVE).matcher(materialStr).matches()) {
          return false;
        }
      }
    }
  
    // Check include patterns
    if (includeList != null) {
      for (Object includePattern : includeList) {
        if (includePattern instanceof String && Pattern.compile((String) includePattern, Pattern.CASE_INSENSITIVE).matcher(materialStr).matches()) {
          return true;
        }
      }
    }
  
    return false;
  }
  
  public static boolean isWood(Material m) {
    String mstring = m.toString();
    return mstring.endsWith("_LOG");
  }

  public static boolean isOreBlock(Material m) {
    String mstring = m.toString();
    return mstring.endsWith("_ORE");
  }

  public static void destroyBranch(Player p, Block b) {

    Material branchType = b.getType();
    final Queue<Block> blocksToDestroy = new LinkedList<>();
    final Set<Block> processedBlocks = new HashSet<>();
    blocksToDestroy.add(b);

    while (!blocksToDestroy.isEmpty()) {
      Block currentBlock = blocksToDestroy.remove();
      Material currentBlockType = currentBlock.getType();

      if (processedBlocks.contains(currentBlock) || currentBlockType != branchType) continue;

      processedBlocks.add(currentBlock);

      for (int x = -1; x <= 1; x++) {
        for (int z = -1; z <= 1; z++) {
          for (int y = -1; y <= 1; y++) {
            Block neighbor = currentBlock.getRelative(x, y, z);
            if (neighbor.getType() == branchType) {
              blocksToDestroy.add(neighbor);
            }
          }
        }
      }

      p.breakBlock(currentBlock);
    }
  }
}
