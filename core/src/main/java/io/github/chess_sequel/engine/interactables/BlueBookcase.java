package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.jsonTypes.Dialogue;
import io.github.chess_sequel.engine.jsonTypes.DialogueNode;
import io.github.chess_sequel.engine.pieces.factories.KingPowerFactory;
import io.github.chess_sequel.engine.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Map-passive bookcase — grants one random map-passive ability on first interaction.
 * After claiming, subsequent reads show "already gained power from this book."
 * Resets each run because the instance is created fresh per run.
 */
public class BlueBookcase extends Interactable {

    private final GameRun gameRun;
    private boolean claimed = false;

    public BlueBookcase(GameRun gameRun, int col, int row) {
        this.gameRun = gameRun;
        this.col = col;
        this.row = row;
        this.filePath = "blue-bookshelf.png";
    }

    @Override
    public boolean isPassable() { return false; }

    @Override
    public void interaction() {
        if (claimed) {
            showDialogue("You've already gained power from this book.");
            return;
        }
        Player player = gameRun.getPlayer();
        List<ShopEffect> offers = KingPowerFactory.generateOrbOffers(
            KingPowerFactory.OrbType.MAP,
            player.getKing(),
            player.getPlayerClass(),
            gameRun.getCurrentMap()
        );
        if (!offers.isEmpty()) {
            ShopEffect effect = offers.get(0);
            effect.apply(player);
            claimed = true;
            showDialogue("You draw power from the pages.", "You gain: " + effect.getName() + ".");
        } else {
            showDialogue("The pages hold nothing new for you.");
        }
    }

    private void showDialogue(String... lines) {
        Dialogue d = new Dialogue();
        d.entry = "msg";
        DialogueNode node = new DialogueNode();
        node.id = "msg";
        node.lines = new ArrayList<>();
        for (String line : lines) node.lines.add(line);
        d.nodes = new ArrayList<>();
        d.nodes.add(node);
        gameRun.startDialogue(d, "msg", null);
    }
}
