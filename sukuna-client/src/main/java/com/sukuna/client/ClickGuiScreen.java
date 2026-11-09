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
    private ClientModule selectedModule;
    private int scroll, settingScroll;
    private boolean settingsOpen, moduleSettingsOpen;

    public ClickGuiScreen() {
        super(Component.literal("Sukuna Client"));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        float scale = ClientConfig.uiScale / 100.0f;
        int mx = (int) ((mouseX - width / 2.0f) / scale + width / 2.0f);
        int my = (int) ((mouseY - height / 2.0f) / scale + height / 2.0f);
        int accent = ACCENTS[ClientConfig.accentIndex];

        g.pose().pushMatrix();
        g.pose().translate(width / 2.0f, height / 2.0f);
        g.pose().scale(scale, scale);
        g.pose().translate(-width / 2.0f, -height / 2.0f);

        int l = width / 2 - 220, t = height / 2 - 160, r = l + 440, b = t + 320;
        g.fill(0, 0, width, height, (ClientConfig.backdropOpacity << 24) | 0x00060008);
        g.fill(l, t, r, b, (ClientConfig.panelOpacity << 24) | 0x0017121D);
        g.fill(l, t, r, t + 3, accent);
        g.drawString(font, "SUKUNA CLIENT", l + 16, t + 13, accent);
        g.drawString(font, "Minecraft 1.21.11  •  Right Shift para fechar", l + 16, t + 29, 0xFFB8AEBB);
        g.fill(r - 30, t + 10, r - 12, t + 28, settingsOpen ? accent : 0xFF302633);
        g.drawCenteredString(font, "⚙", r - 21, t + 15, 0xFFFFFFFF);

        if (settingsOpen) {
            g.drawString(font, "DEFINIÇÕES DO MENU", l + 18, t + 60, accent);
            row(g, l, t + 91, "Cor de destaque", colorName(), mx, my);
            row(g, l, t + 125, "Opacidade do painel", ClientConfig.panelOpacity + "%", mx, my);
            row(g, l, t + 159, "Escurecimento do fundo", ClientConfig.backdropOpacity + "%", mx, my);
            row(g, l, t + 193, "Escala da interface", ClientConfig.uiScale + "%", mx, my);
            g.drawString(font, "Clica numa opção para alterar. Clica na engrenagem para voltar.", l + 16, b - 22, 0xFFB8AEBB);
            g.pose().popMatrix();
            super.render(g, mouseX, mouseY, partialTick);
            return;
        }

        if (moduleSettingsOpen && selectedModule != null) {
            g.drawString(font, selectedModule.name() + " — DEFINIÇÕES", l + 18, t + 60, accent);
            List<ModuleSetting> opts = selectedModule.settings();
            int start = t + 88;
            for (int i = settingScroll; i < Math.min(opts.size(), settingScroll + 8); i++) {
                ModuleSetting s = opts.get(i);
                row(g, l, start + (i - settingScroll) * 25, s.name(), s.displayValue(), mx, my);
            }
            g.drawString(font, "Clica numa opção para alterar. ESC volta.", l + 16, b - 22, 0xFFB8AEBB);
            g.pose().popMatrix();
            super.render(g, mouseX, mouseY, partialTick);
            return;
        }

        int tx = l + 12, ty = t + 52;
        for (ClientModule.Category c : CATEGORIES) {
            String label = title(c);
            int w = Math.max(54, font.width(label) + 14);
            g.fill(tx, ty, tx + w, ty + 20, c == selected ? accent : 0xFF302633);
            g.drawCenteredString(font, label, tx + w / 2, ty + 6, 0xFFFFFFFF);
            tx += w + 5;
        }
        g.drawString(font, "MÓDULO", l + 18, t + 83, 0xFFFF7890);
        g.drawString(font, "ESTADO", r - 78, t + 83, 0xFFFF7890);
        g.fill(l + 12, t + 98, r - 12, t + 99, 0xFF493440);
        List<ClientModule> modules = ModuleRegistry.byCategory(selected);
        int rowY = t + 106, visible = Math.min(9, Math.max(0, modules.size() - scroll));
        for (int i = 0; i < visible; i++) {
            ClientModule m = modules.get(i + scroll);
            int y = rowY + i * 21;
            if (mx >= l + 12 && mx <= r - 12 && my >= y - 3 && my < y + 17)
                g.fill(l + 12, y - 3, r - 12, y + 17, 0xFF332431);
            g.drawString(font, m.name(), l + 20, y + 2, 0xFFEDE7EF);
            g.drawString(font, m.enabled() ? "ON" : "OFF", r - 58, y + 2,
                    m.enabled() ? 0xFF62E6A0 : 0xFFB5AAB5);
        }
        g.drawString(font, "Clique: ativar/desativar • Botão direito: definições • ESC fecha", l + 16, b - 22, 0xFFB8AEBB);
        g.pose().popMatrix();
        super.render(g, mouseX, mouseY, partialTick);
    }

    private void row(GuiGraphics g, int l, int y, String label, String value, int mx, int my) {
        boolean hover = mx >= l + 12 && mx <= l + 428 && my >= y - 5 && my < y + 24;
        g.fill(l + 12, y - 5, l + 428, y + 24, hover ? 0xFF392A3A : 0xFF29212E);
        g.drawString(font, label, l + 22, y + 4, 0xFFEDE7EF);
        g.drawString(font, value, l + 330, y + 4, ACCENTS[ClientConfig.accentIndex]);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent e, boolean doubleClick) {
        float scale = ClientConfig.uiScale / 100.0f;
        double mx = (e.x() - width / 2.0) / scale + width / 2.0;
        double my = (e.y() - height / 2.0) / scale + height / 2.0;
        int l = width / 2 - 220, t = height / 2 - 160, r = l + 440;

        if (e.button() == 0 && mx >= r - 30 && mx <= r - 12 && my >= t + 10 && my <= t + 28) {
            settingsOpen = !settingsOpen;
            moduleSettingsOpen = false;
            return true;
        }
        if (e.button() == 0 && settingsOpen && mx >= l + 12 && mx <= l + 428) {
            if (my >= t + 86 && my < t + 120) ClientConfig.accentIndex = (ClientConfig.accentIndex + 1) % ACCENTS.length;
            else if (my >= t + 120 && my < t + 154) ClientConfig.panelOpacity = ClientConfig.panelOpacity >= 224 ? 64 : ClientConfig.panelOpacity + 16;
            else if (my >= t + 154 && my < t + 188) ClientConfig.backdropOpacity = ClientConfig.backdropOpacity >= 224 ? 64 : ClientConfig.backdropOpacity + 16;
            else if (my >= t + 188 && my < t + 222) ClientConfig.uiScale = ClientConfig.uiScale >= 150 ? 75 : ClientConfig.uiScale + 5;
            ClientConfig.save();
            return true;
        }
        if (moduleSettingsOpen && selectedModule != null) {
            if (e.button() == 0 && mx >= l + 12 && mx <= l + 428) {
                int index = settingScroll + (int) ((my - (t + 83)) / 25);
                if (index >= settingScroll && index < Math.min(selectedModule.settings().size(), settingScroll + 8)) {
                    selectedModule.settings().get(index).cycle();
                    ClientConfig.save();
                }
                return true;
            }
            return super.mouseClicked(e, doubleClick);
        }
        if (settingsOpen) return super.mouseClicked(e, doubleClick);

        int tx = l + 12, ty = t + 52;
        if (e.button() == 0) {
            for (ClientModule.Category c : CATEGORIES) {
                int w = Math.max(54, font.width(title(c)) + 14);
                if (mx >= tx && mx <= tx + w && my >= ty && my <= ty + 20) {
                    selected = c;
                    scroll = 0;
                    return true;
                }
                tx += w + 5;
            }
        }

        List<ClientModule> modules = ModuleRegistry.byCategory(selected);
        int rowY = t + 106;
        if (e.button() == 0 || e.button() == 1) {
            for (int i = 0; i < 9 && i + scroll < modules.size(); i++) {
                int y = rowY + i * 21;
                if (mx >= l + 12 && mx <= r - 12 && my >= y - 3 && my < y + 17) {
                    selectedModule = modules.get(i + scroll);
                    if (e.button() == 1) {
                        moduleSettingsOpen = true;
                        settingScroll = 0;
                    } else {
                        selectedModule.toggle();
                        ClientConfig.save();
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(e, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double horizontal, double vertical) {
        if (settingsOpen) return true;
        if (moduleSettingsOpen && selectedModule != null) {
            settingScroll = Math.max(0, Math.min(Math.max(0, selectedModule.settings().size() - 8),
                    settingScroll - (int) Math.signum(vertical)));
            return true;
        }
        int max = Math.max(0, ModuleRegistry.byCategory(selected).size() - 9);
        scroll = Math.max(0, Math.min(max, scroll - (int) Math.signum(vertical)));
        return true;
    }

    @Override
    public void onClose() {
        if (moduleSettingsOpen) {
            moduleSettingsOpen = false;
            return;
        }
        if (settingsOpen) {
            settingsOpen = false;
            return;
        }
        super.onClose();
    }

    private String colorName() {
        return switch (ClientConfig.accentIndex) {
            case 1 -> "Roxo";
            case 2 -> "Azul";
            case 3 -> "Verde";
            case 4 -> "Laranja";
            default -> "Vermelho";
        };
    }

    private static String title(ClientModule.Category c) {
        String v = c.name().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(v.charAt(0)) + v.substring(1);
    }
}