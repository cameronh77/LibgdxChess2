package io.github.chess_sequel.gui.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.chess_sequel.ProjectName;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.player.Player;
import io.github.chess_sequel.engine.roster.KingDef;
import io.github.chess_sequel.engine.roster.KingRoster;
import io.github.chess_sequel.gui.TextureCache;

import java.util.List;

public class KingSelectionScreen implements Screen {

    private final ProjectName game;
    private Stage stage;
    private BitmapFont font;

    private int selectedKing = 0;
    private int selectedPreset = 0;

    private Table kingListTable;
    private Table tabBar;
    private PreviewActor previewActor;

    public KingSelectionScreen(ProjectName game) {
        this.game = game;
    }

    @Override
    public void show() {
        font = new BitmapFont();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Top bar — fixed height so the raw texture size of "blue" doesn't drive the row height
        Table topBar = new Table();
        topBar.setBackground(game.skin.getDrawable("blue"));
        topBar.left().pad(6);
        TextButton backBtn = textBtn("< Back", Color.WHITE);
        backBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        topBar.add(backBtn).height(36).minWidth(100);
        rootTable.add(topBar).growX().colspan(2).height(50).row();

        // King list (left)
        kingListTable = new Table();
        kingListTable.setBackground(game.skin.getDrawable("blue"));
        kingListTable.top().pad(8);
        rootTable.add(kingListTable).width(220).growY();

        // Right side
        Table rightTable = new Table();
        rightTable.setBackground(game.skin.getDrawable("red"));
        rightTable.pad(10);

        tabBar = new Table();
        rightTable.add(tabBar).growX().height(56).row();

        previewActor = new PreviewActor();
        rightTable.add(previewActor).grow().row();

        TextButton confirmBtn = textBtn("Confirm", Color.WHITE);
        confirmBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                confirm();
            }
        });
        rightTable.add(confirmBtn).height(50).width(180).padTop(10);

        rootTable.add(rightTable).grow();

        refreshKingList();
        refreshTabBar();
        refreshPreview();
    }

    private void refreshKingList() {
        kingListTable.clear();
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);

        for (int i = 0; i < KingRoster.KINGS.size(); i++) {
            final int idx = i;
            KingDef def = KingRoster.KINGS.get(i);
            boolean selected = i == selectedKing;

            // Use Button so ChangeListener fires reliably regardless of which child is clicked
            Button.ButtonStyle cardStyle = new Button.ButtonStyle();
            cardStyle.up = game.skin.getDrawable(selected ? "yellow" : "white");
            Button card = new Button(cardStyle);
            card.pad(8);

            try {
                Image icon = new Image(TextureCache.get(def.iconPath));
                card.add(icon).size(72, 72).padBottom(6).row();
            } catch (Exception ignored) {}

            card.add(new Label(def.displayName, labelStyle));

            card.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) {
                    selectedKing = idx;
                    selectedPreset = 0;
                    refreshKingList();
                    refreshTabBar();
                    refreshPreview();
                }
            });

            // Explicit height so the drawable's native pixel height doesn't drive the cell
            kingListTable.add(card).growX().height(130).pad(5).row();
        }
    }

    private void refreshTabBar() {
        tabBar.clear();
        KingDef def = KingRoster.KINGS.get(selectedKing);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);

        for (int i = 0; i < def.presets.size(); i++) {
            final int idx = i;
            boolean selected = i == selectedPreset;

            TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
            style.font = font;
            style.fontColor = Color.BLACK;
            style.up = game.skin.getDrawable(selected ? "yellow" : "white");

            TextButton tab = new TextButton(def.presets.get(i).name, style);
            tab.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    selectedPreset = idx;
                    refreshTabBar();
                    refreshPreview();
                }
            });
            tabBar.add(tab).height(44).minWidth(110).pad(3);
        }
    }

    private void refreshPreview() {
        previewActor.setPieces(KingRoster.KINGS.get(selectedKing).presets.get(selectedPreset).build());
    }

    private void confirm() {
        List<Piece> pieces = KingRoster.KINGS.get(selectedKing).presets.get(selectedPreset).build();
        Player player = new Player();
        player.setTeam(pieces);
        game.setScreen(new GameScreen(game, player));
    }

    // Plain text button — no drawable background on the button itself; parent provides color context
    private TextButton textBtn(String text, Color color) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = color;
        return new TextButton(text, style);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.13f, 0.13f, 0.13f, 1f);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { dispose(); }
    @Override public void dispose() { stage.dispose(); font.dispose(); }

    // -------------------------------------------------------------------------

    private static class PreviewActor extends Actor {

        private List<Piece> pieces;

        public void setPieces(List<Piece> pieces) {
            this.pieces = pieces;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (pieces == null || pieces.isEmpty()) return;

            int maxCol = 3, maxRow = 3;
            for (Piece p : pieces) {
                if (p.getTrueCol() > maxCol) maxCol = p.getTrueCol();
                if (p.getTrueRow() > maxRow) maxRow = p.getTrueRow();
            }
            int gridCols = maxCol + 1;
            int gridRows = maxRow + 1;

            // Fill the available space — no artificial cap
            float tileSize = Math.min(getWidth() / gridCols, getHeight() / gridRows);

            Texture light = TextureCache.get("tiles/caramel-tile.png");
            Texture dark  = TextureCache.get("tiles/brown-tile.png");
            for (int c = 0; c < gridCols; c++) {
                for (int r = 0; r < gridRows; r++) {
                    batch.draw(
                        (c + r) % 2 == 0 ? light : dark,
                        getX() + c * tileSize, getY() + r * tileSize,
                        tileSize, tileSize
                    );
                }
            }

            for (Piece p : pieces) {
                batch.draw(
                    TextureCache.get(p.getFilePath()),
                    getX() + p.getTrueCol() * tileSize, getY() + p.getTrueRow() * tileSize,
                    tileSize, tileSize
                );
            }
        }
    }
}
