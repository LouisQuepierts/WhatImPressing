package net.quepierts.wip;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.quepierts.wip.gui.KeystrokesDisplayLayer;
import net.quepierts.wip.gui.KeystrokesEditorScreen;

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
