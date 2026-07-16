package io.github.chess_sequel.gui.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.chess_sequel.ProjectName;
import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.King;
import io.github.chess_sequel.engine.pieces.factories.PieceFactory;
import io.github.chess_sequel.engine.player.BotPlayer;
import io.github.chess_sequel.engine.player.Player;
import io.github.chess_sequel.gui.TextureCache;

import java.util.ArrayList;
import java.util.List;

/**
 * Sandbox screen for testing pieces. Use the White/Black toggle to choose which side to
 * place for, click a piece in the left list to select it, then click a board cell to place
 * it. Right-click a board cell to remove whatever is there.
 */
public class TestScreen implements Screen {

    private static final Color LIGHT_SQUARE = new Color(0.9f,  0.85f, 0.75f, 1f);
    private static final Color DARK_SQUARE  = new Color(0.45f, 0.3f,  0.18f, 1f);

    private final ProjectName game;
    private Stage stage;
    private BitmapFont font;

    private int boardW = 8, boardH = 8;
    private TextField wField, hField;

    // false = White (player, isBlack=false), true = Black (bot, isBlack=true)
    private boolean sideIsBlack = false;
    private TextButton sideToggleBtn;

    private String searchFilter    = "";
    private String selectedPieceId = null;
    private Table  pieceListTable;
    private Label  selectedLabel;

    private TestBoardActor testBoardActor;

    public TestScreen(ProjectName game) {
        this.game = game;
    }

