package net.bloop.testcraft;

import net.bloop.testcraft.block.TestBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Registry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TestCraft.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TestCraft.MODID);

    //TestBlock
    public static final RegistryObject<Block> TEST_BLOCK = BLOCKS.register("testblock", () -> new TestBlock());
    public static final RegistryObject<Item> TEST_BLOCK_ITEM = ITEMS.register("testblock", () -> new BlockItem(TEST_BLOCK.get(), new Item.Properties().group(ItemGroup.MISC)));
}
