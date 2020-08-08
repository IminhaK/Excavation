package bloop.excavation.network;

import bloop.excavation.event.ServerEvent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketKeyIsUp {

    public PacketKeyIsUp(PacketBuffer buf) {
    }

    public PacketKeyIsUp() {
    }

    public void toBytes(PacketBuffer buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            ServerEvent.removePlayer(player.getUniqueID());
        });
    }
}
