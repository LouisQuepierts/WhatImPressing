package net.quepierts.wip;

import net.minecraft.client.renderer.CoreShaders;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.quepierts.urbaneui.Shaders;

import static net.quepierts.wip.CommonClass.KEY_OPEN_EDITOR;

@Mod(Constants.MODID)
public class WhatImPressing {

    public WhatImPressing() {
        CommonClass.init();
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class EventHandler {

        @SubscribeEvent
        public static void onClientSetup(final FMLClientSetupEvent event) {
            CommonClass.onClientSetup();
            CoreShaders.getProgramsToPreload().add(Shaders.COLOR_FIELD);
        }

        @SubscribeEvent
        public static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event) {
            event.register(KEY_OPEN_EDITOR.get());
        }

    }
}
