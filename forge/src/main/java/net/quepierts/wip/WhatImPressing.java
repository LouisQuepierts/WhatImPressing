package net.quepierts.wip;

import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.quepierts.wip.gui.KeystrokesDisplayLayer;

import static net.quepierts.wip.CommonClass.KEY_OPEN_EDITOR;

@Mod(Constants.MODID)
public class WhatImPressing {

    public WhatImPressing() {
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        Constants.LOG.info("Hello Forge world!");
        CommonClass.init();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onRegisterKeyMappings);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        CommonClass.onClientSetup();
    }

    private void onRegisterKeyMappings(final RegisterKeyMappingsEvent event) {
        event.register(KEY_OPEN_EDITOR.get());
    }
}
