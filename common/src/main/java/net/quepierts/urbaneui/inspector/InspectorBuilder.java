package net.quepierts.urbaneui.inspector;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class InspectorBuilder {
    private final ImmutableList.Builder<InspectorWidget> builder = ImmutableList.builder();

    public InspectorBuilder space() {
        this.builder.add(new InspectorSpace(10));
        return this;
    }

    public InspectorBuilder space(int height) {
        this.builder.add(new InspectorSpace(height));
        return this;
    }

    public InspectorBuilder title(Component message) {
        this.builder.add(new InspectorTitle(message, 24));
        return this;
    }

    public <T extends Enum<?>> InspectorBuilder enumBox(Component message, Supplier<T> getter, Consumer<T> setter, T[] values) {
        this.builder.add(new InspectorEnumBox<>(message, getter, setter, values));
        return this;
    }

    public InspectorBuilder keyInputBox(Component message, Supplier<InputConstants.Key> getter, Consumer<InputConstants.Key> setter) {
        this.builder.add(new InspectorKeyBox(message, getter, setter));
        return this;
    }

    public InspectorBuilder intSlider(Component message, Supplier<Integer> getter, Consumer<Integer> setter, int min, int max, int step) {
        this.builder.add(new InspectorIntegerSlider(message, getter, setter, min, max, step));
        return this;
    }

    public InspectorBuilder editBox(Component message, Supplier<String> getter, Consumer<String> setter) {
        this.builder.add(new InspectorEditBox(message, getter, setter));
        return this;
    }

    public InspectorBuilder colorPicker(Component message, Supplier<Integer> getter, Consumer<Integer> setter) {
        this.builder.add(new InspectorColorPicker(message, getter, setter));
        return this;
    }

    public List<InspectorWidget> build() {
        return this.builder.build();
    }
}
