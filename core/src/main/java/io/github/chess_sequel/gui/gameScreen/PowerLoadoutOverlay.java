package io.github.chess_sequel.gui.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import io.github.chess_sequel.ProjectName;
import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.pieces.classic.King;
import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;
import io.github.chess_sequel.gui.TextureCache;

import java.util.List;

/**
 * Full-screen overlay for managing which active king powers are equipped.
 * Shows 3 chosen slots and a bench pool. Drag a bench power onto a chosen slot
 * to swap them. Drag a chosen power onto the bench area to unequip it.
 * All passives are always active and shown read-only at the bottom.
 */
public class PowerLoadoutOverlay extends Table {

    private final GameRun gameRun;
    private final ProjectName game;
    private final Runnable onClose;
    private final BitmapFont font = new BitmapFont();
    private DragAndDrop dnd;

    public PowerLoadoutOverlay(GameRun gameRun, ProjectName game, Runnable onClose) {
        this.gameRun = gameRun;
        this.game = game;
        this.onClose = onClose;
        setFillParent(true);
        setBackground(game.skin.getDrawable("white"));
        setColor(0f, 0f, 0f, 0.78f);
        rebuild();
    }

    private void rebuild() {
        clearChildren();
        dnd = new DragAndDrop();

        King king = gameRun.getPlayer().getKing();
        if (king == null) { onClose.run(); return; }

        List<ActiveKingPower>  chosen   = king.getActivePowers();
        List<ActiveKingPower>  bench    = king.getBenchActivePowers();
        List<PassiveKingPower> passives = king.getPassivePowers();

        Label.LabelStyle darkStyle  = new Label.LabelStyle(font, Color.BLACK);
        Label.LabelStyle dimStyle   = new Label.LabelStyle(font, Color.DARK_GRAY);

        Button.ButtonStyle btnStyle = new Button.ButtonStyle();
        btnStyle.up = game.skin.getDrawable("white");

        Table panel = new Table();
        panel.setBackground(game.skin.getDrawable("white"));
        panel.setColor(Color.WHITE);
        panel.pad(20);
        panel.top().left();

        // Header
        panel.add(new Label("POWER LOADOUT", darkStyle)).expandX().left();
        Button closeBtn = new Button(btnStyle);
        closeBtn.add(new Label("X", darkStyle)).pad(4);
        closeBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { onClose.run(); }
        });
        panel.add(closeBtn).size(32, 32).right();
        panel.row().padTop(14);

        // Active slots
        panel.add(new Label("ACTIVE", dimStyle)).colspan(2).left().padBottom(6);
        panel.row();

        Table slotsRow = new Table();
        for (int i = 0; i < 3; i++) {
            final int slotIdx = i;
            ActiveKingPower slotPower = i < chosen.size() ? chosen.get(i) : null;

            Table slot = buildPowerCard(slotPower, darkStyle, dimStyle);
            slot.setBackground(game.skin.getDrawable("white"));
            slot.setColor(slotPower != null ? Color.LIGHT_GRAY : new Color(0.45f, 0.45f, 0.45f, 0.6f));

            slotsRow.add(slot).size(90, 80).pad(4);

            if (slotPower != null) {
                final ActiveKingPower power = slotPower;
                dnd.addSource(new DragAndDrop.Source(slot) {
                    @Override
                    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                        DragAndDrop.Payload payload = new DragAndDrop.Payload();
                        payload.setObject(new PowerPayload(power, true, slotIdx));
                        payload.setDragActor(buildDragActor(power));
                        return payload;
                    }
                });
            }

            dnd.addTarget(new DragAndDrop.Target(slot) {
                @Override
                public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    getActor().setColor(Color.YELLOW);
                    return true;
                }
                @Override
                public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                    ActiveKingPower p = slotIdx < chosen.size() ? chosen.get(slotIdx) : null;
                    getActor().setColor(p != null ? Color.LIGHT_GRAY : new Color(0.45f, 0.45f, 0.45f, 0.6f));
                }
                @Override
                public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    PowerPayload p = (PowerPayload) payload.getObject();
                    if (p.fromChosen) king.swapChosen(p.fromIndex, slotIdx);
                    else              king.swapChosenWithBench(slotIdx, p.fromIndex);
                    Gdx.app.postRunnable(() -> rebuild());
                }
            });
        }
        panel.add(slotsRow).colspan(2).left();
        panel.row().padTop(14);

        // Bench
        if (!bench.isEmpty()) {
            panel.add(new Label("BENCH", dimStyle)).colspan(2).left().padBottom(6);
            panel.row();

            Table benchRow = new Table();
            benchRow.left();
            for (int i = 0; i < bench.size(); i++) {
                final int benchIdx = i;
                final ActiveKingPower power = bench.get(i);
                Table card = buildPowerCard(power, darkStyle, dimStyle);
                card.setBackground(game.skin.getDrawable("white"));
                card.setColor(new Color(0.82f, 0.82f, 0.82f, 1f));
                benchRow.add(card).size(90, 80).pad(4);

                dnd.addSource(new DragAndDrop.Source(card) {
                    @Override
                    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                        DragAndDrop.Payload payload = new DragAndDrop.Payload();
                        payload.setObject(new PowerPayload(power, false, benchIdx));
                        payload.setDragActor(buildDragActor(power));
                        return payload;
                    }
                });
            }

            // Bench area is a drop target for unequipping chosen powers
            dnd.addTarget(new DragAndDrop.Target(benchRow) {
                @Override
                public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    return ((PowerPayload) payload.getObject()).fromChosen;
                }
                @Override
                public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    PowerPayload p = (PowerPayload) payload.getObject();
                    if (p.fromChosen) king.moveChosenToBench(p.fromIndex);
                    Gdx.app.postRunnable(() -> rebuild());
                }
            });

            panel.add(benchRow).colspan(2).left();
            panel.row().padTop(14);
        }

        // Passives — read-only
        if (!passives.isEmpty()) {
            panel.add(new Label("PASSIVES", dimStyle)).colspan(2).left().padBottom(6);
            panel.row();
            Table passiveRow = new Table();
            passiveRow.left();
            for (PassiveKingPower passive : passives) {
                Table card = new Table();
                card.setBackground(game.skin.getDrawable("white"));
                card.setColor(new Color(0.6f, 0.8f, 1f, 0.9f));
                card.pad(6);

                String iconPath = passive.getIconPath();
                if (iconPath != null) {
                    try {
                        card.add(new Image(TextureCache.get(iconPath))).size(20, 20);
                        card.row();
                    } catch (Exception ignored) {}
                }
                Label nameLabel = new Label(passive.getName(), darkStyle);
                nameLabel.setWrap(true);
                card.add(nameLabel).width(76).center();
                passiveRow.add(card).size(90, 60).pad(4);
            }
            panel.add(passiveRow).colspan(2).left();
            panel.row();
        }

        add(panel).center();
    }

    private Table buildPowerCard(ActiveKingPower power, Label.LabelStyle nameStyle, Label.LabelStyle emptyStyle) {
        Table card = new Table();
        card.pad(6);
        if (power == null) {
            card.add(new Label("—", emptyStyle)).center();
            return card;
        }
        String iconPath = power.getIconPath();
        if (iconPath != null) {
            try {
                card.add(new Image(TextureCache.get(iconPath))).size(24, 24);
                card.row();
            } catch (Exception ignored) {}
        }
        Label name = new Label(power.getName(), nameStyle);
        name.setWrap(true);
        card.add(name).width(76).center();
        return card;
    }

    private Actor buildDragActor(ActiveKingPower power) {
        Table drag = new Table();
        drag.setBackground(game.skin.getDrawable("white"));
        drag.setColor(new Color(1f, 1f, 0.4f, 0.9f));
        drag.pad(6);
        drag.add(new Label(power.getName(), new Label.LabelStyle(font, Color.BLACK))).center();
        drag.pack();
        return drag;
    }

    private static class PowerPayload {
        final ActiveKingPower power;
        final boolean fromChosen;
        final int fromIndex;

        PowerPayload(ActiveKingPower power, boolean fromChosen, int fromIndex) {
            this.power = power;
            this.fromChosen = fromChosen;
            this.fromIndex = fromIndex;
        }
    }
}
