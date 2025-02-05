package net.quepierts.urbaneui.inspector;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.quepierts.urbaneui.widget.TextField;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class InspectorEditBox extends InspectorModifyWidget<String> {
    private final TextField editBox;

    public InspectorEditBox(Component message, Supplier<String> getter, Consumer<String> setter) {
        super(36, message, getter, setter);

        this.editBox = new TextField(Minecraft.getInstance().font, 0, 16, 100, 20, message);
        this.editBox.setValue(getter.get());
        this.editBox.setResponder(setter);
    }

    @Override
    public void render(GuiGraphics graphics, int width, int mouseX, int mouseY, float partialTick, boolean hovered) {
        Font font = Minecraft.getInstance().font;
        graphics.drawString(font, this.message, 0, 4, 0xffffffff);

        if (this.editBox.getWidth() != width) {
            this.editBox.setWidth(width);
        }

        RenderSystem.enableBlend();
        graphics.fill(0, 16, this.editBox.getWidth(), 16 + this.editBox.getHeight(), 0x88000000);
        if (this.editBox.isMouseOver(mouseX, mouseY)) {
            graphics.renderOutline(0, 16, this.editBox.getWidth(), this.editBox.getHeight(), 0xffffffff);
        } else if (this.isFocused()) {
            graphics.renderOutline(0, 16, this.editBox.getWidth(), this.editBox.getHeight(), 0xffbbbbff);
        }
        this.editBox.render(graphics, mouseX, mouseY, partialTick);
        RenderSystem.disableBlend();
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
