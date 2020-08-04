package net.bloop.excavation.event;

import net.bloop.excavation.Excavation;
import net.bloop.excavation.KeyBindings;
import net.bloop.excavation.network.ExcavationPacketHandler;
import net.bloop.excavation.network.PacketKeyIsDown;
import net.bloop.excavation.network.PacketKeyIsUp;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Excavation.MODID, value = Dist.CLIENT)
public class ClientEvent {

    private static boolean excavationPressed = false;

    @SubscribeEvent
    public static void pressKey(InputEvent.KeyInputEvent e) {
        if(KeyBindings.excavate.isKeyDown() && !excavationPressed) {
            excavationPressed = true;
            ExcavationPacketHandler.INSTANCE.sendToServer(new PacketKeyIsDown());
        }
    }

    @SubscribeEvent
    public static void releaseKey(TickEvent.PlayerTickEvent e) {
        if(!KeyBindings.excavate.isKeyDown() && excavationPressed) {
            System.out.println();
            ExcavationPacketHandler.INSTANCE.sendToServer(new PacketKeyIsUp());
            excavationPressed = false;
        }
    }
}
