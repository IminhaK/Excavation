package bloop.excavation.event;

import bloop.excavation.KeyBindings;
import bloop.excavation.network.ExcavationPacketHandler;
import bloop.excavation.network.PacketKeyIsDown;
import bloop.excavation.network.PacketKeyIsUp;
import bloop.excavation.Excavation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Excavation.MODID, value = Dist.CLIENT)
public class ClientEvent {

    private static boolean excavationPressed = false;

    @Mod.EventBusSubscriber(modid = Excavation.MODID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent e) {
            e.register(KeyBindings.EXCAVATE);
        }
    }

    @SubscribeEvent
    public static void pressKey(InputEvent.Key e) {
        boolean crouchingEnabled = Excavation.config.crouchEnable.get();

        //press
        if((KeyBindings.EXCAVATE.isDown() && !crouchingEnabled) && !excavationPressed) {
            excavationPressed = true;
            ExcavationPacketHandler.INSTANCE.sendToServer(new PacketKeyIsDown());
        }

        //release
        if((!KeyBindings.EXCAVATE.isDown() && !crouchingEnabled) && excavationPressed) {
            excavationPressed = false;
            ExcavationPacketHandler.INSTANCE.sendToServer(new PacketKeyIsUp());
        }
    }

    @SubscribeEvent
    public static void pressMouse(InputEvent.MouseButton e) {
        boolean crouchingEnabled = Excavation.config.crouchEnable.get();

        //press
        if((KeyBindings.EXCAVATE.isDown() && !crouchingEnabled) && !excavationPressed) {
            excavationPressed = true;
            ExcavationPacketHandler.INSTANCE.sendToServer(new PacketKeyIsDown());
        }

        //release
        if((!KeyBindings.EXCAVATE.isDown() && !crouchingEnabled) && excavationPressed) {
            excavationPressed = false;
            ExcavationPacketHandler.INSTANCE.sendToServer(new PacketKeyIsUp());
        }
    }
}
