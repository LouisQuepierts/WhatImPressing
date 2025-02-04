package net.quepierts.wip.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.quepierts.urbaneui.inspector.InspectorBuilder;
import net.quepierts.urbaneui.widget.EditorInspector;
import net.quepierts.wip.gui.LayoutMode;
import net.quepierts.wip.gui.MouseType;
import net.quepierts.wip.listener.KeyListener;
import net.quepierts.wip.listener.KeyType;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter
public class EditorKeyListenerSection extends AbstractWidget implements Inspectable {
    private final Minecraft minecraft;
    private final Font font;

    private int colorNormal = 0xbb808080;
    private int colorPressed = 0xbbb0b0b0;

    private LayoutMode horizontalLayout = LayoutMode.LEFT;
    private LayoutMode verticalLayout = LayoutMode.LEFT;

    private Component displayName = Component.literal("none");
    private String name = "#DEFAULT#";

    private int centerX;
    private int centerY;

    private KeyType keyType = KeyType.INPUT;

    private MouseType mouseType = MouseType.LEFT;
    private InputConstants.Key key = InputConstants.UNKNOWN;
    private String keyName = "key.keyboard.unknown";

    public EditorKeyListenerSection(int x, int y, LayoutMode horizontalLayout, LayoutMode verticalLayout, int screenWidth, int screenHeight) {
        super(x, y, 20, 20, Component.literal("section"));

        this.minecraft = Minecraft.getInstance();
        this.font = this.minecraft.font;

        this.move(x, y, horizontalLayout, verticalLayout, screenWidth, screenHeight);
    }

    public EditorKeyListenerSection(KeyListenerSection section) {
        super(section.getX(), section.getY(), section.getWidth(), section.getHeight(), Component.literal("section"));
        this.colorNormal = section.getColorNormal();
        this.colorPressed = section.getColorPressed();
        this.keyType = section.getListener().getType();

        String key = section.getListener().getKey();
        switch (this.keyType) {
            case MOUSE:
                this.mouseType = MouseType.valueOf(key);
                break;
            case INPUT:
                this.key = InputConstants.getKey(key);
                break;
        }

        this.keyName = key;
        this.minecraft = Minecraft.getInstance();
        this.font = this.minecraft.font;
        this.displayName = section.getDisplayName();
    }

    public KeyListenerSection toListenerSection() {
        KeyListener listener = KeyListener.getInstance(this.keyType, this.keyName);
        return new KeyListenerSection(
                listener,
                this.getX(),
                this.getY(),
                this.width,
                this.height,
                name);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int halfWidth = this.getWidth() / 2;
        int halfHeight = this.getHeight() / 2;

        graphics.fill(
                this.getX() - halfWidth,
                this.getY() - halfHeight,
                this.getX() + halfWidth,
                this.getY() + halfHeight,
                this.colorNormal);
        graphics.drawCenteredString(
                this.font,
                this.displayName,
                this.getX(),
                this.getY() - 4,
                0xffffffff
        );
    }

    public void render(GuiGraphics graphics) {
        int x = this.centerX;
        int y = this.centerY;

        int halfWidth = this.getWidth() / 2;
        int halfHeight = this.getHeight() / 2;

        graphics.fill(
                x - halfWidth,
                y - halfHeight,
                x + halfWidth,
                y + halfHeight,
                this.colorNormal);
        graphics.drawCenteredString(
                this.font,
                this.displayName,
                x,
                y - 4,
                0xffffffff
        );
    }

    public void renderOutline(GuiGraphics graphics) {
        int x = this.centerX;
        int y = this.centerY;

        int halfWidth = this.getWidth() / 2;
        int halfHeight = this.getHeight() / 2;

        graphics.renderOutline(
                x - halfWidth - 2,
                y - halfHeight - 2,
                this.width + 4,
                this.height + 4,
                0xffffffff
        );
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return this.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        double halfWidth = this.getWidth() / 2.0;
        double halfHeight = this.getHeight() / 2.0;

        return this.active && this.visible
                && mouseX > this.centerX - halfWidth
                && mouseY > this.centerY - halfHeight
                && mouseX < this.centerX + halfWidth
                && mouseY < this.centerY + halfHeight;
    }

    public void move(int x, int y, LayoutMode horizontalLayout, LayoutMode verticalLayout, int width, int height) {
        this.centerX = x;
        this.centerY = y;

        if (horizontalLayout == LayoutMode.RIGHT) {
            x = width - x;
        }

        if (verticalLayout == LayoutMode.RIGHT) {
            y = height - y;
        }

        this.setPosition(x, y);
    }

    public void updateLayoutPosition(LayoutMode horizontalLayout, LayoutMode verticalLayout, int width, int height) {
        this.centerX = this.getX();
        this.centerY = this.getY();
        if (horizontalLayout == LayoutMode.RIGHT) {
            this.centerX = width - this.centerX;
        }

        if (verticalLayout == LayoutMode.RIGHT) {
            this.centerY = height - this.centerY;
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {

    }

    public void setKeyType(KeyType keyType) {
        if (keyType != this.keyType) {
            this.keyType = keyType;
            EditorInspector.getInspector().rebuildInspector();
        }
    }

    public void setMouseType(MouseType mouseType) {
        if (mouseType != this.mouseType) {
            this.mouseType = mouseType;
            this.keyName = switch (mouseType) {
                case LEFT -> "key.mouse.left";
                case MIDDLE -> "key.mouse.middle";
                case RIGHT -> "key.mouse.right";
            };
            this.refreshName();
        }
    }

    public void setKey(InputConstants.Key key) {
        if (key != this.key) {
            this.key = key;
            this.keyName = key.getName();
            this.refreshName();
        }
    }

    private void setName(String name) {
        if (!name.equals(this.name)) {
            this.name = name;
            this.refreshName();
        }
    }

    private void refreshName() {
        if ("#DEFAULT#".equals(this.name)) {
            switch (this.keyType) {
                case MOUSE:
                    this.displayName = Component.translatable(this.keyName);
                    break;
                case INPUT:
                    this.displayName = this.key.getDisplayName();
                    break;
            }
        } else if (!this.displayName.getString().equals(this.name)) {
            this.displayName = Component.literal(name);
        }
    }

    @Override
    public void onInspect(InspectorBuilder builder) {
        builder.title(Component.literal("Key Listener"))
                .enumBox(Component.literal("Type"), this::getKeyType, this::setKeyType, KeyType.implemented());

        switch (this.keyType) {
            case MOUSE:
                builder.enumBox(Component.literal("Mouse"), this::getMouseType, this::setMouseType, MouseType.values());
                break;
            case INPUT:
                builder.keyInputBox(Component.literal("Key"), this::getKey, this::setKey);
                break;
        }

        builder.title(Component.literal("Information"))
                .intSlider(Component.literal("Width"), this::getWidth, this::setWidth, 20, 80, 2)
                .intSlider(Component.literal("Height"), this::getHeight, this::setHeight, 20, 80, 2)
                .editBox(Component.literal("Name"), this::getName, this::setName);
    }
}
