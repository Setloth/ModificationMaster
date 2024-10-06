package me.setloth.modificationMaster.listeners;

import me.setloth.modificationMaster.util.Utility;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreaking implements Listener {


  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    Block b = event.getBlock();
    Player p = event.getPlayer();
    if (Utility.isWood(b.getType())) {
      ItemStack stack = p.getInventory().getItemInMainHand();
      if (!b.isPreferredTool(stack)) return;
      Utility.destroyBranch(p, b, false);
    }
    if (Utility.isVeinBlock(b.getType())) {
      ItemStack stack = p.getInventory().getItemInMainHand();
      if (!b.isPreferredTool(stack)) return;
      Utility.destroyBranch(p, b, true);
    }
  }






}
