package io.github.chess_sequel.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.chess_sequel.ProjectName;
import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.auras.Aura;
import io.github.chess_sequel.engine.interactables.Interactable;
import io.github.chess_sequel.engine.interactables.ShopItem;
import io.github.chess_sequel.engine.location.board.MapBoard;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;


/**
 * Renderer for the active board. Draws tiles, tile-level aura overlays, board-level aura
 * overlays (with fixed positions), pieces (dragged piece follows the cursor), animated move
 * highlights, interactable icons, and price labels for shop items.
 */
public class GameBoard {

    private static Texture lightTexture;
    private static Texture darkTexture;

    public int TILE_SIZE = 64;

    public GameRun gameRun;
    private ProjectName game;

    public GameBoard (GameRun gameRun, ProjectName game){
        lightTexture = new Texture("tiles/caramel-tile.png");
        darkTexture = new Texture("tiles/brown-tile.png");

        this.gameRun = gameRun;
        this.game = game;
    }


    public void render(Batch batch, float xorigin, float yorigin, BoardInput input){


        for (int x = 0; x < gameRun.getCurrentBoard().boardX; x++) {
            for (int y = 0; y < gameRun.getCurrentBoard().boardY; y++) {
                Texture tileTex = (x + y) % 2 == 0 ? lightTexture : darkTexture;
                batch.draw(tileTex, xorigin+x * TILE_SIZE, yorigin+y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

            }
        }
        // Tile-level aura images (e.g. petrifying aura)
        for (int x = 0; x < gameRun.getCurrentBoard().boardX; x++) {
            for (int y = 0; y < gameRun.getCurrentBoard().boardY; y++) {
                for (Aura aura : gameRun.getCurrentBoard().getTiles().get(x).get(y).getAuras()) {
                    if (aura.getImagePath() != null) {
                        batch.draw(TextureCache.get(aura.getImagePath()), xorigin + x * TILE_SIZE, yorigin + y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }

        // Board-level aura images with a fixed position (e.g. slime)
        for (Aura aura : gameRun.getCurrentBoard().getBoardAuras()) {
            if (aura.getImagePath() != null && aura.getAuraCol() >= 0) {
                batch.draw(TextureCache.get(aura.getImagePath()), xorigin + aura.getAuraCol() * TILE_SIZE, yorigin + aura.getAuraRow() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        for (Piece piece : gameRun.getCurrentBoard().getPieces()) {
            Texture tex = TextureCache.get(piece.getFilePath());
            if(piece == gameRun.getCurrentBoard().getSelectedPiece()){
                batch.draw(tex, xorigin + input.getDragX(), yorigin+input.getDragY(), TILE_SIZE, TILE_SIZE);
            } else{

                batch.draw(tex,
                    xorigin + piece.getCol() * TILE_SIZE,
                    yorigin + piece.getRow() * TILE_SIZE,
                    TILE_SIZE, TILE_SIZE);
            }
        }

        float time = (float)(System.currentTimeMillis() % 1000) / 1000f;
        float alpha = 0.5f + 0.5f * (float)Math.sin(time * Math.PI * 2);
        Texture highlightTex = TextureCache.get("tiles/highlight.png");

        if(gameRun.getCurrentBoard().getSelectedPiece() != null && !gameRun.getCurrentBoard().getValidMoves().isEmpty()){
            for(Move move: gameRun.getCurrentBoard().getValidMoves()){
                batch.setColor(1f, 1f, 1f, alpha);
                batch.draw(highlightTex, xorigin + move.getNewX() * TILE_SIZE, yorigin + move.getNewY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                batch.setColor(1f, 1f, 1f, 1f);
            }
        }

        if(input.hasSelectedPower() && input.getPendingPowerMoves() != null){
            for(Move move: input.getPendingPowerMoves()){
                batch.setColor(1f, 0.8f, 0f, alpha); // gold tint for power targets
                batch.draw(highlightTex, xorigin + move.getNewX() * TILE_SIZE, yorigin + move.getNewY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                batch.setColor(1f, 1f, 1f, 1f);
            }
        }

        if(gameRun.getCurrentBoard() instanceof MapBoard){
            MapBoard currentBoard = (MapBoard) gameRun.getCurrentBoard();
            for(Interactable interactable: currentBoard.getLocations()){
                if (interactable instanceof ShopItem) {
                    ShopItem shopItem = (ShopItem) interactable;
                    Texture tex = TextureCache.get(interactable.getFilePath());
                    batch.draw(tex, (xorigin+interactable.getCol() * TILE_SIZE) + TILE_SIZE/4, (yorigin+interactable.getRow() * TILE_SIZE) + TILE_SIZE/2, TILE_SIZE/2, TILE_SIZE/2);
                    game.font.getData().setScale(2f);
                    game.font.draw(batch, String.valueOf(shopItem.getPrice()), (xorigin+interactable.getCol() * TILE_SIZE) + TILE_SIZE/3, (yorigin+interactable.getRow() * TILE_SIZE) + TILE_SIZE/2);
                } else {
                    Texture tex = TextureCache.get(interactable.getFilePath());
                    batch.draw(tex, xorigin+interactable.getCol() * TILE_SIZE, yorigin+interactable.getRow() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        //batch.draw(TextureCache.get("tiles/highlight.png"), 500, 500, 64, 64);

    }

    public void dispose(){
        lightTexture.dispose();
        darkTexture.dispose();
    }

    public int getPixelWidth(){
        return TILE_SIZE* gameRun.getCurrentBoard().getTiles().size();
    }

    public int getPixelHeight(){
        return TILE_SIZE* gameRun.getCurrentBoard().getTiles().size();
    }

    public GameRun getGame(){
        return gameRun;
    }
}
