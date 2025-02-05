package net.quepierts.urbaneui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.quepierts.urbaneui.inspector.InspectorBuilder;
import net.quepierts.urbaneui.inspector.InspectorWidget;
import net.quepierts.wip.gui.widget.Inspectable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Inspector extends AbstractWidget {
    private static final int HEAD_HEIGHT = 24;
    @Getter
    private static Inspector inspector = new Inspector(0, 0, 0, 0);     // dummy

    private List<InspectorWidget> widgets;
    private Inspectable target;

    private InspectorWidget focus;

    private int frameHeight;
    private int scroll = 0;
    private int available = 0;

    private float ratio = 1.0f;

    public Inspector(int x, int y, int width, int height) {
        super(x, y, width, height, Component.literal("Inspector"));

        inspector = this;
        this.frameHeight = height - HEAD_HEIGHT - 8;
    }

    public boolean isInspecting(Inspectable object) {
        return this.target == object;
    }

    public void setInspectObject(Inspectable target) {
        if (target != this.target) {
            this.target = target;
            this.rebuildInspector();
        }
    }

    public void rebuildInspector() {
        if (this.target == null) {
            this.widgets = null;
            return;
        }

        InspectorBuilder builder = new InspectorBuilder();
        target.onInspect(builder);
        this.widgets = builder.build();
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();

        graphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 0x88000000);
        graphics.renderOutline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xffffffff);

        int top = this.getY() + HEAD_HEIGHT;

        graphics.hLine(this.getX() + 8, this.getX() + this.getWidth() - 8, top, 0xffffffff);
        graphics.drawCenteredString(minecraft.font, "Inspector", this.getX() + this.width / 2, 8, 0xffffffff);

        if (this.widgets == null) {
            return;
        }

        boolean hovered = this.isMouseOver(mouseX, mouseY);

        double scale = minecraft.getWindow().getGuiScale();

        int innerLeft = this.getX() + 8;
        int innerWidth = this.getWidth() - 16;
        RenderSystem.enableScissor(
                (int) (innerLeft * scale), (int) (minecraft.getWindow().getHeight() - (this.getY() + this.height - 4) * scale),
                (int) (innerWidth * scale), (int) (this.frameHeight * scale)
        );

        PoseStack pose = graphics.pose();
        pose.pushPose();
        int offset = (int) (this.scroll / this.ratio);
        pose.translate(innerLeft, top + 1 - offset, 0);

        int maxHeight = this.height + offset;
        int height = HEAD_HEIGHT;
        int width = this.width - 2;

        mouseX -= this.getX() + 8;
        mouseY -= this.getY() - offset;

        for (InspectorWidget widget : this.widgets) {

            if (height > offset && height < maxHeight) {
                widget.render(
                        graphics,
                        innerWidth,
                        mouseX,
                        mouseY - height,
                        partialTick,
                        hovered && mouseY > height && mouseY < height + widget.getHeight()
                );
            }

            pose.translate(0f, widget.getHeight(), 0f);
            height += widget.getHeight();
        }

        pose.popPose();
        RenderSystem.disableScissor();


        int scrollHeight = height;
        int channelHeight = this.frameHeight - 4;
        this.ratio = Math.min((float) this.frameHeight / scrollHeight, 1.0f);
        int sliderHeight = (int) (ratio * channelHeight);

        int sliderTop = top + 4;
        int left = this.getX() + width - 4;

        this.available = channelHeight - sliderHeight;
        graphics.fill(left - 1, sliderTop, left + 3, sliderTop + 1, 0xffffffff);
        graphics.fill(
                left,
                sliderTop + 2 + Math.max(this.scroll, 0),
                left + 2,
                sliderTop + 2 + sliderHeight + Math.min(this.scroll, this.available),
                0xffffffff
        );
        graphics.fill(left - 1, sliderTop + 3 + channelHeight, left + 3, sliderTop + 4 + channelHeight, 0xffffffff);
        this.scroll = Math.clamp(this.scroll, 0, this.available);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        }

        mouseX -= this.getX() + 8;
        mouseY -= this.getY() - this.scroll / this.ratio;

        int height = HEAD_HEIGHT;
        for (InspectorWidget widget : this.widgets) {
            if (mouseY > height && mouseY < height + widget.getHeight()) {
                widget.onMousePressed(mouseX, mouseY - height, button, this.width);

                if (this.focus != null && this.focus != widget) {
                    this.focus.setFocused(false);
                }

                this.focus = widget;
                this.focus.setFocused(true);
                return true;
            }

            height += widget.getHeight();
        }

        if (this.focus != null) {
            this.focus.setFocused(false);
        }
        this.focus = null;
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        }

        mouseX -= this.getX() + 8;
        mouseY -= this.getY() - this.scroll / this.ratio;

        int height = HEAD_HEIGHT;
        for (InspectorWidget widget : this.widgets) {
            if (mouseY > height && mouseY < height + widget.getHeight()) {
                widget.onMouseReleased(mouseX, mouseY - height, button, this.width);
                return true;
            }
            height += widget.getHeight();
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        }

        mouseX -= this.getX() + 8;
        mouseY -= this.getY() - this.scroll / this.ratio;

        int height = HEAD_HEIGHT;
        for (InspectorWidget widget : this.widgets) {
            if (mouseY > height && mouseY < height + widget.getHeight()) {
                widget.onMouseDragging(mouseX, mouseY - height, button, dragX, dragY, this.width);
                return true;
            }
            height += widget.getHeight();
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        if (!this.isMouseOver(pMouseX, pMouseY)) {
            return false;
        }

        this.scroll -= (int) pScrollY * 2;
        this.scroll = Math.clamp(this.scroll, 0, this.available);

        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.focus != null) {
            return this.focus.onKeyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (this.focus != null) {
            return this.focus.onKeyReleased(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.focus != null) {
            return this.focus.charTyped(codePoint, modifiers);
        }
        return false;
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        this.frameHeight = height - HEAD_HEIGHT - 8;
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        this.frameHeight = height - HEAD_HEIGHT - 8;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {

    }
}
