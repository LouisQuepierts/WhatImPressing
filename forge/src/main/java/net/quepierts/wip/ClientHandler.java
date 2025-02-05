package net.quepierts.wip;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.quepierts.wip.gui.KeystrokesDisplayLayer;

@Mod.EventBusSubscriber(modid = Constants.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientHandler {
    @SubscribeEvent
    public static void onRenderOverlay(final CustomizeGuiOverlayEvent.Chat event) {
        KeystrokesDisplayLayer.INSTANCE.render(event.getGuiGraphics(), event.getPartialTick());
    }

    @SubscribeEvent
    public static void onKeyPressed(final InputEvent.Key event) {
        CommonClass.handleInput(event.getKey(), event.getAction());
    }

    @SubscribeEvent
    public static void onMouseClicked(final InputEvent.MouseButton.Pre event) {
        CommonClass.handleInput(event.getButton(), event.getAction());
    }
}
