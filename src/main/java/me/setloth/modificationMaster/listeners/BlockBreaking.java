package me.setloth.modificationMaster.listeners;

import me.setloth.modificationMaster.util.VeinSystem;
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

    if (!VeinSystem.isVeinToggled(p)) return;

    Block b = event.getBlock();
    ItemStack stack = p.getInventory().getItemInMainHand();

    if (!b.isPreferredTool(stack)) return;

    VeinSystem.toggleVeinPlayer(p);

    if (VeinSystem.isWood(b.getType())) {
      VeinSystem.destroyBranch(p, b, false);
    }
    if (VeinSystem.isOreBlock(b.getType())) {
      VeinSystem.destroyBranch(p, b, true);
    }

    VeinSystem.toggleVeinPlayer(p);
  }


}
