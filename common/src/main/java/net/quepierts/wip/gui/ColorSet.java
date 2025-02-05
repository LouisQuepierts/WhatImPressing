package net.quepierts.wip.gui;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ColorSet {
    public static final Codec<ColorSet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("normal").forGetter(ColorSet::getNormal),
            Codec.INT.fieldOf("pressed").forGetter(ColorSet::getPressed)
    ).apply(instance, ColorSet::new));
    private int normal;
    private int pressed;

    public ColorSet(ColorSet other) {
        this.normal = other.normal;
        this.pressed = other.pressed;
    }

    public int getColor(boolean pressed) {
        return pressed ? this.pressed : this.normal;
    }
}
