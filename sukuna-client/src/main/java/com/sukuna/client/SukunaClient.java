package com.sukuna.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public final class SukunaClient implements ClientModInitializer {
    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        ModuleRegistry.registerDefaults();

        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.sukuna.open_menu",
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                // ClickGUI screen will be connected here when its interface is added.
                System.out.println("[Sukuna] Menu key pressed (Right Shift).");
            }
        });

        System.out.println("[Sukuna] Loaded " + ModuleRegistry.all().size() + " module entries.");
    }
}
