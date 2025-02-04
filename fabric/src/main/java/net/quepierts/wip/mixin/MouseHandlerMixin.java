package net.quepierts.wip.mixin;

import net.minecraft.client.MouseHandler;
import net.quepierts.wip.CommonClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(
            method = "onPress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;",
                    ordinal = 0
            )
    )
    private void wip$onMousePress(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
        CommonClass.handleInput(button, action);
    }
}
