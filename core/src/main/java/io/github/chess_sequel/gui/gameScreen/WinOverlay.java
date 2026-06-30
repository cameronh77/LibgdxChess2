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
import io.github.chess_sequel.engine.interactables.ShopEffect;
import io.github.chess_sequel.engine.jsonTypes.Rewards;
import io.github.chess_sequel.engine.pieces.classic.King;
import io.github.chess_sequel.engine.pieces.factories.KingPowerFactory;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;

/**
 * Full-screen overlay shown after a match is won. Lists currency gained, portals unlocked,
 * and any PreKingPower victory bonuses, then provides a "Continue" button that dismisses
 * the overlay and resumes map exploration.
 */
public class WinOverlay extends Table {

    public WinOverlay(GameRun gameRun, ProjectName game, Runnable onContinue) {
        setFillParent(true);
        setBackground(game.skin.getDrawable("blue"));

        BitmapFont font = new BitmapFont();
        Label.LabelStyle titleStyle  = new Label.LabelStyle(font, Color.YELLOW);
        Label.LabelStyle rewardStyle = new Label.LabelStyle(font, Color.WHITE);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        btnStyle.fontColor = Color.BLACK;
        btnStyle.up = game.skin.getDrawable("white");

        Table card = new Table();
        card.setBackground(game.skin.getDrawable("red"));
        card.pad(24);

        card.add(new Label("Victory!", titleStyle)).padBottom(16);
        card.row();

        Rewards rewards = gameRun.getPendingRewards();
        boolean anyReward = false;

        if (rewards != null && rewards.currency != null && rewards.currency > 0) {
            card.add(new Label("+" + rewards.currency + " Gold", rewardStyle)).left().padBottom(6);
            card.row();
            anyReward = true;
        }

        if (rewards != null && rewards.portals != null) {
            for (String portal : rewards.portals) {
                card.add(new Label("New path unlocked: " + portal, rewardStyle)).left().padBottom(6);
                card.row();
                anyReward = true;
            }
        }

        King king = gameRun.getPlayer().getKing();
        if (king != null) {
            for (PreKingPower power : king.getPreGamePowers()) {
                String desc = power.getVictoryDescription();
                if (desc != null) {
                    card.add(new Label(desc + "  (" + power.getName() + ")", rewardStyle)).left().padBottom(6);
                    card.row();
                    anyReward = true;
                }
            }
        }

        // Power choice — player picks one before continuing
        final ShopEffect[] selectedPower = {null};
        if (rewards != null && rewards.powerChoices != null && !rewards.powerChoices.isEmpty()) {
            card.add(new Label("Choose a power:", titleStyle)).padBottom(8).padTop(8);
            card.row();

            Table choiceRow = new Table();
            for (String id : rewards.powerChoices) {
                ShopEffect effect = KingPowerFactory.createEffect(id);
                if (effect == null) continue;

                TextButton choiceBtn = new TextButton(effect.getName(), btnStyle);
                choiceBtn.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        selectedPower[0] = effect;
                        // Highlight selected, dim others
                        for (com.badlogic.gdx.scenes.scene2d.Actor sibling : choiceRow.getChildren()) {
                            sibling.setColor(sibling == choiceBtn ? Color.WHITE : new Color(0.55f, 0.55f, 0.55f, 1f));
                        }
                    }
                });
                choiceRow.add(choiceBtn).pad(6).minWidth(130).height(50);
            }
            card.add(choiceRow).padBottom(8);
            card.row();
            anyReward = true;
        }

        if (!anyReward) {
            card.add(new Label("No rewards this time.", rewardStyle)).left().padBottom(6);
            card.row();
        }

        TextButton continueBtn = new TextButton("Continue", btnStyle);
        continueBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedPower[0] != null) {
                    selectedPower[0].apply(gameRun.getPlayer());
                }
                onContinue.run();
            }
        });
        card.add(continueBtn).padTop(16).width(140).height(50);

        add(card);
    }
}
