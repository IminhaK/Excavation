package bloop.excavation;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    private static final String EXCAVATION_CATEGORY = Excavation.NAME;
    private static final String KEY_EXCAVATE = "key.excavate.excavate";

    public static final KeyMapping EXCAVATE = new KeyMapping(KEY_EXCAVATE, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, EXCAVATION_CATEGORY);
}
