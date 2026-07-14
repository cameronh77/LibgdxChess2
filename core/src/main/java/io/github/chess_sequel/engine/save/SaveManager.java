package io.github.chess_sequel.engine.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class SaveManager {

    private static final String FILE = "chess-sequel-save.json";

    public static PersistentData load() {
        FileHandle file = Gdx.files.local(FILE);
        if (!file.exists()) return new PersistentData();
        try {
            return new Json().fromJson(PersistentData.class, file);
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Failed to load save, starting fresh: " + e.getMessage());
            return new PersistentData();
        }
    }

    public static void save(PersistentData data) {
        try {
            Json json = new Json();
            json.setUsePrototypes(false);
            Gdx.files.local(FILE).writeString(json.prettyPrint(data), false);
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Failed to write save: " + e.getMessage());
        }
    }
}
