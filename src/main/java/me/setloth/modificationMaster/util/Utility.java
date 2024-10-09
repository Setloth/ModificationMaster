package me.setloth.modificationMaster.util;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Utility {


  public static boolean isWood(Material m) {
    String mstring = m.toString();
    return mstring.endsWith("_LOG") && !mstring.startsWith("STRIPPED_");
  }

  public static boolean isVeinBlock(Material m) {
    String mstring = m.toString();
    return mstring.endsWith("_ORE");
  }


  public static void destroyBranch(Player p, Block b, boolean down) {
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
          for (int y = (down ? -1 : 0); y <= 1; y++) {
            Block neighbor = currentBlock.getRelative(x, y, z);
            if (neighbor.getType() == branchType) {
              blocksToDestroy.add(neighbor);
            }
          }
        }
      }

      currentBlock.breakNaturally(p.getInventory().getItemInMainHand());

    }
  }


  /**
   * Sorts and combines an array of ItemStacks.
   *
   * @param items      the ItemStack array to sort and combine
   * @return the sorted and combined ItemStack array
   */

@SuppressWarnings("all")
  public static ItemStack[] sortAndCombine(ItemStack[] items) {
    // Map to store item counts with metadata as the key
    Map<ItemStack, Integer> itemCount = new HashMap<>();

    // Count items with respect to metadata
    for (ItemStack item : items) {
      if (item != null && item.getType() != Material.AIR) {
        boolean found = false;

        // Try to find an existing item stack with the same metadata
        for (ItemStack key : itemCount.keySet()) {
          if (areItemStacksEqual(key, item)) {
            itemCount.put(key, itemCount.get(key) + item.getAmount());
            found = true;
            break;
          }
        }

        // If no matching item stack was found, add it as a new key
        if (!found) {
          itemCount.put(item.clone(), item.getAmount());
        }
      }
    }

    // Create a sorted array of item stacks based on their counts
    ItemStack[] sortedItems = itemCount.keySet().toArray(new ItemStack[0]);
    Arrays.sort(sortedItems, Comparator.comparingInt(itemCount::get).reversed());

    // Prepare a list to hold combined item stacks
    ItemStack[] combinedItems = new ItemStack[itemCount.size() * 64]; // Max possible stacks
    int index = 0;

    // Fill combinedItems array with item stacks, respecting max stack size
    for (ItemStack itemStack : sortedItems) {
      int totalAmount = itemCount.get(itemStack);
      int maxStackSize = itemStack.getMaxStackSize();

      // Continue creating stacks until all items are accounted for
      while (totalAmount > 0) {
        int stackSize = Math.min(totalAmount, maxStackSize);

        // Check if we have space in combinedItems
        if (index < combinedItems.length) {
          ItemStack newStack = itemStack.clone(); // Clone to preserve metadata
          newStack.setAmount(stackSize);
          combinedItems[index] = newStack;
          index++; // Increment index
        } else {
          // Log a warning if we hit the limit
          System.err.println("Exceeded max combined items limit for item: " + itemStack);
          break; // Prevent index out of bounds
        }

        totalAmount -= stackSize; // Reduce the remaining count
      }
    }

    // Resize the array to remove nulls or empty slots
    combinedItems = Arrays.copyOf(combinedItems, index);

    return combinedItems;
  }

  // Helper method to compare ItemStacks including their metadata
  private static boolean areItemStacksEqual(ItemStack a, ItemStack b) {
    if (a.getType() != b.getType()) return false;
    if (a.hasItemMeta() && b.hasItemMeta()) {
      ItemMeta metaA = a.getItemMeta();
      ItemMeta metaB = b.getItemMeta();

      // Compare display name
      if (metaA.hasDisplayName() && metaB.hasDisplayName() && !Objects.equals(metaA.displayName(), metaB.displayName())) {
        return false;
      }

      // Compare enchantments
      if (!metaA.getEnchants().equals(metaB.getEnchants())) {
        return false;
      }

      // Compare lore
      return !metaA.hasLore() || !metaB.hasLore() || Objects.equals(metaA.lore(), metaB.lore());

      // Add other metadata comparisons as needed (e.g. custom tags, etc.)
    } else return !a.hasItemMeta() && !b.hasItemMeta(); // One has metadata, the other doesn't
  }

  public static void sort(Inventory inv) {
    // Map to store item counts
    ItemStack[] contents = inv.getContents();

    ItemStack[] hotbar;
    ItemStack[] armorAndOffhand;
    if (inv.getType() == InventoryType.PLAYER) {
      // Split hotbar and armor/offhand from main inventory
      hotbar = Arrays.copyOfRange(contents, 0, 9);
      armorAndOffhand = Arrays.copyOfRange(contents, 36, contents.length);

      // Extract and sort the main inventory (slots 9-35)
      ItemStack[] mainInventory = Arrays.copyOfRange(contents, 9, 36);
      ItemStack[] sortedMainInventory = sortAndCombine(mainInventory); // Bottom up sorting handled in setting

      // Create a new array for all contents, placing sorted items in the bottom slots
      ItemStack[] newContents = new ItemStack[contents.length];
      // Fill the hotbar
      System.arraycopy(hotbar, 0, newContents, 0, hotbar.length);
      // Fill sorted main inventory from bottom up (slots 35 to 9)
      for (int i = 0; i < sortedMainInventory.length; i++) {
        newContents[35 - i] = sortedMainInventory[i]; // Place sorted items from bottom to top
      }
      // Fill armor and offhand
      System.arraycopy(armorAndOffhand, 0, newContents, 36, armorAndOffhand.length);

      inv.setContents(newContents); // Update inventory with new arrangement
    } else {
      // For other inventories, sort directly
      ItemStack[] sortedContents = sortAndCombine(contents);
      inv.setContents(sortedContents);
    }

  }

}
