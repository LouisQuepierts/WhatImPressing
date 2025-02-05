package net.quepierts.urbaneui.inspector;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class InspectorTitle extends InspectorWidget {
    private final Component message;

    public InspectorTitle(Component message, int height) {
        super(Math.max(height, 16));
        this.message = message;
    }

    @Override
    public void render(GuiGraphics graphics, int width, int mouseX, int mouseY, float partialTick, boolean hovered) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.scale(1.1f, 1.1f, 1.1f);
        graphics.drawString(Minecraft.getInstance().font, this.message, 0, this.getHeight() / 2, 0xffffffff);
        pose.popPose();
    }
}
