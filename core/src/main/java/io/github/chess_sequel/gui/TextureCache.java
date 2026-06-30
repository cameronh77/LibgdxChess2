package io.github.chess_sequel.gui;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

/** Lazy singleton texture cache. Loads each asset path once and returns the cached instance on subsequent calls. */
public class TextureCache {
    private static final Map<String, Texture> map = new HashMap<>();

    public static Texture get(String path) {
        return map.computeIfAbsent(path, Texture::new);
    }
}

