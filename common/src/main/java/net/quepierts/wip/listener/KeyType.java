package net.quepierts.wip.listener;

import com.mojang.serialization.Codec;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.quepierts.urbaneui.DisplayableType;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum KeyType implements StringRepresentable, DisplayableType {
    INPUT(Component.translatable("enums.wip.key_type.input")),
    MOUSE(Component.translatable("enums.wip.key_type.mouse")),
    KEYMAPPING(Component.translatable("enums.wip.key_type.keymapping"));

    private static final Component TYPE_NAME = Component.translatable("enums.wip.key_type");

    private static final KeyType[] CACHED = values();
    public static final Codec<KeyType> CODEC = StringRepresentable.fromEnum(KeyType::cache);

    private final Component displayName;


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

    @Override
    public Component getTypeDisplayName() {
        return TYPE_NAME;
    }
}
