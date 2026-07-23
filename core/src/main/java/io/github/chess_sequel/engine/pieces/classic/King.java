package io.github.chess_sequel.engine.pieces.classic;


import io.github.chess_sequel.engine.location.Tile;
import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.moves.Castling;
import io.github.chess_sequel.engine.moves.InteractMove;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.PieceType;
import java.util.ArrayList;

/**
 * The King piece. Moves one square in any direction and supports castling on 8×8 boards.
 * Holds three lists of {@link io.github.chess_sequel.engine.powers.kingPower.KingPower}:
 * active (usable in-match), passive (permanent auras), and pre-game (pre-match / on-victory effects).
 * Passive powers are registered as board auras when the match starts and removed on capture.
 */
public class King extends Piece {

    @Override
    public String getDescription() { return "Moves one square in any direction. Protect it at all costs — if it falls, you lose."; }

    public King(int x, int y, boolean isBlack){
        super(x, y, isBlack, "king", ChessClass.CLASSIC);
        pieceType = PieceType.KING;
    }

    public King(int x, int y, boolean isBlack, String name, ChessClass chessClass){
        super(x, y, isBlack, name, chessClass);
        pieceType = PieceType.KING;
    }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck){
        if(board instanceof AlterLayoutBoard){
            return generateAlterLayoutMoves(board);
        }
        ArrayList<Move> moves = new ArrayList<>();
        if(isBlack == board.getWhiteToMove()) {
            //Currently no moves are showing and this setup also means king can take pieces of its own colour
            if (col - 1 >= 0) {
                moves.add(new Move(this, col - 1, row, board));
                if (row - 1 >= 0) {
                    moves.add(new Move(this, col - 1, row - 1, board));
                }
                if (row + 1 < board.boardY) {
                    moves.add(new Move(this, col - 1, row + 1, board));
                }
            }

            if (row - 1 >= 0) {
                moves.add(new Move(this, col, row - 1, board));
            }

            if (col + 1 < board.boardX) {
                moves.add(new Move(this, col + 1, row, board));
                if (row + 1 < board.boardY) {
                    moves.add(new Move(this, col + 1, row + 1, board));
                }
                if (row - 1 >= 0) {
                    moves.add(new Move(this, col + 1, row - 1, board));
                }
            }

            if (row + 1 < board.boardY) {
                moves.add(new Move(this, col, row + 1, board));
            }


            //Castling is being restricted to 8x8 boards
            if (isFirstMove && board.boardX == 8 && board.boardY == 8) {
                Piece rightCastle = board.getTiles().get(7).get(isBlack ? 7 : 0).getPiece();
                //I refuse to believe that there isn't a more efficient way to do this
                if (rightCastle != null && rightCastle.getIsFirstMove() && board.getTiles().get(6).get(isBlack ? 7 : 0).getPiece() == null && board.getTiles().get(5).get(isBlack ? 7 : 0).getPiece() == null && rightCastle.getName() == "castle" && !board.tileCheckEvaluator(board.getTiles().get(6).get(isBlack ? 7 : 0)) && !board.tileCheckEvaluator(board.getTiles().get(5).get(isBlack ? 7 : 0))) {
                    moves.add(new Castling(this, 6, isBlack ? 7 : 0, board, rightCastle));
                }

                Piece leftCastle = board.getTiles().get(0).get(isBlack ? 7 : 0).getPiece();
                //I refuse to believe that there isn't a more efficient way to do this
                if (leftCastle != null && leftCastle.getIsFirstMove() && board.getTiles().get(1).get(isBlack ? 7 : 0).getPiece() == null && board.getTiles().get(2).get(isBlack ? 7 : 0).getPiece() == null && board.getTiles().get(3).get(isBlack ? 7 : 0).getPiece() == null && leftCastle.getName() == "castle"  && !board.tileCheckEvaluator(board.getTiles().get(2).get(isBlack ? 7 : 0)) && !board.tileCheckEvaluator(board.getTiles().get(3).get(isBlack ? 7 : 0))) {
                    moves.add(new Castling(this, 2, isBlack ? 7 : 0, board, leftCastle));
                }
            }

            ArrayList<Move> trueMoves = new ArrayList<>();
            for (Move move : moves) {
                Tile dest = board.getTiles().get(move.getNewX()).get(move.getNewY());
                boolean blockedByPiece = dest.getPiece() != null && dest.getPiece().getIsBlack() == isBlack;
                boolean blockedByTerrain = dest.getInteractable() != null && !dest.getInteractable().isPassable();
                if (!blockedByPiece && !blockedByTerrain) {
                    trueMoves.add(move);
                } else if (!blockedByPiece && blockedByTerrain) {
                    trueMoves.add(new InteractMove(this, move.getNewX(), move.getNewY(), board));
                }
            }

            if(!ignoreCheck){
                ArrayList<Move> truerMoves = new ArrayList<>();
                for (Move move : trueMoves) {
                    if (!board.checkEvaluator(move)) {
                        truerMoves.add(move);
                    }
                }
                return truerMoves;
            }
            return trueMoves;

        }
        else{
            return moves;
        }
    }

}
