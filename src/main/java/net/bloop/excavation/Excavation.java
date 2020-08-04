package net.bloop.excavation;

import net.bloop.excavation.ConfigHelper.ConfigValueListener;

import net.bloop.excavation.network.ExcavationPacketHandler;
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
        public ConfigValueListener<Integer> maxBlocks;
        public ConfigValueListener<Integer> vacuumBlocks;

        public ConfigImplementation(ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber) {
            builder.push("General Category");
            this.maxBlocks = subscriber.subscribe(builder
                    .comment("Maximum Blocks")
                    .translation("config.max")
                    .defineInRange("max", 64, 1, 1024));
            this.vacuumBlocks = subscriber.subscribe(builder
            .comment("All blocks are spawned under the player")
            .translation("config.vacuum")
            .defineInRange("vacuum", 1, 0, 1));
            builder.pop();
        }
    }
}
