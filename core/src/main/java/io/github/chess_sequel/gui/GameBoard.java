package io.github.chess_sequel.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.chess_sequel.ProjectName;
import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.interactables.Interactable;
import io.github.chess_sequel.engine.interactables.ShopItem;
import io.github.chess_sequel.engine.location.board.MapBoard;
import io.github.chess_sequel.engine.location.board.ShopBoard;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;


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
        //System.out.println(game.getCurrentBoard().getPieces());
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

        if(gameRun.getCurrentBoard().getSelectedPiece() != null && !gameRun.getCurrentBoard().getValidMoves().isEmpty()){
            // calculate pulsing alpha
            float time = (float)(System.currentTimeMillis() % 1000) / 1000f; // cycles every 1 sec
            float alpha = 0.5f + 0.5f * (float)Math.sin(time * Math.PI * 2); // 0->1->0

            Texture highlightTex = TextureCache.get("tiles/highlight.png"); // white square texture

            for(Move move: gameRun.getCurrentBoard().getValidMoves()){
                int col = move.getNewX();
                int row = move.getNewY();

                batch.setColor(1f, 1f, 1f, alpha); // set alpha for pulsing
                batch.draw(highlightTex, xorigin+col * TILE_SIZE, yorigin+row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                batch.setColor(1f, 1f, 1f, 1f); // reset to fully opaque for next draw
            }
        }

        if(gameRun.getCurrentBoard() instanceof MapBoard){
            MapBoard currentBoard = (MapBoard) gameRun.getCurrentBoard();
            for(Interactable interactable: currentBoard.getLocations()){
                Texture tex = TextureCache.get(interactable.getFilePath());
                batch.draw(tex, xorigin+interactable.getCol() * TILE_SIZE, yorigin+interactable.getRow() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        if(gameRun.getCurrentBoard() instanceof ShopBoard){
            ShopBoard currentBoard = (ShopBoard) gameRun.getCurrentBoard();
            for(Interactable interactable: currentBoard.getWares()){
                if(interactable instanceof ShopItem){
                    Texture tex = TextureCache.get(interactable.getFilePath());
                    batch.draw(tex, (xorigin+interactable.getCol() * TILE_SIZE) + TILE_SIZE/4, (yorigin+interactable.getRow() * TILE_SIZE) + TILE_SIZE/2, TILE_SIZE/2, TILE_SIZE/2);
                    ShopItem shopItem = (ShopItem) interactable;
                    game.font.getData().setScale(2f);
                    game.font.draw(batch, String.valueOf(shopItem.getPrice()), (xorigin+interactable.getCol() * TILE_SIZE) + TILE_SIZE/3, (yorigin+interactable.getRow() * TILE_SIZE) + TILE_SIZE/2);

                } else{
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
