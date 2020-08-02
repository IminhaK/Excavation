package net.bloop.testcraft;

import com.sun.org.apache.xpath.internal.operations.Bool;
import net.bloop.testcraft.ConfigHelper.ConfigValueListener;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TestCraft.MODID)
public class TestCraft {

    public static final String MODID = "testcraft";
    public static ConfigImplementation config;

    public TestCraft() {
        config = ConfigHelper.register(ModConfig.Type.SERVER, ConfigImplementation::new);

        Registry.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Registry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static class ConfigImplementation {
        public ConfigValueListener<Integer> maxBlocks;
        public ConfigValueListener<Integer> vacuumBlocks;

        public ConfigImplementation(ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber) {
            builder.push("General Category");
            this.maxBlocks = subscriber.subscribe(builder
                    .comment("Maximum Blocks")
                    .translation("config.max")
                    .defineInRange("max", 64, 1, 256));
            this.vacuumBlocks = subscriber.subscribe(builder
            .comment("All blocks are spawned under the player")
            .translation("config.vacuum")
            .defineInRange("vacuum", 1, 0, 1));
            builder.pop();
        }
    }
}
