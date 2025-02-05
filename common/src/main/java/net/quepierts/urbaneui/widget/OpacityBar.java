package net.quepierts.urbaneui.widget;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.quepierts.urbaneui.ColorHelper;
import net.quepierts.urbaneui.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Setter
@Getter
public class OpacityBar extends AbstractWidget {
    private int opacity;
    private int color;

    private Consumer<Integer> callback;

    public OpacityBar(int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight, Component.literal("Opacity Bar"));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int innerWidth = this.getWidth() - 4;
        int fragHeight = this.getHeight() - 4;

        int iTop = this.getY() + 2;
        int iLeft = this.getX() + 2;
        int right = iLeft + innerWidth;
        int bottom = iTop + fragHeight;

        int transparent = ColorHelper.color(0, this.color);

        graphics.fillGradient(iLeft, iTop, right, bottom, transparent, this.color);
        graphics.renderOutline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xffffffff);

        int selectionY = this.getY() + (int) (this.opacity / 255f * this.getHeight()) - 2;
        int interpolation = ColorHelper.color(this.opacity, this.color);

        graphics.fill(this.getX() - 1, selectionY, this.getX() + this.getWidth() + 1, selectionY + 4, 0xffffffff);
        graphics.fill(this.getX(), selectionY + 1, this.getX() + this.getWidth(), selectionY + 3, interpolation);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        double ratio = MathHelper.clamp((mouseY - this.getY()) / this.getHeight(), 0.0, 1.0);
        this.opacity = (int) (ratio * 255);

        if (this.callback != null) {
            this.callback.accept(this.opacity);
        }
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.onClick(mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {

    }
}
