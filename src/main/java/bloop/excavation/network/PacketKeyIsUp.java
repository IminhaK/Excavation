package bloop.excavation.network;

import bloop.excavation.event.ServerEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketKeyIsUp {

    public PacketKeyIsUp(FriendlyByteBuf buf) {
    }

    public PacketKeyIsUp() {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            ServerEvent.removePlayer(player.getUUID());
        });
    }
}
