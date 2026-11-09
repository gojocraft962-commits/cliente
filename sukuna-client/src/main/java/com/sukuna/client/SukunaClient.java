package com.sukuna.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public final class SukunaClient implements ClientModInitializer {
    private static KeyMapping openMenuKey;

    @Override
    public void onInitializeClient() {
        ModuleRegistry.registerDefaults();

        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.sukuna.open_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyMapping.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.consumeClick()) {
                // The ClickGUI screen will be opened here once its interface is implemented.
                System.out.println("[Sukuna] Menu key pressed (Right Shift).");
            }
        });

        System.out.println("[Sukuna] Loaded " + ModuleRegistry.all().size() + " module entries.");
    }
}
