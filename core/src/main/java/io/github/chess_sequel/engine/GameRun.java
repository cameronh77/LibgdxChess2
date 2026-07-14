package io.github.chess_sequel.engine;

import io.github.chess_sequel.engine.interactables.BombItem;
import io.github.chess_sequel.engine.interactables.LevelPortal;
import io.github.chess_sequel.engine.interactables.NPCPiece;
import io.github.chess_sequel.engine.interactables.RoomDoor;
import io.github.chess_sequel.engine.interactables.ShopEffect;
import io.github.chess_sequel.engine.interactables.ShopItem;
import io.github.chess_sequel.engine.pieces.factories.KingPowerFactory;
import io.github.chess_sequel.engine.jsonTypes.Coordinates;
import io.github.chess_sequel.engine.jsonTypes.Dialogue;
import io.github.chess_sequel.engine.jsonTypes.DialogueChoice;
import io.github.chess_sequel.engine.jsonTypes.DialogueNode;
import io.github.chess_sequel.engine.jsonTypes.EnemyData;
import io.github.chess_sequel.engine.jsonTypes.Rewards;
import io.github.chess_sequel.engine.jsonTypes.RoomTypeData;
import io.github.chess_sequel.engine.jsonTypes.RoomVariant;
import io.github.chess_sequel.engine.jsonTypes.SectionConfig;
import io.github.chess_sequel.engine.jsonTypes.ZoneData;
import io.github.chess_sequel.engine.jsonTypes.ZoneVariant;
import io.github.chess_sequel.engine.location.board.*;
import io.github.chess_sequel.engine.player.BotPlayer;
import io.github.chess_sequel.engine.player.Player;
import io.github.chess_sequel.engine.pieces.classic.King;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;
import io.github.chess_sequel.engine.hub.HubLayout;
import io.github.chess_sequel.engine.hub.HubRoom;
import io.github.chess_sequel.engine.interactables.HubDoor;
import io.github.chess_sequel.engine.interactables.SectionPortal;
import io.github.chess_sequel.engine.interactables.Throne;
import io.github.chess_sequel.engine.jsonTypes.NpcQuestDef;
import io.github.chess_sequel.engine.jsonTypes.QuestCondition;
import io.github.chess_sequel.engine.jsonTypes.QuestStep;
import io.github.chess_sequel.engine.save.PersistentData;
import io.github.chess_sequel.engine.save.NpcQuestState;
import io.github.chess_sequel.engine.save.SaveManager;
import io.github.chess_sequel.engine.section.Direction;
import io.github.chess_sequel.engine.section.RoomNode;
import io.github.chess_sequel.engine.section.SectionLayout;
import io.github.chess_sequel.engine.section.SectionLayoutGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * Central controller for a single game run. Manages the board stack (map navigation,
 * combat, layout editing), dialogue state, and pending rewards. The active board is
 * always at the top of {@code gameBoards}.
 */
public class GameRun {

    private Stack<Board> gameBoards = new Stack<>();
    private Stack<String> mapStack = new Stack<>();
    private Stack<int[]> returnPositions = new Stack<>();
    private HashMap<String, MapBoard> cachedMapBoards = new HashMap<>();
    private HashMap<String, ZoneVariant> cachedVariants = new HashMap<>();
    private HashMap<String, int[]> cachedKingPositions = new HashMap<>();
    private Player player;
    private JsonLoader jsonLoader = new JsonLoader();
    private String currentMap;
    private GameState gameState = GameState.NEUTRAL;
    private SectionLayout activeSectionLayout;
    private HubLayout hubLayout;
    private PersistentData persistentData;
    private Rewards pendingRewards = null;
    private Rewards pendingDisplayReward = null;

    private List<ShopEffect> pendingPowerOffer = null;
    private ShopItem pendingShopItem = null;
    private BombItem pendingBomb = null;

    private Dialogue activeDialogue;
    private DialogueNode currentNode;
    private int currentLineIndex;
    private NPCPiece dialogueNPC;

    /** Initialises the run, loads all data, and enters the strategy section. */
    public GameRun(Player player){
        this.player = player;
        persistentData = SaveManager.load();
        jsonLoader.loadZoneData();
        jsonLoader.loadSectionData();
        jsonLoader.loadRoomData();
        jsonLoader.loadQuestData();
        enterHub();
    }

