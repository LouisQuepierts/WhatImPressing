package net.quepierts.urbaneui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.quepierts.urbaneui.inspector.InspectorBuilder;
import net.quepierts.urbaneui.inspector.InspectorWidget;
import net.quepierts.wip.gui.widget.Inspectable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EditorInspector extends AbstractWidget {
    private static final int HEAD_HEIGHT = 24;
    private static EditorInspector inspector;

    private List<InspectorWidget> widgets;
    private Inspectable target;

    private InspectorWidget focus;

    public EditorInspector(int x, int y, int width, int height) {
        super(x, y, width, height, Component.literal("Inspector"));

        inspector = this;
    }

    @Nullable
    public static EditorInspector getInspector() {
        return inspector;
    }

    public boolean isInspecting(Inspectable object) {
        return this.target == object;
    }

    public void setInspectObject(Inspectable target) {
        if (target != this.target) {
            this.target = target;

            if (target == null) {
                this.widgets = null;
                return;
            }

            InspectorBuilder builder = new InspectorBuilder();
            target.onInspect(builder);
            this.widgets = builder.build();
        }
    }

    public void rebuildInspector() {
        if (this.target == null) {
            return;
        }

        InspectorBuilder builder = new InspectorBuilder();
        target.onInspect(builder);
        this.widgets = builder.build();
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 0x88000000);
        graphics.renderOutline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xffffffff);
        graphics.hLine(this.getX() + 8, this.getX() + this.getWidth() - 8, this.getY() + 24, 0xffffffff);
        graphics.drawCenteredString(Minecraft.getInstance().font, "Inspector", this.getX() + this.width / 2, 8, 0xffffffff);

        if (this.widgets == null) {
            return;
        }

        boolean hovered = this.isMouseOver(mouseX, mouseY);

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(this.getX(), this.getY() + HEAD_HEIGHT + 1, 0);

        int height = HEAD_HEIGHT;
        int width = this.width - 2;

        mouseX -= this.getX();
        mouseY -= this.getY();

        for (InspectorWidget widget : this.widgets) {
            widget.render(
                    graphics,
                    width,
                    mouseX,
                    mouseY - height,
                    partialTick,
                    hovered && mouseY > height && mouseY < height + widget.getHeight()
            );

            pose.translate(0f, widget.getHeight(), 0f);
            height += widget.getHeight();

            if (height > this.height) {
                break;
            }
        }

        pose.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        }

        mouseX -= this.getX();
        mouseY -= this.getY();

        if (mouseY < HEAD_HEIGHT) {
            return false;
        }

        int height = HEAD_HEIGHT;
        for (InspectorWidget widget : this.widgets) {
            height += widget.getHeight();

            if (mouseY < height) {
                widget.onMousePressed(mouseX, mouseY - height + widget.getHeight(), button, this.width);

                if (this.focus != null && this.focus != widget) {
                    this.focus.setFocused(false);
                }

                this.focus = widget;
                this.focus.setFocused(true);
                return true;
            }
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

        mouseX -= this.getX();
        mouseY -= this.getY();

        if (mouseY < HEAD_HEIGHT) {
            return false;
        }

        int height = HEAD_HEIGHT;
        for (InspectorWidget widget : this.widgets) {
            height += widget.getHeight();

            if (mouseY < height) {
                widget.onMouseReleased(mouseX, mouseY - height + widget.getHeight(), button, this.width);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        }

        mouseX -= this.getX();
        mouseY -= this.getY();

        if (mouseY < HEAD_HEIGHT) {
            return false;
        }

        int height = HEAD_HEIGHT;
        for (InspectorWidget widget : this.widgets) {
            height += widget.getHeight();

            if (mouseY < height) {
                widget.onMouseDragging(mouseX, mouseY - height + widget.getHeight(), button, dragX, dragY, this.width);
                return true;
            }
        }
        return false;
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
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {

    }
}
