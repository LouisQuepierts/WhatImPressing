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
import net.quepierts.urbaneui.widget.Inspector;
import net.quepierts.wip.gui.ColorSet;
import net.quepierts.wip.gui.LayoutMode;
import net.quepierts.wip.gui.MouseType;
import net.quepierts.wip.listener.*;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter
public class EditorKeyListenerSection extends AbstractWidget implements Inspectable {
    private final Minecraft minecraft;
    private final Font font;

    private ColorSet baseColor = new ColorSet(0xbb808080, 0xbbb0b0b0);
    private ColorSet frameColor = new ColorSet(0x00000000, 0x00000000);
    private ColorSet textColor = new ColorSet(0xffffffff, 0xffffffff);

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
        this.baseColor = new ColorSet(section.getBaseColor());
        this.frameColor = new ColorSet(section.getFrameColor());
        this.textColor = new ColorSet(section.getTextColor());

        this.keyType = section.getListener().getType();

        String key = section.getListener().getKey();
        switch (this.keyType) {
            case MOUSE:
                this.mouseType = MouseType.parse(key);
                break;
            case INPUT:
                this.key = InputConstants.getKey(key);
                break;
        }

        this.keyName = key;
        this.minecraft = Minecraft.getInstance();
        this.font = this.minecraft.font;

        this.name = section.getName();
        this.displayName = section.getDisplayName();
    }

    public EditorKeyListenerSection(EditorKeyListenerSection section) {
        super(section.getX(), section.getY(), section.getWidth(), section.getHeight(), Component.literal("section"));

        this.baseColor = new ColorSet(section.getBaseColor());
        this.frameColor = new ColorSet(section.getFrameColor());
        this.textColor = new ColorSet(section.getTextColor());

        this.centerX = section.getCenterX();
        this.centerY = section.getCenterY();

        this.keyType = section.getKeyType();
        switch (this.keyType) {
            case MOUSE:
                this.mouseType = section.getMouseType();
                break;
            case INPUT:
                this.key = section.getKey();
                break;
        }

        this.keyName = section.getKeyName();
        this.minecraft = Minecraft.getInstance();
        this.font = this.minecraft.font;

        this.name = section.getName();
        this.displayName = section.getDisplayName();
    }

    public KeyListenerSection toListenerSection() {
        KeyListener listener = switch (this.keyType) {
            case INPUT -> new InputListener(this.keyName);
            case MOUSE -> new MouseListener(this.mouseType);
            case KEYMAPPING -> new KeymappingListener(this.keyName);
        };

        return new KeyListenerSection(
                listener,
                this.getX(),
                this.getY(),
                this.width,
                this.height,
                this.name,
                this.baseColor,
                this.frameColor,
                this.textColor
        );
    }

    public void copyDisplay(EditorKeyListenerSection other) {
        this.baseColor = new ColorSet(other.getBaseColor());
        this.frameColor = new ColorSet(other.getFrameColor());
        this.textColor = new ColorSet(other.getTextColor());
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int halfWidth = this.getWidth() / 2;
        int halfHeight = this.getHeight() / 2;

        int x = this.centerX;
        int y = this.centerY;

        this.isHovered = this.isMouseOver(mouseX, mouseY);
        graphics.fill(
                x - halfWidth,
                y - halfHeight,
                x + halfWidth,
                y + halfHeight,
                this.baseColor.getColor(this.isHovered)
        );
        graphics.renderOutline(
                x - halfWidth,
                y - halfHeight,
                this.getWidth(),
                this.getHeight(),
                this.frameColor.getColor(this.isHovered)
        );
        graphics.drawCenteredString(
                this.font,
                this.displayName,
                x,
                y - 4,
                this.textColor.getColor(this.isHovered)
        );
    }

    public void renderOutline(GuiGraphics graphics, int color, int expand) {
        int x = this.centerX;
        int y = this.centerY;

        int halfWidth = this.getWidth() / 2;
        int halfHeight = this.getHeight() / 2;
        int expand2 = expand * 2;

        graphics.renderOutline(
                x - halfWidth - expand,
                y - halfHeight - expand,
                this.width + expand2,
                this.height + expand2,
                color
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
            this.refreshName();
            Inspector.getInspector().rebuildInspector();
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
                    this.keyName = switch (mouseType) {
                        case LEFT -> "key.mouse.left";
                        case MIDDLE -> "key.mouse.middle";
                        case RIGHT -> "key.mouse.right";
                    };
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
                .title(Component.literal("Display"))
                .editBox(Component.literal("Name"), this::getName, this::setName)
                .title(Component.literal("Base Color"))
                .colorPicker(Component.literal("Normal"), this.baseColor::getNormal, this.baseColor::setNormal)
                .colorPicker(Component.literal("Pressed"), this.baseColor::getPressed, this.baseColor::setPressed)
                .title(Component.literal("Frame Color"))
                .colorPicker(Component.literal("Normal"), this.frameColor::getNormal, this.frameColor::setNormal)
                .colorPicker(Component.literal("Pressed"), this.frameColor::getPressed, this.frameColor::setPressed)
                .title(Component.literal("Text Color"))
                .colorPicker(Component.literal("Normal"), this.textColor::getNormal, this.textColor::setNormal)
                .colorPicker(Component.literal("Pressed"), this.textColor::getPressed, this.textColor::setPressed);
    }
}
