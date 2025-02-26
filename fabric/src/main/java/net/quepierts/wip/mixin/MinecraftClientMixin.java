package net.quepierts.wip.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.renderer.CoreShaders;
import net.quepierts.urbaneui.Shaders;
import net.quepierts.wip.CommonClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {
    @Inject(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/Window;updateVsync(Z)V"
            )
    )
    private void wip$onClientSetup(GameConfig gameConfig, CallbackInfo ci) {
        CommonClass.onClientSetup();
        CoreShaders.getProgramsToPreload().add(Shaders.COLOR_FIELD);
    }
}
