package me.setloth.modificationMaster.listeners;

import me.setloth.modificationMaster.util.Utility;
import me.setloth.modificationMaster.util.VeinToggled;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreaking implements Listener {


  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    Player p = event.getPlayer();

    if (!VeinToggled.isVeinToggled(p)) return;

    Block b = event.getBlock();
    ItemStack stack = p.getInventory().getItemInMainHand();

    if (!b.isPreferredTool(stack)) return;

    VeinToggled.toggleVeinPlayer(p);

    if (Utility.isWood(b.getType())) {
      Utility.destroyBranch(p, b, false);
    }
    if (Utility.isVeinBlock(b.getType())) {
      Utility.destroyBranch(p, b, true);
    }

    VeinToggled.toggleVeinPlayer(p);
  }






}
