package com.sukuna.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class ClickGuiScreen extends Screen {
    private static final ClientModule.Category[] CATEGORIES = ClientModule.Category.values();
    private static final int[] ACCENTS = {0xFFB20D2D, 0xFF7A35D1, 0xFF167AC6, 0xFF168A62, 0xFFE07820};
    private ClientModule.Category selected = ClientModule.Category.COMBAT;
    private int scroll;
    private boolean settingsOpen;
    private int accentIndex;
    private int panelOpacity = 96;
    private int backdropOpacity = 153;
    private int uiScale = 100;

    public ClickGuiScreen() { super(Component.literal("Sukuna Client")); }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        float scale = uiScale / 100.0f;
        int mouseXScaled = (int) ((mouseX - width / 2.0f) / scale + width / 2.0f);
        int mouseYScaled = (int) ((mouseY - height / 2.0f) / scale + height / 2.0f);
        graphics.pose().pushPose();
        graphics.pose().translate(width / 2.0f, height / 2.0f, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.pose().translate(-width / 2.0f, -height / 2.0f, 0);
        int left = width / 2 - 220, top = height / 2 - 160, right = left + 440, bottom = top + 320;
        graphics.fill(0, 0, width, height, (backdropOpacity << 24) | 0x00060008);
        graphics.fill(left, top, right, bottom, (panelOpacity << 24) | 0x0017121D);
        graphics.fill(left, top, right, top + 3, ACCENTS[accentIndex]);
        graphics.drawString(font, Component.literal("SUKUNA CLIENT"), left + 16, top + 13, ACCENTS[accentIndex]);
        graphics.drawString(font, Component.literal("Minecraft 1.21.11  •  Right Shift para fechar"), left + 16, top + 29, 0xFFB8AEBB);
        graphics.fill(right - 30, top + 10, right - 12, top + 28, settingsOpen ? ACCENTS[accentIndex] : 0xFF302633);
        graphics.drawCenteredString(font, "⚙", right - 21, top + 15, 0xFFFFFFFF);

        if (settingsOpen) {
            graphics.drawString(font, Component.literal("DEFINIÇÕES DO MENU"), left + 18, top + 60, ACCENTS[accentIndex]);
            settingRow(graphics, left, top + 91, "Cor de destaque", colorName(), mouseXScaled, mouseYScaled);
            settingRow(graphics, left, top + 125, "Opacidade do painel", panelOpacity + "%", mouseXScaled, mouseYScaled);
            settingRow(graphics, left, top + 159, "Escurecimento do fundo", backdropOpacity + "%", mouseXScaled, mouseYScaled);
            settingRow(graphics, left, top + 193, "Escala da interface", uiScale + "%", mouseXScaled, mouseYScaled);
            graphics.drawString(font, Component.literal("Clica numa opção para alterar. ESC volta ao menu."), left + 16, bottom - 22, 0xFFB8AEBB);
            super.render(graphics, mouseX, mouseY, partialTick);
            return;
        }

        int tabY = top + 52, tabX = left + 12;
        for (ClientModule.Category category : CATEGORIES) {
            String label = title(category);
            int tabWidth = Math.max(54, font.width(label) + 14);
            graphics.fill(tabX, tabY, tabX + tabWidth, tabY + 20, category == selected ? ACCENTS[accentIndex] : 0xFF302633);
            graphics.drawCenteredString(font, label, tabX + tabWidth / 2, tabY + 6, 0xFFFFFFFF);
            tabX += tabWidth + 5;
        }

        graphics.drawString(font, Component.literal("MÓDULO"), left + 18, top + 83, 0xFFFF7890);
        graphics.drawString(font, Component.literal("ESTADO"), right - 78, top + 83, 0xFFFF7890);
        graphics.fill(left + 12, top + 98, right - 12, top + 99, 0xFF493440);
        List<ClientModule> modules = ModuleRegistry.byCategory(selected);
        int rowY = top + 106, visible = Math.min(9, Math.max(0, modules.size() - scroll));
        for (int i = 0; i < visible; i++) {
            ClientModule module = modules.get(i + scroll);
            int y = rowY + i * 21;
            if (mouseXScaled >= left + 12 && mouseXScaled <= right - 12 && mouseYScaled >= y - 3 && mouseYScaled < y + 17)
                graphics.fill(left + 12, y - 3, right - 12, y + 17, 0xFF332431);
            graphics.drawString(font, Component.literal(module.name()), left + 20, y + 2, 0xFFEDE7EF);
            graphics.drawString(font, Component.literal(module.enabled() ? "ON" : "OFF"), right - 58, y + 2,
                    module.enabled() ? 0xFF62E6A0 : 0xFFB5AAB5);
        }
        graphics.drawString(font, Component.literal("Clique num módulo para alternar. ESC ou Right Shift fecha o menu."),
                left + 16, bottom - 22, 0xFFB8AEBB);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void settingRow(GuiGraphics g, int left, int y, String label, String value, int mx, int my) {
        boolean hover = mx >= left + 12 && mx <= left + 428 && my >= y - 5 && my < y + 24;
        g.fill(left + 12, y - 5, left + 428, y + 24, hover ? 0xFF392A3A : 0xFF29212E);
        g.drawString(font, Component.literal(label), left + 22, y + 4, 0xFFEDE7EF);
        g.drawString(font, Component.literal(value), left + 330, y + 4, ACCENTS[accentIndex]);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mx = event.x(), my = event.y();
        if (event.button() != 0) return super.mouseClicked(event, doubleClick);
        int left = width / 2 - 220, top = height / 2 - 160, right = left + 440;
        if (mx >= right - 30 && mx <= right - 12 && my >= top + 10 && my <= top + 28) {
            settingsOpen = !settingsOpen;
            return true;
        }
        if (settingsOpen) {
            if (mx >= left + 12 && mx <= left + 428) {
                if (my >= top + 86 && my < top + 120) accentIndex = (accentIndex + 1) % ACCENTS.length;
                else if (my >= top + 120 && my < top + 154) panelOpacity = panelOpacity >= 224 ? 64 : panelOpacity + 32;
                else if (my >= top + 154 && my < top + 188) backdropOpacity = backdropOpacity >= 224 ? 64 : backdropOpacity + 32;
                else if (my >= top + 188 && my < top + 222) uiScale = uiScale >= 125 ? 75 : uiScale + 25;
            }
            return true;
        }
        int tabY = top + 52, tabX = left + 12;
        for (ClientModule.Category category : CATEGORIES) {
            int tabWidth = Math.max(54, font.width(title(category)) + 14);
            if (mx >= tabX && mx <= tabX + tabWidth && my >= tabY && my <= tabY + 20) {
                selected = category; scroll = 0; return true;
            }
            tabX += tabWidth + 5;
        }
        List<ClientModule> modules = ModuleRegistry.byCategory(selected);
        int rowY = top + 106;
        for (int i = 0; i < 9 && i + scroll < modules.size(); i++) {
            int y = rowY + i * 21;
            if (mx >= left + 12 && mx <= left + 428 && my >= y - 3 && my < y + 17) {
                modules.get(i + scroll).toggle(); return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (settingsOpen) return true;
        int maxScroll = Math.max(0, ModuleRegistry.byCategory(selected).size() - 9);
        scroll = Math.max(0, Math.min(maxScroll, scroll - (int) Math.signum(verticalAmount)));
        return true;
    }

    private String colorName() {
        return switch (accentIndex) { case 1 -> "Roxo"; case 2 -> "Azul"; case 3 -> "Verde"; case 4 -> "Laranja"; default -> "Vermelho"; };
    }

    private static String title(ClientModule.Category category) {
        String value = category.name().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}
