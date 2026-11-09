package com.sukuna.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class ClickGuiScreen extends Screen {
    private static final ClientModule.Category[] CATEGORIES = ClientModule.Category.values();
    private ClientModule.Category selected = ClientModule.Category.COMBAT;
    private int scroll;

    public ClickGuiScreen() {
        super(Component.literal("Sukuna Client"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0x99060008);

        int left = this.width / 2 - 220;
        int top = this.height / 2 - 160;
        int right = left + 440;
        int bottom = top + 320;

        graphics.fill(left, top, right, bottom, 0xFF17121D);
        graphics.fill(left, top, right, top + 3, 0xFFB20D2D);
        graphics.drawString(this.font, Component.literal("SUKUNA CLIENT"), left + 16, top + 13, 0xFFFF365B);
        graphics.drawString(this.font, Component.literal("Minecraft 1.21.11  •  Right Shift para fechar"), left + 16, top + 29, 0xFFB8AEBB);

        int tabY = top + 52;
        int tabX = left + 12;
        for (ClientModule.Category category : CATEGORIES) {
            String label = title(category);
            int tabWidth = Math.max(54, this.font.width(label) + 14);
            int color = category == selected ? 0xFF8F102B : 0xFF302633;
            graphics.fill(tabX, tabY, tabX + tabWidth, tabY + 20, color);
            graphics.drawCenteredString(this.font, label, tabX + tabWidth / 2, tabY + 6, 0xFFFFFFFF);
            tabX += tabWidth + 5;
        }

        graphics.drawString(this.font, Component.literal("MÓDULO"), left + 18, top + 83, 0xFFFF7890);
        graphics.drawString(this.font, Component.literal("ESTADO"), right - 78, top + 83, 0xFFFF7890);
        graphics.fill(left + 12, top + 98, right - 12, top + 99, 0xFF493440);

        List<ClientModule> modules = ModuleRegistry.byCategory(selected);
        int rowY = top + 106;
        int visible = Math.min(9, Math.max(0, modules.size() - scroll));
        for (int i = 0; i < visible; i++) {
            ClientModule module = modules.get(i + scroll);
            int y = rowY + i * 21;
            if (mouseX >= left + 12 && mouseX <= right - 12 && mouseY >= y - 3 && mouseY < y + 17) {
                graphics.fill(left + 12, y - 3, right - 12, y + 17, 0xFF332431);
            }
            graphics.drawString(this.font, Component.literal(module.name()), left + 20, y + 2, 0xFFEDE7EF);
            String state = module.enabled() ? "ON" : "OFF";
            int stateColor = module.enabled() ? 0xFF62E6A0 : 0xFFB5AAB5;
            graphics.drawString(this.font, Component.literal(state), right - 58, y + 2, stateColor);
        }

        graphics.drawString(this.font, Component.literal("Clique num módulo para alternar. ESC ou Right Shift fecha o menu."),
                left + 16, bottom - 22, 0xFFB8AEBB);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        int left = this.width / 2 - 220;
        int top = this.height / 2 - 160;
        int tabY = top + 52;
        int tabX = left + 12;

        for (ClientModule.Category category : CATEGORIES) {
            int tabWidth = Math.max(54, this.font.width(title(category)) + 14);
            if (mouseX >= tabX && mouseX <= tabX + tabWidth && mouseY >= tabY && mouseY <= tabY + 20) {
                selected = category;
                scroll = 0;
                return true;
            }
            tabX += tabWidth + 5;
        }

        List<ClientModule> modules = ModuleRegistry.byCategory(selected);
        int rowY = top + 106;
        for (int i = 0; i < 9 && i + scroll < modules.size(); i++) {
            int y = rowY + i * 21;
            if (mouseX >= left + 12 && mouseX <= left + 428 && mouseY >= y - 3 && mouseY < y + 17) {
                modules.get(i + scroll).toggle();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int maxScroll = Math.max(0, ModuleRegistry.byCategory(selected).size() - 9);
        scroll = Math.max(0, Math.min(maxScroll, scroll - (int) Math.signum(verticalAmount)));
        return true;
    }

    private static String title(ClientModule.Category category) {
        String value = category.name().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}
