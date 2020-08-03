package net.bloop.excavation.veinmine;

import net.bloop.excavation.Excavation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
    private int totalXp = 0;
    private BlockPos playerPos;
    private List<ItemStack> itemsToDrop = new ArrayList<>();

    public MiningAlgorithm(BlockPos start, World worldIn, PlayerEntity playerIn) {
        world = worldIn;
        player = (ServerPlayerEntity)playerIn;
        playerPos = new BlockPos(player.getPosX(), player.getPosY(), player.getPosZ());
        startingBlock = start;
        blocksToBreak.add(start);
        itemsToDrop.add(ItemStack.EMPTY);
    }

    //TODO: Optimize like minesweeper search pattern
    //TODO: Reduce number of unique item entities dropped
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
                            checking.setPos(p).move(x, y, z); //3x3x3
                            if (alreadyChecked.contains(checking.toImmutable()))
                                continue;
                            blocksToBreak.add(checking.toImmutable());
                            alreadyChecked.add(checking.toImmutable());
                        }
                    }
                }
                /*for (Direction dir : Direction.values()) {
                    checking.setPos(p).move(dir); //each block thats touching the checked block
                    if (alreadyChecked.contains(checking.toImmutable()))
                        continue;
                    blocksToBreak.add(checking.toImmutable());
                    alreadyChecked.add(checking.toImmutable());
                }*/
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

            if(!player.isCreative()) {
                if(Excavation.config.vacuumBlocks.get() == 0) //Causes extra lag
                    block.harvestBlock(world, player, p, state, world.getTileEntity(p), player.getHeldItemMainhand());
                else {
                    addToDropsList(p, block, state);
                    player.addStat(Stats.BLOCK_MINED.get(block));
                    player.addExhaustion(0.005F);
                    //block.harvestBlock(world, player, playerPos, state, world.getTileEntity(p), player.getHeldItemMainhand());
                }
                if(xp > 0)
                    totalXp += xp;
            }
        } else {
            if(!block.removedByPlayer(state, world, p, player, !player.isCreative(), state.getFluidState()))
                return false;
            block.onPlayerDestroy(world, p, state);
        }

        return true;
    }

    private void addToDropsList(BlockPos p, Block block, BlockState state) {
        List<ItemStack> drops = block.getDrops(state, (ServerWorld)world, p, world.getTileEntity(p), player, player.getHeldItemMainhand());
        int searchingIndex;
        List<ItemStack> dummyItemsToDrop = new ArrayList<>();

        for(ItemStack drop : drops) {
            searchingIndex = 0;
            dummyItemsToDrop.clear();
            dummyItemsToDrop.addAll(itemsToDrop);
            for(ItemStack existingItem : dummyItemsToDrop) {
                //TODO: items get duped. fix
                if(existingItem.getItem() == drop.getItem()) {
                    itemsToDrop.get(searchingIndex).setCount(itemsToDrop.get(searchingIndex).getCount() + drop.getCount()); //add (amount of drop) to existing amount
                } else {
                    itemsToDrop.add(drop); //add another entry cuz it doesnt already exist
                }
                searchingIndex++;
            }
        }
    }

    private void dropItems() {
        for(ItemStack item : itemsToDrop) {
            Block.spawnAsEntity(world, playerPos, item);
        }
    }

    private void cleanOutTrash() {
        blocksToBreak.removeIf(p -> world.getBlockState(startingBlock).getBlock() != world.getBlockState(p).getBlock());
    }

    public void mine() {
        totalXp = 0;
        for(BlockPos p : blocksToBreak) {
            if (p.equals(startingBlock))
                continue;

            if(tryBreak(p)) {
                if(!world.isRemote)
                    player.getHeldItemMainhand().attemptDamageItem(1, new Random(), player);
            }

        }
        if(!world.isRemote)
            dropItems();
        world.getBlockState(startingBlock).getBlock().dropXpOnBlockBreak(world, playerPos, totalXp);
    }
}
