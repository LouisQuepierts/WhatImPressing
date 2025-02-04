package net.quepierts.wip.gui;

import com.google.common.collect.ImmutableMap;

import java.util.Locale;
import java.util.Map;

public enum MouseType {
    LEFT("key.mouse.left"),
    RIGHT("key.mouse.right"),
    MIDDLE("key.mouse.middle");

    public final String key;

    private final static Map<String, MouseType> REFERENCE;

    MouseType(String key) {
        this.key = key;
    }

    public static MouseType parse(String string) {
        return REFERENCE.get(string.toUpperCase(Locale.ROOT));
    }

    static {
        ImmutableMap.Builder<String, MouseType> builder = ImmutableMap.builder();

        for (MouseType value : MouseType.values()) {
            builder.put(value.name(), value);
        }

        REFERENCE = builder.build();
    }
}
