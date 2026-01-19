package io.github.chess_sequel.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.chess_sequel.engine.jsonTypes.*;

import java.util.ArrayList;
import java.util.Map;

public class JsonLoader {

    Json json = new Json();
    ObjectMap<String, MapData> currentMap;
    ObjectMap<String, MapEnemies> currentEnemies;
    ObjectMap<String, MapShops> currentShops;

    private int mapSizeX, mapSizeY;

    public JsonLoader() {

    }

    public String loadMapData(){
        Json json = new Json();
        ArrayList<MapData> maps = json.fromJson(ArrayList.class, MapData.class, Gdx.files.internal("jsons/mapData.json"));

        ObjectMap<String, MapData> mapByName = new ObjectMap<>();

        for (MapData map : maps) {
            mapByName.put(map.mapName, map);
        }

        currentMap = mapByName;

        return null;
    }

    public String loadEnemyData(){
        Json json = new Json();
        ArrayList<MapEnemies> enemies = json.fromJson(ArrayList.class, MapEnemies.class, Gdx.files.internal("jsons/enemydata.json"));

        ObjectMap<String, MapEnemies> enemyByMap = new ObjectMap<>();

        for (MapEnemies enemy : enemies) {
            enemyByMap.put(enemy.mapName, enemy);
        }

        currentEnemies = enemyByMap;

        return null;
    }

    public String loadShopData(){
        Json json = new Json();
        ArrayList<MapShops> shops = json.fromJson(ArrayList.class, MapShops.class, Gdx.files.internal("jsons/shopdata.json"));

        ObjectMap<String, MapShops> shopByMap = new ObjectMap<>();

        for (MapShops shop:shops){
            shopByMap.put(shop.mapName, shop);
        }

        currentShops = shopByMap;

        return null;
    }

    public MapData getMapData(String map){
        return currentMap.get(map);
    }
    public void setMapSizeXY(String map){
        this.mapSizeX = currentMap.get(map).mapX;
        this.mapSizeY = currentMap.get(map).mapY;
    };

    public int getMapSizeX(){
        return mapSizeX;
    }

    public int getMapSizeY(){
        return mapSizeY;
    }

    public EnemyData getEnemyData(String map, String enemy){

        String chosenEnemy = enemy;

        if(enemy.equals("random")){
            chosenEnemy = currentEnemies.get(map).randomEnemies.get((int) (Math.random() * currentEnemies.get(map).randomEnemies.size()));
        }

        ArrayList<EnemyData> enemyData = currentEnemies.get(map).enemies;

        for(EnemyData enemyCheck: enemyData){
            if (enemyCheck.enemyId.equals(chosenEnemy)){
                return enemyCheck;
            }
        }

        return null;
    }

    public ShopData getShopData(String map, String shop){
        String chosenShop = shop;

        if(shop.equals("random")){
            chosenShop = currentShops.get(map).randomShops.get((int) (Math.random() * currentShops.get(map).randomShops.size()));
        }

        ArrayList<ShopData> shopData = currentShops.get(map).shops;

        for(ShopData shopCheck: shopData){
            if (shopCheck.shopId.equals(chosenShop)){
                return shopCheck;
            }
        }

        return null;
    }



}


