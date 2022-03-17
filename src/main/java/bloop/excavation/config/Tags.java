package bloop.excavation.config;

import bloop.excavation.Excavation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class Tags {

    public static final TagKey<Block> whitelist = getBlockTagWrapper("whitelist");
    public static final TagKey<Block> blacklist = getBlockTagWrapper("blacklist");

    public static TagKey<Block> getBlockTagWrapper(String path) {
        //return BlockTags.makeWrapperTag(Excavation.MODID + ":" + path);
        //return BlockTags.bind(Excavation.MODID + ":" + path);
        return BlockTags.create(new ResourceLocation(Excavation.MODID + ":" + path));
    }
}
