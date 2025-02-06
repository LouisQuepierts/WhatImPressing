package net.quepierts.wip.gui;

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
public enum LayoutMode implements StringRepresentable, DisplayableType {
    LEFT(Component.translatable("enums.wip.layout_mode.left")),
    RIGHT(Component.translatable("enums.wip.layout_mode.right"));

    private static final Component TYPE_NAME = Component.translatable("enums.wip.layout_mode");
    private static final LayoutMode[] CACHED = {LEFT, RIGHT};
    public static final Codec<LayoutMode> CODEC = StringRepresentable.fromEnum(LayoutMode::cached);

    private static LayoutMode[] cached() {
        return CACHED;
    }

    private final Component displayName;

    @NotNull
    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }

    @Override
    public Component getTypeDisplayName() {
        return TYPE_NAME;
    }
}
