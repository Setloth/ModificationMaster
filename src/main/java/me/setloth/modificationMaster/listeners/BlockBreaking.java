package me.setloth.modificationMaster.listeners;

import me.setloth.modificationMaster.systems.ConfigurationSystem;
import me.setloth.modificationMaster.systems.VeinSystem;
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

    if (!p.hasPermission("modificationmaster.veinmining")) return;

    if (!VeinSystem.isVeinToggled(p)) return;

    Block b = event.getBlock();
    ItemStack stack = p.getInventory().getItemInMainHand();

    Boolean requireTool = ConfigurationSystem.REQUIRE_TOOL.value(Boolean.class);

    if (Boolean.TRUE.equals(requireTool) && !b.isPreferredTool(stack)) return;

    VeinSystem.toggleVeinPlayer(p);

    if (VeinSystem.isVeinBlock(b.getType())) {
      VeinSystem.destroyBranch(p, b);
    }

    VeinSystem.toggleVeinPlayer(p);
  }

}