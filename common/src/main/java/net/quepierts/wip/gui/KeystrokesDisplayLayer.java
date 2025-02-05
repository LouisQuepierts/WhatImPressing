package net.quepierts.wip.gui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Setter;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.quepierts.wip.CommonClass;
import net.quepierts.wip.Constants;
import net.quepierts.wip.gui.widget.KeyListenerSection;
import net.quepierts.wip.listener.KeyListenersSetting;
import org.jetbrains.annotations.NotNull;

@Setter
public class KeystrokesDisplayLayer implements LayeredDraw.Layer {
    public static final int MIDDLE_WIDTH = 40;
    public static final int HALF_MIDDLE_WIDTH = MIDDLE_WIDTH / 2;

    public static final KeystrokesDisplayLayer INSTANCE = new KeystrokesDisplayLayer();
    public static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "keystrokes");

    private boolean hide = false;

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker tracker) {
        if (this.hide) {
            return;
        }

        KeyListenersSetting setting = CommonClass.getSetting();
        Window window = Minecraft.getInstance().getWindow();

        int width = window.getGuiScaledWidth();
        int height = window.getGuiScaledHeight();

        boolean algHorizontal = setting.getHorizontalLayout() == LayoutMode.RIGHT;
        boolean algVertical = setting.getVerticalLayout() == LayoutMode.RIGHT;

        for (KeyListenerSection section : setting.getListeners()) {
            if (!section.getListener().isActive()) {
                continue;
            }

            RenderSystem.enableBlend();
            int halfWidth = section.getWidth() / 2;
            int halfHeight = section.getHeight() / 2;

            int x = section.getX();
            int y = section.getY();

            if (algHorizontal) {
                x = width - x;
            }

            if (algVertical) {
                y = height - y;
            }

            int baseColorValue = section.getBaseColorValue();
            if ((baseColorValue & 0xff000000) != 0) {
                graphics.fill(
                        x - halfWidth,
                        y - halfHeight,
                        x + halfWidth,
                        y + halfHeight,
                        baseColorValue
                );
            }

            int frameColorValue = section.getFrameColorValue();
            if ((frameColorValue & 0xff000000) != 0) {
                graphics.renderOutline(
                        x - halfHeight,
                        y - halfHeight,
                        section.getWidth(),
                        section.getHeight(),
                        frameColorValue
                );
            }

            int textColorValue = section.getTextColorValue();
            if ((textColorValue & 0xff000000) != 0) {
                graphics.drawCenteredString(
                        Minecraft.getInstance().font,
                        section.getDisplayName(),
                        x,
                        y - 4,
                        textColorValue
                );
            }
            RenderSystem.disableBlend();
        }
    }

}
