package net.bloop.excavation.event;

import net.bloop.excavation.Excavation;
import net.bloop.excavation.config.Tags;
import net.bloop.excavation.veinmine.MiningAlgorithm;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Excavation.MODID)
public class ServerEvent {

    private static boolean alreadyBreaking = false;
    private static boolean excavationPressed = false;

    @SubscribeEvent
    public static void veinMine(BlockEvent.BreakEvent e) {
        PlayerEntity player = e.getPlayer();
        World world = e.getWorld().getWorld();
        BlockPos blockPos = e.getPos();
        Block block = world.getBlockState(blockPos).getBlock();
        //checks once
        if(player.getFoodStats().getFoodLevel() == 0)
            return;
        if(alreadyBreaking)
            return;

        boolean correctTool = ForgeHooks.canToolHarvestBlock(world, blockPos, player.getHeldItemMainhand());

        if(!correctTool && Excavation.config.mineWithTool.get() == 1 && !player.isCreative())
            return;

        if((!world.getBlockState(blockPos).getBlock().canHarvestBlock(world.getBlockState(blockPos), world, blockPos, player) && !player.isCreative()) || !world.isBlockLoaded(blockPos))
            return;
        //check to see if world.getBlockState(blockPos).getBlock() is in the white/blacklist
        boolean blockIsAllowed = !Tags.blacklist.contains(block) && (Tags.whitelist.getAllElements().isEmpty() || Tags.whitelist.contains(block));
        if(!blockIsAllowed)
            return;


        if(!excavationPressed) {
            return;
        } else {
            alreadyBreaking = true;
            e.setCanceled(true);
            MiningAlgorithm miningAlgorithm = new MiningAlgorithm(blockPos, world, player);

            miningAlgorithm.findBlocks();

            miningAlgorithm.mine();
            setAlreadyBreaking(false);
        }
    }

    public static void setAlreadyBreaking(boolean breaking) {
        alreadyBreaking = breaking;
    }

    public static void setExcavationPressed(boolean pressed) {
        excavationPressed = pressed;
    }
}
