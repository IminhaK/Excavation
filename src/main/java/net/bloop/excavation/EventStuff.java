package net.bloop.excavation;

import net.bloop.excavation.veinmine.MiningAlgorithm;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputEvent;
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
        if(!(KeyBindings.excavate.isKeyDown()))
            return;
        if(!world.getBlockState(e.getPos()).getBlock().canHarvestBlock(world.getBlockState(e.getPos()), world, e.getPos(), player))
            return;

        MiningAlgorithm miningAlgorithm = new MiningAlgorithm(e.getPos(), world, player);

        miningAlgorithm.findBlocks();

        alreadyBreaking = true;
        miningAlgorithm.mine();
        e.setCanceled(true);
        //reeeee just let me break you
        miningAlgorithm.tryBreak(e.getPos());
        miningAlgorithm.dropItems();
        alreadyBreaking = false;
    }

    /*@SubscribeEvent
    public static void pressKey(InputEvent.KeyInputEvent e) {
        if(KeyBindings.mode.isPressed())
            System.out.println("HE DO BE PRESSIN THO");
    }*/

}
