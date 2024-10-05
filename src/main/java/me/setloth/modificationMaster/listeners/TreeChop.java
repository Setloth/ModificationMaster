package me.setloth.modificationMaster.listeners;

import me.setloth.modificationMaster.util.Utility;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class TreeChop implements Listener {


  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    Block b = event.getBlock();
    Player p = event.getPlayer();
    if (Utility.isWood(b.getType())) {
      ItemStack stack = p.getInventory().getItemInMainHand();

      if (Utility.isAxe(stack.getType())) {
        Utility.destroyBranch(b, stack, false);
      }
    }
    if (Utility.isVeinBlock(b.getType())) {
      ItemStack stack = p.getInventory().getItemInMainHand();


      if (Utility.isPickaxe(stack.getType())) {
        Utility.destroyBranch(b, stack, true);
      }
    }
  }






}
