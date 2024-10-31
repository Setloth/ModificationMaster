package me.setloth.modificationMaster.listeners;

import me.setloth.modificationMaster.util.VeinSystem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class RightClickToggle implements Listener {

  @EventHandler
  public void onRightClick(PlayerInteractEvent event) {

    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
    if (Objects.equals(event.getHand(), EquipmentSlot.OFF_HAND)) return;

    Player p = event.getPlayer();
    if (!p.isSneaking()) return;

    Block tb = event.getClickedBlock();
    ItemStack stack = p.getInventory().getItemInMainHand();
    if (tb == null || !tb.isPreferredTool(stack)) return;

    if (VeinSystem.isWood(tb.getType()) || VeinSystem.isOreBlock(tb.getType())) { // only apply for
      // vein-mine able blocks
      event.setCancelled(true); // prevent action consequence
      VeinSystem.toggleVeinPlayer(event.getPlayer());
      VeinSystem.statusMessage(event.getPlayer());
    }


  }

}
