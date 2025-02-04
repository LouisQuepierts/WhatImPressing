package net.quepierts.urbaneui.inspector;

import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class InspectorModifyWidget<T> extends InspectorWidget {
    protected final Component message;
    protected final Supplier<T> getter;
    protected final Consumer<T> setter;

    protected InspectorModifyWidget(int height, Component message, Supplier<T> getter, Consumer<T> setter) {
        super(height);
        this.message = message;
        this.getter = getter;
        this.setter = setter;
    }
}
