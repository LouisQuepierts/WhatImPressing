package net.quepierts.urbaneui;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderProgram;
import net.minecraft.resources.ResourceLocation;

public class Shaders {
    public static final ShaderProgram COLOR_FIELD;

    static {
        COLOR_FIELD = new ShaderProgram(
                ResourceLocation.fromNamespaceAndPath("urbaneui", "core/color_field"),
                DefaultVertexFormat.POSITION_TEX_COLOR,
                ShaderDefines.EMPTY
        );
    }
}
