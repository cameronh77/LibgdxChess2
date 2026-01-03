package io.github.chess_sequel.gui.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
import io.github.chess_sequel.engine.GameState;
import io.github.chess_sequel.engine.player.Player;
import io.github.chess_sequel.gui.BoardInput;
import io.github.chess_sequel.gui.GameBoard;
import io.github.chess_sequel.gui.gameScreen.BoardActor;


public class GameScreen implements Screen {

    final ProjectName game;

    Game gameInstance;
    private SpriteBatch batch;
    private GameBoard board;

    private Stage uiStage;
    private Table rootTable;

    private Table leftMenu;
    private Table rightMenu;
    private Table centerContainer;
    private Table bottomMenu;
    private BoardActor boardActor;

    private OrthographicCamera camera;
    BoardInput input;

    public GameScreen(ProjectName game){
        batch = game.batch;

        game.viewport.apply();
        batch.setProjectionMatrix(game.viewport.getCamera().combined);
        uiStage = new Stage(new ScreenViewport());

        rootTable = new Table();
        rootTable.setFillParent(true);
        uiStage.addActor(rootTable);

        Player player = new Player();
        player.createPieceList();
        gameInstance = new Game(player);
        board = new GameBoard(gameInstance);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, board.game.getCurrentBoard().boardX, board.game.getCurrentBoard().boardY);
        this.input = new BoardInput(camera, board, board.game);
        boardActor = new BoardActor(board, input);

        //This is awful practice must find a way to fix at some point
        this.input.setBoardActor(boardActor);



        InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(uiStage);
        mux.addProcessor(input);
        Gdx.input.setInputProcessor(mux);


        this.game = game;
        buildUILayout();


    }

    @Override
    public void show() {

        /**
        Player player = new Player();
        player.createPieceList();
        gameInstance = new Game(player);
        board = new GameBoard(gameInstance);
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, board.game.getCurrentBoard().boardX, board.game.getCurrentBoard().boardY);
        this.input = new BoardInput(camera, board, board.game);

        Gdx.input.setInputProcessor(input);
        */
    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(Color.BLACK);

        //batch.begin();
        //board.render(batch, input);
        //batch.end();

        if(
            boardActor.getGameBoard().getGame().getGameState() == GameState.CHANGING_MAP
        ){
            boardActor.setSize(boardActor.getGameBoard().getPixelWidth(), boardActor.getGameBoard().getPixelHeight());
            boardActor.getGameBoard().getGame().setGameState(GameState.NEUTRAL);
        }

        uiStage.act(delta);
        uiStage.draw();

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

    private void buildUILayout() {

        leftMenu = new Table();
        rightMenu = new Table();
        centerContainer = new Table();
        bottomMenu = new Table();

        // Debug borders so you can SEE the layout
        leftMenu.setDebug(true);
        rightMenu.setDebug(true);
        centerContainer.setDebug(true);
        bottomMenu.setDebug(true);
        //boardActor.setDebug(true);

        // Add to root table (3 columns)
        rootTable.add(leftMenu).width(220).growY();
        rootTable.add(centerContainer).grow();
        rootTable.add(rightMenu).width(220).growY();

        // Bottom menu sits inside center column
        centerContainer.row();
        centerContainer.add(boardActor).grow(); // space for board rendering
        centerContainer.row();
        centerContainer.add(bottomMenu).height(160).growX();
    }

}
