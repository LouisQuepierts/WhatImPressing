package net.quepierts.wip.gui;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum LayoutMode implements StringRepresentable {
    LEFT, RIGHT;

    private static final LayoutMode[] CACHED = {LEFT, RIGHT};
    public static final Codec<LayoutMode> CODEC = StringRepresentable.fromEnum(LayoutMode::cached);

    private static LayoutMode[] cached() {
        return CACHED;
    }

    @NotNull
    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}
