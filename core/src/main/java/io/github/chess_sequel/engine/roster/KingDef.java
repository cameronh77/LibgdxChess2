package io.github.chess_sequel.engine.roster;

import java.util.List;

public class KingDef {

    public final String displayName;
    public final String iconPath;
    public final List<TeamPreset> presets;

    public KingDef(String displayName, String iconPath, List<TeamPreset> presets) {
        this.displayName = displayName;
        this.iconPath = iconPath;
        this.presets = presets;
    }
}