    /** Pushes a new {@link io.github.chess_sequel.engine.location.board.MatchBoard} for combat against {@code opponent}. */
    public void addMatchBoard(BotPlayer opponent){
        System.out.println("Adding match board");
        player.setLeadPieceX(player.getLeadPiece().getCol());
        player.setLeadPieceY(player.getLeadPiece().getRow());
        gameBoards.push(new MatchBoard(jsonLoader.getCombatSizeX(), jsonLoader.getCombatSizeY(), player, opponent));
        gameState = GameState.BOARD_STATE_CHANGED;
    }

    /** Applies all reward effects to the current map (portals, currency, PreKingPower bonuses, power choices). */
    public void handleRewards(Rewards rewards){
        MapBoard mapBoard = (MapBoard) getCurrentBoard();
        if(rewards.portals != null){
            for(int p = 0; p<rewards.portals.size();p++){
                mapBoard.addLocation(new LevelPortal(rewards.portals.get(p), this, rewards.portalLocations.get(p).x, rewards.portalLocations.get(p).y));
            }
        }
        if(rewards.currency != null){
            player.incrementCurrency(rewards.currency);
        }
        King king = player.getKing();
        if (king != null) {
            for (PreKingPower power : king.getPreGamePowers()) {
                power.onVictory(player);
            }
        }
        if (rewards.items != null) {
            for (String ref : rewards.items) {
                io.github.chess_sequel.engine.interactables.ConsumableItemEffect e =
                    io.github.chess_sequel.engine.pieces.factories.ShopFactory.consumableEffectFromRef(ref);
                if (e != null) e.apply(player);
            }
        }
        if (rewards.powerChoices != null && !rewards.powerChoices.isEmpty()) {
            for (String id : rewards.powerChoices) {
                ShopEffect e = KingPowerFactory.createEffect(id);
                if (e != null) e.apply(player);
            }
        }
    }

    public void setPendingPowerOffer(List<ShopEffect> offers) {
        this.pendingPowerOffer = offers;
        setGameState(GameState.BOARD_STATE_CHANGED);
    }

    public boolean hasPendingPowerOffer() { return pendingPowerOffer != null && !pendingPowerOffer.isEmpty(); }
    public List<ShopEffect> getPendingPowerOffer() { return pendingPowerOffer; }

    public void setPendingShopItem(ShopItem item) {
        this.pendingShopItem = item;
        setGameState(GameState.BOARD_STATE_CHANGED);
    }

    public boolean hasPendingShopItem() { return pendingShopItem != null; }
    public ShopItem getPendingShopItem() { return pendingShopItem; }

    public void purchaseShopItem() {
        ShopItem item = pendingShopItem;
        pendingShopItem = null;
        if (item != null) item.purchase();
        setGameState(GameState.BOARD_STATE_CHANGED);
    }

    public void dismissShopItem() {
        pendingShopItem = null;
        setGameState(GameState.BOARD_STATE_CHANGED);
    }

    public void setPendingBomb(BombItem bomb) {
        this.pendingBomb = bomb;
        setGameState(GameState.BOARD_STATE_CHANGED);
    }

    public boolean hasPendingBomb() { return pendingBomb != null; }
    public BombItem getPendingBomb() { return pendingBomb; }

    public void cancelBomb() {
        pendingBomb = null;
        setGameState(GameState.BOARD_STATE_CHANGED);
    }

    public void selectPowerOffer(ShopEffect effect) {
        effect.apply(player);
        pendingPowerOffer = null;
        setGameState(GameState.BOARD_STATE_CHANGED);
    }

    /** Pops the current board and replaces it with the named map — used by {@link io.github.chess_sequel.engine.interactables.LevelPortal}. */
    public void progressGame(String level){
        gameBoards.clear();
        mapStack.clear();
        returnPositions.clear();
        this.currentMap = level;
        jsonLoader.setMapSizeXY(currentMap);
        addMapBoard();
        gameState = GameState.BOARD_STATE_CHANGED;
    }

