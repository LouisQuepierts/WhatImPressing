package net.quepierts.wip.listener;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.network.chat.Component;

@Getter
public abstract class KeyListener {
    public static Codec<KeyListener> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            KeyType.CODEC.fieldOf("type").forGetter(KeyListener::getType),
            Codec.STRING.fieldOf("key").forGetter(KeyListener::getKey)
    ).apply(instance, KeyListener::getInstance));

    private final KeyType type;
    private final String key;

    protected boolean active = true;
    protected boolean pressed = false;

    public static KeyListener getInstance(KeyType type, String key) {
        return switch (type) {
            case INPUT -> new InputListener(key);
            case MOUSE -> new MouseListener(key);
            case KEYMAPPING -> new KeymappingListener(key);
        };
    }

    public abstract void handle(int key, int action);

    public abstract Component getDisplayName();

    protected KeyListener(KeyType type, String key) {
        this.type = type;
        this.key = key;
    }

}
