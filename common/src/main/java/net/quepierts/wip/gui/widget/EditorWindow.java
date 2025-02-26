package net.quepierts.wip.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.quepierts.urbaneui.MathHelper;
import net.quepierts.urbaneui.inspector.InspectorBuilder;
import net.quepierts.urbaneui.widget.Inspector;
import net.quepierts.wip.CommonClass;
import net.quepierts.wip.gui.ColorSet;
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
    private EditorKeyListenerSection copy;

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
            graphics.hLine(this.getX() - 1, this.getX() + this.getWidth(), mouseY, 0xff00ff00);
            graphics.vLine(mouseX, this.getY() - 2, this.getY() + this.getHeight() + 1, 0xff00ff00);

            this.mouseX = localMouseX;
            this.mouseY = localMouseY;
        }

        //RenderSystem.enableScissor(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        pose.pushPose();
        pose.translate(this.getX(), this.getY(), 0);
        pose.scale(EditorWindow.EDITOR_RATIO, EditorWindow.EDITOR_RATIO, 0f);

        for (EditorKeyListenerSection section : this.sections) {
            section.renderWidget(graphics, localMouseX, localMouseY, partialTick);
        }

        if (this.focused != null) {
            this.focused.renderOutline(graphics, 0xffffffff, 2);
        }

        if (this.copy != null) {
            this.copy.renderOutline(graphics, 0xffbbffbb, 4);
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

        int targetX = MathHelper.clamp(this.focused.getCenterX() + localDragX, halfWidth, this.screenWidth - halfWidth);
        int targetY = MathHelper.clamp(this.focused.getCenterY() + localDragY, halfHeight, this.screenHeight - halfHeight);

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
            case InputConstants.KEY_R:
                if (this.focused != null && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown()) {
                    this.focused.setBaseColor(new ColorSet(0xbb808080, 0xbbb0b0b0));
                    this.focused.setFrameColor(new ColorSet(0x00000000, 0x00000000));
                    this.focused.setTextColor(new ColorSet(0xffffffff, 0xffffffff));
                    Inspector.getInspector().rebuildInspector();
                }
                break;
            case InputConstants.KEY_C:
                if (Screen.hasControlDown() && !Screen.hasAltDown() && !Screen.hasShiftDown()) {
                    this.copy = this.focused;
                }
                break;
            case InputConstants.KEY_V:
                if (this.copy != null && Screen.hasControlDown() && !Screen.hasAltDown() && !Screen.hasShiftDown()) {
                    if (this.focused != null) {
                        if (this.focused != this.copy) {
                            this.focused.pasteDisplay(this.copy);
                            Inspector.getInspector().rebuildInspector();
                        }
                    } else {
                        EditorKeyListenerSection copied = new EditorKeyListenerSection(this.copy);
                        copied.move(
                                this.mouseX, this.mouseY,
                                this.horizontalLayout, this.verticalLayout,
                                this.screenWidth, this.screenHeight
                        );
                        this.sections.add(copied);
                        Inspector.getInspector().setInspectObject(copied);
                        this.setFocused(copied);
                    }
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

        this.setPosition(innerLeft, innerTop);
        this.width = innerWidth;
        this.height = innerHeight;
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
        builder.title(Component.translatable("inspector.wip.layout"))
                .enumBox(Component.translatable("inspector.wip.layout.horizontal"), this::getHorizontalLayout, this::setHorizontalLayout, LayoutMode.values())
                .enumBox(Component.translatable("inspector.wip.layout.vertical"), this::getVerticalLayout, this::setVerticalLayout, LayoutMode.values());
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
