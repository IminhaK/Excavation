package bloop.excavation;

import bloop.excavation.config.ConfigHelper;
import bloop.excavation.network.ExcavationPacketHandler;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Excavation.MODID)
public class Excavation {

    public static final String MODID = "excavation";
    public static final String NAME = "Excavation";
    public static ConfigImplementation config;

    public Excavation() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        config = ConfigHelper.register(ModConfig.Type.SERVER, ConfigImplementation::new);
        DistExecutor.runWhenOn(Dist.CLIENT, ()->()-> clientStart(modEventBus));

        ExcavationPacketHandler.registerMessages();
    }

    private static void clientStart(IEventBus modEventBus) {
            modEventBus.addListener(EventPriority.NORMAL, false, ColorHandlerEvent.Block.class, setupEvent ->
                    modEventBus.addListener(EventPriority.NORMAL, false, FMLLoadCompleteEvent.class, fmlLoadCompleteEvent ->
                            KeyBindings.init()));
    }

    public static class ConfigImplementation {
        public ConfigHelper.ConfigValueListener<Integer> maxBlocks;
        public ConfigHelper.ConfigValueListener<Integer> vacuumBlocks;
        public ConfigHelper.ConfigValueListener<Double> exhaustionMultiplier;
        public ConfigHelper.ConfigValueListener<Integer> mineWithTool;

        public ConfigImplementation(ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber) {
            builder.push("General Category");
            this.maxBlocks = subscriber.subscribe(builder
                    .comment("Maximum blocks to break with one excavation (Integer)")
                    .translation("config.max")
                    .defineInRange("max", 64, 1, Integer.MAX_VALUE));
            this.vacuumBlocks = subscriber.subscribe(builder
                    .comment("All blocks are spawned under the player (0 false, 1 true)")
                    .translation("config.vacuum")
                    .defineInRange("vacuum", 1, 0, 1));
            this.exhaustionMultiplier = subscriber.subscribe(builder
                    .comment("Multiply the default Minecraft block exhaustion by this much (Double)")
                    .translation("config.exhaustion")
                    .defineInRange("exhaustion", 1.0, 0.0, Double.MAX_VALUE));
            this.mineWithTool = subscriber.subscribe(builder
                    .comment("Does the player need to mine with a valid tool? (0 false, 1 true)")
                    .translation("config.tool")
                    .defineInRange("tool", 0 , 0 ,1));
            builder.pop();
        }
    }
}
