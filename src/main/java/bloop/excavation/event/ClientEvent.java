package bloop.excavation.event;

import bloop.excavation.network.ExcavationPacketHandler;
import bloop.excavation.network.PacketKeyIsDown;
import bloop.excavation.network.PacketKeyIsUp;
import bloop.excavation.Excavation;
import bloop.excavation.KeyBindings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Excavation.MODID, value = Dist.CLIENT)
public class ClientEvent {

    private static boolean excavationPressed = false;

    @SubscribeEvent
    public static void pressKey(InputEvent.KeyInputEvent e) {
        boolean crouchingEnabled = Excavation.config.crouchEnable.get();

        //press
        if((KeyBindings.excavate.isKeyDown() && !crouchingEnabled) && !excavationPressed) {
            excavationPressed = true;
            ExcavationPacketHandler.INSTANCE.sendToServer(new PacketKeyIsDown());
        }

        //release
        if((!KeyBindings.excavate.isKeyDown() && !crouchingEnabled) && excavationPressed) {
            excavationPressed = false;
            ExcavationPacketHandler.INSTANCE.sendToServer(new PacketKeyIsUp());
        }
    }

    @SubscribeEvent
    public static void pressMouse(InputEvent.MouseInputEvent e) {
        boolean crouchingEnabled = Excavation.config.crouchEnable.get();

        //press
        if((KeyBindings.excavate.isKeyDown() && !crouchingEnabled) && !excavationPressed) {
            excavationPressed = true;
            ExcavationPacketHandler.INSTANCE.sendToServer(new PacketKeyIsDown());
        }

        //release
        if((!KeyBindings.excavate.isKeyDown() && !crouchingEnabled) && excavationPressed) {
            excavationPressed = false;
            ExcavationPacketHandler.INSTANCE.sendToServer(new PacketKeyIsUp());
        }
    }
}
