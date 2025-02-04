package net.quepierts.wip.listener;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.quepierts.wip.CommonClass;

public class KeymappingListener extends KeyListener {
    private final KeyMapping mapping;

    public KeymappingListener(String key) {
        super(KeyType.KEYMAPPING, key);

        this.mapping = CommonClass.getKeyMappings(key);
        if (this.mapping == null) {
            this.active = false;
        }
    }

    @Override
    public void handle(int key, int action) {
        this.pressed = this.mapping.isDown();
    }

    @Override
    public Component getDisplayName() {
        return this.mapping.getTranslatedKeyMessage();
    }
}
