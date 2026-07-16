package io.github.chess_sequel.gui.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.chess_sequel.ProjectName;
import io.github.chess_sequel.engine.player.Player;
import io.github.chess_sequel.engine.roster.KingDef;
import io.github.chess_sequel.engine.roster.KingRoster;
import io.github.chess_sequel.engine.save.PersistentData;
import io.github.chess_sequel.engine.save.SaveManager;
import java.util.List;
import io.github.chess_sequel.engine.pieces.Piece;

/** Title/main menu screen. Provides buttons to start a new run (→ KingSelectionScreen), view the piecetiary, or exit. */
public class MenuScreen implements Screen {

    Stage stage;
    ImageButton playButton, piecetiaryButton, optionsMenu, exitButton;
    ProjectName game;
    private BitmapFont font;

    public MenuScreen(ProjectName gameEntry){
        this.game = gameEntry;
    }

    public void show(){
        font = new BitmapFont();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        playButton = new ImageButton(game.skin.getDrawable("play"));
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, buildPlayer()));
            }
        });
        piecetiaryButton = new ImageButton(game.skin.getDrawable("piecetiary"));
        exitButton = new ImageButton(game.skin.getDrawable("exit"));

        TextButton.TextButtonStyle tbStyle = new TextButton.TextButtonStyle();
        tbStyle.font = font;
        tbStyle.fontColor = Color.WHITE;
        tbStyle.up = game.skin.getDrawable("blue");
        TextButton testButton = new TextButton("Test Board", tbStyle);
        testButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new TestScreen(game));
            }
        });

        table.add(playButton).pad(10).width(300).height(200).row();
        table.add(piecetiaryButton).pad(10).width(300).height(200).row();
        table.add(testButton).pad(10).width(300).height(60).row();
        table.add(exitButton).pad(10).width(300).height(200).row();
    }

    private Player buildPlayer() {
        PersistentData data = SaveManager.load();
        int kingIdx   = Math.max(0, Math.min(data.selectedKingIndex,  KingRoster.KINGS.size() - 1));
        KingDef def   = KingRoster.KINGS.get(kingIdx);
        int presetIdx = Math.max(0, Math.min(data.selectedPresetIndex, def.presets.size() - 1));
        List<Piece> pieces = def.presets.get(presetIdx).build();
        Player player = new Player();
        player.setTeam(pieces);
        return player;
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        if (font != null) font.dispose();
    }
}
