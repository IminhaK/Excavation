package net.bloop.excavation.event;

import net.bloop.excavation.Excavation;
import net.bloop.excavation.KeyBindings;
import net.bloop.excavation.network.ExcavationPacketHandler;
import net.bloop.excavation.network.PacketKeyIsDown;
import net.bloop.excavation.network.PacketKeyIsUp;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Excavation.MODID, value = Dist.CLIENT)
public class ClientEvent {

    private static boolean excavationPressed = false;

    @SubscribeEvent
    public static void pressKey(InputEvent.KeyInputEvent e) {
        //press
        if(KeyBindings.excavate.isKeyDown() && !excavationPressed) {
            excavationPressed = true;
            ExcavationPacketHandler.INSTANCE.sendToServer(new PacketKeyIsDown());
        }

        //release
        if(!KeyBindings.excavate.isKeyDown() && excavationPressed) {
            excavationPressed = false;
            ExcavationPacketHandler.INSTANCE.sendToServer(new PacketKeyIsUp());
        }
    }

    @SubscribeEvent
    public static void pressMouse(InputEvent.MouseInputEvent e) {
        //press
        if(KeyBindings.excavate.isKeyDown() && !excavationPressed) {
            excavationPressed = true;
            ExcavationPacketHandler.INSTANCE.sendToServer(new PacketKeyIsDown());
        }

        //release
        if(!KeyBindings.excavate.isKeyDown() && excavationPressed) {
            excavationPressed = false;
            ExcavationPacketHandler.INSTANCE.sendToServer(new PacketKeyIsUp());
        }
    }
}
