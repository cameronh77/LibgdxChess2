package io.github.chess_sequel.gui.gameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.chess_sequel.ProjectName;
import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.BoardType;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.King;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;
import io.github.chess_sequel.gui.PieceIcon;

public class RightPanel extends Table {

    private final GameRun gameRun;
    private final ProjectName game;
    private final GameScreen gameScreen;
    private final BitmapFont font = new BitmapFont();
    private Label goldLabel;

    public RightPanel(GameRun gameRun, ProjectName game, GameScreen gameScreen) {
        this.gameRun = gameRun;
        this.game = game;
        this.gameScreen = gameScreen;
        setBackground(game.skin.getDrawable("red"));
        top();
    }

    public void refresh(Board board) {
        clear();

        // Money bar — always shown on every board
        Table moneyBar = new Table();
        moneyBar.setBackground(game.skin.getDrawable("white"));
        moneyBar.pad(8);
        moneyBar.left();
        goldLabel = new Label(String.valueOf(gameRun.getPlayer().getCurrency()), new Label.LabelStyle(font, Color.BLACK));
        moneyBar.add(goldLabel).expandX().left();
        add(moneyBar).growX().height(40).top();

        // Passive powers — always shown
        King king = gameRun.getPlayer().getKing();
        if (king != null) {
            Label.LabelStyle whiteStyle = new Label.LabelStyle(font, Color.WHITE);
            for (PassiveKingPower power : king.getPassivePowers()) {
                row();
                add(new Label("* " + power.getName(), whiteStyle)).left().pad(2);
            }
        }

        // Piece inventory — only shown during layout phase
        if (board.getBoardType() == BoardType.ALTER_LAYOUT) {
            row();
            Table inventoryTable = new Table();
            inventoryTable.top().left();
            int col = 0;
            int tileSize = gameScreen.getBoardActor().getGameBoard().TILE_SIZE;
            for (Piece piece : gameRun.getPlayer().getPieceInventory()) {
                inventoryTable.add(new PieceIcon(piece, gameScreen)).size(tileSize).pad(4);
                col++;
                if (col == 2) { inventoryTable.row(); col = 0; }
            }
            add(inventoryTable).growY().top();
        }
    }

    public void updateCurrency() {
        if (goldLabel != null) {
            goldLabel.setText(String.valueOf(gameRun.getPlayer().getCurrency()));
        }
    }
}
