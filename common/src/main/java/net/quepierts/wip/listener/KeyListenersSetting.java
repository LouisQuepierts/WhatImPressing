package net.quepierts.wip.listener;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.quepierts.wip.CommonClass;
import net.quepierts.wip.gui.LayoutMode;
import net.quepierts.wip.gui.widget.KeyListenerSection;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Setter
@Getter
public class KeyListenersSetting {
    public static final Codec<KeyListenersSetting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            KeyListenerSection.CODEC.listOf().fieldOf("listeners").forGetter(KeyListenersSetting::getListeners),
            LayoutMode.CODEC.fieldOf("horizontalLayout").forGetter(KeyListenersSetting::getHorizontalLayout),
            LayoutMode.CODEC.fieldOf("verticalLayout").forGetter(KeyListenersSetting::getVerticalLayout)
    ).apply(instance, KeyListenersSetting::new));

    private static final String PATH = "config/whatimpressing.json";
    private static final Logger LOGGER = LogUtils.getLogger();

    private List<KeyListenerSection> listeners;
    private LayoutMode horizontalLayout;
    private LayoutMode verticalLayout;

    public static KeyListenersSetting load() {
        return fromFile(PATH);
    }

    public static void save(KeyListenersSetting setting) {
        toFile(setting, PATH);
    }

    public static void toFile(KeyListenersSetting setting, String path) {
        try (FileWriter writer = new FileWriter(path)) {
            DataResult<JsonElement> encode = CODEC.encode(setting, JsonOps.INSTANCE, new JsonObject());
            JsonElement element = encode.getOrThrow();
            writer.write(element.toString());
        } catch (IOException e) {
            LOGGER.warn("Cannot write key listener setting from {}", path, e);
        }
    }

    public static KeyListenersSetting fromFile(String path) {
        File file = new File(path);

        if (file.exists()) {
            try (FileReader reader = new FileReader(path)) {
                JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
                DataResult<Pair<KeyListenersSetting, JsonElement>> decode = CODEC.decode(JsonOps.INSTANCE, object);
                return decode.getOrThrow().getFirst();
            } catch (IOException e) {
                LOGGER.warn("Cannot read key listener setting from {}", path, e);
            }
        }

        KeyListenersSetting setting = new KeyListenersSetting(CommonClass.KEY_LISTENER_SECTIONS, LayoutMode.LEFT, LayoutMode.LEFT);
        toFile(setting, path);
        return setting;
    }
}
