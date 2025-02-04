package net.quepierts.wip.gui.widget;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;
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
            Codec.INT.optionalFieldOf("normal_color").forGetter(section -> Optional.of(section.colorNormal)),
            Codec.INT.optionalFieldOf("pressed_color").forGetter(section -> Optional.of(section.colorPressed)),
            Codec.STRING.fieldOf("name").forGetter(KeyListenerSection::getName)
    ).apply(instance, (listener, x, y, width, height, colorNormal, colorPressed, name) -> {
        KeyListenerSection section = new KeyListenerSection(listener, x, y, width, height, name);
        colorNormal.ifPresent(section::setColorNormal);
        colorPressed.ifPresent(section::setColorNormal);
        return section;
    }));

    private final KeyListener listener;
    private int x;
    private int y;
    private int width;
    private int height;

    private int colorNormal = 0xbb808080;
    private int colorPressed = 0xbbb0b0b0;

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

    public KeyListenerSection(KeyListener listener, int x, int y, int width, int height, String name, int colorNormal, int colorPressed) {
        this.listener = listener;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.colorNormal = colorNormal;
        this.colorPressed = colorPressed;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
        this.updateName();
    }

    public int getColor() {
        return this.listener.isPressed() ? this.colorPressed : this.colorNormal;
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
