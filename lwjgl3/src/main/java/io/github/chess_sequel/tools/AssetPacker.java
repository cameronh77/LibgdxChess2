package io.github.chess_sequel.tools;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class AssetPacker {
    public static void main(String[] args) {
        TexturePacker.process(
            "raw-ui",
            "assets/ui",
            "ui"
        );
    }
}
