package io.github.chess_sequel;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.chess_sequel.gui.gameScreen.MenuScreen;

public class ProjectName extends Game {

    public SpriteBatch batch;
    public BitmapFont font;
    public FitViewport viewport;

    public Skin skin;

    public void create(){
        batch = new SpriteBatch();
        font = new BitmapFont();
        viewport = new FitViewport(10, 10);

        skin = new Skin();
        skin.add("blue", new Texture("ui/blue.png"));
        skin.add("yellow", new Texture("ui/yellow.png"));
        skin.add("red", new Texture("ui/red.png"));
        skin.add("change", new Texture("ui/change-layout.png"));
        skin.add("white", new Texture("ui/white.png"));
        skin.add("play", new Texture("playButton.png"));
        skin.add("piecetiary", new Texture("piecetiaryButton.png"));
        skin.add("exit", new Texture("exitButton.png"));

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
