package net.bloop.testcraft.veinmine;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

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
        boolean tooBig = false;
        while(!blocksToBreak.equals(dummyBlocks) || !tooBig) {
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
            tooBig = blocksToBreak.size() < 100;
        }
        cleanOutTrash(); //security
    }

    public void mine() {
        /*for(BlockPos p : blocksToBreak) {
            try {
                player.interactionManager.tryHarvestBlock(p);
            } catch(NullPointerException e) {
                System.out.println("ERROR BLOCK WAS MISSING");
            }
        }*/
        for(BlockPos p : blocksToBreak)
            world.destroyBlock(p, true);
    }

    private void cleanOutTrash() {
        blocksToBreak.removeIf(p -> world.getBlockState(startingBlock).getBlock() != world.getBlockState(p).getBlock());
    }
}
