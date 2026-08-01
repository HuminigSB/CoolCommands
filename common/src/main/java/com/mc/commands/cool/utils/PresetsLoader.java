package com.mc.commands.cool.utils;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.Gson;
import com.mc.commands.cool.models.presets.PresetJson;
import com.mc.commands.cool.models.presets.PresetMob;

public class PresetsLoader {
    private static PresetsLoader instance;
    private List<PresetMob> presetMobs;

    private PresetsLoader() {};

    public static PresetsLoader getInstance() {
        if (instance == null) {
            instance = new PresetsLoader();
        }
        return instance;
    }

    public List<PresetMob> loadPresetMobs() {
        Gson gson = new Gson();
        try (Reader reader = Files.newBufferedReader(Paths.get("config/presets.json"))) {
            PresetJson presets = gson.fromJson(reader, PresetJson.class);
            this.presetMobs = presets.mobs;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.presetMobs;
    }

    public PresetMob getRandomPresetMob() {
        return this.presetMobs.get(new java.util.Random().nextInt(this.presetMobs.size()));
    }
    
    public PresetMob getPresetMobByIndex(int index) {
        if (index >= 0 && index < this.presetMobs.size()) {
            return this.presetMobs.get(index);
        }
        return null;
    }
}
