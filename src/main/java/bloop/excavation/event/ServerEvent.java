package bloop.excavation.event;

import bloop.excavation.config.Tags;
import bloop.excavation.veinmine.MiningAlgorithm;
import bloop.excavation.Excavation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
        Player player = e.getPlayer();
        Level level = e.getPlayer().level;
        BlockPos blockPos = e.getPos();
        Block block = level.getBlockState(blockPos).getBlock();
        //checks once
        if (player.getFoodData().getFoodLevel() == 0)
            return;
        if (alreadyBreaking)
            return;
        if (player instanceof FakePlayer)
            return;
        if (Excavation.config.crouchEnable.get()) {
            if (!player.isCrouching()) {
                removePlayer(player.getUUID());
                return;
            } else {
                addPlayer(player.getUUID());
            }
        }

        boolean correctTool = ForgeHooks.isCorrectToolForDrops(level.getBlockState(blockPos), player);
        if (!correctTool && Excavation.config.mineWithTool.get() && !player.isCreative())
            return;

        if ((!level.getBlockState(blockPos).getBlock().canHarvestBlock(level.getBlockState(blockPos), level, blockPos, player) && !player.isCreative()) || !level.isLoaded(blockPos))
            return;
        //check to see if level.getBlockState(blockPos).getBlock() is in the white/blacklist
        //boolean blockIsAllowed = !Tags.blacklist.getValues().contains(block) && (Tags.whitelist.getValues().isEmpty() || Tags.whitelist.getValues().contains(block));
        boolean blockIsAllowed = !block.builtInRegistryHolder().is(Tags.blacklist) &&
                ((Registry.BLOCK.getTag(Tags.whitelist).map(tag -> tag.size() == 0).orElse(false))
                || block.builtInRegistryHolder().is(Tags.whitelist));
        if(!blockIsAllowed)
            return;

        if(playersWithButtonDown.contains(player.getUUID())) {
            alreadyBreaking = true;
            e.setCanceled(true);
            MiningAlgorithm miningAlgorithm = new MiningAlgorithm(blockPos, level, player);

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
