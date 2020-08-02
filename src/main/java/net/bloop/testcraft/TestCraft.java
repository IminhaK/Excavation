package net.bloop.testcraft;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TestCraft.MODID)
public class TestCraft {
    public static final String MODID = "testcraft";

    public TestCraft() {
        Registry.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Registry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
