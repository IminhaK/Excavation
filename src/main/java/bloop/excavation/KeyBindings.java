package bloop.excavation;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class KeyBindings {

    private static final String categoryName = Excavation.NAME;

    public static final KeyMapping excavate;
    private static final List<KeyMapping> allBindings;

    static InputConstants.Key getKey(int key) {
        return InputConstants.Type.KEYSYM.getOrCreate(key);
    }

    static {
        allBindings = ImmutableList.of(
                excavate = new KeyMapping("key.excavate.excavate", KeyConflictContext.IN_GAME, getKey(GLFW.GLFW_KEY_GRAVE_ACCENT), categoryName)
        );
    }

    private KeyBindings() {

    }

    public static void init() {
        for(KeyMapping binding : allBindings) {
            ClientRegistry.registerKeyBinding(binding);
        }
    }
}
