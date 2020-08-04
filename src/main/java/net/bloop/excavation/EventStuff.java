package net.bloop.excavation;

import net.bloop.excavation.network.ExcavationPacketHandler;
import net.bloop.excavation.network.PacketExcavate;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Excavation.MODID)
public class EventStuff {

    private static boolean alreadyBreaking = false;

    @SubscribeEvent
    public static void veinMine(BlockEvent.BreakEvent e) {
        if(alreadyBreaking)
            return;
        PlayerEntity player = e.getPlayer();
        World world = e.getWorld().getWorld();

        if(!world.getBlockState(e.getPos()).getBlock().canHarvestBlock(world.getBlockState(e.getPos()), world, e.getPos(), player))
            return;

        boolean singleplayer = Minecraft.getInstance().isSingleplayer() && !Minecraft.getInstance().getIntegratedServer().getPublic();
        if(singleplayer) { //client
            if(!(KeyBindings.excavate.isKeyDown())) {
                return;
            } else {
                alreadyBreaking = true;
                //send packet when the key is held down
                e.setCanceled(true);
                PacketExcavate exca = new PacketExcavate(e.getPos());
                ExcavationPacketHandler.INSTANCE.sendToServer(exca);
            }
        }
    }

    public static void setAlreadyBreaking(boolean breaking) {
        alreadyBreaking = breaking;
    }

}
