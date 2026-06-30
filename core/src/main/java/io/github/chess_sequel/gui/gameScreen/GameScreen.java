package io.github.chess_sequel.gui.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.chess_sequel.ProjectName;
import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.GameState;
import io.github.chess_sequel.engine.jsonTypes.Rewards;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.player.Player;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.gui.BoardActor;
import io.github.chess_sequel.gui.BoardInput;
import io.github.chess_sequel.gui.GameBoard;


/**
 * Main gameplay screen. Owns the {@link io.github.chess_sequel.engine.GameRun} instance,
 * the board renderer, the UI stage (left/right/bottom panels + win overlay), and the input
 * handler. Each frame it polls {@link io.github.chess_sequel.engine.GameState} to decide
 * whether to show the win overlay or rebuild the UI panels.
 */
public class GameScreen implements Screen {

    final ProjectName game;

    GameRun gameRunInstance;
    private GameBoard board;

    private Stage uiStage;
    private Table rootTable;
    private Table centerContainer;
    private BoardActor boardActor;

    private OrthographicCamera camera;
    private DragAndDrop dragAndDrop;
    BoardInput input;

    private LeftPanel leftPanel;
    private RightPanel rightPanel;
    private BottomPanel bottomPanel;
    private WinOverlay winOverlay;

    private final BitmapFont tooltipFont = new BitmapFont();
    private Table pieceTooltip;
    private Piece pendingHoverPiece;
    private float hoverTime = 0f;

    public GameScreen(ProjectName game, Player player) {
        game.viewport.apply();
        uiStage = new Stage();
        dragAndDrop = new DragAndDrop();

        rootTable = new Table();
        rootTable.setFillParent(true);
        uiStage.addActor(rootTable);

        gameRunInstance = new GameRun(player);
        board = new GameBoard(gameRunInstance, game);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, board.gameRun.getCurrentBoard().boardX, board.gameRun.getCurrentBoard().boardY);

        this.input = new BoardInput(board, board.gameRun);
        boardActor = new BoardActor(board, input, dragAndDrop);
        this.input.setBoardActor(boardActor);

        InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(uiStage);
        mux.addProcessor(input);
        Gdx.input.setInputProcessor(mux);

        this.game = game;
        buildUILayout();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        if (gameRunInstance.getGameState() == GameState.MATCH_WON) {
            showWinOverlay();
            gameRunInstance.setGameState(GameState.NEUTRAL);
        } else if (gameRunInstance.getGameState() == GameState.BOARD_STATE_CHANGED) {
            Board current = gameRunInstance.getCurrentBoard();
            boardActor.setSize(board.getPixelWidth(), board.getPixelHeight());
            centerContainer.invalidateHierarchy();
            leftPanel.refresh(current);
            rightPanel.refresh(current);
            bottomPanel.refresh(current);
            gameRunInstance.setGameState(GameState.NEUTRAL);
        }

        rightPanel.updateCurrency();
        updatePieceTooltip(delta);

        uiStage.act(delta);
        uiStage.draw();
    }

    private void updatePieceTooltip(float delta) {
        Piece hovered = input.getHoveredPiece();

        if (hovered == null || hovered.getDescription() == null || gameRunInstance.getCurrentBoard().getSelectedPiece() != null) {
            clearPieceTooltip();
            return;
        }

        if (hovered != pendingHoverPiece) {
            clearPieceTooltip();
            pendingHoverPiece = hovered;
        }

        hoverTime += delta;
        if (hoverTime < 3.0f) return;

        if (pieceTooltip == null) {
            Label.LabelStyle nameStyle = new Label.LabelStyle(tooltipFont, Color.BLACK);
            Label.LabelStyle descStyle = new Label.LabelStyle(tooltipFont, Color.DARK_GRAY);

            Label nameLabel = new Label(hovered.getName().toUpperCase(), nameStyle);
            Label descLabel = new Label(hovered.getDescription(), descStyle);
            descLabel.setWrap(true);

            pieceTooltip = new Table();
            pieceTooltip.setBackground(game.skin.getDrawable("white"));
            pieceTooltip.pad(8);
            pieceTooltip.add(nameLabel).left().row();
            pieceTooltip.add(descLabel).width(200).left();
            pieceTooltip.pack();
            uiStage.addActor(pieceTooltip);
        }

        Vector2 stagePos = uiStage.screenToStageCoordinates(new Vector2(input.getHoverScreenX(), input.getHoverScreenY()));
        float tx = stagePos.x + 16;
        float ty = stagePos.y - pieceTooltip.getHeight() / 2f;
        if (tx + pieceTooltip.getWidth() > uiStage.getWidth()) tx = stagePos.x - pieceTooltip.getWidth() - 8;
        ty = Math.max(0, Math.min(ty, uiStage.getHeight() - pieceTooltip.getHeight()));
        pieceTooltip.setPosition(tx, ty);
    }

    private void clearPieceTooltip() {
        if (pieceTooltip != null) { pieceTooltip.remove(); pieceTooltip = null; }
        hoverTime = 0f;
        pendingHoverPiece = null;
    }

    private void buildUILayout() {
        centerContainer = new Table();

        leftPanel   = new LeftPanel(gameRunInstance, game);
        rightPanel  = new RightPanel(gameRunInstance, game, this);
        bottomPanel = new BottomPanel(gameRunInstance, game, input);

        rootTable.add(leftPanel).width(220).growY();
        rootTable.add(centerContainer).grow();
        rootTable.add(rightPanel).width(220).growY();
        rootTable.row();
        rootTable.add(bottomPanel).colspan(3).height(160).growX();

        centerContainer.add(boardActor).grow();

        // Initial population of all panels
        Board current = gameRunInstance.getCurrentBoard();
        leftPanel.refresh(current);
        rightPanel.refresh(current);
        bottomPanel.refresh(current);
    }

    private void showWinOverlay() {
        winOverlay = new WinOverlay(gameRunInstance, game, this::dismissWinOverlay);
        uiStage.addActor(winOverlay);
    }

    private void dismissWinOverlay() {
        Rewards rewards = gameRunInstance.consumePendingRewards();
        if (rewards != null) {
            gameRunInstance.handleRewards(rewards);
        }
        if (winOverlay != null) {
            winOverlay.remove();
            winOverlay = null;
        }
        clearPieceTooltip();
        Board current = gameRunInstance.getCurrentBoard();
        boardActor.setSize(board.getPixelWidth(), board.getPixelHeight());
        centerContainer.invalidateHierarchy();
        leftPanel.refresh(current);
        rightPanel.refresh(current);
        bottomPanel.refresh(current);
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) { uiStage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}

    public BoardActor getBoardActor() { return boardActor; }
    public DragAndDrop getDragAndDrop() { return dragAndDrop; }
}
