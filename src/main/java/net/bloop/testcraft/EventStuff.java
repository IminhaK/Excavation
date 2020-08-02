package net.bloop.testcraft;

import net.bloop.testcraft.veinmine.MiningAlgorithm;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = TestCraft.MODID)
public class EventStuff {

    /*@SubscribeEvent
    public static void breakEvent(BlockEvent.BreakEvent e) {
        PlayerEntity player = e.getPlayer();

        int length = new Random().nextInt(11) + 10;
        //https://stackoverflow.com/a/16812721/8846453 random number of a specific string "A"
        String screaming = new String(new char[length]).replace("\0", "A");

        player.sendMessage(ITextComponent.func_241827_a_(screaming), player.getGameProfile().getId());

    }*/

    @SubscribeEvent
    public static void veinMine(BlockEvent.BreakEvent e) {
        PlayerEntity player = e.getPlayer();
        World world = e.getWorld().getWorld();
        if(!player.isSneaking())
            return;
        if(!world.getBlockState(e.getPos()).getBlock().canHarvestBlock(world.getBlockState(e.getPos()), world, e.getPos(), player))
            return;

        MiningAlgorithm miningAlgorithm = new MiningAlgorithm(e.getPos(), world, player);

        miningAlgorithm.findBlocks();

        System.out.println("Its time to b-b-b-b-break!");
        miningAlgorithm.mine();
    }

}
