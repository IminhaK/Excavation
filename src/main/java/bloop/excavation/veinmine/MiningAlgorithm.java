package bloop.excavation.veinmine;

import bloop.excavation.Excavation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;
import java.util.List;

public class MiningAlgorithm {

    private final List<BlockPos> blocksToBreak = new ArrayList<>();
    private final BlockPos startingBlock;
    private final List<BlockPos> alreadyChecked = new ArrayList<>();
    private final Level level;
    private final ServerPlayer player;
    private int totalXp = 0;
    private final BlockPos playerPos;
    private final List<ItemStack> itemsToDrop = new ArrayList<>();

    public MiningAlgorithm(BlockPos start, Level level, Player playerIn) {
        this.level = level;
        player = (ServerPlayer) playerIn;
        playerPos = new BlockPos(player.getX(), player.getY(), player.getZ());
        startingBlock = start;
        blocksToBreak.add(start);
        alreadyChecked.add(start);
        itemsToDrop.add(ItemStack.EMPTY);
    }

    public void findBlocks() {
        BlockPos.MutableBlockPos checking = new BlockPos.MutableBlockPos();

        int[] range = {0, -1 ,1};
        List<BlockPos> dummyBlocks = new ArrayList<>();
        while(!blocksToBreak.equals(dummyBlocks)) {
            dummyBlocks.clear();
            dummyBlocks.addAll(blocksToBreak);
            for (BlockPos p : dummyBlocks) {
                for (int x : range) {
                    for (int y : range) {
                        for (int z : range) {
                            checking.set(p).move(x, y, z); //3x3x3
                            if (alreadyChecked.contains(checking))
                                continue;
                            blocksToBreak.add(checking.immutable());
                            alreadyChecked.add(checking.immutable());
                        }
                    }
                }
                //"Minesweeper" search pattern aka only touching
                /*for (Direction dir : Direction.values()) {
                    checking.setPos(p).move(dir); //each block thats touching the checked block
                    if (alreadyChecked.contains(checking.toImmutable()))
                        continue;
                    blocksToBreak.add(checking.toImmutable());
                    alreadyChecked.add(checking.toImmutable());
                }*/
            }
            dummyBlocks.clear();
            dummyBlocks.addAll(blocksToBreak);

            /*if(isBlockGrouped) { //TODO:Remove and replace with oredictionary
                for(BlockPos pos : dummyBlocks) {
                    boolean matches = false;
                    for(Block block : GroupFileReader.groups.get(blockRegistryName)) {
                        if(world.getBlockState(pos).getBlock() == block) {
                            matches = true;
                        }
                    }
                    if(!matches)
                        blocksToBreak.remove(pos);
                }
            } else {
                blocksToBreak.removeIf(p -> world.getBlockState(startingBlock).getBlock() != world.getBlockState(p).getBlock());
            }*/

            blocksToBreak.removeIf(p -> !level.getBlockState(startingBlock).getBlock().equals(level.getBlockState(p).getBlock()));
            if(blocksToBreak.size() >= Excavation.config.maxBlocks.get()) {
                blocksToBreak.subList(Excavation.config.maxBlocks.get(), blocksToBreak.size()).clear();
                break;
            }
        }
    }

    public boolean tryBreak(BlockPos p) {
        BlockState state = level.getBlockState(p);
        Block block = state.getBlock();
        int xp;

        if(block == Blocks.AIR) {
            return false;
        }
        if(!ForgeHooks.isCorrectToolForDrops(state, player) && !player.isCreative())
            return false;

        if(!level.isClientSide()) {
            xp = ForgeHooks.onBlockBreakEvent(level, player.gameMode.getGameModeForPlayer(), player, p);
            if(xp == -1)
                return false;

            if(!block.removedByPlayer(state, level, p, player, !player.isCreative(), state.getFluidState()))
                return false;
            //block.playerDestroy(world, player, p, state, world.getBlockEntity(p), player.getMainHandItem());

            if(!player.isCreative()) {
                if(!Excavation.config.vacuumBlocks.get()) //Causes extra lag
                    block.playerDestroy(level, player, p, state, level.getBlockEntity(p), player.getMainHandItem());
                else {
                    addToDropsList(p, block, state);
                    player.awardStat(Stats.BLOCK_MINED.get(block));
                    player.causeFoodExhaustion((float)(0.005F * Excavation.config.exhaustionMultiplier.get()));
                }
                if(xp > 0)
                    totalXp += xp;
            }
        } else {
            if(!block.removedByPlayer(state, level, p, player, !player.isCreative(), state.getFluidState()))
                return false;
            block.playerDestroy(level, player, p, state, level.getBlockEntity(p), player.getMainHandItem()); //Simulate breaking client side?
        }

        return true;
    }

    private void addToDropsList(BlockPos p, Block block, BlockState state) {
        List<ItemStack> drops = block.getDrops(state, (ServerLevel) level, p, level.getBlockEntity(p), player, player.getMainHandItem());
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
        if(!level.isClientSide() && level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !level.restoringBlockSnapshots) {
            for (ItemStack item : itemsToDrop) {
                if(!player.getInventory().add(item)) {
                    ItemEntity itemEntity = new ItemEntity(level, playerPos.getX() + 0.5, playerPos.getY() + 0.5, playerPos.getZ() + 0.5, item);
                    itemEntity.setDefaultPickUpDelay();
                    level.addFreshEntity(itemEntity);
                }
            }
        }
        itemsToDrop.clear();
    }

    public void mine() {
        totalXp = 0;
        for(BlockPos p : blocksToBreak) {
            if(tryBreak(p)) {
                if(!level.isClientSide()) {
                    ItemStack mh = player.getMainHandItem();
                    if(mh.isDamageableItem() && !player.isCreative()) {
                        if (mh.getDamageValue() < mh.getMaxDamage()) {
                            mh.hurt(1, player.getRandom(), player);
                        } else {
                            //player.sendBreakAnimation(EquipmentSlotType.MAINHAND);
                            //player.getHeldItemMainhand().shrink(1);
                            mh.hurtAndBreak(1, player, player ->
                                    player.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                            break;
                        }
                    }
                }
            }

        }
        if(!level.isClientSide())
            dropItems();
        //world.getBlockState(startingBlock).getBlock().dropXpOnBlockBreak(world, playerPos, totalXp);
        level.getBlockState(startingBlock).getBlock().popExperience((ServerLevel) level, playerPos, totalXp);
    }
}
