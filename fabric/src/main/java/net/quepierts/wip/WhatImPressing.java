package net.quepierts.wip;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.quepierts.wip.gui.KeystrokesDisplayLayer;

public class WhatImPressing implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CommonClass.init();

        HudRenderCallback.EVENT.register(this::onRenderOverlay);
        KeyBindingHelper.registerKeyBinding(CommonClass.KEY_OPEN_EDITOR.get());

        CommonClass.onClientSetup();
    }

    private void onRenderOverlay(GuiGraphics graphics, DeltaTracker deltaTracker) {
        KeystrokesDisplayLayer.INSTANCE.render(graphics, deltaTracker);
    }
}
