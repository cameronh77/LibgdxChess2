package io.github.chess_sequel.engine;

/**
 * Signals from the engine that the GUI needs to act on each frame.
 * {@code NEUTRAL} — nothing to do; {@code BOARD_STATE_CHANGED} — rebuild UI panels;
 * {@code MATCH_WON} — show the win overlay.
 */
public enum GameState {
    NEUTRAL,
    BOARD_STATE_CHANGED,
    MATCH_WON,
    GO_TO_KING_SELECTION,
}
