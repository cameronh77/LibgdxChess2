package io.github.chess_sequel.gui.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.chess_sequel.ProjectName;

public class MenuScreen implements Screen {

    ProjectName game;
    public MenuScreen(ProjectName gameEntry){
        this.game = gameEntry;
    }

    public void show(){

    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        game.font.draw(game.batch, "Welcome", 4, 1.5f);
        game.font.draw(game.batch, "click anywhere", 4, 1);

        game.batch.end();

        if (Gdx.input.isTouched()){
            game.setScreen(new GameScreen(game));
        }
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

    }
}
