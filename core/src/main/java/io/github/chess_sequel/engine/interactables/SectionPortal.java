package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;

/** Hub portal that starts a procedurally generated section run. */
public class SectionPortal extends Interactable {

    private final GameRun gameRun;
    private final String sectionId;

    public SectionPortal(GameRun gameRun, int col, int row, String sectionId) {
        this.gameRun = gameRun;
        this.col = col;
        this.row = row;
        this.sectionId = sectionId;
        this.filePath = "level-portal.png";
    }

    @Override
    public void interaction() {
        gameRun.enterSection(sectionId);
    }
}
