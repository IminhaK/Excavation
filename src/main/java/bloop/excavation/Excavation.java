package bloop.excavation;

import bloop.excavation.config.ConfigHelper;
import bloop.excavation.config.ConfigHelper.ConfigValueListener;

import bloop.excavation.network.ExcavationPacketHandler;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Excavation.MODID)
public class Excavation {

    public static final String MODID = "excavation";
    public static final String NAME = "excavation.name";
    public static ConfigImplementation config;

    public Excavation() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        config = ConfigHelper.register(ModConfig.Type.COMMON, ConfigImplementation::new);

        ExcavationPacketHandler.registerMessages();
    }

    public static class ConfigImplementation {
        public ConfigValueListener<Integer> maxBlocks;
        public ConfigValueListener<Boolean> vacuumBlocks;
        public ConfigValueListener<Double> exhaustionMultiplier;
        public ConfigValueListener<Boolean> mineWithTool;
        public ConfigValueListener<Boolean> crouchEnable;

        public ConfigImplementation(ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber) {
            builder.comment("Per-client config.").push("client");
            this.crouchEnable = subscriber.subscribe(builder
                    .comment("Disable the keybind and activate excavation by crouching. DEFAULT: false")
                    .translation("config.crouch")
                    .define("crouch", false));
            builder.pop();
            builder.comment("Server-side config. Client's version of these settings are ignored if on a dedicated server.").push("server");
            this.maxBlocks = subscriber.subscribe(builder
                    .comment("Maximum blocks to break with one excavation (Integer). DEFAULT: 64")
                    .translation("config.max")
                    .defineInRange("max", 64, 1, Integer.MAX_VALUE));
            this.vacuumBlocks = subscriber.subscribe(builder
                    .comment("All blocks are placed in the player's inventory unless it is full (False may cause extra lag on slower machines.) DEFAULT: true")
                    .translation("config.vacuum")
                    .define("vacuum", true));
            this.mineWithTool = subscriber.subscribe(builder
                    .comment("Does the player need to mine with a valid tool? DEFAULT: false")
                    .translation("config.tool")
                    .define("tool", false));
            this.exhaustionMultiplier = subscriber.subscribe(builder
                    .comment("Multiply the default Minecraft block exhaustion by this much (Double). DEFAULT: 1.0")
                    .translation("config.exhaustion")
                    .defineInRange("exhaustion", 1.0, 0.0, Double.MAX_VALUE));
            builder.pop();
        }
    }
}