    @Override
    public void show() {
        font  = new BitmapFont();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // ── Top bar ────────────────────────────────────────────────────────
        Table topBar = new Table();
        topBar.setBackground(game.skin.getDrawable("blue"));
        topBar.pad(4);

        topBar.add(textBtn("< Back", Color.WHITE, () -> game.setScreen(new MenuScreen(game))))
              .height(26).minWidth(58).padRight(8);
        topBar.add(new Label("Test Board", labelStyle(Color.WHITE))).expandX().left();
        topBar.add(new Label("W:", labelStyle(Color.WHITE))).padRight(2);
        wField = new TextField("8", tfStyle());
        topBar.add(wField).width(36).height(24).padRight(6);
        topBar.add(new Label("H:", labelStyle(Color.WHITE))).padRight(2);
        hField = new TextField("8", tfStyle());
        topBar.add(hField).width(36).height(24).padRight(6);
        topBar.add(textBtn("Apply", Color.WHITE, this::applyDimensions)).height(24).minWidth(44).padRight(4);
        topBar.add(textBtn("Clear", Color.WHITE, () -> testBoardActor.clearPieces())).height(24).minWidth(44).padRight(10);
        topBar.add(textBtn("PLAY", Color.WHITE, this::launchGame)).height(26).minWidth(58);

        root.add(topBar).growX().height(40).colspan(2).row();

        // ── Left panel ─────────────────────────────────────────────────────
        Table left = new Table();
        left.setBackground(game.skin.getDrawable("blue"));
        left.top().pad(4);

        // Toggle which side newly placed pieces belong to.
        sideToggleBtn = textBtn("Side: White", Color.WHITE, () -> {
            sideIsBlack = !sideIsBlack;
            sideToggleBtn.setText(sideIsBlack ? "Side: Black" : "Side: White");
        });
        left.add(sideToggleBtn).growX().height(26).padBottom(3).row();

        TextField searchField = new TextField("", tfStyle());
        searchField.setMessageText("Search...");
        searchField.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                searchFilter = searchField.getText().toLowerCase();
                selectedPieceId = null;
                refreshPieceList();
            }
        });
        left.add(searchField).growX().height(24).padBottom(2).row();

        selectedLabel = new Label("None selected", labelStyle(Color.YELLOW));
        left.add(selectedLabel).growX().padBottom(3).row();

        pieceListTable = new Table();
        pieceListTable.top().left();
        ScrollPane pane = new ScrollPane(pieceListTable);
        pane.setScrollingDisabled(true, false);
        pane.setFadeScrollBars(false);
        left.add(pane).grow().row();

        root.add(left).width(175).growY();

        // ── Centre: board ──────────────────────────────────────────────────
        Table centre = new Table();
        centre.setBackground(game.skin.getDrawable("red"));

        testBoardActor = new TestBoardActor(boardW, boardH);
        centre.add(testBoardActor).grow().maxWidth(400).maxHeight(400);

        // Left-click: place selected piece. Right-click: remove piece at cell.
        testBoardActor.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                float cw = testBoardActor.getWidth()  / testBoardActor.bW;
                float ch = testBoardActor.getHeight() / testBoardActor.bH;
                int col = Math.max(0, Math.min((int)(x / cw), testBoardActor.bW - 1));
                int row = Math.max(0, Math.min((int)(y / ch), testBoardActor.bH - 1));
                if (button == Input.Buttons.RIGHT) {
                    testBoardActor.removePieceAt(col, row);
                } else if (selectedPieceId != null) {
                    // isShop=true → isBlack=false (white); isShop=false → isBlack=true (black)
                    Piece p = PieceFactory.generatePiece(selectedPieceId, col, row, !sideIsBlack);
                    if (p != null) testBoardActor.place(p, col, row);
                }
                return true;
            }
        });

        root.add(centre).grow();

        refreshPieceList();
    }

    // ── Piece list ─────────────────────────────────────────────────────────────

    private void refreshPieceList() {
        pieceListTable.clear();
        for (String id : PieceFactory.ALL_PIECE_IDS) {
            if (!searchFilter.isEmpty() && !id.contains(searchFilter)) continue;

            boolean isSelected = id.equals(selectedPieceId);
            final String pieceId = id;

            Button.ButtonStyle style = new Button.ButtonStyle();
            style.up = game.skin.getDrawable(isSelected ? "yellow" : "white");
            Button btn = new Button(style);
            btn.pad(3, 6, 3, 6);
            btn.add(new Label(id, labelStyle(Color.BLACK))).expandX().left();
            btn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    selectedPieceId = isSelected ? null : pieceId;
                    selectedLabel.setText(selectedPieceId != null ? "Selected: " + selectedPieceId : "None selected");
                    refreshPieceList();
                }
            });
            pieceListTable.add(btn).growX().padBottom(2).row();
        }
    }

    // ── Board size ─────────────────────────────────────────────────────────────

    private void applyDimensions() {
        try {
            int w = Integer.parseInt(wField.getText().trim());
            int h = Integer.parseInt(hField.getText().trim());
            if (w >= 2 && w <= 16 && h >= 2 && h <= 16) {
                boardW = w; boardH = h;
                testBoardActor.setDimensions(w, h);
            }
        } catch (NumberFormatException ignored) {}
    }

    // ── Launch game ────────────────────────────────────────────────────────────

    private void launchGame() {
        List<Piece> playerPieces = new ArrayList<>();
        List<Piece> botPieces    = new ArrayList<>();
        for (Piece p : testBoardActor.getPlacedPieces()) {
            if (p.isBlack()) botPieces.add(p);
            else             playerPieces.add(p);
        }

        // Auto-add Classic King if a side has none.
        if (playerPieces.stream().noneMatch(p -> p instanceof King))
            playerPieces.add(0, new King(0, 0, false));
        if (botPieces.stream().noneMatch(p -> p instanceof King))
            botPieces.add(0, new King(0, boardH - 1, true));

        Player     playerObj  = new Player();
        playerObj.setTeam(playerPieces);
        BotPlayer  botObj     = new BotPlayer(3, botPieces);
        MatchBoard testBoard  = new MatchBoard(boardW, boardH, playerObj, botObj);
        GameRun    testRun    = new GameRun(playerObj, testBoard);
        botObj.setGameRun(testRun);

        game.setScreen(new GameScreen(game, testRun));
    }

    // ── UI helpers ─────────────────────────────────────────────────────────────

    @FunctionalInterface interface Action { void run(); }

    private TextButton textBtn(String text, Color color, Action action) {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.font = font; s.fontColor = color;
        TextButton btn = new TextButton(text, s);
        btn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { action.run(); }
        });
        return btn;
    }

    private Label.LabelStyle labelStyle(Color color) { return new Label.LabelStyle(font, color); }

    private TextField.TextFieldStyle tfStyle() {
        TextField.TextFieldStyle s = new TextField.TextFieldStyle();
        s.font = font; s.fontColor = Color.BLACK;
        s.background = game.skin.getDrawable("white");
        s.cursor = game.skin.newDrawable("white", Color.DARK_GRAY);
        return s;
    }

    // ── Screen lifecycle ───────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.13f, 0.13f, 0.13f, 1f);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   { dispose(); }
    @Override public void dispose() { stage.dispose(); font.dispose(); }

    // ── Board actor ────────────────────────────────────────────────────────────

    static class TestBoardActor extends Actor {

        int bW, bH;
        private final List<Piece> placedPieces = new ArrayList<>();

        TestBoardActor(int w, int h) { bW = w; bH = h; }

        void place(Piece p, int col, int row) {
            placedPieces.removeIf(x -> x.getCol() == col && x.getRow() == row);
            p.setStartCords();
            placedPieces.add(p);
        }

        void removePieceAt(int col, int row) {
            placedPieces.removeIf(p -> p.getCol() == col && p.getRow() == row);
        }

        void clearPieces() { placedPieces.clear(); }

        void setDimensions(int w, int h) {
            bW = w; bH = h;
            placedPieces.removeIf(p -> p.getCol() >= w || p.getRow() >= h);
        }

        List<Piece> getPlacedPieces() { return placedPieces; }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (bW <= 0 || bH <= 0) return;
            float cw = getWidth()  / bW;
            float ch = getHeight() / bH;
            Texture white = TextureCache.get("ui/white.png");

            for (int c = 0; c < bW; c++) {
                for (int r = 0; r < bH; r++) {
                    batch.setColor((c + r) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE);
                    batch.draw(white, getX() + c * cw, getY() + r * ch, cw, ch);
                }
            }
            batch.setColor(Color.WHITE);

            for (Piece p : placedPieces) {
                try {
                    batch.draw(TextureCache.get(p.getFilePath()),
                               getX() + p.getCol() * cw,
                               getY() + p.getRow() * ch, cw, ch);
                } catch (Exception ignored) {}
            }
        }
    }
}
