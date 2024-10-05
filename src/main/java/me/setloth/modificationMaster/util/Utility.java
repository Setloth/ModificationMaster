package me.setloth.modificationMaster.util;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Utility {

  public static boolean isAxe(Material m) {
    String mstring = m.toString();
    return mstring.endsWith("_AXE");
  }

  public static boolean isPickaxe(Material m) {
    String mstring = m.toString();
    return mstring.endsWith("_AXE");
  }

  public static boolean isWood(Material m) {
    String mstring = m.toString();
    return mstring.endsWith("_LOG") && !mstring.startsWith("STRIPPED_");
  }

  public static boolean isVeinBlock(Material m) {
    String mstring = m.toString();
    return mstring.endsWith("_ORE");
  }

  public static void destroyBranch(Block block, ItemStack stack, boolean down) {
    // traverse upwards (if !down) + (left | right) on branch and destroy all blocks that are the
    // same by
    // recursion i.e. each time you find a block that matches, recurse where that block breaks
    // and looks for blocks that are the same

    Material bt = block.getType();
    boolean b = block.breakNaturally(stack);
    if (!b) return; // couldnt break

    for (int x = -1; x <= 1; x++) {
      for (int y = (down ? -1 : 0); y <= 1; y++) {
        for (int z = -1; z <= 1; z++) {
//          if (x == 0 && y == 0 && z == 0) continue;
          Block bb = block.getRelative(x, y, z);
          if (bb.getType().equals(bt)) destroyBranch(bb, stack, down);
        }
      }
    }

  }

  /**
   * Sorts and combines an array of ItemStacks.
   *
   * @param items      the ItemStack array to sort and combine
   * @return the sorted and combined ItemStack array
   */
  public static ItemStack[] sortAndCombine(ItemStack[] items) {
    // Map to store item counts
    Map<Material, Integer> materialCount = new HashMap<>();

    // Count items
    for (ItemStack item : items) {
      if (item != null && item.getType() != Material.AIR) {
        materialCount.put(item.getType(), materialCount.getOrDefault(item.getType(), 0) + item.getAmount());
      }
    }

    // Create a sorted array of materials based on their counts
    Material[] sortedMaterials = materialCount.keySet().toArray(new Material[0]);
    Arrays.sort(sortedMaterials, Comparator.comparingInt(materialCount::get).reversed());

    // Prepare a list to hold combined item stacks
    ItemStack[] combinedItems = new ItemStack[materialCount.size() * 64]; // Max possible stacks to avoid index out of bounds
    int index = 0;

    // Fill combinedItems array with item stacks, respecting max stack size
    for (Material material : sortedMaterials) {
      int totalAmount = materialCount.get(material);
      int maxStackSize = material.getMaxStackSize();

      // Continue creating stacks until all items are accounted for
      while (totalAmount > 0) {
        int stackSize = Math.min(totalAmount, maxStackSize);

        // Check if we have space in combinedItems
        if (index < combinedItems.length) {
          combinedItems[index] = new ItemStack(material, stackSize); // Initialize ItemStack
          index++; // Increment index
        } else {
          // Log a warning if we hit the limit
          System.err.println("Exceeded max combined items limit for material: " + material);
          break; // Prevent index out of bounds
        }

        totalAmount -= stackSize; // Reduce the remaining count
      }
    }

    // Resize the array to remove nulls or empty slots
    combinedItems = Arrays.copyOf(combinedItems, index);

    // Handle bottom-up arrangement if specified

    return combinedItems;

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
