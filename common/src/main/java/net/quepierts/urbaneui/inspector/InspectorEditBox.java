package net.quepierts.urbaneui.inspector;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class InspectorEditBox extends InspectorModifyWidget<String> {
    private final EditBox editBox;

    public InspectorEditBox(Component message, Supplier<String> getter, Consumer<String> setter) {
        super(36, message, getter, setter);

        this.editBox = new EditBox(Minecraft.getInstance().font, 8, 16, 100, 20, message);
        this.editBox.setValue(getter.get());
        this.editBox.setResponder(setter);
    }

    @Override
    public void render(GuiGraphics graphics, int width, int mouseX, int mouseY, float partialTick, boolean hovered) {
        int boxWidth = width - 16;

        Font font = Minecraft.getInstance().font;
        graphics.drawString(font, this.message, 8, 4, 0xffffffff);

        if (this.editBox.getWidth() != boxWidth) {
            this.editBox.setWidth(boxWidth);
        }

        this.editBox.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        this.editBox.setFocused(focused);
    }

    @Override
    public void onMousePressed(double mouseX, double mouseY, int button, int width) {
        this.editBox.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, int button, int width) {
        this.editBox.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        return this.editBox.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onKeyReleased(int keyCode, int scanCode, int modifiers) {
        return this.editBox.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return this.editBox.charTyped(codePoint, modifiers);
    }
}
