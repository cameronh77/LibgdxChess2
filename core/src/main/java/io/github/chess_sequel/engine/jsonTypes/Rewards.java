package io.github.chess_sequel.engine.jsonTypes;

import java.util.ArrayList;

public class Rewards {
    public ArrayList<String> portals;
    public ArrayList<Coordinates> portalLocations;
    public Integer currency;
    /** Power IDs offered as a choice on victory — player picks one, e.g. ["power-bouncing-bishops", "power-win-bonus"]. */
    public ArrayList<String> powerChoices;
}
