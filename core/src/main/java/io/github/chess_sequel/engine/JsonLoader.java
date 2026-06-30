package io.github.chess_sequel.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.chess_sequel.engine.jsonTypes.*;
import io.github.chess_sequel.engine.jsonTypes.Dialogue;
import io.github.chess_sequel.engine.jsonTypes.DialogueChoice;
import io.github.chess_sequel.engine.jsonTypes.DialogueNode;

import java.util.ArrayList;

public class JsonLoader {

    Json json = new Json();
    ObjectMap<String, ZoneData> zones = new ObjectMap<>();

    private int mapSizeX, mapSizeY;
    private int combatSizeX, combatSizeY;

    public JsonLoader() {}

    public void loadZoneData() {
        json.setElementType(ZoneData.class, "variants", ZoneVariant.class);
        json.setElementType(ZoneData.class, "enemies", EnemyData.class);
        json.setElementType(ZoneData.class, "shops", ShopData.class);
        json.setElementType(ZoneVariant.class, "nodes", MapNode.class);
        json.setElementType(EnemyData.class, "enemyLayout", PiecePlacement.class);
        json.setElementType(Rewards.class, "portals", String.class);
        json.setElementType(Rewards.class, "portalLocations", Coordinates.class);
        json.setElementType(ShopData.class, "shopLayout", IndividualWare.class);
        json.setElementType(Dialogue.class, "nodes", DialogueNode.class);
        json.setElementType(DialogueNode.class, "lines", String.class);
        json.setElementType(DialogueNode.class, "choices", DialogueChoice.class);

        ArrayList<ZoneData> zoneList = json.fromJson(ArrayList.class, ZoneData.class, Gdx.files.internal("jsons/zoneData.json"));
        for (ZoneData zone : zoneList) {
            zones.put(zone.mapName, zone);
        }
    }

    public ZoneData getZone(String map) {
        return zones.get(map);
    }

    public void setMapSizeXY(String map) {
        this.mapSizeX = zones.get(map).mapX;
        this.mapSizeY = zones.get(map).mapY;
        this.combatSizeX = zones.get(map).combatX;
        this.combatSizeY = zones.get(map).combatY;
    }

    public int getMapSizeX() { return mapSizeX; }
    public int getMapSizeY() { return mapSizeY; }
    public int getCombatSizeX() { return combatSizeX; }
    public int getCombatSizeY() { return combatSizeY; }

    public EnemyData getEnemyData(String map, String ref) {
        ZoneData zone = zones.get(map);

        if (ref.equals("random")) {
            ArrayList<EnemyData> pool = new ArrayList<>();
            for (EnemyData e : zone.enemies) {
                if (!e.fixed) pool.add(e);
            }
            return pool.get((int) (Math.random() * pool.size()));
        }

        for (EnemyData e : zone.enemies) {
            if (e.enemyId.equals(ref)) return e;
        }
        return null;
    }

    public ShopData getShopData(String map, String ref) {
        ZoneData zone = zones.get(map);

        if (ref.equals("random")) {
            ArrayList<ShopData> pool = new ArrayList<>();
            for (ShopData s : zone.shops) {
                if (!s.fixed) pool.add(s);
            }
            return pool.get((int) (Math.random() * pool.size()));
        }

        for (ShopData s : zone.shops) {
            if (s.shopId.equals(ref)) return s;
        }
        return null;
    }
}
