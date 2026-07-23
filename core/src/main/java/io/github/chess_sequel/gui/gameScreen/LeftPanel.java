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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.chess_sequel.ProjectName;
import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.GameState;
import io.github.chess_sequel.engine.interactables.ConsumableItem;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.BoardType;
import io.github.chess_sequel.engine.pieces.classic.King;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;
import io.github.chess_sequel.gui.TextureCache;

/**
 * Left-side UI panel. On map and layout boards it shows the "alter layout" toggle button.
 * On the layout board it also renders buttons for each of the king's pre-game powers so the
 * player can apply them before the match begins.
 */
public class LeftPanel extends Table {

    private final GameRun gameRun;
    private final ProjectName game;
    private final Runnable onPowerLoadout;
    private final BitmapFont font = new BitmapFont();

    public LeftPanel(GameRun gameRun, ProjectName game, Runnable onPowerLoadout) {
        this.gameRun = gameRun;
        this.game = game;
        this.onPowerLoadout = onPowerLoadout;
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
                addPowerLoadoutButton();
                addPreGamePowerButtons();
                break;
            case MAP:
            case SHOP:
                addAlterLayoutButton();
                addPowerLoadoutButton();
                addConsumableButtons();
                break;
        }
    }

    private void addPowerLoadoutButton() {
        Button.ButtonStyle btnStyle = new Button.ButtonStyle();
        btnStyle.up = game.skin.getDrawable("white");
        Button btn = new Button(btnStyle);
        btn.add(new Label("Powers", new Label.LabelStyle(font, Color.BLACK))).pad(6);
        btn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { onPowerLoadout.run(); }
        });
        row();
        add(btn).width(100).pad(4).top();
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

        Label.LabelStyle whiteStyle = new Label.LabelStyle(font, Color.WHITE);

        for (PreKingPower power : king.getPreGamePowers()) {
            row();
            Table row = new Table();
            row.left();

            String iconPath = power.getIconPath();
            if (iconPath != null) {
                try {
                    Texture tex = TextureCache.get(iconPath);
                    Image icon = new Image(tex);
                    row.add(icon).size(20, 20).padRight(4);
                } catch (Exception ignored) {}
            }

            row.add(new Label(power.getName(), whiteStyle)).left();

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
                        float tx = pos.x + row.getWidth() + 6;
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

            row.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    gameRun.applyPreGamePower(power);
                }
            });

            add(row).growX().left().pad(2);
        }
    }

    private void addConsumableButtons() {
        java.util.ArrayList<ConsumableItem> consumables = gameRun.getPlayer().getConsumables();
        if (consumables.isEmpty()) return;

        Label.LabelStyle whiteStyle = new Label.LabelStyle(font, Color.WHITE);

        row();
        add(new Label("Items", whiteStyle)).left().padLeft(6).padTop(8);

        for (ConsumableItem item : new java.util.ArrayList<>(consumables)) {
            boolean active = item.isActive(gameRun);
            row();
            Table row = new Table();
            row.left();
            if (active) {
                row.setBackground(game.skin.getDrawable("white"));
                row.setColor(1f, 1f, 0.3f, 1f);
            }

            String iconPath = item.getIconPath();
            if (iconPath != null) {
                try {
                    Texture tex = TextureCache.get(iconPath);
                    Image icon = new Image(tex);
                    if (active) icon.setColor(Color.BLACK);
                    row.add(icon).size(20, 20).padRight(4);
                } catch (Exception ignored) {}
            }

            Label.LabelStyle nameStyle = new Label.LabelStyle(font, active ? Color.BLACK : Color.WHITE);
            row.add(new Label(item.getName(), nameStyle)).left();

            String description = item.getDescription();
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
                        float tx = pos.x + row.getWidth() + 6;
                        float ty = pos.y - tooltip.getHeight();
                        tooltip.setPosition(tx, Math.max(0, ty));
                        stage.addActor(tooltip);
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        if (pointer != -1) return;
                        if (tooltip != null) { tooltip.remove(); tooltip = null; }
                    }
                });
            }

            row.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (item.isActive(gameRun)) {
                        item.onDeactivate(gameRun);
                    } else {
                        // Deactivate any currently active consumable before activating this one
                        for (ConsumableItem other : gameRun.getPlayer().getConsumables()) {
                            if (other != item && other.isActive(gameRun)) other.onDeactivate(gameRun);
                        }
                        item.onActivate(gameRun);
                    }
                    gameRun.setGameState(GameState.BOARD_STATE_CHANGED);
                }
            });

            add(row).growX().left().pad(2);
        }
    }
}
