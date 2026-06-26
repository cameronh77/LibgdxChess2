package io.github.chess_sequel.gui.gameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.github.chess_sequel.ProjectName;
import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.GameState;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.BoardType;
import io.github.chess_sequel.engine.pieces.classic.King;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;

public class LeftPanel extends Table {

    private final GameRun gameRun;
    private final ProjectName game;
    private final BitmapFont font = new BitmapFont();

    public LeftPanel(GameRun gameRun, ProjectName game) {
        this.gameRun = gameRun;
        this.game = game;
        setBackground(game.skin.getDrawable("blue"));
        top();
    }

    public void refresh(Board board) {
        clear();

        switch (board.getBoardType()) {
            case MATCH:
                return;
            case ALTER_LAYOUT:
                addAlterLayoutButton();
                addPreGamePowerButtons();
                break;
            case MAP:
            case SHOP:
                addAlterLayoutButton();
                break;
        }
    }

    private void addAlterLayoutButton() {
        ImageButton alterBtn = new ImageButton(game.skin.getDrawable("change"));
        alterBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameRun.alterLayout();
                gameRun.setGameState(GameState.BOARD_STATE_CHANGED);
            }
        });
        add(alterBtn).size(100).pad(10).top();
    }

    private void addPreGamePowerButtons() {
        King king = gameRun.getPlayer().getKing();
        if (king == null || king.getPreGamePowers().isEmpty()) return;

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.up = game.skin.getDrawable("white");

        for (PreKingPower power : king.getPreGamePowers()) {
            row();
            TextButton btn = new TextButton(power.getName(), style);
            btn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    gameRun.applyPreGamePower(power);
                }
            });
            add(btn).growX().pad(4);
        }
    }
}
