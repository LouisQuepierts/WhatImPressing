package net.quepierts.urbaneui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.quepierts.urbaneui.ColorHelper;
import net.quepierts.urbaneui.MathHelper;
import net.quepierts.urbaneui.Shaders;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.function.Consumer;

@Setter
@Getter
public class ColorField extends AbstractWidget {
    private int color = 0xffff0000;
    private int hueColor = 0xffff0000;

    private float hue = 0.0f;
    private float saturation = 0.0f;
    private float brightness = 1.0f;

    private Consumer<Integer> callback;

    public ColorField(int x, int y, int scale) {
        super(x, y, scale, scale, Component.literal("Color Field"));
    }

    public void setColor(int color) {
        if (color != this.color) {
            this.color = color;
            ColorHelper.HSVColor hsv = ColorHelper.toHSV(color);
            this.setHSV(hsv);
        }
    }

    public void setHue(float hue) {
        if (hue != this.hue) {
            this.hue = hue;
            this.hueColor = ColorHelper.getHueColor(this.hue);
            this.updateColor();
        }
    }

    public void setSaturation(float saturation) {
        if (saturation != this.saturation) {
            this.saturation = saturation;
            this.updateColor();
        }
    }

    public void setBrightness(float brightness) {
        if (brightness != this.brightness) {
            this.brightness = brightness;
            this.updateColor();
        }
    }

    public void setHSV(ColorHelper.HSVColor hsv) {
        this.hue = hsv.hue();
        this.saturation = hsv.saturation();
        this.brightness  = hsv.brightness();

        this.hueColor = ColorHelper.getHueColor(this.hue);
        this.updateColor();
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int scale = this.getWidth() - 4;

        int iLeft = this.getX() + 2;
        int iTop = this.getY() + 2;

        this.drawColorField(graphics, iLeft, iTop, scale);
        graphics.renderOutline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xffffffff);

        int sx = (int) (iLeft + this.saturation * scale);
        int sy = (int) (iTop + (1 - this.brightness) * scale);

        graphics.fill(sx - 3, sy - 3, sx + 3, sy + 3, 0xffffffff);
        graphics.fill(sx - 2, sy - 2, sx + 2, sy + 2, this.color);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        float rx = ((float) mouseX - (this.getX() + 2)) / (this.getWidth() - 4);
        float ry = 1.0f - ((float) mouseY - (this.getY() + 2)) / (this.getHeight() - 4);

        this.saturation = MathHelper.clamp(rx, 0.0f, 1.0f);
        this.brightness = MathHelper.clamp(ry, 0.0f, 1.0f);
        this.updateColor();
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.onClick(mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {

    }

    private void updateColor() {
        this.color = ColorHelper.toRGB(this.hue, this.saturation, this.brightness);
        if (this.callback != null) {
            this.callback.accept(this.color);
        }
    }

    private void drawColorField(GuiGraphics graphics, int left, int top, int scale) {
        int right = left + scale;
        int bottom = top + scale;

        RenderSystem.setShader(Shaders::getColorFieldShader);

        Matrix4f matrix4f = graphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(matrix4f, (float)left, (float)top, 0).uv(0, 0).color(this.hueColor).endVertex();
        bufferbuilder.vertex(matrix4f, (float)left, (float)bottom, 0).uv(0, 1).color(this.hueColor).endVertex();
        bufferbuilder.vertex(matrix4f, (float)right, (float)bottom, 0).uv(1, 1).color(this.hueColor).endVertex();
        bufferbuilder.vertex(matrix4f, (float)right, (float)top, 0).uv(1, 0).color(this.hueColor).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }
}
