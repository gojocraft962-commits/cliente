package com.sukuna.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;

import java.util.Comparator;

public final class CombatModuleController {
    private static long nextAuraAttackAt;
    private static long nextClickerAt;

    private CombatModuleController() {}

    public static void tick(Minecraft client) {
        if (client.player == null || client.level == null || client.gameMode == null || client.screen != null) return;

        ClientModule aura = enabledAura();
        if (aura != null) tickAura(client, aura);

        ClientModule clicker = ModuleRegistry.find("AutoClicker");
        if (clicker != null && clicker.enabled()) tickClicker(client, clicker);
    }

    private static ClientModule enabledAura() {
        for (String name : new String[]{"KillAura", "Aura", "AuraV2"}) {
            ClientModule module = ModuleRegistry.find(name);
            if (module != null && module.enabled()) return module;
        }
        return null;
    }

    private static void tickAura(Minecraft mc, ClientModule module) {
        boolean targetPlayers = value(module, "Target Players", true);
        boolean targetMobs = value(module, "Target Mobs", true);
        if (!targetPlayers && !targetMobs) return;

        double range = number(module, "Range", 4.0);
        double cps = number(module, "CPS", 8.0);
        String priority = mode(module, "Priority", "Closest");

        Comparator<LivingEntity> ordering = priority.equalsIgnoreCase("Lowest health")
                ? Comparator.comparingDouble(LivingEntity::getHealth)
                    .thenComparingDouble(e -> e.distanceToSqr(mc.player))
                : Comparator.comparingDouble(e -> e.distanceToSqr(mc.player));

        LivingEntity target = mc.level.getEntitiesOfClass(
                LivingEntity.class,
                mc.player.getBoundingBox().inflate(range),
                entity -> entity != mc.player
                        && entity.isAlive()
                        && entity.distanceTo(mc.player) <= range
                        && ((targetPlayers && entity instanceof Player)
                        || (targetMobs && entity instanceof Mob))
        ).stream().min(ordering).orElse(null);

        long now = System.currentTimeMillis();
        if (target != null && now >= nextAuraAttackAt) {
            mc.gameMode.attack(mc.player, target);
            mc.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            nextAuraAttackAt = now + (long) (1000.0 / Math.max(1.0, Math.min(20.0, cps)));
        }
    }

    private static void tickClicker(Minecraft mc, ClientModule module) {
        String button = mode(module, "Button", "Left");
        boolean pressed = button.equals("Right") ? mc.options.keyUse.isDown() : mc.options.keyAttack.isDown();
        long now = System.currentTimeMillis();
        if (!pressed || now < nextClickerAt) return;

        if (button.equals("Right")) {
            if (mc.hitResult instanceof EntityHitResult hit) {
                mc.gameMode.interact(mc.player, hit.getEntity(), net.minecraft.world.InteractionHand.MAIN_HAND);
            }
        } else if (mc.hitResult instanceof EntityHitResult hit) {
            Entity target = hit.getEntity();
            mc.gameMode.attack(mc.player, target);
            mc.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        }

        double cps = number(module, "CPS", 10.0);
        nextClickerAt = now + (long) (1000.0 / Math.max(1.0, Math.min(20.0, cps)));
    }

    private static boolean value(ClientModule module, String name, boolean fallback) {
        return module.settings().stream().filter(s -> s.name().equals(name))
                .findFirst().map(ModuleSetting::enabled).orElse(fallback);
    }

    private static double number(ClientModule module, String name, double fallback) {
        return module.settings().stream().filter(s -> s.name().equals(name))
                .findFirst().map(ModuleSetting::number).orElse(fallback);
    }

    private static String mode(ClientModule module, String name, String fallback) {
        return module.settings().stream().filter(s -> s.name().equals(name))
                .findFirst().map(ModuleSetting::modeValue).orElse(fallback);
    }
}