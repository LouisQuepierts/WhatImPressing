package net.quepierts.urbaneui.inspector;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;

@Getter
public abstract class InspectorWidget {
    private final int height;

    @Setter
    private boolean focused = false;

    protected InspectorWidget(int height) {
        this.height = height;
    }

    public void render(GuiGraphics graphics, int width, int mouseX, int mouseY, float partialTick, boolean hovered) {}

    public void onMousePressed(double mouseX, double mouseY, int button, int width) {}

    public void onMouseReleased(double mouseX, double mouseY, int button, int width) {}

    public void onMouseDragging(double mouseX, double mouseY, int button, double deltaX, double deltaY, int width) {}

    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) { return false; }

    public boolean onKeyReleased(int keyCode, int scanCode, int modifiers) { return false; }

    public boolean charTyped(char codePoint, int modifiers) { return false; }

    public boolean paste(InspectorWidget copy) { return false; }
}
