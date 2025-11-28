package io.github.chess_sequel;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.chess_sequel.gameScreen.MenuScreen;

public class GameInstance extends Game {

    public SpriteBatch batch;
    public BitmapFont font;
    public FitViewport viewport;

    public void create(){
        batch = new SpriteBatch();
        font = new BitmapFont();
        viewport = new FitViewport(10, 10);

        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight()/ Gdx.graphics.getHeight());

        this.setScreen(new MenuScreen(this));
    }

    @Override
    public void render(){
        super.render();
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
