package net.quepierts.wip.listener;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;

public class InputListener extends KeyListener {
    private final InputConstants.Key key;

    public InputListener(String key) {
        super(KeyType.INPUT, key);

        this.key = getKey(key);
        if (this.key == null) {
            this.active = false;
        }
    }

    @Override
    public void handle(int key, int action) {
        if (key != this.key.getValue()) {
            return;
        }

        this.pressed = action == 1;
    }

    @Override
    public Component getDisplayName() {
        return this.key.getDisplayName();
    }

    private static InputConstants.Key getKey(String key) {
        try {
            return InputConstants.getKey(key);
        } catch (IllegalArgumentException ignored) {}
        return InputConstants.UNKNOWN;
    }
}
