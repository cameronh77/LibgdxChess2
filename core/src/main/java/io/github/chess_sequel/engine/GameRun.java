package io.github.chess_sequel.engine;

import io.github.chess_sequel.engine.interactables.LevelPortal;
import io.github.chess_sequel.engine.interactables.NPCPiece;
import io.github.chess_sequel.engine.interactables.ShopEffect;
import io.github.chess_sequel.engine.pieces.factories.KingPowerFactory;
import io.github.chess_sequel.engine.jsonTypes.Dialogue;
import io.github.chess_sequel.engine.jsonTypes.DialogueChoice;
import io.github.chess_sequel.engine.jsonTypes.DialogueNode;
import io.github.chess_sequel.engine.jsonTypes.Rewards;
import java.util.List;
import io.github.chess_sequel.engine.jsonTypes.ZoneData;
import io.github.chess_sequel.engine.jsonTypes.ZoneVariant;
import io.github.chess_sequel.engine.location.board.*;
import io.github.chess_sequel.engine.player.BotPlayer;
import io.github.chess_sequel.engine.player.Player;
import io.github.chess_sequel.engine.pieces.classic.King;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;


import java.util.ArrayList;
import java.util.HashMap;
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
    private Rewards pendingRewards = null;
    private Rewards pendingDisplayReward = null;

    private List<ShopEffect> pendingPowerOffer = null;

    private Dialogue activeDialogue;
    private DialogueNode currentNode;
    private int currentLineIndex;
    private NPCPiece dialogueNPC;

    /** Initialises the run, loads zone data, and pushes the starting map board. */
    public GameRun(Player player){
        this.player = player;
        jsonLoader.loadZoneData();
        this.currentMap = "classic4";
        jsonLoader.setMapSizeXY(currentMap);
        addMapBoard();
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
        if (rewards.powerChoices != null && !rewards.powerChoices.isEmpty()) {
            List<ShopEffect> offers = new ArrayList<>();
            for (String id : rewards.powerChoices) {
                ShopEffect e = KingPowerFactory.createEffect(id);
                if (e != null) offers.add(e);
            }
            if (!offers.isEmpty()) setPendingPowerOffer(offers);
        }
    }

    public void setPendingPowerOffer(List<ShopEffect> offers) {
        this.pendingPowerOffer = offers;
        setGameState(GameState.BOARD_STATE_CHANGED);
    }

    public boolean hasPendingPowerOffer() { return pendingPowerOffer != null && !pendingPowerOffer.isEmpty(); }
    public List<ShopEffect> getPendingPowerOffer() { return pendingPowerOffer; }

    public void selectPowerOffer(ShopEffect effect) {
        effect.apply(player);
        pendingPowerOffer = null;
        setGameState(GameState.BOARD_STATE_CHANGED);
    }

    /** Pops the current board and replaces it with the named map — used by {@link io.github.chess_sequel.engine.interactables.LevelPortal}. */
    public void progressGame(String level){
        //this.currentMap = level.getLevel();
        gameState = GameState.BOARD_STATE_CHANGED;
        this.currentMap = level;
        jsonLoader.setMapSizeXY(currentMap);
        popBoard();
        addMapBoard();
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

    /** Pops the top board (e.g. after a match ends) and restores the king's pre-match position. */
    public void popBoard(){
        gameBoards.pop();
        player.getLeadPiece().setCol(player.getLeadPieceX());
        player.getLeadPiece().setRow(player.getLeadPieceY());
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

    public String getCurrentMap(){
        return currentMap;
    }

    public JsonLoader getJsonLoader(){
        return jsonLoader;
    }

    public void setGameState(GameState gameState){
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

}