    /**
     * Pushes a {@link MapBoard} for {@code currentMap}. Re-uses the cached instance if the
     * map was visited before, re-placing the king at the zone's start position.
     */
    public void addMapBoard(){
        if (cachedMapBoards.containsKey(currentMap)) {
            MapBoard cached = cachedMapBoards.get(currentMap);
            ZoneVariant variant = cachedVariants.get(currentMap);
            int entryX = variant.playerStart.x;
            int entryY = variant.playerStart.y;
            // Clear king from wherever they were on this map when they last left
            int[] lastPos = cachedKingPositions.get(currentMap);
            if (lastPos != null) {
                cached.getTiles().get(lastPos[0]).get(lastPos[1]).setPiece(null);
            }
            player.setLeadPieceX(entryX);
            player.setLeadPieceY(entryY);
            player.getLeadPiece().setCol(entryX);
            player.getLeadPiece().setRow(entryY);
            cached.getTiles().get(entryX).get(entryY).setPiece(player.getLeadPiece());
            gameBoards.push(cached);
        } else {
            ZoneData zone = jsonLoader.getZone(currentMap);
            int randomIndex = (int)(Math.random() * zone.variants.size());
            ZoneVariant variant = zone.variants.get(randomIndex);
            player.setLeadPieceX(variant.playerStart.x);
            player.setLeadPieceY(variant.playerStart.y);
            MapBoard newBoard = new MapBoard(this, zone.mapX, zone.mapY, player, variant);
            cachedMapBoards.put(currentMap, newBoard);
            cachedVariants.put(currentMap, variant);
            gameBoards.push(newBoard);
        }
    }

    /** Navigates into a sub-zone by name, saving the king's current position so it can be restored on {@link #popMap()}. */
    public void pushMap(String zoneName) {
        returnPositions.push(new int[]{player.getLeadPiece().getCol(), player.getLeadPiece().getRow()});
        mapStack.push(currentMap);
        currentMap = zoneName;
        jsonLoader.setMapSizeXY(currentMap);
        addMapBoard();
        gameState = GameState.BOARD_STATE_CHANGED;
    }

    /** Returns from a sub-zone, restoring the king's position on the parent map. */
    public void popMap() {
        // Remember where king was on this sub-map so the tile can be cleared on re-entry
        cachedKingPositions.put(currentMap, new int[]{player.getLeadPiece().getCol(), player.getLeadPiece().getRow()});
        gameBoards.pop();
        if (!mapStack.isEmpty()) {
            currentMap = mapStack.pop();
            jsonLoader.setMapSizeXY(currentMap);
        }
        if (!returnPositions.isEmpty()) {
            int[] pos = returnPositions.pop();
            player.getLeadPiece().setCol(pos[0]);
            player.getLeadPiece().setRow(pos[1]);
            player.setLeadPieceX(pos[0]);
            player.setLeadPieceY(pos[1]);
        }
        gameState = GameState.BOARD_STATE_CHANGED;
    }

    /** Returns the board currently at the top of the stack. */
    public Board getCurrentBoard(){
        return gameBoards.peek();
    }

    public String getCurrentMap() { return currentMap; }

    /** Pops the top board (e.g. after a match ends) and restores the king's pre-match position. */
    public void popBoard(){
        gameBoards.pop();
        int rx = player.getLeadPieceX();
        int ry = player.getLeadPieceY();
        player.getLeadPiece().setCol(rx);
        player.getLeadPiece().setRow(ry);
        Board current = getCurrentBoard();
        io.github.chess_sequel.engine.pieces.Piece tileKing = current.getTiles().get(rx).get(ry).getPiece();
        System.out.println("[POPBOARD] king restored to (" + rx + "," + ry + ") on " + current.getClass().getSimpleName() + ", tile.piece=" + (tileKing == null ? "NULL" : tileKing.getName()) + " whiteToMove=" + current.getWhiteToMove());
        if (current instanceof io.github.chess_sequel.engine.location.board.MapBoard) {
            ((io.github.chess_sequel.engine.location.board.MapBoard) current).cleanupDefeatedNpcs();
        }
        gameState = GameState.BOARD_STATE_CHANGED;
    }

