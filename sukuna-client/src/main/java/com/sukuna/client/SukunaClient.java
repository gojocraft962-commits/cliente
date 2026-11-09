package com.sukuna.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
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
            CombatModuleController.tick(client);
            while (openMenuKey.consumeClick()) {
                if (client.screen instanceof ClickGuiScreen) {
                    client.setScreen(null);
                } else {
                    client.setScreen(new ClickGuiScreen());
                }
            }
        });

        System.out.println("[Sukuna] Loaded " + ModuleRegistry.all().size() + " module entries.");
    }
}
