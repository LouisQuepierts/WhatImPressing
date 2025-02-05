package net.quepierts.wip;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.quepierts.urbaneui.Shaders;
import net.quepierts.wip.gui.KeystrokesDisplayLayer;

import java.io.IOException;

public class WhatImPressing implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CommonClass.init();

        HudRenderCallback.EVENT.register(this::onRenderOverlay);
        CoreShaderRegistrationCallback.EVENT.register(this::onRegisterShader);
        KeyBindingHelper.registerKeyBinding(CommonClass.KEY_OPEN_EDITOR.get());
    }

    private void onRegisterShader(CoreShaderRegistrationCallback.RegistrationContext context) throws IOException {
        context.register(
                ResourceLocation.fromNamespaceAndPath("urbaneui", "color_field"),
                DefaultVertexFormat.POSITION_TEX_COLOR,
                Shaders::setColorFieldShader
        );
    }

    private void onRenderOverlay(GuiGraphics graphics, DeltaTracker deltaTracker) {
        KeystrokesDisplayLayer.INSTANCE.render(graphics, deltaTracker);
    }
}
