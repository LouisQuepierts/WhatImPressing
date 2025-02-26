package net.quepierts.urbaneui.inspector;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.quepierts.urbaneui.DisplayableType;
import net.quepierts.urbaneui.MathHelper;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class InspectorEnumBox<T extends DisplayableType> extends InspectorModifyWidget<T> {
    private final T[] values;

    private boolean dropped = false;

    private T selected;

    public InspectorEnumBox(Component message, Supplier<T> getter, Consumer<T> setter, T[] values) {
        super(22, message, getter, setter);
        this.values = values;
        this.selected = getter.get();
    }

    @Override
    public void render(GuiGraphics graphics, int width, int mouseX, int mouseY, float partialTick, boolean hovered) {
        int buttonWidth = width / 2;
        int left = width - buttonWidth;

        graphics.drawString(Minecraft.getInstance().font, this.message, 0, 8, 0xffffffff);
        RenderSystem.enableBlend();

        int hover = hovered ? mouseY / 20 : -1;
        graphics.fill(left, 2, width, 20, 0xbb000000);
        if (this.dropped) {
            graphics.renderOutline(left, 2, buttonWidth, 20, 0xffbbbbff);
        } else if (hover == 0) {
            graphics.renderOutline(left, 2, buttonWidth, 20, 0xffffffff);
        }

        int half = buttonWidth / 2;
        graphics.drawCenteredString(
                Minecraft.getInstance().font,
                this.selected.getDisplayName(),
                left + half, 8,
                0xffffffff
        );

        if (this.dropped) {
            for (int i = 0; i < values.length; i++) {
                RenderSystem.enableBlend();
                graphics.fill(left, 22 + 20 * i, width, 42 + 20 * i, 0xbb000000);
                if (hover == i + 1) {
                    graphics.renderOutline(left, 22 + 20 * i, buttonWidth, 20, 0xffffffff);
                }

                graphics.drawCenteredString(
                        Minecraft.getInstance().font,
                        this.values[i].getDisplayName(),
                        left + half, 28 + 20 * i,
                        0xffffffff
                );
            }
        }
    }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, int button, int width) {
        if (button != 0) {
            return;
        }

        if (mouseY > 20)  {
            int i = MathHelper.clamp((int) (mouseY / 20) - 1, 0, 1);
            T value = this.values[i];

            if (value != this.selected) {
                this.selected = value;
                this.setter.accept(value);
            }
        }

        this.dropped = !this.dropped;
    }

    @Override
    public int getHeight() {
        return this.dropped ? 24 + this.values.length * 20 : 24;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean paste(InspectorWidget copy) {
        if (copy instanceof InspectorEnumBox<?> box) {
            if (box.selected.getClass() == this.selected.getClass()) {
                this.selected = (T) box.selected;
                this.setter.accept(this.selected);
                return true;
            }
        }
        return false;
    }
}
