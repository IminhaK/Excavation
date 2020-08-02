package net.bloop.excavation.veinmine;

import net.bloop.excavation.Excavation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MiningAlgorithm {

    private List<BlockPos> blocksToBreak = new ArrayList<>();
    private BlockPos startingBlock;
    private List<BlockPos> alreadyChecked = new ArrayList<>();
    private World world;
    private ServerPlayerEntity player;

    public MiningAlgorithm(BlockPos start, World worldIn, PlayerEntity playerIn) {
        world = worldIn;
        player = (ServerPlayerEntity)playerIn;
        startingBlock = start;
        blocksToBreak.add(start);
    }

    public void findBlocks() {
        BlockPos.Mutable checking = new BlockPos.Mutable();

        int[] range = {0, -1 ,1};
        List<BlockPos> dummyBlocks = new ArrayList<>();
        while(!blocksToBreak.equals(dummyBlocks)) {
            dummyBlocks.clear();
            dummyBlocks.addAll(blocksToBreak);
            for (BlockPos p : dummyBlocks) {
                for (int x : range) {
                    for (int y : range) {
                        for (int z : range) {
                            checking.setPos(p).move(x, y, z);
                            if (alreadyChecked.contains(checking.toImmutable()))
                                continue;
                            blocksToBreak.add(checking.toImmutable());
                            alreadyChecked.add(checking.toImmutable());
                        }
                    }
                }
            }
            cleanOutTrash();
            if(blocksToBreak.size() >= Excavation.config.maxBlocks.get())
                break;
        }
        cleanOutTrash(); //security
    }

    private boolean tryBreak(BlockPos p) {
        BlockState state = world.getBlockState(p);
        Block block = state.getBlock();
        BlockPos playerPos = new BlockPos(player.getPosX(), player.getPosY(), player.getPosZ());
        int xp;

        if(world.isAirBlock(p))
            return false;
        if(!ForgeHooks.canHarvestBlock(state, player, world, p))
            return false;

        if(!world.isRemote) {
            xp = ForgeHooks.onBlockBreakEvent(world, player.interactionManager.getGameType(), player, p);
            if(xp == -1)
                return false;

            if(!block.removedByPlayer(state, world, p, player, !player.isCreative(), state.getFluidState()))
                return false;
            block.onPlayerDestroy(world, p, state);

            System.out.println("The player is in survival: " + !player.isCreative());
            if(!player.isCreative()) {
                System.out.println("We really do be breaking those blocks tho");
                if(Excavation.config.vacuumBlocks.get() == 0)
                    block.harvestBlock(world, player, p, state, world.getTileEntity(p), player.getHeldItemMainhand());
                else {
                    block.harvestBlock(world, player, playerPos, state, world.getTileEntity(p), player.getHeldItemMainhand());
                }
                if(xp > 0)
                    block.dropXpOnBlockBreak(world, p, xp);
            }
        } else {
            if(!block.removedByPlayer(state, world, p, player, !player.isCreative(), state.getFluidState()))
                return false;
            block.onPlayerDestroy(world, p, state);
        }

        return true;
    }

    private void cleanOutTrash() {
        blocksToBreak.removeIf(p -> world.getBlockState(startingBlock).getBlock() != world.getBlockState(p).getBlock());
    }

    public void mine() {
        for(BlockPos p : blocksToBreak) {
            if (p.equals(startingBlock))
                continue;

            if(tryBreak(p)) {
                if(!world.isRemote)
                    player.getHeldItemMainhand().attemptDamageItem(1, new Random(), player);
            }

        }
    }
}
