package bloop.excavation.network;

import bloop.excavation.Excavation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

public class ExcavationPacketHandler {
    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Excavation.MODID, "excavation"), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(nextID(),
                PacketKeyIsDown.class,
                PacketKeyIsDown::toBytes,
                PacketKeyIsDown::new,
                PacketKeyIsDown::handle);

        INSTANCE.registerMessage(nextID(),
                PacketKeyIsUp.class,
                PacketKeyIsUp::toBytes,
                PacketKeyIsUp::new,
                PacketKeyIsUp::handle);
    }
}
