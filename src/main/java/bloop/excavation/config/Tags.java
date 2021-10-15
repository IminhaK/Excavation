package bloop.excavation.config;

import bloop.excavation.Excavation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;

public class Tags {

    public static final Tag<Block> whitelist = getBlockTagWrapper("whitelist");
    public static final Tag<Block> blacklist = getBlockTagWrapper("blacklist");

    public static Tag<Block> getBlockTagWrapper(String path) {
        //return BlockTags.makeWrapperTag(Excavation.MODID + ":" + path);
        return BlockTags.bind(Excavation.MODID + ":" + path);
    }
}
