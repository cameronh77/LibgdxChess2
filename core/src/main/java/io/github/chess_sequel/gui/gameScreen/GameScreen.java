package io.github.chess_sequel.gui.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.chess_sequel.ProjectName;
import io.github.chess_sequel.engine.Game;
import io.github.chess_sequel.engine.player.Player;
import io.github.chess_sequel.gui.BoardInput;
import io.github.chess_sequel.gui.GameBoard;



public class GameScreen implements Screen {

    final ProjectName game;

    Game gameInstance;
    private SpriteBatch batch;
    private GameBoard board;

    private Stage uiStage;
    private Table rootTable;
    BoardInput input;

    public GameScreen(ProjectName game){
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

        gameInstance = new Game(new Player());
        board = new GameBoard(gameInstance);
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, board.game.getCurrentBoard().boardX, board.game.getCurrentBoard().boardY);
        this.input = new BoardInput(camera, board, board.game);

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
