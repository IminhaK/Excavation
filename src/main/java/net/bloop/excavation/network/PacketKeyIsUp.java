package net.bloop.excavation.network;

import net.bloop.excavation.event.ServerEvent;
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
            System.out.println("KEY IS UP");
            ServerEvent.setExcavationPressed(false);
        });
    }
}
