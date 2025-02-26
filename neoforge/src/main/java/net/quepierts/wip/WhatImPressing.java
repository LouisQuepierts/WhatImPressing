package net.quepierts.wip;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.quepierts.urbaneui.Shaders;
import net.quepierts.wip.gui.KeystrokesDisplayLayer;

import static net.quepierts.wip.CommonClass.KEY_OPEN_EDITOR;

@Mod(Constants.MODID)
public class WhatImPressing {

    public WhatImPressing(IEventBus bus) {
        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
        Constants.LOG.info("Hello NeoForge world!");
        CommonClass.init();

        bus.addListener(this::onClientSetup);
        bus.addListener(this::onRegisterGuiLayers);
        bus.addListener(this::onRegisterKeyMappings);
        bus.addListener(this::onRegisterShader);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        CommonClass.onClientSetup();
    }

    private void onRegisterKeyMappings(final RegisterKeyMappingsEvent event) {
        event.register(KEY_OPEN_EDITOR.get());
    }

    private void onRegisterGuiLayers(final RegisterGuiLayersEvent event) {
        event.registerBelow(VanillaGuiLayers.DEBUG_OVERLAY, KeystrokesDisplayLayer.LOCATION, KeystrokesDisplayLayer.INSTANCE);
    }

    private void onRegisterShader(final RegisterShadersEvent event)  {
        event.registerShader(Shaders.COLOR_FIELD);
    }
}
