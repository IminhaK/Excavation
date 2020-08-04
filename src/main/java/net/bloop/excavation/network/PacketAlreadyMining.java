package net.bloop.excavation.network;

import net.bloop.excavation.EventStuff;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketAlreadyMining {

    private final boolean alreadyMining;

    public PacketAlreadyMining(PacketBuffer buf) {
        alreadyMining = buf.readBoolean();
    }

    public PacketAlreadyMining(boolean alreadyMining) {
        this.alreadyMining = alreadyMining;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBoolean(alreadyMining);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            EventStuff.setAlreadyBreaking(alreadyMining);
                });
    }
}
