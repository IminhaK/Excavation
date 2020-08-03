package net.bloop.excavation;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class KeyBindings {

    private static final String categoryName = Excavation.MODID + " (" + Excavation.NAME + ")";

    public static final KeyBinding excavate;
    public static final KeyBinding mode;
    private static final List<KeyBinding> allBindings;

    static InputMappings.Input getKey(int key) {
        return InputMappings.Type.KEYSYM.getOrMakeInput(key);
    }

    static {
        allBindings = ImmutableList.of(
                excavate = new KeyBinding("key.excavate.excavate", KeyConflictContext.IN_GAME, getKey(GLFW.GLFW_KEY_GRAVE_ACCENT), categoryName),
                mode = new KeyBinding("key.excavate.mode", KeyConflictContext.IN_GAME, getKey(GLFW.GLFW_KEY_R), categoryName)
        );
    }

    private KeyBindings() {

    }

    public static void init() {
        for(KeyBinding binding : allBindings) {
            ClientRegistry.registerKeyBinding(binding);
        }
    }
}
