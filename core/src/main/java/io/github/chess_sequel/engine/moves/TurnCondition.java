package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.pieces.Piece;
import java.util.HashSet;
import java.util.Set;

/**
 * Tracks an active Blood Frenzy chain. Immutable — each capture produces a new instance
 * with the captor added, so undo restores the previous instance by reference.
 */
public class TurnCondition {
    public final boolean frenzySide;
    private final Set<Piece> alreadyActed;

    public TurnCondition(boolean frenzySide) {
        this.frenzySide = frenzySide;
        this.alreadyActed = new HashSet<>();
    }

    private TurnCondition(boolean frenzySide, Set<Piece> acted) {
        this.frenzySide = frenzySide;
        this.alreadyActed = acted;
    }

    /** Returns a new TurnCondition with {@code actor} added to the acted set. */
    public TurnCondition withActor(Piece actor) {
        Set<Piece> next = new HashSet<>(alreadyActed);
        next.add(actor);
        return new TurnCondition(frenzySide, next);
    }

    public boolean hasActed(Piece p) { return alreadyActed.contains(p); }
}