    /** Toggles the {@link io.github.chess_sequel.engine.location.board.AlterLayoutBoard}: pushes one if not active, pops it if already on top. */
    public void alterLayout(){
        if(gameBoards.peek() instanceof AlterLayoutBoard){
            gameBoards.pop();
            if (!returnPositions.isEmpty()) {
                int[] pos = returnPositions.pop();
                player.getLeadPiece().setCol(pos[0]);
                player.getLeadPiece().setRow(pos[1]);
            }
            gameState = GameState.BOARD_STATE_CHANGED;
        }
        else{
            // Push position before AlterLayout corrupts col/row via setStartCords(); don't touch leadPieceX/Y
            // so that popBoard() can still restore the correct pre-shop position on shop exit.
            returnPositions.push(new int[]{player.getLeadPiece().getCol(), player.getLeadPiece().getRow()});
            gameBoards.push(new AlterLayoutBoard(this, jsonLoader.getCombatSizeX(), jsonLoader.getCombatSizeY(), player));
            gameState = GameState.BOARD_STATE_CHANGED;
        }
    }

    public JsonLoader getJsonLoader(){
        return jsonLoader;
    }

    public void setGameState(GameState gameState){
        // Don't overwrite a pending screen transition with a lower-priority state
        if (this.gameState == GameState.GO_TO_KING_SELECTION) return;
        this.gameState = gameState;
    }

    public GameState getGameState(){
        return gameState;
    }

    public Player getPlayer(){
        return player;
    }

    public void setPendingRewards(Rewards rewards) { this.pendingRewards = rewards; }
    public Rewards getPendingRewards() { return pendingRewards; }
    public Rewards consumePendingRewards() {
        Rewards r = pendingRewards;
        pendingRewards = null;
        return r;
    }

    public void applyPreGamePower(PreKingPower power) {
        if (getCurrentBoard() instanceof AlterLayoutBoard) {
            power.apply((AlterLayoutBoard) getCurrentBoard());
        }
    }

    /** Begins a dialogue tree starting at the given node ID, blocking board input until it resolves. */
    public void startDialogue(Dialogue dialogue, String entryNodeId, NPCPiece npc) {
        this.activeDialogue = dialogue;
        this.dialogueNPC = npc;
        enterNode(findNode(dialogue, entryNodeId));
        setGameState(GameState.BOARD_STATE_CHANGED);
    }

    public boolean isDialogueActive() {
        return activeDialogue != null;
    }

    /** Returns the line of text to display for the current dialogue position, or {@code null} if there are none left. */
    public String getCurrentDialogueLine() {
        if (currentNode == null) return null;
        if (currentNode.lines != null && currentLineIndex < currentNode.lines.size()) {
            return currentNode.lines.get(currentLineIndex);
        }
        return null;
    }

    /** Returns the player-selectable choices once all lines in the current node are exhausted, or {@code null} otherwise. */
    public List<DialogueChoice> getCurrentChoices() {
        if (currentNode == null) return null;
        boolean linesExhausted = currentNode.lines == null || currentLineIndex >= currentNode.lines.size();
        if (linesExhausted && currentNode.choices != null && !currentNode.choices.isEmpty()) {
            return currentNode.choices;
        }
        return null;
    }

    /** Advances to the next line in the current node; triggers outcome resolution when lines and choices are exhausted. */
    public void advanceLine() {
        currentLineIndex++;
        if (getCurrentDialogueLine() == null && getCurrentChoices() == null) {
            resolveOutcome(currentNode);
        }
    }

    /** Selects a dialogue choice by index and transitions to its target node. */
    public void selectChoice(int index) {
        DialogueChoice choice = currentNode.choices.get(index);
        DialogueNode next = findNode(activeDialogue, choice.next);
        enterNode(next);
    }

    private void enterNode(DialogueNode node) {
        this.currentNode = node;
        this.currentLineIndex = 0;
        boolean hasLines = node.lines != null && !node.lines.isEmpty();
        boolean hasChoices = node.choices != null && !node.choices.isEmpty();
        if (!hasLines && !hasChoices) {
            resolveOutcome(node);
        }
    }

    /** Applies rewards immediately and queues them for display in the UI. */
    public void showReward(Rewards rewards) {
        handleRewards(rewards);
        pendingDisplayReward = rewards;
        setGameState(GameState.BOARD_STATE_CHANGED);
    }

