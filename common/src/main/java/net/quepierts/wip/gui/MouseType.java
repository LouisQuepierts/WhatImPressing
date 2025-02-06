package net.quepierts.wip.gui;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.quepierts.urbaneui.DisplayableType;

import java.util.Locale;
import java.util.Map;

@Getter
public enum MouseType implements DisplayableType {
    LEFT("key.mouse.left"),
    RIGHT("key.mouse.right"),
    MIDDLE("key.mouse.middle");

    private static final Component TYPE_NAME = Component.translatable("enums.wip.mouse_type");
    private static final Map<String, MouseType> REFERENCE;
    
    private final String key;
    private final Component displayName;

    MouseType(String key) {
        this.key = key;
        this.displayName = Component.translatable(key);
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

    @Override
    public Component getTypeDisplayName() {
        return TYPE_NAME;
    }
}
