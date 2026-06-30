package io.github.chess_sequel.engine.roster;

import java.util.List;

/**
 * Defines a playable king archetype: its display name, icon texture path, and the list of
 * selectable {@link TeamPreset}s that the player can choose from on the character select screen.
 */
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
