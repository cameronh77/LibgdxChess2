package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.jsonTypes.Dialogue;
import io.github.chess_sequel.engine.jsonTypes.DialogueNode;
import io.github.chess_sequel.engine.pieces.factories.KingPowerFactory;
import io.github.chess_sequel.engine.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Class-power bookcase — grants one random class/classic ability on first interaction,
 * drawn from the same pool as the regular orb. After claiming, shows "already gained power."
 * Resets each run because the instance is created fresh per run.
 */
public class GreenBookcase extends Interactable {

    private final GameRun gameRun;
    private boolean claimed = false;

    public GreenBookcase(GameRun gameRun, int col, int row) {
        this.gameRun = gameRun;
        this.col = col;
        this.row = row;
        this.filePath = "green-bookshelf.png";
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
            KingPowerFactory.OrbType.MIXED,
            player.getKing(),
            player.getPlayerClass(),
            gameRun.getCurrentMap()
        );
        if (!offers.isEmpty()) {
            ShopEffect effect = offers.get(0);
            effect.apply(player);
            claimed = true;
            showDialogue("Ancient tactics flow through you.", "You gain: " + effect.getName() + ".");
        } else {
            showDialogue("You have mastered everything these tomes can offer.");
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
