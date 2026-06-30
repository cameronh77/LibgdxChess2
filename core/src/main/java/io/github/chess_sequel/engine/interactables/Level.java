package io.github.chess_sequel.engine.interactables;

/**
 * Enum of available zone identifiers used by {@link LevelPortal}.
 * Each value maps to a {@code mapName} key in {@code zoneData.json}.
 */
public enum Level
{
    CLASSIC4("classic4"), CLASSIC6("classic6"), CLASSIC8("classic8");

    // declaring private variable for getting values
    private String level;

    // getter method
    public String getLevel()
    {
        return this.level;
    }

    // enum constructor - cannot be public or protected
    private Level(String level)
    {
        this.level = level;
    }
}
