package net.bloop.excavation.config;

import net.bloop.excavation.Excavation;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;

public class Tags {

    public static final ITag<Block> whitelist = getBlockTagWrapper("whitelist");
    public static final ITag<Block> blacklist = getBlockTagWrapper("blacklist");

    public static ITag<Block> getBlockTagWrapper(String path) {
        return BlockTags.makeWrapperTag(Excavation.MODID + ":" + path);
    }
}
