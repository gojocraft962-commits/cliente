package com.sukuna.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.sukuna.client.ClientModule.Category.*;

public final class ModuleRegistry {
    private static final List<ClientModule> MODULES = new ArrayList<>();
    private static boolean initialized;

    private ModuleRegistry() {}

    public static void registerDefaults() {
        if (initialized) return;
        initialized = true;

        // Combat
        add("AimBot", COMBAT); add("AnchorAura", COMBAT); add("Aura", COMBAT);
        add("AuraV2", COMBAT); add("AutoArmor", COMBAT); add("AutoClicker", COMBAT);
        add("AutoCrystal", COMBAT); add("AutoCrystalLegacy", COMBAT); add("BowSpam", COMBAT);
        add("ChineseCrits", COMBAT); add("Criticals", COMBAT); add("FacePlaceReducer", COMBAT);
        add("ForceTotem", COMBAT); add("Hitbox", COMBAT); add("InfiniteAura", COMBAT);
        add("InsideAura", COMBAT); add("NHackKA", COMBAT); add("Reach", COMBAT);
        add("Surround", COMBAT); add("TPAura", COMBAT); add("TeleportReach", COMBAT);
        add("TriggerBot", COMBAT); add("XCrystal", COMBAT);

        // Movement
        add("AirJump", MOVEMENT); add("AntiMagma", MOVEMENT); add("AntiVoid", MOVEMENT);
        add("AutoJump", MOVEMENT); add("AutoMove", MOVEMENT); add("AutoSprint", MOVEMENT);
        add("ClickTP", MOVEMENT); add("Clip", MOVEMENT); add("CubeGlide", MOVEMENT);
        add("ElytraFly", MOVEMENT); add("EntityFly", MOVEMENT); add("Fly", MOVEMENT);
        add("HighJump", MOVEMENT); add("Ice", MOVEMENT); add("InventoryMove", MOVEMENT);
        add("Jesus", MOVEMENT); add("Jetpack", MOVEMENT); add("LBMineFly", MOVEMENT);
        add("LiquidMove", MOVEMENT); add("MineplexBP", MOVEMENT); add("NoClip", MOVEMENT);
        add("NoFall", MOVEMENT); add("NoSlowDown", MOVEMENT); add("NoWeb", MOVEMENT);
        add("PacketFly", MOVEMENT);

        // Rendering
        add("BlockHighlight", RENDERING); add("BreakIndicators", RENDERING);
        add("CameraTweaks", RENDERING); add("ChunkBorders", RENDERING); add("CoreHat", RENDERING);
        add("CrystalChams", RENDERING); add("CustomFov", RENDERING); add("ESP", RENDERING);
        add("FOV", RENDERING); add("Fullbright", RENDERING); add("GlintColor", RENDERING);
        add("HitFX", RENDERING); add("JumpCircle", RENDERING); add("NameTags", RENDERING);
        add("NewChunks", RENDERING); add("NoHurtCam", RENDERING); add("NoRender", RENDERING);
        add("PortalESP", RENDERING); add("Search", RENDERING); add("Swing", RENDERING);
        add("TargetHUD", RENDERING); add("Tracer", RENDERING); add("Trails", RENDERING);
        add("Trajectories", RENDERING); add("ViewModel", RENDERING);

        // Player
        add("AntiBot", PLAYER); add("AutoGapple", PLAYER); add("AutoPot", PLAYER);
        add("AutoTool", PLAYER); add("BlockReach", PLAYER); add("ChestStealer", PLAYER);
        add("FastEat", PLAYER); add("FastUse", PLAYER); add("FreeCam", PLAYER);
        add("MidClick", PLAYER); add("NoHunger", PLAYER); add("NoRotate", PLAYER);
        add("NoSwing", PLAYER); add("Offhand", PLAYER); add("PacketMine", PLAYER);
        add("PacketXP", PLAYER); add("Twerk", PLAYER);

        // World
        add("AirPlace", WORLD); add("AutoGrind", WORLD); add("AutoMine", WORLD);
        add("AutoOreBreaker", WORLD); add("BedNuker", WORLD); add("DelayDisabler", WORLD);
        add("FastPlace", WORLD); add("NoEntityTrace", WORLD); add("Nuker", WORLD);
        add("PacketHand", WORLD); add("Placer", WORLD); add("Scaffold", WORLD);

        // Miscellaneous
        add("Announcer", MISCELLANEOUS); add("AntiImmobilie", MISCELLANEOUS);
        add("AutoHighway", MISCELLANEOUS); add("BlockInspector", MISCELLANEOUS);
        add("Chat", MISCELLANEOUS); add("Crasher", MISCELLANEOUS);
        add("Disabler", MISCELLANEOUS); add("NoPacket", MISCELLANEOUS);
        add("PopCounter", MISCELLANEOUS); add("Spammer", MISCELLANEOUS);
        add("TickShift", MISCELLANEOUS); add("Timer", MISCELLANEOUS);

        // HUD
        add("Arraylist", HUD); add("BPS", HUD); add("CPS", HUD); add("ClickGUI", HUD);
        add("Colors", HUD); add("FPS", HUD); add("Fonts", HUD); add("HUD", HUD);
        add("HUDCompass", HUD); add("InvView", HUD); add("Keystrokes", HUD);
        add("MultiTask", HUD); add("Notifications", HUD);
    }

    private static void add(String name, ClientModule.Category category) {
        ClientModule module = new ClientModule(name, category, "Módulo em desenvolvimento.");
        module.addSetting(ModuleSetting.toggle("Enabled", false));
        module.addSetting(ModuleSetting.slider("Range", 4.0, 1.0, 6.0, 0.5));
        module.addSetting(ModuleSetting.slider("Delay", 100.0, 0.0, 500.0, 25.0));
        module.addSetting(ModuleSetting.mode("Mode", "Normal", "Legit", "Strict"));
        module.addSetting(ModuleSetting.toggle("Show notifications", true));
        MODULES.add(module);
    }

    public static List<ClientModule> all() {
        return Collections.unmodifiableList(MODULES);
    }

    public static List<ClientModule> byCategory(ClientModule.Category category) {
        return MODULES.stream().filter(module -> module.category() == category).toList();
    }

    public static ClientModule find(String name) {
        return MODULES.stream()
                .filter(module -> module.name().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }
}
