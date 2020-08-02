package net.bloop.testcraft.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class TestBlock extends Block {

    public TestBlock() {
        super(AbstractBlock.Properties.create(Material.EARTH)
                .sound(SoundType.GROUND)
                .hardnessAndResistance(1.5f)
                .harvestTool(ToolType.SHOVEL)
                );
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            worldIn.createExplosion(new TNTEntity(worldIn, 0, 0, 0, player), pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 4.0f, Explosion.Mode.BREAK);
            return ActionResultType.CONSUME;
        }
    }
}
