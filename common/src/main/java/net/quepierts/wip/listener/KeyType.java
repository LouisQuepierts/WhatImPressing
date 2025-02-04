package net.quepierts.wip.listener;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum KeyType implements StringRepresentable {
    INPUT,
    MOUSE,
    KEYMAPPING;

    private static final KeyType[] CACHED = values();
    public static final Codec<KeyType> CODEC = StringRepresentable.fromEnum(KeyType::cache);

    @NotNull
    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }

    private static KeyType[] cache() {
        return CACHED;
    }

    public static KeyType[] implemented() {
        return new KeyType[] {INPUT, MOUSE};
    }
}