    public boolean hasPendingDisplayReward() { return pendingDisplayReward != null; }
    public Rewards getPendingDisplayReward() { return pendingDisplayReward; }
    public void dismissDisplayReward() {
        pendingDisplayReward = null;
        setGameState(GameState.BOARD_STATE_CHANGED);
    }

    private void resolveOutcome(DialogueNode node) {
        if (node.setEntry != null && dialogueNPC != null) {
            dialogueNPC.setEntryNode(node.setEntry);
        }
        NPCPiece npc = dialogueNPC;
        activeDialogue = null;
        currentNode = null;
        dialogueNPC = null;

        if ("combat".equals(node.outcome) && npc != null) {
            npc.startCombat();
        } else if ("remove".equals(node.outcome) && npc != null) {
            npc.removeFromMap(getCurrentBoard());
            setGameState(GameState.BOARD_STATE_CHANGED);
        } else if (node.reward != null) {
            showReward(node.reward);
        } else {
            setGameState(GameState.BOARD_STATE_CHANGED);
        }
    }

    private DialogueNode findNode(Dialogue dialogue, String id) {
        for (DialogueNode node : dialogue.nodes) {
            if (node.id.equals(id)) return node;
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Section navigation
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Hub navigation
    // -------------------------------------------------------------------------

    /**
     * Enters the hub, placing the king at the throne's fixed spawn tile.
     * Hub room boards are cached — NPCs and items persist across visits.
     */
    public void enterHub() {
        if (hubLayout == null) hubLayout = HubLayout.create();
        HubRoom throne = hubLayout.spawnRoom;
        hubLayout.currentRoom = throne;
        if (throne.board == null) throne.board = createHubRoomBoard(throne, null);
        else repositionKingOnCachedHubRoom(throne, throne.spawnCol, throne.spawnRow);
        gameBoards.clear();
        gameBoards.push(throne.board);
        gameState = GameState.BOARD_STATE_CHANGED;
    }

    /** Moves the player from the current hub room into the adjacent room in {@code dir}. */
    public void navigateHubRoom(Direction dir) {
        if (hubLayout == null) return;
        HubRoom current = hubLayout.currentRoom;
        if (!current.doors.contains(dir)) return;
        HubRoom next = hubLayout.getRoomInDirection(dir);
        if (next == null) return;

        current.kingExitCol = player.getLeadPiece().getCol();
        current.kingExitRow = player.getLeadPiece().getRow();
        gameBoards.pop();

        hubLayout.currentRoom = next;
        Direction entryFrom = dir.opposite();
        int[] entry = entryPosition(entryFrom, next.mapX, next.mapY);

        if (next.board == null) {
            next.board = createHubRoomBoard(next, entryFrom);
        } else {
            repositionKingOnCachedHubRoom(next, entry[0], entry[1]);
        }
        gameBoards.push(next.board);
        gameState = GameState.BOARD_STATE_CHANGED;
    }

    private MapBoard createHubRoomBoard(HubRoom room, Direction entryFrom) {
        String virtualId = "hub-" + room.id;
        jsonLoader.registerRoomData(virtualId, room.mapX, room.mapY, 8, 8, new ArrayList<>());
        currentMap = virtualId;
        jsonLoader.setMapSizeXY(currentMap);

        int entryX, entryY;
        if (entryFrom == null && room.spawnCol >= 0) {
            entryX = room.spawnCol;
            entryY = room.spawnRow;
        } else {
            int[] entry = entryPosition(entryFrom, room.mapX, room.mapY);
            entryX = entry[0];
            entryY = entry[1];
        }
        player.setLeadPieceX(entryX);
        player.setLeadPieceY(entryY);

        ZoneVariant zoneVariant = new ZoneVariant();
        zoneVariant.playerStart = new Coordinates();
        zoneVariant.playerStart.x = entryX;
        zoneVariant.playerStart.y = entryY;
        zoneVariant.nodes = new ArrayList<>();

        MapBoard board = new MapBoard(this, room.mapX, room.mapY, player, zoneVariant);

        for (Direction dir : room.doors) {
            board.addLocation(new HubDoor(this, doorCol(dir, room.mapX, room.mapY),
                    doorRow(dir, room.mapX, room.mapY), dir));
        }

        if (room.portalSectionId != null) {
            board.addLocation(new SectionPortal(this, room.mapX / 2, room.mapY / 2, room.portalSectionId));
        }

        if ("throne".equals(room.id)) {
            board.addLocation(new Throne(this, 4, 4));
        }

        return board;
    }

    private void repositionKingOnCachedHubRoom(HubRoom room, int entryX, int entryY) {
        if (room.kingExitCol >= 0) {
            room.board.getTiles().get(room.kingExitCol).get(room.kingExitRow).setPiece(null);
        }
        io.github.chess_sequel.engine.pieces.Piece occupant =
                room.board.getTiles().get(entryX).get(entryY).getPiece();
        if (occupant != null && occupant != player.getLeadPiece()) {
            room.board.getTiles().get(entryX).get(entryY).setPiece(null);
        }
        player.setLeadPieceX(entryX);
        player.setLeadPieceY(entryY);
        player.getLeadPiece().setCol(entryX);
        player.getLeadPiece().setRow(entryY);
        room.board.getTiles().get(entryX).get(entryY).setPiece(player.getLeadPiece());
    }

    // -------------------------------------------------------------------------
    // Section navigation
    // -------------------------------------------------------------------------

    /** Generates a new section layout and pushes the spawn room as the active board. */
    public void enterSection(String sectionId) {
        SectionConfig config = jsonLoader.getSectionConfig(sectionId);
        activeSectionLayout = SectionLayoutGenerator.generate(config);
        RoomNode spawn = activeSectionLayout.spawnRoom;
        spawn.board = createRoomBoard(spawn, null);
        gameBoards.clear();
        gameBoards.push(spawn.board);
        gameState = GameState.BOARD_STATE_CHANGED;
    }

    /**
     * Moves the player from the current section room into the adjacent room in {@code dir}.
     * The current board is cached; on re-entry the king is repositioned without rebuilding.
     */
    public void navigateRoom(Direction dir) {
        if (activeSectionLayout == null) return;
        RoomNode current = activeSectionLayout.currentRoom;
        if (!current.doors.contains(dir)) return;

        RoomNode next = activeSectionLayout.getRoomInDirection(dir);
        if (next == null) return;

        current.kingExitCol = player.getLeadPiece().getCol();
        current.kingExitRow = player.getLeadPiece().getRow();
        gameBoards.pop();

        activeSectionLayout.currentRoom = next;
        next.visited = true;
        revealAdjacentRooms(next);

        Direction entryFrom = dir.opposite();
        if (next.board == null) {
            next.board = createRoomBoard(next, entryFrom);
        } else {
            repositionKingOnCachedBoard(next, entryFrom);
        }
        gameBoards.push(next.board);
        gameState = GameState.BOARD_STATE_CHANGED;
    }

    public SectionLayout getActiveSectionLayout() { return activeSectionLayout; }

    private MapBoard createRoomBoard(RoomNode node, Direction entryFrom) {
        SectionConfig config = jsonLoader.getSectionConfig(activeSectionLayout.sectionId);
        RoomTypeData roomTypeData = jsonLoader.getRoomTypeData(config.sectionId, node.type);

        // Room type overrides section defaults; 0 means "use section default"
        int mapX   = (roomTypeData != null && roomTypeData.mapX   > 0) ? roomTypeData.mapX   : config.mapX;
        int mapY   = (roomTypeData != null && roomTypeData.mapY   > 0) ? roomTypeData.mapY   : config.mapY;
        int combatX = (roomTypeData != null && roomTypeData.combatX > 0) ? roomTypeData.combatX : config.combatX;
        int combatY = (roomTypeData != null && roomTypeData.combatY > 0) ? roomTypeData.combatY : config.combatY;

        node.mapX = mapX; node.mapY = mapY;
        node.combatX = combatX; node.combatY = combatY;

        String virtualId = config.sectionId + "-" + node.type;
        ArrayList<EnemyData> enemies = (roomTypeData != null && roomTypeData.enemies != null)
                ? roomTypeData.enemies : new ArrayList<>();
        jsonLoader.registerRoomData(virtualId, mapX, mapY, combatX, combatY, enemies);
        currentMap = virtualId;
        jsonLoader.setMapSizeXY(currentMap);

        int[] entry = entryPosition(entryFrom, mapX, mapY);
        int entryX = entry[0], entryY = entry[1];
        player.setLeadPieceX(entryX);
        player.setLeadPieceY(entryY);

        ArrayList<io.github.chess_sequel.engine.jsonTypes.MapNode> nodes = new ArrayList<>();
        if (roomTypeData != null && roomTypeData.variants != null && !roomTypeData.variants.isEmpty()) {
            RoomVariant variant = pickCompatibleVariant(roomTypeData.variants, node.doors);
            if (variant.nodes != null) {
                for (io.github.chess_sequel.engine.jsonTypes.MapNode mn : variant.nodes) {
                    if (mn.x != entryX || mn.y != entryY) nodes.add(mn);
                }
            }
        }

        ZoneVariant zoneVariant = new ZoneVariant();
        zoneVariant.playerStart = new Coordinates();
        zoneVariant.playerStart.x = entryX;
        zoneVariant.playerStart.y = entryY;
        zoneVariant.nodes = nodes;

        MapBoard mapBoard = new MapBoard(this, mapX, mapY, player, zoneVariant);

        for (Direction dir : node.doors) {
            mapBoard.addLocation(new RoomDoor(this, doorCol(dir, mapX, mapY), doorRow(dir, mapX, mapY), dir));
        }

        return mapBoard;
    }

    private void revealAdjacentRooms(RoomNode room) {
        SectionConfig config = jsonLoader.getSectionConfig(activeSectionLayout.sectionId);
        int w = config.gridWidth, h = config.gridHeight;
        int[][] offsets = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] o : offsets) {
            int nx = room.gridX + o[0], ny = room.gridY + o[1];
            if (nx >= 0 && nx < w && ny >= 0 && ny < h && activeSectionLayout.grid[nx][ny] != null) {
                activeSectionLayout.grid[nx][ny].revealed = true;
            }
        }
    }

