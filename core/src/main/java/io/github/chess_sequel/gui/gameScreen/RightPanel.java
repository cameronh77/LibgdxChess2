package io.github.chess_sequel.gui.gameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.chess_sequel.ProjectName;
import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.BoardType;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.King;
import io.github.chess_sequel.engine.powers.kingPower.KingPower;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;
import io.github.chess_sequel.gui.PieceIcon;
import io.github.chess_sequel.gui.TextureCache;

/**
 * Right-side UI panel. Always shows the player's gold balance at the top. During combat and
 * map exploration it also lists the king's passive and pre-game powers. On the layout board
 * it additionally shows the piece inventory so items can be dragged onto the board.
 */
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

        Table moneyBar = new Table();
        moneyBar.setBackground(game.skin.getDrawable("white"));
        moneyBar.pad(8);
        moneyBar.left();
        goldLabel = new Label(String.valueOf(gameRun.getPlayer().getCurrency()), new Label.LabelStyle(font, Color.BLACK));
        moneyBar.add(goldLabel).expandX().left();
        add(moneyBar).growX().height(40).top();

        King king = gameRun.getPlayer().getKing();
        if (king != null) {
            Label.LabelStyle whiteStyle = new Label.LabelStyle(font, Color.WHITE);

            for (PassiveKingPower power : king.getPassivePowers()) {
                row();
                add(powerRow(power, whiteStyle)).growX().left().pad(2);
            }

            for (PreKingPower power : king.getPreGamePowers()) {
                row();
                add(powerRow(power, whiteStyle)).growX().left().pad(2);
            }
        }

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

    private Table powerRow(KingPower power, Label.LabelStyle labelStyle) {
        Table row = new Table();
        row.left();

        String iconPath = power.getIconPath();
        if (iconPath != null) {
            try {
                Texture tex = TextureCache.get(iconPath);
                Image icon = new Image(tex);
                row.add(icon).size(20, 20).padRight(4);
            } catch (Exception ignored) {
                // icon file not yet added — skip silently
            }
        }

        row.add(new Label(power.getName(), labelStyle)).left();

        String description = power.getDescription();
        if (description != null && !description.isEmpty()) {
            row.addListener(new InputListener() {
                private Table tooltip;

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (pointer != -1 || tooltip != null) return;
                    Stage stage = row.getStage();
                    if (stage == null) return;

                    Label.LabelStyle darkStyle = new Label.LabelStyle(font, Color.BLACK);
                    Label desc = new Label(description, darkStyle);
                    desc.setWrap(true);

                    tooltip = new Table();
                    tooltip.setBackground(game.skin.getDrawable("white"));
                    tooltip.pad(8);
                    tooltip.add(desc).width(160);
                    tooltip.pack();

                    Vector2 pos = row.localToStageCoordinates(new Vector2(0, 0));
                    float tx = pos.x - tooltip.getWidth() - 6;
                    float ty = pos.y - tooltip.getHeight();
                    tooltip.setPosition(tx, Math.max(0, ty));
                    stage.addActor(tooltip);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    if (pointer != -1) return;
                    if (tooltip != null) {
                        tooltip.remove();
                        tooltip = null;
                    }
                }
            });
        }

        return row;
    }

    public void updateCurrency() {
        if (goldLabel != null) {
            goldLabel.setText(String.valueOf(gameRun.getPlayer().getCurrency()));
        }
    }
}
