package net.quepierts.wip.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.quepierts.urbaneui.widget.Inspector;
import net.quepierts.wip.gui.widget.EditorWindow;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class KeystrokesEditorScreen extends Screen {
    private static final Component HINT_ADD = Component.translatable("hint.wip.add");
    private static final Component HINT_DEL = Component.translatable("hint.wip.del");

    private final EditorWindow window;
    private final Inspector inspector;

    public KeystrokesEditorScreen() {
        super(Component.translatable("menu.wip.editor"));
        KeystrokesDisplayLayer.INSTANCE.setHide(true);

        this.window = EditorWindow.getInstance();

        int left = this.window.getWidth() + this.window.getX() + 8;
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth() - left;
        this.inspector = new Inspector(left, 0, width, Minecraft.getInstance().getWindow().getGuiScaledHeight());

        this.addRenderableWidget(this.window);
        this.addRenderableWidget(this.inspector);

        this.inspector.setInspectObject(this.window);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Window window = this.minecraft.getWindow();
        int screenWidth = window.getGuiScaledWidth();
        int screenHeight = window.getGuiScaledHeight();

        this.renderBlurredBackground();
        graphics.fill(0, 0, screenWidth, screenHeight, 0x80000000);

        RenderSystem.enableBlend();
        drawHint(graphics, 2, screenHeight, "A", HINT_ADD);
        drawHint(graphics, 14, screenHeight, "X", HINT_DEL);
        this.window.render(graphics, mouseX, mouseY, partialTick);
        this.inspector.render(graphics, mouseX, mouseY, partialTick);
    }

    private void drawHint(GuiGraphics graphics, int y, int bottom, String key, Component hint) {
        int srcY = bottom - y;
        graphics.fill(2, srcY - 10, 23, srcY, 0xff000000);
        graphics.fill(3, srcY - 9, 22, srcY - 1, 0xff606060);
        graphics.drawCenteredString(this.font, key, 13, srcY - 9, 0xffffffff);
        graphics.drawString(this.font, hint, 26, srcY - 9, 0xffffffff);
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        this.window.resize();

        int left = this.window.getWidth() + this.window.getX() + 8;
        int inspectorWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth() - left;

        this.inspector.setRectangle(inspectorWidth, Minecraft.getInstance().getWindow().getGuiScaledHeight(), left, 0);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.getFocused() != null && this.getFocused().mouseClicked(mouseX, mouseY, button)) {
            if (button == 0) {
                this.setDragging(true);
            }
            return true;
        }

        Iterator<? extends GuiEventListener> iterator = this.children().iterator();

        GuiEventListener guieventlistener;
        do {
            if (!iterator.hasNext()) {
                return false;
            }

            guieventlistener = iterator.next();
        } while(!guieventlistener.mouseClicked(mouseX, mouseY, button));

        this.setFocused(guieventlistener);
        if (button == 0) {
            this.setDragging(true);
        }

        return true;
    }

    @Override
    public void onClose() {
        super.onClose();
        KeystrokesDisplayLayer.INSTANCE.setHide(false);

        this.window.save();
    }
}
