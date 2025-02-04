package net.quepierts.urbaneui.inspector;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class InspectorKeyMappingBox extends InspectorModifyWidget<KeyMapping> {
    public InspectorKeyMappingBox(Component message, Supplier<KeyMapping> getter, Consumer<KeyMapping> setter) {
        super(20, message, getter, setter);
    }
}
