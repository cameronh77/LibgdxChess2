package io.github.chess_sequel.engine;

import io.github.chess_sequel.engine.interactables.Exit;
import io.github.chess_sequel.engine.interactables.LevelPortal;
import io.github.chess_sequel.engine.interactables.NPCPiece;
import io.github.chess_sequel.engine.interactables.Shop;
import io.github.chess_sequel.engine.interactables.ShopItem;
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
import java.util.Stack;

public class GameRun {

    private Stack<Board> gameBoards = new Stack<>();
    private Stack<String> mapStack = new Stack<>();
    private Stack<int[]> returnPositions = new Stack<>();
    private Player player;
    private JsonLoader jsonLoader = new JsonLoader();
    private String currentMap;
    private GameState gameState = GameState.NEUTRAL;
    private Rewards pendingRewards = null;
    private Rewards pendingDisplayReward = null;

    private Dialogue activeDialogue;
    private DialogueNode currentNode;
    private int currentLineIndex;
    private NPCPiece dialogueNPC;

    public GameRun(Player player){
        this.player = player;
        jsonLoader.loadZoneData();
        this.currentMap = "classic4";
        jsonLoader.setMapSizeXY(currentMap);
        addMapBoard();
    }

    public void addMatchBoard(BotPlayer opponent){
        System.out.println("Adding match board");
        player.setLeadPieceX(player.getLeadPiece().getCol());
        player.setLeadPieceY(player.getLeadPiece().getRow());
        gameBoards.push(new MatchBoard(jsonLoader.getCombatSizeX(), jsonLoader.getCombatSizeY(), player, opponent));
        gameState = GameState.BOARD_STATE_CHANGED;
    }

    public void addShopBoard(Shop shop, int xOrd, int yOrd){
        System.out.println("Adding shop board");
        player.setLeadPieceX(player.getLeadPiece().getCol());
        player.setLeadPieceY(player.getLeadPiece().getRow());

        player.getLeadPiece().setCol(xOrd);
        player.getLeadPiece().setRow(yOrd);

        ShopBoard shopBoard = new ShopBoard(jsonLoader.getCombatSizeX(), jsonLoader.getCombatSizeY(), player, shop);
        shopBoard.addLocation(new Exit(xOrd, yOrd, this));
        gameBoards.push(shopBoard);
        gameState = GameState.BOARD_STATE_CHANGED;
    }

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
    }

    public void progressGame(String level){
        //this.currentMap = level.getLevel();
        gameState = GameState.BOARD_STATE_CHANGED;
        this.currentMap = level;
        jsonLoader.setMapSizeXY(currentMap);
        popBoard();
        addMapBoard();
    }

    public void addMapBoard(){
        ZoneData zone = jsonLoader.getZone(currentMap);
        int randomIndex = (int)(Math.random() * zone.variants.size());
        ZoneVariant variant = zone.variants.get(randomIndex);
        player.setLeadPieceX(variant.playerStart.x);
        player.setLeadPieceY(variant.playerStart.y);
        gameBoards.push(new MapBoard(this, zone.mapX, zone.mapY, player, variant));
    }

    public void pushMap(String zoneName) {
        returnPositions.push(new int[]{player.getLeadPiece().getCol(), player.getLeadPiece().getRow()});
        mapStack.push(currentMap);
        currentMap = zoneName;
        jsonLoader.setMapSizeXY(currentMap);
        addMapBoard();
        gameState = GameState.BOARD_STATE_CHANGED;
    }

    public void popMap() {
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

    public Board getCurrentBoard(){
        return gameBoards.peek();
    }
    public void popBoard(){
        gameBoards.pop();
        player.getLeadPiece().setCol(player.getLeadPieceX());
        player.getLeadPiece().setRow(player.getLeadPieceY());
        gameState = GameState.BOARD_STATE_CHANGED;
    }

    public void alterLayout(){
        if(gameBoards.peek() instanceof AlterLayoutBoard){
            gameBoards.pop();
            // Restore king to its map position — AlterLayout's addToBoard overwrites col/row with trueCol/trueRow
            player.getLeadPiece().setCol(player.getLeadPieceX());
            player.getLeadPiece().setRow(player.getLeadPieceY());
            gameState = GameState.BOARD_STATE_CHANGED;
        }
        else{
            // Save king's current map position before AlterLayout corrupts it via setStartCords()
            player.setLeadPieceX(player.getLeadPiece().getCol());
            player.setLeadPieceY(player.getLeadPiece().getRow());
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

    public void startDialogue(Dialogue dialogue, String entryNodeId, NPCPiece npc) {
        this.activeDialogue = dialogue;
        this.dialogueNPC = npc;
        enterNode(findNode(dialogue, entryNodeId));
        setGameState(GameState.BOARD_STATE_CHANGED);
    }

    public boolean isDialogueActive() {
        return activeDialogue != null;
    }

    public String getCurrentDialogueLine() {
        if (currentNode == null) return null;
        if (currentNode.lines != null && currentLineIndex < currentNode.lines.size()) {
            return currentNode.lines.get(currentLineIndex);
        }
        return null;
    }

    public List<DialogueChoice> getCurrentChoices() {
        if (currentNode == null) return null;
        boolean linesExhausted = currentNode.lines == null || currentLineIndex >= currentNode.lines.size();
        if (linesExhausted && currentNode.choices != null && !currentNode.choices.isEmpty()) {
            return currentNode.choices;
        }
        return null;
    }

    public void advanceLine() {
        currentLineIndex++;
        if (getCurrentDialogueLine() == null && getCurrentChoices() == null) {
            resolveOutcome(currentNode);
        }
    }

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
