package bloop.excavation.config;

import bloop.excavation.Excavation;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagRegistry;

public class Tags {

    public static final ITag<Block> whitelist = getBlockTagWrapper("whitelist");
    public static final ITag<Block> blacklist = getBlockTagWrapper("blacklist");

    public static ITag<Block> getBlockTagWrapper(String path) {
        //return BlockTags.makeWrapperTag(Excavation.MODID + ":" + path);
        return BlockTags.bind(Excavation.MODID + ":" + path);
    }
}
