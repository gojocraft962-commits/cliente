package com.sukuna.client;

import net.fabricmc.api.ClientModInitializer;

public final class SukunaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModuleRegistry.registerDefaults();
        System.out.println("[Sukuna] Loaded " + ModuleRegistry.all().size() + " module entries.");
    }
}
