package bloop.excavation.veinmine;

import bloop.excavation.Excavation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;
import java.util.List;

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
                //"Minesweeper" search pattern
                /*for (Direction dir : Direction.values()) {
                    checking.setPos(p).move(dir); //each block thats touching the checked block
                    if (alreadyChecked.contains(checking.toImmutable()))
                        continue;
                    blocksToBreak.add(checking.toImmutable());
                    alreadyChecked.add(checking.toImmutable());
                }*/
            }
            blocksToBreak.removeIf(p -> world.getBlockState(startingBlock).getBlock() != world.getBlockState(p).getBlock());
            if(blocksToBreak.size() >= Excavation.config.maxBlocks.get())
                break;
        }
        blocksToBreak.removeIf(p -> world.getBlockState(startingBlock).getBlock() != world.getBlockState(p).getBlock()); //security
    }

    public boolean tryBreak(BlockPos p) {
        BlockState state = world.getBlockState(p);
        Block block = state.getBlock();
        int xp;

        if(world.isAirBlock(p))
            return false;
        if(!ForgeHooks.canHarvestBlock(state, player, world, p) && !player.isCreative())
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
                    player.addExhaustion((float)(0.005F * Excavation.config.exhaustionMultiplier.get()));
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
        List<Item> dummyItemsToDrop = new ArrayList<>();
        int oldCount;
        int extraCount;

        itemsToDrop.forEach(i -> dummyItemsToDrop.add(i.getItem()));

        for(ItemStack drop : drops) {
            if(dummyItemsToDrop.contains(drop.getItem())) {
                    oldCount = itemsToDrop.get(dummyItemsToDrop.indexOf(drop.getItem())).getCount();
                    extraCount = drop.getCount();
                    itemsToDrop.get(dummyItemsToDrop.indexOf(drop.getItem())).setCount(oldCount + extraCount);
            } else {
                itemsToDrop.add(drop);
            }
        }

        itemsToDrop.remove(ItemStack.EMPTY);
    }

    public void dropItems() {
        if(!world.isRemote && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && !world.restoringBlockSnapshots) {
            for (ItemStack item : itemsToDrop) {
                //Block.spawnAsEntity(world, playerPos, item);
                if(!player.inventory.addItemStackToInventory(item)) {
                    ItemEntity itemEntity = new ItemEntity(world, playerPos.getX() + 0.5, playerPos.getY() + 0.5, playerPos.getZ() + 0.5, item);
                    itemEntity.setDefaultPickupDelay();
                    world.addEntity(itemEntity);
                }
            }
        }
        itemsToDrop.clear();blocksToBreak.removeIf(p -> world.getBlockState(startingBlock).getBlock() != world.getBlockState(p).getBlock());
    }

    public void mine() {
        totalXp = 0;
        for(BlockPos p : blocksToBreak) {
            if(tryBreak(p)) {
                if(!world.isRemote) {
                    if(player.getHeldItemMainhand().isDamageable() && !player.isCreative()) {
                        if (player.getHeldItemMainhand().getDamage() < player.getHeldItemMainhand().getMaxDamage()) {
                            player.getHeldItemMainhand().attemptDamageItem(1, player.getRNG(), player);
                        } else {
                            player.getHeldItemMainhand().shrink(1);
                            break;
                        }
                    }
                }
            }

        }
        if(!world.isRemote)
            dropItems();
        world.getBlockState(startingBlock).getBlock().dropXpOnBlockBreak(world, playerPos, totalXp);
    }
}