    private void repositionKingOnCachedBoard(RoomNode node, Direction entryFrom) {
        int[] entry = entryPosition(entryFrom, node.mapX, node.mapY);
        int entryX = entry[0], entryY = entry[1];

        if (node.kingExitCol >= 0) {
            node.board.getTiles().get(node.kingExitCol).get(node.kingExitRow).setPiece(null);
        }

        // Clear whatever occupies the entry tile — the player must be able to land there
        io.github.chess_sequel.engine.pieces.Piece occupant =
                node.board.getTiles().get(entryX).get(entryY).getPiece();
        if (occupant != null && occupant != player.getLeadPiece()) {
            node.board.getTiles().get(entryX).get(entryY).setPiece(null);
        }

        player.setLeadPieceX(entryX);
        player.setLeadPieceY(entryY);
        player.getLeadPiece().setCol(entryX);
        player.getLeadPiece().setRow(entryY);
        node.board.getTiles().get(entryX).get(entryY).setPiece(player.getLeadPiece());
    }

    // -------------------------------------------------------------------------
    // Quest and persistent data
    // -------------------------------------------------------------------------

    public PersistentData getPersistentData() { return persistentData; }

    /**
     * Flags a passive condition as met (clearSection, defeat, goTo).
     * Persists immediately. The NPC will acknowledge it on the next conversation.
     */
    public void flagConditionMet(String type, String target) {
        switch (type) {
            case "clearSection":
                if (!persistentData.clearedSections.contains(target))
                    persistentData.clearedSections.add(target);
                break;
            case "defeat":
                if (!persistentData.defeatedEnemies.contains(target))
                    persistentData.defeatedEnemies.add(target);
                break;
            case "goTo":
                if (!persistentData.visitedLocations.contains(target))
                    persistentData.visitedLocations.add(target);
                break;
        }
        SaveManager.save(persistentData);
    }

