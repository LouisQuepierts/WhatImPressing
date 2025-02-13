package net.quepierts.urbaneui.inspector;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class InspectorIntegerSlider extends InspectorModifyWidget<Integer> {
    private final int min;
    private final int max;
    private final int step;

    private int value;

    public InspectorIntegerSlider(Component message, Supplier<Integer> getter, Consumer<Integer> setter, int min, int max, int step) {
        super(36, message, getter, setter);

        this.min = min;
        this.max = max;

        this.value = getter.get();
        this.step = step;
    }

    @Override
    public void render(GuiGraphics graphics, int width, int mouseX, int mouseY, float partialTick, boolean hovered) {
        Font font = Minecraft.getInstance().font;
        graphics.drawString(font, this.message, 0, 4, 0xffffffff);

        String text = Integer.toString(this.value);
        int textWidth = font.width(text);
        graphics.drawString(font, text, width - textWidth, 4, 0xffffffff);
        RenderSystem.enableBlend();

        int length = width - 18;
        float interpolate = (float) (this.value - this.min) / (this.max - this.min);
        int offset = 4 + (int) (length * interpolate);

        boolean hover = hovered && mouseY > 14;
        graphics.fill(0, 14, width, 34, 0xbb000000);
        graphics.fill(4, 23, width - 4, 25, 0x88ffffff);
        graphics.fill(offset, 17, offset + 2, 31, 0xffffffff);

        if (hover) {
            graphics.renderOutline(0, 14, width, 20, 0xffffffff);
        }
    }

    @Override
    public void onMousePressed(double mouseX, double mouseY, int button, int width) {
        if (button != 0 || mouseY <= 14) {
            return;
        }

        if (mouseX < 8 || mouseX > width - 8) {
            return;
        }

        float interpolate = Math.clamp(((float) mouseX - 8) / (width - 18), 0.0f, 1.0f);
        float target = (this.max - this.min) * interpolate + this.min;
        int value = Math.round(target / this.step) * this.step;

        if (value != this.value) {
            this.value = value;
            this.setter.accept(value);
        }
    }

    @Override
    public void onMouseDragging(double mouseX, double mouseY, int button, double deltaX, double deltaY, int width) {
        if (button != 0) {
            return;
        }

        float interpolate = Math.clamp(((float) mouseX - 8) / (width - 18), 0.0f, 1.0f);
        float target = (this.max - this.min) * interpolate + this.min;
        int value = Math.round(target / this.step) * this.step;

        if (value != this.value) {
            this.value = value;
            this.setter.accept(value);
        }
    }

    @Override
    public boolean paste(InspectorWidget copy) {
        if (copy instanceof InspectorIntegerSlider slider) {
            this.value = slider.value;
            this.setter.accept(this.value);
            return true;
        }
        return false;
    }
}
