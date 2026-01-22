package io.github.chess_sequel.gui.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.chess_sequel.ProjectName;
import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.GameState;
import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.player.Player;
import io.github.chess_sequel.gui.BoardInput;
import io.github.chess_sequel.gui.GameBoard;
import io.github.chess_sequel.gui.BoardActor;
import io.github.chess_sequel.gui.PieceIcon;


public class GameScreen implements Screen {

    final ProjectName game;

    GameRun gameRunInstance;
    private GameBoard board;

    private Stage uiStage;
    private Table rootTable;

    private Table leftMenu;
    private Table rightMenu;
    private Table centerContainer;
    private Table bottomMenu;
    private BoardActor boardActor;
    private Table inventoryTable;

    private OrthographicCamera camera;

    private Label goldLabel;
    private DragAndDrop dragAndDrop;
    BoardInput input;

    public GameScreen(ProjectName game){

        game.viewport.apply();
        //batch.setProjectionMatrix(game.viewport.getCamera().combined);
        uiStage = new Stage();

        dragAndDrop = new DragAndDrop();

        rootTable = new Table();
        rootTable.setFillParent(true);

        uiStage.addActor(rootTable);

        Player player = new Player();
        player.createPieceList();
        gameRunInstance = new GameRun(player);
        board = new GameBoard(gameRunInstance, game);
        camera = new OrthographicCamera();

        camera.setToOrtho(false, board.gameRun.getCurrentBoard().boardX, board.gameRun.getCurrentBoard().boardY);
        this.input = new BoardInput(board, board.gameRun);
        boardActor = new BoardActor(board, input, dragAndDrop);

        //This is awful practice must find a way to fix at some point
        this.input.setBoardActor(boardActor);



        InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(uiStage);
        mux.addProcessor(input);
        Gdx.input.setInputProcessor(mux);


        this.game = game;
        buildUILayout();


    }

    public DragAndDrop getDragAndDrop(){
        return dragAndDrop;
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

        //Change height
        if(
            boardActor.getGameBoard().getGame().getGameState() == GameState.CHANGING_MAP
        ){
            boardActor.setSize(boardActor.getGameBoard().getPixelWidth(), boardActor.getGameBoard().getPixelHeight());
            boardActor.getGameBoard().getGame().setGameState(GameState.NEUTRAL);

            centerContainer.invalidateHierarchy();
        }

        if(
            boardActor.getGameBoard().getGame().getGameState() == GameState.CHANGING_INVENTORY
        ){
            rebuildInventory();
            boardActor.getGameBoard().getGame().setGameState(GameState.NEUTRAL);
        }

        goldLabel.setText(String.valueOf(gameRunInstance.getPlayer().getCurrency()));

        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
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

        Table moneyBar = new Table();
        moneyBar.setBackground(game.skin.getDrawable("white"));
        moneyBar.pad(8);
        moneyBar.left();

        //Image goldIcon = new Image(game.skin.getDrawable("icon-gold"));
        BitmapFont font = new BitmapFont(); // default font
        Label.LabelStyle style = new Label.LabelStyle(font, Color.BLACK);
        goldLabel = new Label("0", style);

        //moneyBar.add(goldIcon).size(20).padRight(6);
        moneyBar.add(goldLabel).expandX().left();

        leftMenu = new Table();
        rightMenu = new Table();
        centerContainer = new Table();
        bottomMenu = new Table();

        rightMenu.add(moneyBar)
            .growX()
            .height(40).top();

        rightMenu.row();
        inventoryTable = new Table();
        inventoryTable.top().left();
        inventoryTable.setVisible(false);

        //inventoryTable.pad(8);
        rightMenu.add(inventoryTable).growY().top();

        ImageButton btn = new ImageButton(game.skin.getDrawable("change"));
        btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                gameRunInstance.alterLayout();
                if(gameRunInstance.getCurrentBoard() instanceof AlterLayoutBoard){
                    inventoryTable.setVisible(true);
                } else{
                    inventoryTable.setVisible(false);
                }
            }
        });

        leftMenu.setBackground(game.skin.getDrawable("blue"));
        rightMenu.setBackground(game.skin.getDrawable("red"));
        bottomMenu.setBackground(game.skin.getDrawable("yellow"));

        leftMenu.row();
        leftMenu.add(btn).size(100).pad(10).center();

        // --- TOP ROW ---
        rootTable.add(leftMenu).width(220).growY();
        rootTable.add(centerContainer).grow();
        rootTable.add(rightMenu).width(220).growY();

        // --- BOTTOM ROW ---
        rootTable.row();
        rootTable.add(bottomMenu)
            .colspan(3)
            .height(160)
            .growX();

        // Board goes inside center
        centerContainer.add(boardActor).grow();
        rebuildInventory();
    }

    private void rebuildInventory() {
        inventoryTable.clear();

        int col = 0;

        for (Piece piece : gameRunInstance.getPlayer().getPieceInventory()) {
            PieceIcon icon = new PieceIcon(piece, this);

            inventoryTable.add(icon).size(boardActor.getGameBoard().TILE_SIZE).pad(4);
            col++;

            if (col == 2) {
                inventoryTable.row();
                col = 0;
            }
        }
    }

    public BoardActor getBoardActor(){
        return boardActor;
    }

    public Table getInventoryTable() {
        return inventoryTable;
    }
}
