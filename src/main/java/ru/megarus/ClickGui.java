package ru.megarus;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ClickGui extends Screen {
    private static final int PANEL_WIDTH = 310;
    private static final int HEADER_HEIGHT = 52;
    private static final int ROW_HEIGHT = 34;
    private static final Identifier WAIFU_TEXTURE =
            Identifier.of("megarushack", "textures/gui/waifu.png");

    private float openProgress = 0.0f;

    public ClickGui() {
        super(Text.literal("MegaRusHack — настройки"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Плавное появление панели (0..1). Сама игра не ставится на паузу.
        openProgress = Math.min(1.0f, openProgress + Math.max(0.0f, delta) * 4.5f);
        renderBackground(context, mouseX, mouseY, delta);

        int panelHeight = HEADER_HEIGHT + 8 + MegaRusHack.modules().size() * ROW_HEIGHT + 28;
        int left = (width - PANEL_WIDTH) / 2;
        int targetTop = (height - panelHeight) / 2;
        int top = targetTop - Math.round((1.0f - easeOutCubic(openProgress)) * 28.0f);
        int alpha = Math.round(238.0f * openProgress);
        int panelColor = (alpha << 24) | 0x00101219;
        int headerColor = (alpha << 24) | 0x0020192B;

        // Неоновая рамка и тёмная подложка.
        context.fill(left - 2, top - 2, left + PANEL_WIDTH + 2, top + panelHeight + 2,
                withAlpha(0xFFB20CFF, openProgress));
        context.fill(left, top, left + PANEL_WIDTH, top + panelHeight, panelColor);
        context.fill(left, top, left + PANEL_WIDTH, top + HEADER_HEIGHT, headerColor);
        context.fill(left, top, left + PANEL_WIDTH, top + 3, withAlpha(0xFFFF244F, openProgress));

        context.drawText(textRenderer, "MEGARUSHACK", left + 14, top + 10, 0xFFFFFFFF, false);
        context.drawText(textRenderer, "CLICK GUI  //  МОДУЛИ", left + 14, top + 28, 0xFFBDA7D8, false);
        context.fill(left + PANEL_WIDTH - 76, top + 16, left + PANEL_WIDTH - 14, top + 18,
                withAlpha(0xFFB20CFF, openProgress));

        int y = top + HEADER_HEIGHT + 8;
        for (MegaRusHack.Module module : MegaRusHack.modules()) {
            boolean hovered = mouseX >= left && mouseX < left + PANEL_WIDTH
                    && mouseY >= y && mouseY < y + ROW_HEIGHT - 3;
            int rowColor = hovered ? 0xFF292136 : 0xFF191923;
            context.fill(left + 8, y, left + PANEL_WIDTH - 8, y + ROW_HEIGHT - 3, rowColor);
            context.fill(left + 8, y, left + 11, y + ROW_HEIGHT - 3,
                    module.enabled() ? 0xFFB7FF00 : 0xFF4B4657);
            context.drawText(textRenderer, module.name(), left + 18, y + 4, 0xFFF4F0FA, false);
            context.drawText(textRenderer, module.description(), left + 18, y + 17, 0xFFAAA4B8, false);

            String state = module.enabled() ? "ON" : "OFF";
            int stateColor = module.enabled() ? 0xFFB7FF00 : 0xFFFF496C;
            int stateWidth = textRenderer.getWidth(state);
            context.drawText(textRenderer, state, left + PANEL_WIDTH - 18 - stateWidth,
                    y + 10, stateColor, false);
            y += ROW_HEIGHT;
        }

        context.drawText(textRenderer, "ЛКМ — переключить    ESC — закрыть",
                left + 10, top + panelHeight - 16, 0xFFB8B1C7, false);

        // Персонаж рисуется поверх меню в правом нижнем углу.
        // Положите PNG по пути: assets/megarushack/textures/gui/waifu.png
        drawWaifu(context);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawWaifu(DrawContext context) {
        int textureWidth = 128;
        int textureHeight = 192;
        int drawWidth = 96;
        int drawHeight = 144;
        int x = width - drawWidth - 12;
        int y = height - drawHeight - 10;
        context.drawTexture(RenderLayer::getGuiTextured, WAIFU_TEXTURE,
                x, y, 0.0f, 0.0f, drawWidth, drawHeight, textureWidth, textureHeight);
    }

    private static int withAlpha(int argb, float progress) {
        int alpha = Math.round(((argb >>> 24) & 0xFF) * progress);
        return (alpha << 24) | (argb & 0x00FFFFFF);
    }

    private static float easeOutCubic(float t) {
        float clamped = Math.max(0.0f, Math.min(1.0f, t));
        return 1.0f - (float) Math.pow(1.0f - clamped, 3.0);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int panelHeight = HEADER_HEIGHT + 8 + MegaRusHack.modules().size() * ROW_HEIGHT + 28;
            int left = (width - PANEL_WIDTH) / 2;
            int top = (height - panelHeight) / 2;
            int y = top + HEADER_HEIGHT + 8;

            for (MegaRusHack.Module module : MegaRusHack.modules()) {
                if (mouseX >= left + 8 && mouseX < left + PANEL_WIDTH - 8
                        && mouseY >= y && mouseY < y + ROW_HEIGHT - 3) {
                    module.toggle();
                    return true;
                }
                y += ROW_HEIGHT;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
