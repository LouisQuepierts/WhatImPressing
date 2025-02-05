package net.quepierts.urbaneui;

import lombok.Setter;
import net.minecraft.client.renderer.ShaderInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Shaders {
    @Nullable
    @Setter
    private static ShaderInstance colorFieldShader;

    @NotNull
    public static ShaderInstance getColorFieldShader() {
        return Objects.requireNonNull(colorFieldShader, "Attempted to call getColorFieldShader before shaders have finished loading.");
    }
}
