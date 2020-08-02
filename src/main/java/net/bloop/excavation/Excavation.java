package net.bloop.excavation;

import net.bloop.excavation.ConfigHelper.ConfigValueListener;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(Excavation.MODID)
public class Excavation {

    public static final String MODID = "excavation";
    public static ConfigImplementation config;

    public Excavation() {
        config = ConfigHelper.register(ModConfig.Type.SERVER, ConfigImplementation::new);
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
