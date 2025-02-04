package net.quepierts.wip.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.quepierts.urbaneui.inspector.InspectorBuilder;
import net.quepierts.urbaneui.widget.Inspector;
import net.quepierts.wip.CommonClass;
import net.quepierts.wip.gui.LayoutMode;
import net.quepierts.wip.listener.KeyListenersSetting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EditorWindow extends AbstractWidget implements Inspectable {
    public static final float EDITOR_RATIO = 0.7f;
    public static EditorWindow getInstance() {
        Minecraft minecraft = Minecraft.getInstance();
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        int innerWidth = (int) (screenWidth * EditorWindow.EDITOR_RATIO);
        int innerHeight = (int) (screenHeight * EditorWindow.EDITOR_RATIO);
        int innerLeft = 4;
        int innerTop = (screenHeight - innerHeight) / 4;

        return new EditorWindow(innerLeft, innerTop, innerWidth, innerHeight, screenWidth, screenHeight);
    }

    private final List<EditorKeyListenerSection> sections;

    @Setter
    @Getter
    private boolean dragging = false;

    @Getter
    private EditorKeyListenerSection focused;

    private int screenWidth;
    private int screenHeight;

    private double pressedX = 0;
    private double pressedY = 0;

    @Getter
    private LayoutMode horizontalLayout;
    @Getter
    private LayoutMode verticalLayout;

    private int mouseX;
    private int mouseY;

    private EditorWindow(int x, int y, int width, int height, int screenWidth, int screenHeight) {
        super(x, y, width, height, Component.literal("editor"));

        KeyListenersSetting setting = CommonClass.getSetting();

        this.horizontalLayout = setting.getHorizontalLayout();
        this.verticalLayout = setting.getVerticalLayout();

        List<KeyListenerSection> listeners = setting.getListeners();
        this.sections = new ArrayList<>(listeners.size());
        for (KeyListenerSection section : listeners) {
            EditorKeyListenerSection editorSection = new EditorKeyListenerSection(section);
            this.sections.add(editorSection);
            editorSection.updateLayoutPosition(this.horizontalLayout, this.verticalLayout, screenWidth, screenHeight);
        }

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        PoseStack pose = graphics.pose();

        int localMouseX = (int) ((mouseX - this.getX()) / EditorWindow.EDITOR_RATIO);
        int localMouseY = (int) ((mouseY - this.getY()) / EditorWindow.EDITOR_RATIO);

        if (this.isHovered()) {
            graphics.hLine(this.getX(), this.getX() + this.getWidth(), mouseY, 0xff00ff00);
            graphics.vLine(mouseX, this.getY(), this.getY() + this.getHeight(), 0xff00ff00);

            this.mouseX = localMouseX;
            this.mouseY = localMouseY;
        }

        //RenderSystem.enableScissor(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        pose.pushPose();
        pose.translate(this.getX(), this.getY(), 0);
        pose.scale(EditorWindow.EDITOR_RATIO, EditorWindow.EDITOR_RATIO, 0f);

        for (EditorKeyListenerSection section : this.sections) {
            section.render(graphics);
        }

        if (this.focused != null) {
            this.focused.renderOutline(graphics);
        }

        pose.popPose();
        //RenderSystem.disableScissor();

        graphics.renderOutline(this.getX() - 2, this.getY() - 2, this.getWidth() + 4, this.getHeight() + 4, 0xffffffff);

        graphics.drawString(
                Minecraft.getInstance().font,
                "[%d, %d]".formatted(localMouseX, localMouseY),
                this.getX() + 2,
                this.getY() + this.getHeight() - 8,
                0xffffffff
        );
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        }

        double localMouseX = (mouseX - this.getX()) / EditorWindow.EDITOR_RATIO;
        double localMouseY = (mouseY - this.getY()) / EditorWindow.EDITOR_RATIO;

        boolean missed = this.focused == null;
        if (!missed) {
            missed = !this.focused.isMouseOver(localMouseX, localMouseY);
        }

        if (missed) {
            int i = this.sections.size() - 1;
            for (; i >= 0; i--) {
                EditorKeyListenerSection section = this.sections.get(i);

                if (section.mouseClicked(localMouseX, localMouseY, button)) {
                    this.setFocused(section);
                    break;
                }
            }

            if (i == -1) {
                this.setFocused(null);
                return true;
            }
        }

        if (button == 0) {
            this.pressedX = localMouseX;
            this.pressedY = localMouseY;
            this.setDragging(true);
        }

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        }

        int localMouseX = (int) ((mouseX - this.getX()) / EditorWindow.EDITOR_RATIO);
        int localMouseY = (int) ((mouseY - this.getY()) / EditorWindow.EDITOR_RATIO);

        if (button == 0 && this.isDragging()) {
            this.setDragging(false);
            if (this.getFocused() != null) {
                return this.getFocused().mouseReleased(localMouseX, localMouseY, button);
            }
        }

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        }

        if (this.focused == null || !this.dragging || button != 0) {
            return false;
        }

        int localMouseX = (int) ((mouseX - this.getX()) / EditorWindow.EDITOR_RATIO);
        int localMouseY = (int) ((mouseY - this.getY()) / EditorWindow.EDITOR_RATIO);

        int localDragX = (int) (localMouseX - this.pressedX);
        int localDragY = (int) (localMouseY - this.pressedY);

        int halfWidth = this.focused.getWidth() / 2;
        int halfHeight = this.focused.getHeight() / 2;

        int targetX = Math.clamp(this.focused.getCenterX() + localDragX, halfWidth, this.screenWidth - halfWidth);
        int targetY = Math.clamp(this.focused.getCenterY() + localDragY, halfHeight, this.screenHeight - halfHeight);

        int deltaX = targetX - this.focused.getCenterX();
        int deltaY = targetY - this.focused.getCenterY();

        this.focused.move(targetX, targetY, this.horizontalLayout, this.verticalLayout, this.screenWidth, this.screenHeight);

        this.pressedX += deltaX;
        this.pressedY += deltaY;
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.focused == null) {
            return false;
        }

        int halfWidth = this.focused.getWidth() / 2;
        int halfHeight = this.focused.getHeight() / 2;

        int x = this.focused.getCenterX();
        int y = this.focused.getCenterY();

        switch (keyCode) {
            case InputConstants.KEY_UP:
                y = Math.max(halfHeight, this.focused.getCenterY() - 1);
                this.focused.move(x, y, this.horizontalLayout, this.verticalLayout, this.screenWidth, this.screenHeight);
                break;
            case InputConstants.KEY_DOWN:
                y = Math.min(this.screenHeight - halfHeight, this.focused.getCenterY() + 1);
                this.focused.move(x, y, this.horizontalLayout, this.verticalLayout, this.screenWidth, this.screenHeight);
                break;
            case InputConstants.KEY_LEFT:
                x = Math.max(halfWidth, this.focused.getCenterX() - 1);
                this.focused.move(x, y, this.horizontalLayout, this.verticalLayout, this.screenWidth, this.screenHeight);
                break;
            case InputConstants.KEY_RIGHT:
                x = Math.min(this.screenWidth - halfWidth, this.focused.getCenterX() + 1);
                this.focused.move(x, y, this.horizontalLayout, this.verticalLayout, this.screenWidth, this.screenHeight);
                break;
        }

        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        switch (keyCode) {
            case InputConstants.KEY_A:
                if (this.isHovered()) {
                    EditorKeyListenerSection section = new EditorKeyListenerSection(
                            this.mouseX, this.mouseY,
                            this.horizontalLayout, this.verticalLayout,
                            this.screenWidth, this.screenHeight
                    );
                    this.sections.add(section);
                    Inspector.getInspector().setInspectObject(section);
                    this.setFocused(section);
                }
                break;
            case InputConstants.KEY_X:
                if (this.focused != null) {
                    this.sections.remove(this.focused);
                    this.setFocused(null);
                }
                break;
            case InputConstants.KEY_S:
                if ((modifiers & 2) != 0) {
                    this.save();
                }
                break;
        }
        return true;
    }

    public void setHorizontalLayout(LayoutMode horizontalLayout) {
        if (horizontalLayout != this.horizontalLayout) {
            this.horizontalLayout = horizontalLayout;
            this.resize();
        }
    }

    public void setVerticalLayout(LayoutMode verticalLayout) {
        if (verticalLayout != this.verticalLayout) {
            this.verticalLayout = verticalLayout;
            this.resize();
        }
    }

    public void resize() {
        Minecraft minecraft = Minecraft.getInstance();
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        int innerWidth = (int) (screenWidth * EditorWindow.EDITOR_RATIO);
        int innerHeight = (int) (screenHeight * EditorWindow.EDITOR_RATIO);
        int innerLeft = 4;
        int innerTop = (screenHeight - innerHeight) / 4;

        this.setRectangle(innerWidth, innerHeight, innerLeft, innerTop);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        for (EditorKeyListenerSection section : this.sections) {
            section.updateLayoutPosition(this.horizontalLayout, this.verticalLayout, screenWidth, screenHeight);
        }
    }

    public void setFocused(@Nullable GuiEventListener guiEventListener) {
        if (guiEventListener == this.focused) {
            return;
        }

        if (this.focused != null) {
            this.focused.setFocused(false);
        }

        if (guiEventListener instanceof EditorKeyListenerSection section) {
            this.focused = section;
            this.focused.setFocused(true);

            this.sections.remove(this.focused);
            this.sections.add(this.focused);

            Inspector.getInspector().setInspectObject(section);
        } else {
            this.focused = null;
            Inspector.getInspector().setInspectObject(this);
        }
    }

    @Override
    public void onInspect(InspectorBuilder builder) {
        builder.title(Component.literal("Layout"))
                .enumBox(Component.literal("Horizontal"), this::getHorizontalLayout, this::setHorizontalLayout, LayoutMode.values())
                .enumBox(Component.literal("Vertical"), this::getVerticalLayout, this::setVerticalLayout, LayoutMode.values());
    }

    public void save() {
        List<KeyListenerSection> listenerSections = new ArrayList<>(this.sections.size());
        for (EditorKeyListenerSection section : this.sections) {
            listenerSections.add(section.toListenerSection());
        }

        KeyListenersSetting setting = CommonClass.getSetting();
        setting.setListeners(listenerSections);
        setting.setHorizontalLayout(this.horizontalLayout);
        setting.setVerticalLayout(this.verticalLayout);

        KeyListenersSetting.save(setting);
    }
}
