package me.setloth.modificationMaster.systems;

import io.netty.buffer.Unpooled;
import me.setloth.modificationMaster.ModificationMaster;
import me.setloth.modificationMaster.util.NetworkingConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ChiseledBookshelf;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ChiseledBookshelfInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

import static me.setloth.modificationMaster.ModificationMaster.log;

public class PacketSystem {

  public static void register() {

    Bukkit.getMessenger().registerOutgoingPluginChannel(ModificationMaster.instance(), NetworkingConstants.RESPONSE_BOOKSHELF_PACKET_ID.toString());

    Bukkit.getMessenger().registerIncomingPluginChannel(ModificationMaster.instance(), NetworkingConstants.REQUEST_BOOKSHELF_PACKET_ID.toString(), new MessageListener());
  }

  public static ItemStack getSlot(int i, Block bookshelf) {
    BlockState state = bookshelf.getState();
    if (state.getType() != Material.CHISELED_BOOKSHELF) return ItemStack.empty();

    ChiseledBookshelf cb = (ChiseledBookshelf) state;

    ChiseledBookshelfInventory inv = cb.getInventory();

    return inv.getItem(i);

  }

  static class MessageListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
      if (!channel.equals(NetworkingConstants.REQUEST_BOOKSHELF_PACKET_ID.toString())) return;

      ByteBuffer bb = ByteBuffer.wrap(message);


      int slot = bb.getInt();

      Block b = player.getTargetBlockExact(10, FluidCollisionMode.ALWAYS);
      if (b != null && b.getType() == Material.CHISELED_BOOKSHELF) {

      ItemStack stack = getSlot(slot, b);

      ResourceLocation id = NetworkingConstants.RESPONSE_BOOKSHELF_PACKET_ID;

      net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);

      RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), MinecraftServer.getServer().registryAccess());

      net.minecraft.world.item.ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, nmsStack);

      ((CraftPlayer) player).getHandle().connection.sendPacket(new ClientboundCustomPayloadPacket(new DiscardedPayload(id, buf)));

      }

    }
  }

}