package net.quepierts.urbaneui;

import net.minecraft.network.chat.Component;

public interface DisplayableType {
    Component getTypeDisplayName();

    Component getDisplayName();
}
