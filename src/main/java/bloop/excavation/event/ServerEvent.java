package bloop.excavation.event;

import bloop.excavation.config.Tags;
import bloop.excavation.veinmine.MiningAlgorithm;
import bloop.excavation.Excavation;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Excavation.MODID)
public class ServerEvent {

    private static boolean alreadyBreaking = false;
    public static Set<UUID> playersWithButtonDown = new HashSet<>();

    @SubscribeEvent
    public static void veinMine(BlockEvent.BreakEvent e) {
        PlayerEntity player = e.getPlayer();
        World world = e.getPlayer().level;
        BlockPos blockPos = e.getPos();
        Block block = world.getBlockState(blockPos).getBlock();
        //checks once
        if(player.getFoodData().getFoodLevel() == 0)
            return;
        if(alreadyBreaking)
            return;
        if(player instanceof FakePlayer)
            return;
        if(Excavation.config.crouchEnable.get()) {
            if(!player.isCrouching()) {
                removePlayer(player.getUUID());
                return;
            } else {
                addPlayer(player.getUUID());
            }
        }

        boolean correctTool = ForgeHooks.canHarvestBlock(world.getBlockState(blockPos), player, world, blockPos);
        if(!correctTool && Excavation.config.mineWithTool.get() && !player.isCreative())
            return;

        if((!world.getBlockState(blockPos).getBlock().canHarvestBlock(world.getBlockState(blockPos), world, blockPos, player) && !player.isCreative()) || !world.isLoaded(blockPos))
            return;
        //check to see if world.getBlockState(blockPos).getBlock() is in the white/blacklist
        boolean blockIsAllowed = !Tags.blacklist.contains(block) && (Tags.whitelist.getValues().isEmpty() || Tags.whitelist.contains(block));
        if(!blockIsAllowed)
            return;

        if(!playersWithButtonDown.contains(player.getUUID())) {
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

    public static void addPlayer(UUID uuid) {
        playersWithButtonDown.add(uuid);
    }

    public static void removePlayer(UUID uuid) {
        playersWithButtonDown.removeIf(u -> u.equals(uuid));
    }
}
