package net.quepierts.urbaneui.widget;

import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.quepierts.urbaneui.ColorHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.function.Consumer;

@Setter
@Getter
public class ColorSpectrum extends AbstractWidget {
    private static final int[] SPECTRUM = {
            0xffff0000,
            0xffffff00,
            0xff00ff00,
            0xff00ffff,
            0xff0000ff,
            0xffff00ff,
            0xffff0000
    };

    private float hue = 0.0f;
    private Consumer<Float> callback;

    public ColorSpectrum(int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight, Component.literal("Color Spectrum"));
    }

    public int getColor() {
        return ColorHelper.getHueColor(this.hue);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        VertexConsumer buffer = graphics.bufferSource().getBuffer(RenderType.gui());
        Matrix4f matrix4f = graphics.pose().last().pose();

        float innerWidth = this.getWidth() - 4;
        float fragHeight = (this.getHeight() - 4) / 6f;

        float iTop = this.getY() + 2;
        float iLeft = this.getX() + 2;
        float right = iLeft + innerWidth;
        float bottom = iTop + fragHeight;

        for (int i = 0; i < 6; i++) {
            float offset = fragHeight * i;
            buffer.addVertex(matrix4f, iLeft, iTop + offset, 0.0f).setColor(SPECTRUM[i]);
            buffer.addVertex(matrix4f, iLeft, bottom + offset, 0.0f).setColor(SPECTRUM[i + 1]);
            buffer.addVertex(matrix4f, right, bottom + offset, 0.0f).setColor(SPECTRUM[i + 1]);
            buffer.addVertex(matrix4f, right, iTop + offset, 0.0f).setColor(SPECTRUM[i]);
        }

        graphics.renderOutline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xffffffff);

        int selectionY = this.getY() + (int) (this.hue / 360f * this.getHeight()) - 2;
        int interpolation = this.getColor();

        graphics.fill(this.getX() - 1, selectionY, this.getX() + this.getWidth() + 1, selectionY + 4, 0xffffffff);
        graphics.fill(this.getX(), selectionY + 1, this.getX() + this.getWidth(), selectionY + 3, interpolation);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        double ratio = Math.clamp((mouseY - this.getY()) / this.getHeight(), 0.0, 1.0);
        this.hue = (float) (ratio * 360.0f);

        if (this.callback != null) {
            this.callback.accept(this.hue);
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
