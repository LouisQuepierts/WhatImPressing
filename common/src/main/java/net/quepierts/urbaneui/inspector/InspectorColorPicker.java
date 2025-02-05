package net.quepierts.urbaneui.inspector;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.quepierts.urbaneui.ColorHelper;
import net.quepierts.urbaneui.widget.ColorField;
import net.quepierts.urbaneui.widget.ColorSpectrum;
import net.quepierts.urbaneui.widget.OpacityBar;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class InspectorColorPicker extends InspectorModifyWidget<Integer> {
    private static final int HEIGHT = 90;

    private final ColorField colorField;
    private final ColorSpectrum colorSpectrum;
    private final OpacityBar opacityBar;

    private AbstractWidget focused;
    private boolean drop = false;

    private final AbstractWidget[] children;

    protected InspectorColorPicker(Component message, Supplier<Integer> getter, Consumer<Integer> setter) {
        super(20, message, getter, setter);

        int argb = getter.get();

        int scale = HEIGHT - 30;
        this.colorField = new ColorField(4, 24, scale);
        this.colorSpectrum = new ColorSpectrum(scale + 8, 24, 10, scale);
        this.opacityBar = new OpacityBar(scale + 22, 24, 10, scale);

        this.opacityBar.setOpacity(FastColor.ARGB32.alpha(argb));

        this.colorField.setColor(255 << 24 | argb);
        this.colorField.setCallback(this.opacityBar::setColor);

        this.colorSpectrum.setHue(this.colorField.getHue());
        this.colorSpectrum.setCallback(this.colorField::setHue);

        this.setARGB(argb);

        this.children = new AbstractWidget[] {
                this.colorField,
                this.colorSpectrum,
                this.opacityBar
        };
    }

    @Override
    public void render(GuiGraphics graphics, int width, int mouseX, int mouseY, float partialTick, boolean hovered) {
        graphics.drawString(Minecraft.getInstance().font, this.message, 0, 6, 0xffffffff);

        int previewLeft = width - 20;
        graphics.renderOutline(previewLeft, 2, 16, 16, mouseY < 20 && hovered ? 0xffffffff : 0x88000000);
        graphics.fill(previewLeft + 1, 3, width - 5, 17, ColorHelper.color(this.opacityBar.getOpacity(), this.colorField.getColor()));

        if (this.drop) {
            for (AbstractWidget widget : this.children) {
                widget.render(graphics, mouseX, mouseY, partialTick);
            }

            graphics.hLine(2, width - 2, HEIGHT - 2, 0xffffffff);
        }
    }

    @Override
    public void onMousePressed(double mouseX, double mouseY, int button, int width) {
        if (button != 0) {
            return;
        }

        if (!this.drop) {
            this.drop = true;
            return;
        }

        if (mouseY < 20) {
            this.drop = false;
            return;
        }

        for (AbstractWidget widget : this.children) {
            if (widget.mouseClicked(mouseX, mouseY, button)) {
                if (this.focused != null && this.focused != widget) {
                    this.focused.setFocused(false);
                }

                widget.setFocused(true);
                this.focused = widget;
                this.setter.accept(ColorHelper.color(this.opacityBar.getOpacity(), this.colorField.getColor()));
                break;
            }
        }
    }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, int button, int width) {
        if (button != 0) {
            return;
        }

        if (!this.drop) {
            return;
        }

        if (this.focused != null) {
            this.focused.setFocused(false);
            this.focused = null;
        }

        for (AbstractWidget widget : this.children) {
            if (widget.mouseReleased(mouseX, mouseY, button)) {
                break;
            }
        }
    }

    @Override
    public void onMouseDragging(double mouseX, double mouseY, int button, double deltaX, double deltaY, int width) {
        if (!this.drop) {
            return;
        }

        if (this.focused != null) {
            this.focused.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            this.setter.accept(ColorHelper.color(this.opacityBar.getOpacity(), this.colorField.getColor()));
        }
    }

    @Override
    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        for (AbstractWidget widget : this.children) {
            if (widget.isFocused()) {
                return widget.keyPressed(keyCode, scanCode, modifiers);
            }
        }

        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (AbstractWidget widget : this.children) {
            if (widget.isFocused()) {
                return widget.charTyped(codePoint, modifiers);
            }
        }

        return false;
    }

    @Override
    public int getHeight() {
        return this.drop ? HEIGHT : 20;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused && this.focused != null) {
            this.focused.setFocused(false);
        }
    }

    @Override
    public boolean paste(InspectorWidget copy) {
        if (copy instanceof InspectorColorPicker picker) {
            this.opacityBar.setOpacity(picker.opacityBar.getOpacity());
            this.opacityBar.setColor(picker.opacityBar.getColor());
            this.colorSpectrum.setHue(picker.colorSpectrum.getHue());
            this.colorField.setColor(picker.colorField.getColor());this.setter.accept(ColorHelper.color(this.opacityBar.getOpacity(), this.colorField.getColor()));
            return true;
        }
        return false;
    }

    public void setARGB(int argb) {
        ColorHelper.HSVColor hsv = ColorHelper.toHSV(argb);
        this.colorSpectrum.setHue(hsv.hue());
        this.colorField.setHSV(hsv);
    }
}
