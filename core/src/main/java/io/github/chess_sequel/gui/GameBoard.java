package io.github.chess_sequel.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.chess_sequel.engine.Game;
import io.github.chess_sequel.engine.interactables.Interactable;
import io.github.chess_sequel.engine.location.board.MapBoard;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;


public class GameBoard {

    private static Texture lightTexture;
    private static Texture darkTexture;

    public int TILE_SIZE = 64;

    public Game game;

    public GameBoard (Game game){
        lightTexture = new Texture("tiles/caramel-tile.png");
        darkTexture = new Texture("tiles/brown-tile.png");

        this.game = game;
    }


    public void render(SpriteBatch batch, BoardInput input){


        for (int x = 0; x < game.getCurrentBoard().boardX; x++) {
            for (int y = 0; y < game.getCurrentBoard().boardY; y++) {
                Texture tileTex = (x + y) % 2 == 0 ? lightTexture : darkTexture;
                batch.draw(tileTex, + x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        //System.out.println(game.getCurrentBoard().getPieces());
        for (Piece piece : game.getCurrentBoard().getPieces()) {
            Texture tex = TextureCache.get(piece.getFilePath());
            if(piece == game.getCurrentBoard().getSelectedPiece()){
                batch.draw(tex, input.getDragX(), input.getDragY(), TILE_SIZE, TILE_SIZE);
            } else{

                batch.draw(tex,
                    piece.getCol() * TILE_SIZE,
                    piece.getRow() * TILE_SIZE,
                    TILE_SIZE, TILE_SIZE);
            }
        }

        if(game.getCurrentBoard().getSelectedPiece() != null && !game.getCurrentBoard().getValidMoves().isEmpty()){
            // calculate pulsing alpha
            float time = (float)(System.currentTimeMillis() % 1000) / 1000f; // cycles every 1 sec
            float alpha = 0.5f + 0.5f * (float)Math.sin(time * Math.PI * 2); // 0->1->0

            Texture highlightTex = TextureCache.get("tiles/highlight.png"); // white square texture

            for(Move move: game.getCurrentBoard().getValidMoves()){
                int col = move.getNewX();
                int row = move.getNewY();

                batch.setColor(1f, 1f, 1f, alpha); // set alpha for pulsing
                batch.draw(highlightTex, col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                batch.setColor(1f, 1f, 1f, 1f); // reset to fully opaque for next draw
            }
        }

        if(game.getCurrentBoard() instanceof MapBoard){
            MapBoard currentBoard = (MapBoard) game.getCurrentBoard();
            for(Interactable interactable: currentBoard.getLocations()){
                Texture tex = TextureCache.get(interactable.getFilePath());
                batch.draw(tex, interactable.getCol() * TILE_SIZE, interactable.getRow() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        //batch.draw(TextureCache.get("tiles/highlight.png"), 500, 500, 64, 64);

    }

    public void dispose(){
        lightTexture.dispose();
        darkTexture.dispose();
    }

    public int getPixelWidth(){
        return TILE_SIZE*game.getCurrentBoard().getTiles().size();
    }

    public int getPixelHeight(){
        return TILE_SIZE*game.getCurrentBoard().getTiles().size();
    }
}