    /** Returns true if the given quest condition is currently satisfied. */
    public boolean isConditionMet(QuestCondition cond) {
        if (cond == null || "none".equals(cond.type)) return true;
        switch (cond.type) {
            case "clearSection": return persistentData.hasClearedSection(cond.target);
            case "defeat":       return persistentData.hasDefeated(cond.target);
            case "goTo":         return persistentData.hasVisited(cond.target);
            // talkTo and giveItem are triggered actively in dialogue — treat as met when checked here
            default: return false;
        }
    }

    /**
     * Called when the player starts talking to a quest NPC.
     * Returns the appropriate dialogue for their current step:
     * readyDialogue if the condition is met and reward is pending, pendingDialogue otherwise.
     * Returns null if this NPC has no quest definition.
     */
    public Dialogue getQuestDialogue(String npcId) {
        NpcQuestDef def = jsonLoader.getQuestDef(npcId);
        if (def == null) return null;
        NpcQuestState state = persistentData.getOrCreateNpcState(npcId);
        if (state.step >= def.steps.size()) return null;

        QuestStep step = def.steps.get(state.step);

        // talkTo condition: talking to this NPC IS the trigger — mark it met now
        if ("talkTo".equals(step.condition.type) && npcId.equals(step.condition.target)) {
            flagConditionMet("talkTo", npcId);
        }

        boolean conditionMet = isConditionMet(step.condition);
        if (conditionMet && !state.rewardClaimed && step.readyDialogue != null) {
            return step.readyDialogue;
        }
        return step.pendingDialogue;
    }

