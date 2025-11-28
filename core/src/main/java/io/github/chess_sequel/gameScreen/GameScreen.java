package io.github.chess_sequel.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.chess_sequel.GameInstance;
import io.github.chess_sequel.gui.BoardInput;
import io.github.chess_sequel.gui.GameBoard;



public class GameScreen implements Screen {

    final GameInstance game;
    private SpriteBatch batch;
    private GameBoard board;

    private Stage uiStage;
    private Table rootTable;
    BoardInput input;

    public GameScreen(GameInstance game){
        batch = game.batch;


        game.viewport.apply();
        batch.setProjectionMatrix(game.viewport.getCamera().combined);
        uiStage = new Stage(new ScreenViewport());

        rootTable = new Table();
        rootTable.setFillParent(true);
        uiStage.addActor(rootTable);



        this.game = game;


    }

    @Override
    public void show() {
        board = new GameBoard();
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, board.board.boardX, board.board.boardY);
        this.input = new BoardInput(camera, board.board);

        Gdx.input.setInputProcessor(input);
    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(Color.BLACK);

        batch.begin();
        board.render(batch, input);
        batch.end();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
