package net.quepierts.wip;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.InputConstants;
import lombok.Getter;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.quepierts.wip.gui.KeystrokesEditorScreen;
import net.quepierts.wip.gui.widget.KeyListenerSection;
import net.quepierts.wip.listener.InputListener;
import net.quepierts.wip.listener.KeyListenersSetting;
import net.quepierts.wip.platform.Services;
import org.apache.logging.log4j.util.Lazy;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class CommonClass {
    public final static List<KeyListenerSection> KEY_LISTENER_SECTIONS = new ArrayList<>();
    public static final Lazy<KeyMapping> KEY_OPEN_EDITOR;

    @Getter
    private static KeyListenersSetting setting;

    @Getter
    private static ImmutableMap<String, KeyMapping> keyMappings;

    public static void init() {
        Constants.LOG.info("Hello from Common init on {}! we are currently in a {} environment!", Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());

        if (Services.PLATFORM.isModLoaded("wip")) {
            Constants.LOG.info("Hello to WhatI'mPressing");
        }
    }

    public static KeyMapping getKeyMappings(String key) {
        return keyMappings.get(key);
    }

    public static void onClientSetup() {
        KeyMapping[] keyMappings = Minecraft.getInstance().options.keyMappings;
        ImmutableMap.Builder<String, KeyMapping> builder = ImmutableMap.builder();
        for (KeyMapping mapping : keyMappings) {
            builder.put(mapping.getName(), mapping);
        }
        CommonClass.keyMappings = builder.build();

        KEY_LISTENER_SECTIONS.add(new KeyListenerSection(
                new InputListener("key.keyboard.w"), 32, 10, 20, 20
        ));
        KEY_LISTENER_SECTIONS.add(new KeyListenerSection(
                new InputListener("key.keyboard.a"), 10, 32, 20, 20
        ));
        KEY_LISTENER_SECTIONS.add(new KeyListenerSection(
                new InputListener("key.keyboard.s"), 32, 32, 20, 20
        ));
        KEY_LISTENER_SECTIONS.add(new KeyListenerSection(
                new InputListener("key.keyboard.d"), 54, 32, 20, 20
        ));

        setting = KeyListenersSetting.load();
    }


    public static void handleInput(int key, int action) {
        if (Minecraft.getInstance().level == null) {
            return;
        }

        if (Minecraft.getInstance().screen == null && CommonClass.KEY_OPEN_EDITOR.get().isDown()) {
            Minecraft.getInstance().setScreen(new KeystrokesEditorScreen());
            return;
        }

        if (action == 2) {
            return;
        }

        for (KeyListenerSection section : CommonClass.getSetting().getListeners()) {
            section.getListener().handle(key, action);
        }
    }

    static {
        KEY_OPEN_EDITOR = Lazy.lazy(
            () -> new KeyMapping(
                    "key.wip.open_editor",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_RIGHT_BRACKET,
                    "key.categories.wip"
            )
        );
    }
}
