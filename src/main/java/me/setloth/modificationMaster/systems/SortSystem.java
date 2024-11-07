package me.setloth.modificationMaster.systems;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class SortSystem {


  public static ItemStack[] combine(ItemStack[] items) {

    ArrayList<ItemStack> combined = new ArrayList<>();

    for (ItemStack item : items) {
      if (item == null || item.getAmount() == 0) continue;

      boolean added = false;

      for (ItemStack combinedItem : combined) {
        if (combinedItem.isSimilar(item)) {
          int maxStack = combinedItem.getMaxStackSize();
          int combinedAmount = combinedItem.getAmount() + item.getAmount();

          if (combinedAmount <= maxStack) {
            combinedItem.setAmount(combinedAmount);
            added = true;

          } else {
            combinedItem.setAmount(maxStack);
            item.setAmount(combinedAmount - maxStack);
          }
        }
      }

      if (!added) combined.add(item);

    }

    ArrayList<ItemStack> sorted = new ArrayList<>();
    for (ItemStack item : combined) {
      if (sorted.contains(item)) continue;
      List<ItemStack> similar = combined.stream().filter(i -> i.isSimilar(item)).toList();
      sorted.addAll(similar);
    }

    return sorted.toArray(new ItemStack[0]);
  }


  public static void sort(Inventory inv) {
    ItemStack[] contents = inv.getContents();
    if (inv instanceof PlayerInventory pi) {
      ItemStack[] storageContents = pi.getStorageContents();
      ItemStack[] hotBar = new ItemStack[9];
      System.arraycopy(storageContents, 0, hotBar, 0, 9);

      ItemStack[] mainInventory = new ItemStack[storageContents.length - 9];
      System.arraycopy(storageContents, 9, mainInventory, 0, storageContents.length - 9);

      ItemStack[] sortedMain = combine(mainInventory);

      ItemStack[] combinedContents = new ItemStack[storageContents.length];

      System.arraycopy(hotBar, 0, combinedContents, 0, 9);
      System.arraycopy(sortedMain, 0, combinedContents, 9, sortedMain.length);

      pi.setStorageContents(combinedContents);
    } else {

      inv.clear();
      inv.setContents(combine(contents));

    }

  }
}
