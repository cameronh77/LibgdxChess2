package io.github.chess_sequel.gui.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.chess_sequel.ProjectName;

public class MenuScreen implements Screen {

    Stage stage;
    ImageButton playButton, piecetiaryButton, optionsMenu, exitButton;
    ProjectName game;
    public MenuScreen(ProjectName gameEntry){
        this.game = gameEntry;

    }

    public void show(){
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        playButton = new ImageButton(game.skin.getDrawable("play"));

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setScreen(new KingSelectionScreen(game));
            }
        });
        piecetiaryButton = new ImageButton(game.skin.getDrawable("piecetiary"));
        exitButton = new ImageButton(game.skin.getDrawable("exit"));

        table.add(playButton).pad(10).width(300).height(200).row();
        table.add(piecetiaryButton).pad(10).width(300).height(200).row();
        table.add(exitButton).pad(10).width(300).height(200).row();
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
    }
}
