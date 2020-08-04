package net.bloop.excavation.event;

import net.bloop.excavation.Excavation;
import net.bloop.excavation.KeyBindings;
import net.bloop.excavation.network.ExcavationPacketHandler;
import net.bloop.excavation.network.PacketExcavate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Excavation.MODID)
public class ServerEvent {

    private static boolean alreadyBreaking = false;
    private static boolean excavationPressed = false;

    @SubscribeEvent
    public static void veinMine(BlockEvent.BreakEvent e) {
        if(alreadyBreaking)
            return;
        PlayerEntity player = e.getPlayer();
        World world = e.getWorld().getWorld();

        if(!world.getBlockState(e.getPos()).getBlock().canHarvestBlock(world.getBlockState(e.getPos()), world, e.getPos(), player))
            return;

        if(!excavationPressed) {
            return;
        } else {
            alreadyBreaking = true;
            System.out.println("SENDING!");
            //send packet when the key is held down
            e.setCanceled(true);
            ExcavationPacketHandler.INSTANCE.sendToServer(new PacketExcavate(e.getPos()));
            //alredyBreaking = false; Sent back to client by PacketExcavate
        }
    }

    public static void setAlreadyBreaking(boolean breaking) {
        alreadyBreaking = breaking;
    }

    public static void setExcavationPressed(boolean pressed) {
        excavationPressed = pressed;
    }

}
