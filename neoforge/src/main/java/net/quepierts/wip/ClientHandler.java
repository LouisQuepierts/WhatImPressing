package net.quepierts.wip;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME, modid = Constants.MODID)
public class ClientHandler {
    @SubscribeEvent
    public static void onKeyPressed(final InputEvent.Key event) {
        CommonClass.onKeyInput(event.getKey(), event.getAction());
    }
}
