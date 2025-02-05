package net.quepierts.wip.gui.widget;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.quepierts.wip.gui.ColorSet;
import net.quepierts.wip.listener.KeyListener;

import java.util.Optional;

@Getter
@Setter
public class KeyListenerSection {
    public static final Codec<KeyListenerSection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            KeyListener.CODEC.fieldOf("listener").forGetter(KeyListenerSection::getListener),
            Codec.INT.fieldOf("x").forGetter(KeyListenerSection::getX),
            Codec.INT.fieldOf("y").forGetter(KeyListenerSection::getY),
            Codec.INT.fieldOf("width").forGetter(KeyListenerSection::getWidth),
            Codec.INT.fieldOf("height").forGetter(KeyListenerSection::getHeight),
            Codec.STRING.fieldOf("name").forGetter(KeyListenerSection::getName),
            ColorSet.CODEC.optionalFieldOf("baseColor").forGetter(section -> Optional.of(section.baseColor)),
            ColorSet.CODEC.optionalFieldOf("frameColor").forGetter(section -> Optional.of(section.frameColor)),
            ColorSet.CODEC.optionalFieldOf("textColor").forGetter(section -> Optional.of(section.textColor))
    ).apply(instance, (listener, x, y, width, height, name, baseColor, frameColor, textColor) -> {
        KeyListenerSection section = new KeyListenerSection(listener, x, y, width, height, name);
        baseColor.ifPresent(section::setBaseColor);
        frameColor.ifPresent(section::setFrameColor);
        textColor.ifPresent(section::setTextColor);
        return section;
    }));

    private final KeyListener listener;
    private int x;
    private int y;
    private int width;
    private int height;

    private ColorSet baseColor = new ColorSet(0xbb808080, 0xbbb0b0b0);
    private ColorSet frameColor = new ColorSet(0x00000000, 0x00000000);
    private ColorSet textColor = new ColorSet(0xffffffff, 0xffffffff);

    protected String name = "#DEFAULT#";
    private Component displayName;

    public KeyListenerSection(KeyListener listener, int x, int y, int width, int height) {
        this.listener = listener;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public KeyListenerSection(KeyListener listener, int x, int y, int width, int height, String name) {
        this.listener = listener;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.setName(name);
    }

    public KeyListenerSection(KeyListener listener, int x, int y, int width, int height, String name, ColorSet baseColor, ColorSet frameColor, ColorSet textColor) {
        this.listener = listener;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;

        this.baseColor = new ColorSet(baseColor);
        this.frameColor = new ColorSet(frameColor);
        this.textColor = new ColorSet(textColor);
    }

    public void setName(String name) {
        this.name = name;
        this.updateName();
    }

    public int getBaseColorValue() {
        return this.baseColor.getColor(this.listener.isPressed());
    }

    public int getFrameColorValue() {
        return this.frameColor.getColor(this.listener.isPressed());
    }

    public int getTextColorValue() {
        return this.textColor.getColor(this.listener.isPressed());
    }

    protected void updateName() {
        if ("#DEFAULT#".equals(this.name)) {
            this.displayName = this.listener.getDisplayName();
        } else {
            this.displayName = Component.literal(this.name);
        }
    }

    public Component getDisplayName() {
        if (this.displayName == null) {
            this.updateName();
        }

        return this.displayName;
    }

}
