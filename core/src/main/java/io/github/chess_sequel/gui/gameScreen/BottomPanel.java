package io.github.chess_sequel.gui.gameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.github.chess_sequel.ProjectName;
import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.interactables.ShopEffect;
import io.github.chess_sequel.engine.interactables.ShopItem;
import io.github.chess_sequel.engine.jsonTypes.DialogueChoice;
import io.github.chess_sequel.engine.jsonTypes.Rewards;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.BoardType;
import io.github.chess_sequel.engine.pieces.classic.King;
import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.gui.BoardInput;
import io.github.chess_sequel.gui.TextureCache;

import java.util.List;

/**
 * Bottom UI panel that shows contextual content depending on game state:
 * active king power buttons (during a match), dialogue lines and choices, or a reward
 * summary after NPC dialogue concludes. Rebuilt from scratch on every {@link #refresh} call.
 */
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
        top().left();
        pad(6);
    }

    public void refresh(Board board) {
        clear();

        if (gameRun.hasPendingPowerOffer()) {
            buildPowerOfferUI();
            return;
        }

        if (gameRun.hasPendingDisplayReward()) {
            buildRewardUI(gameRun.getPendingDisplayReward());
            return;
        }

        if (gameRun.hasPendingShopItem()) {
            buildShopItemUI();
            return;
        }

        if (gameRun.isDialogueActive()) {
            buildDialogueUI();
            return;
        }

        if (board.getBoardType() != BoardType.MATCH) return;

        King king = gameRun.getPlayer().getKing();
        List<ActiveKingPower> powers = king != null ? king.getActivePowers() : List.of();

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        Button.ButtonStyle btnStyle = new Button.ButtonStyle();
        btnStyle.up = game.skin.getDrawable("white");

        for (int i = 0; i < 3; i++) {
            if (i < powers.size()) {
                final ActiveKingPower power = powers.get(i);
                boolean available = power.isAvailable(board);

                Button slot = new Button(btnStyle);
                slot.setColor(available ? Color.WHITE : new Color(0.4f, 0.4f, 0.4f, 1f));
                slot.pad(6);

                String iconPath = power.getIconPath();
                if (iconPath != null) {
                    Texture tex = TextureCache.get(iconPath);
                    Image icon = new Image(tex);
                    icon.setColor(available ? Color.WHITE : new Color(0.5f, 0.5f, 0.5f, 1f));
                    slot.add(icon).size(24, 24);
                    slot.row();
                }

                slot.add(new Label(power.getName(), labelStyle)).padTop(2);

                slot.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (!power.isAvailable(gameRun.getCurrentBoard())) return;
                        if (input.getSelectedPower() == power) {
                            input.cancelPower();
                        } else {
                            input.selectPower(power);
                        }
                    }
                });

                add(slot).size(64, 52).pad(3);
            } else {
                Table slot = new Table();
                slot.setBackground(game.skin.getDrawable("white"));
                slot.setColor(0.3f, 0.3f, 0.3f, 0.6f);
                slot.add(new Label("—", labelStyle));
                add(slot).size(64, 52).pad(3);
            }
        }
    }

    private void buildPowerOfferUI() {
        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.YELLOW);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        add(new Label("Choose a power:", titleStyle)).padLeft(16).padRight(12).top().padTop(12);

        Button.ButtonStyle btnStyle = new Button.ButtonStyle();
        btnStyle.up = game.skin.getDrawable("white");

        for (ShopEffect offer : gameRun.getPendingPowerOffer()) {
            Button btn = new Button(btnStyle);

            String iconPath = offer.getIconPath();
            if (iconPath != null) {
                try {
                    Texture tex = TextureCache.get(iconPath);
                    btn.add(new Image(tex)).size(22, 22).padBottom(2);
                    btn.row();
                } catch (Exception ignored) {}
            }

            btn.add(new Label(offer.getName(), labelStyle)).pad(4);
            btn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    gameRun.selectPowerOffer(offer);
                    refresh(gameRun.getCurrentBoard());
                }
            });
            add(btn).size(110, 52).pad(4).top();
        }
    }

    private void buildRewardUI(Rewards rewards) {
        Label.LabelStyle titleStyle  = new Label.LabelStyle(font, Color.YELLOW);
        Label.LabelStyle labelStyle  = new Label.LabelStyle(font, Color.WHITE);

        add(new Label("Reward!", titleStyle)).padLeft(16).padRight(8).top().padTop(12);

        Table lines = new Table();
        lines.left().top();
        if (rewards.currency != null && rewards.currency > 0) {
            lines.add(new Label("+" + rewards.currency + " Gold", labelStyle)).left();
            lines.row();
        }
        if (rewards.portals != null) {
            for (String portal : rewards.portals) {
                lines.add(new Label("New path: " + portal, labelStyle)).left();
                lines.row();
            }
        }
        add(lines).expandX().left().pad(12).top();

        Button.ButtonStyle btnStyle = new Button.ButtonStyle();
        btnStyle.up = game.skin.getDrawable("white");
        Button continueBtn = new Button(btnStyle);
        continueBtn.add(new Label("Continue", labelStyle)).pad(8);
        continueBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameRun.dismissDisplayReward();
                refresh(gameRun.getCurrentBoard());
            }
        });
        add(continueBtn).size(88, 36).pad(6).top();
    }

    private void buildShopItemUI() {
        ShopItem item = gameRun.getPendingShopItem();
        ShopEffect effect = item.getEffect();

        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.YELLOW);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Button.ButtonStyle btnStyle = new Button.ButtonStyle();
        btnStyle.up = game.skin.getDrawable("white");

        String iconPath = effect.getIconPath();
        if (iconPath != null) {
            try {
                Texture tex = TextureCache.get(iconPath);
                add(new Image(tex)).size(36, 36).pad(6);
            } catch (Exception ignored) {}
        }

        Table info = new Table();
        info.left().top();
        info.add(new Label(effect.getName(), titleStyle)).left().padBottom(2);
        info.row();
        String desc = effect.getDescription();
        if (desc != null && !desc.isEmpty()) {
            Label descLabel = new Label(desc, labelStyle);
            descLabel.setWrap(true);
            info.add(descLabel).left().fillX().padBottom(4);
            info.row();
        }
        info.add(new Label(item.getPrice() + " Gold", labelStyle)).left();
        add(info).expandX().fillX().pad(8).left().top();

        boolean canBuy = gameRun.getPlayer().getCurrency() >= item.getPrice()
                         && effect.canPurchase(gameRun.getPlayer());

        Table buttons = new Table();
        Button buyBtn = new Button(btnStyle);
        buyBtn.add(new Label("Buy", labelStyle)).pad(6);
        buyBtn.setColor(canBuy ? Color.WHITE : new Color(0.4f, 0.4f, 0.4f, 1f));
        buyBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!canBuy) return;
                gameRun.purchaseShopItem();
                refresh(gameRun.getCurrentBoard());
            }
        });
        buttons.add(buyBtn).size(72, 32).pad(3);
        buttons.row();

        Button leaveBtn = new Button(btnStyle);
        leaveBtn.add(new Label("Leave", labelStyle)).pad(6);
        leaveBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameRun.dismissShopItem();
                refresh(gameRun.getCurrentBoard());
            }
        });
        buttons.add(leaveBtn).size(72, 32).pad(3);
        add(buttons).right().pad(6).top();
    }

    private void buildDialogueUI() {
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        Button.ButtonStyle btnStyle = new Button.ButtonStyle();
        btnStyle.up = game.skin.getDrawable("white");

        String line = gameRun.getCurrentDialogueLine();

        if (line != null) {
            Label text = new Label(line, labelStyle);
            text.setWrap(true);
            add(text).expandX().fillX().pad(16).left();

            Button nextBtn = new Button(btnStyle);
            nextBtn.add(new Label("Next", labelStyle)).pad(8);
            nextBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    gameRun.advanceLine();
                    refresh(gameRun.getCurrentBoard());
                }
            });
            add(nextBtn).size(72, 36).pad(6).top();
        } else {
            List<DialogueChoice> choices = gameRun.getCurrentChoices();
            if (choices != null) {
                for (int i = 0; i < choices.size(); i++) {
                    final int idx = i;
                    Button choiceBtn = new Button(btnStyle);
                    choiceBtn.add(new Label(choices.get(i).text, labelStyle)).pad(8);
                    choiceBtn.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            gameRun.selectChoice(idx);
                            refresh(gameRun.getCurrentBoard());
                        }
                    });
                    add(choiceBtn).size(130, 40).pad(4).top();
                }
            }
        }
    }

}
