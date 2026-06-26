package io.github.chess_sequel.gui.gameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.github.chess_sequel.ProjectName;
import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.BoardType;
import io.github.chess_sequel.engine.pieces.classic.King;
import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.gui.BoardInput;

import java.util.List;

public class BottomPanel extends Table {

    private final GameRun gameRun;
    private final ProjectName game;
    private final BoardInput input;
    private final BitmapFont font = new BitmapFont();

    public BottomPanel(GameRun gameRun, ProjectName game, BoardInput input) {
        this.gameRun = gameRun;
        this.game = game;
        this.input = input;
        setBackground(game.skin.getDrawable("yellow"));
    }

    public void refresh(Board board) {
        clear();

        if (board.getBoardType() != BoardType.MATCH) return;

        King king = gameRun.getPlayer().getKing();
        List<ActiveKingPower> powers = king != null ? king.getActivePowers() : List.of();

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.up = game.skin.getDrawable("white");

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        for (int i = 0; i < 3; i++) {
            if (i < powers.size()) {
                final ActiveKingPower power = powers.get(i);
                TextButton slot = new TextButton(power.getName(), style);
                slot.setColor(power.isAvailable() ? Color.WHITE : Color.GRAY);
                slot.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (power.isAvailable()) input.selectPower(power);
                    }
                });
                add(slot).size(120, 100).pad(8);
            } else {
                Table slot = new Table();
                slot.setBackground(game.skin.getDrawable("white"));
                slot.setColor(0.3f, 0.3f, 0.3f, 0.6f);
                slot.add(new Label("—", labelStyle));
                add(slot).size(120, 100).pad(8);
            }
        }
    }
}
