package net.bloop.excavation.network;

import net.bloop.excavation.EventStuff;
import net.bloop.excavation.veinmine.MiningAlgorithm;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketExcavate {

    private final BlockPos pos;

    public PacketExcavate(PacketBuffer buf) {
        pos = buf.readBlockPos();
    }

    public PacketExcavate(BlockPos pos) {
        this.pos = pos;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            //server does stuff here instead?
            ServerPlayerEntity player = ctx.get().getSender();
            World world = player.world;
            MiningAlgorithm miningAlgorithm = new MiningAlgorithm(pos, world, player);

            miningAlgorithm.findBlocks();

            miningAlgorithm.mine();
            //reeeee just let me break you
            miningAlgorithm.tryBreak(pos);
            miningAlgorithm.dropItems();
            //EventStuff.setAlreadyBreaking(alreadyMining); SEND PACKET TO MAKE IT FALSE
            ExcavationPacketHandler.INSTANCE.sendTo(new PacketAlreadyMining(false), player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        });
        ctx.get().setPacketHandled(true);
    }
}
