package io.github.chess_sequel.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.chess_sequel.engine.jsonTypes.*;
import io.github.chess_sequel.engine.jsonTypes.Dialogue;
import io.github.chess_sequel.engine.jsonTypes.DialogueChoice;
import io.github.chess_sequel.engine.jsonTypes.DialogueNode;

import java.util.ArrayList;

/**
 * Deserialises {@code jsons/zoneData.json}, {@code jsons/sectionData.json}, and
 * {@code jsons/roomData.json}. Provides lookup methods for zone, enemy, shop, section,
 * and room-type data. Virtual zone entries can be registered at runtime for procedurally
 * generated rooms so that existing MapBoard enemy-lookup code works unchanged.
 */
public class JsonLoader {

    Json json = new Json();
    ObjectMap<String, ZoneData> zones = new ObjectMap<>();
    ObjectMap<String, SectionConfig> sectionConfigs = new ObjectMap<>();
    ObjectMap<String, RoomTypeData> roomDataMap = new ObjectMap<>();
    ObjectMap<String, NpcQuestDef> questDefs = new ObjectMap<>();

    private int mapSizeX, mapSizeY;
    private int combatSizeX, combatSizeY;

    public JsonLoader() {}

    /** Parses {@code jsons/zoneData.json} and indexes all zones by {@code mapName}. Must be called once before any lookups. */
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

    /** Caches the map and combat board dimensions for the given zone so callers can retrieve them cheaply. */
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

    /**
     * Returns the {@link EnemyData} for the given zone and ref ID.
     * Pass {@code "random"} as {@code ref} to pick a non-fixed enemy at random.
     */
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

    /** Parses {@code jsons/sectionData.json} and indexes all section configs by {@code sectionId}. */
    public void loadSectionData() {
        Json sectionJson = new Json();
        sectionJson.setElementType(SectionConfig.class, "guaranteedRooms", GuaranteedRoomConfig.class);
        sectionJson.setElementType(SectionConfig.class, "roomPool", RoomPoolEntry.class);

        ArrayList<SectionConfig> list = sectionJson.fromJson(ArrayList.class, SectionConfig.class,
                Gdx.files.internal("jsons/sectionData.json"));
        for (SectionConfig cfg : list) {
            sectionConfigs.put(cfg.sectionId, cfg);
        }
    }

    /** Parses {@code jsons/roomData.json} and indexes all room-type data by {@code "sectionId-roomType"}. */
    public void loadRoomData() {
        Json roomJson = new Json();
        roomJson.setElementType(RoomTypeData.class, "variants", RoomVariant.class);
        roomJson.setElementType(RoomTypeData.class, "enemies", EnemyData.class);
        roomJson.setElementType(RoomVariant.class, "nodes", MapNode.class);
        roomJson.setElementType(EnemyData.class, "enemyLayout", PiecePlacement.class);
        roomJson.setElementType(Dialogue.class, "nodes", DialogueNode.class);
        roomJson.setElementType(DialogueNode.class, "lines", String.class);
        roomJson.setElementType(DialogueNode.class, "choices", DialogueChoice.class);
        roomJson.setElementType(Rewards.class, "portals", String.class);
        roomJson.setElementType(Rewards.class, "portalLocations", Coordinates.class);

        ArrayList<RoomTypeData> list = roomJson.fromJson(ArrayList.class, RoomTypeData.class,
                Gdx.files.internal("jsons/roomData.json"));
        for (RoomTypeData rd : list) {
            roomDataMap.put(rd.sectionId + "-" + rd.roomType, rd);
        }
    }

    /**
     * Registers a virtual {@link ZoneData} entry so that MapBoard enemy-lookup calls
     * ({@code getEnemyData(virtualId, ref)}) work for procedurally generated section rooms.
     */
    public void registerRoomData(String virtualId, int mapX, int mapY, int combatX, int combatY,
                                 ArrayList<EnemyData> enemies) {
        ZoneData fake = new ZoneData();
        fake.mapName = virtualId;
        fake.mapX = mapX;
        fake.mapY = mapY;
        fake.combatX = combatX;
        fake.combatY = combatY;
        fake.enemies = enemies != null ? enemies : new ArrayList<>();
        fake.variants = new ArrayList<>();
        fake.shops = new ArrayList<>();
        zones.put(virtualId, fake);
    }

    public SectionConfig getSectionConfig(String sectionId) {
        return sectionConfigs.get(sectionId);
    }

    public RoomTypeData getRoomTypeData(String sectionId, String roomType) {
        return roomDataMap.get(sectionId + "-" + roomType);
    }

    /** Parses {@code jsons/questData.json} and indexes all NPC quest definitions by {@code npcId}. */
    public void loadQuestData() {
        Json questJson = new Json();
        questJson.setElementType(NpcQuestDef.class, "steps", QuestStep.class);
        questJson.setElementType(Dialogue.class, "nodes", DialogueNode.class);
        questJson.setElementType(DialogueNode.class, "lines", String.class);
        questJson.setElementType(DialogueNode.class, "choices", DialogueChoice.class);
        questJson.setElementType(Rewards.class, "portals", String.class);
        questJson.setElementType(Rewards.class, "portalLocations", Coordinates.class);
        questJson.setElementType(Rewards.class, "powerChoices", String.class);

        ArrayList<NpcQuestDef> list = questJson.fromJson(ArrayList.class, NpcQuestDef.class,
                Gdx.files.internal("jsons/questData.json"));
        for (NpcQuestDef def : list) {
            questDefs.put(def.npcId, def);
        }
    }

    public NpcQuestDef getQuestDef(String npcId) {
        return questDefs.get(npcId);
    }

    /**
     * Returns the {@link ShopData} for the given zone and ref ID.
     * Pass {@code "random"} as {@code ref} to pick a non-fixed shop at random.
     */
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