    /**
     * Called after a quest NPC's dialogue resolves. If their condition was met,
     * gives the reward and advances to the next step.
     */
    public void resolveQuestDialogue(String npcId) {
        NpcQuestDef def = jsonLoader.getQuestDef(npcId);
        if (def == null) return;
        NpcQuestState state = persistentData.getOrCreateNpcState(npcId);
        if (state.step >= def.steps.size()) return;

        QuestStep step = def.steps.get(state.step);
        boolean conditionMet = isConditionMet(step.condition)
                || ("talkTo".equals(step.condition.type) && npcId.equals(step.condition.target));

        if (conditionMet && !state.rewardClaimed) {
            if (step.reward != null) handleRewards(step.reward);
            state.rewardClaimed = true;
            state.step++;
            SaveManager.save(persistentData);
        }
    }

    /** Returns which location this NPC should be at given their current quest step. */
    public String getNpcLocation(String npcId) {
        NpcQuestDef def = jsonLoader.getQuestDef(npcId);
        if (def == null) return null;
        int step = persistentData.getNpcStep(npcId);
        if (step >= def.steps.size()) return def.steps.get(def.steps.size() - 1).location;
        return def.steps.get(step).location;
    }

    private static RoomVariant pickCompatibleVariant(List<RoomVariant> variants, java.util.Set<Direction> requiredDoors) {
        List<RoomVariant> compatible = new ArrayList<>();
        for (RoomVariant v : variants) {
            if (v.possibleDoors == null) {
                compatible.add(v);
            } else {
                boolean ok = true;
                for (Direction d : requiredDoors) {
                    if (!v.possibleDoors.contains(d.name())) { ok = false; break; }
                }
                if (ok) compatible.add(v);
            }
        }
        if (compatible.isEmpty()) compatible = variants; // fallback: any variant
        return compatible.get((int)(Math.random() * compatible.size()));
    }

    private static int[] entryPosition(Direction entryFrom, int mapX, int mapY) {
        if (entryFrom == null) return new int[]{ mapX / 2, mapY / 2 };
        switch (entryFrom) {
            case EAST:  return new int[]{ mapX - 2, mapY / 2 };
            case WEST:  return new int[]{ 1,        mapY / 2 };
            case NORTH: return new int[]{ mapX / 2, mapY - 2 };
            case SOUTH: return new int[]{ mapX / 2, 1        };
            default:    return new int[]{ mapX / 2, mapY / 2 };
        }
    }

    private static int doorCol(Direction dir, int mapX, int mapY) {
        switch (dir) {
            case EAST:  return mapX - 1;
            case WEST:  return 0;
            default:    return mapX / 2;
        }
    }

    private static int doorRow(Direction dir, int mapX, int mapY) {
        switch (dir) {
            case NORTH: return mapY - 1;
            case SOUTH: return 0;
            default:    return mapY / 2;
        }
    }

}
