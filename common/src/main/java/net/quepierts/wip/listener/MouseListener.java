package net.quepierts.wip.listener;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.quepierts.wip.gui.MouseType;

public class MouseListener extends KeyListener {
    private final InputConstants.Key key;

    public MouseListener(MouseType type) {
        super(KeyType.MOUSE, type.name().toLowerCase());
        this.key = InputConstants.getKey(type.getKey());
    }

    public MouseListener(String key) {
        super(KeyType.MOUSE, key);

        switch (key.toLowerCase()) {
            case "left": case "key.mouse.left":
                this.key = InputConstants.getKey("key.mouse.left");
                break;
            case "right": case "key.mouse.right":
                this.key = InputConstants.getKey("key.mouse.right");
                break;
            case "middle": case "key.mouse.middle":
                this.key = InputConstants.getKey("key.mouse.middle");
                break;
            default:
                this.key = InputConstants.UNKNOWN;
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
}