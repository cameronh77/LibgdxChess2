package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.auras.PetrifyingAura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.pieces.Piece;

public class GoblinQueenMove extends Move{

    public GoblinQueenMove(Piece piece, int newX, int newY, Board board) {
        super(piece, newX, newY, board);
    }

    public void execute(){
        //Vacate old piece
        board.getTiles().get(oldX).get(oldY).setPiece(null);

        //Adding aura to new tiles
        if(newX-1 < board.boardX && newX-1 >=0){
            board.getTiles().get(newX -1).get(newY).getAuras().add(new PetrifyingAura(movingPiece));

            if(newY-1 < board.boardY && newY-1 >= 0){
                board.getTiles().get(newX-1).get(newY-1).getAuras().add(new PetrifyingAura(movingPiece));
            }

            if(newY+1 < board.boardY){
                board.getTiles().get(newX-1).get(newY+1).getAuras().add(new PetrifyingAura(movingPiece));
            }
        }

        if(newX+1 < board.boardX && newX+1 >= 0){
            board.getTiles().get(newX +1).get(newY).getAuras().add(new PetrifyingAura(movingPiece));

            if(newY-1 < board.boardY && newY-1 >= 0){
                board.getTiles().get(newX+1).get(newY-1).getAuras().add(new PetrifyingAura(movingPiece));
            }

            if(newY+1 < board.boardY && newY+1 >= 0){
                board.getTiles().get(newX+1).get(newY+1).getAuras().add(new PetrifyingAura(movingPiece));
            }
        }

        if(newY -1 < board.boardY && newY-1 >= 0){
            board.getTiles().get(newX).get(newY-1).getAuras().add(new PetrifyingAura(movingPiece));
        }

        if(newY +1 < board.boardY && newY+1 >= 0){
            board.getTiles().get(newX).get(newY+1).getAuras().add(new PetrifyingAura(movingPiece));
        }

        //Removing aura from tiles
        if(oldX-1 < board.boardX && oldX-1 >=0){
            board.getTiles().get(oldX -1).get(oldY).removeAura(movingPiece, "petrifyingAura");

            if(oldY-1 < board.boardY && oldY-1 >= 0){
                board.getTiles().get(oldX-1).get(oldY-1).removeAura(movingPiece, "petrifyingAura");
            }

            if(oldY+1 < board.boardY){
                board.getTiles().get(oldX-1).get(oldY+1).removeAura(movingPiece, "petrifyingAura");
            }
        }

        if(oldX+1 < board.boardX && oldX+1 >= 0){
            board.getTiles().get(oldX +1).get(oldY).removeAura(movingPiece, "petrifyingAura");

            if(oldY-1 < board.boardY && oldY-1 >= 0){
                board.getTiles().get(oldX+1).get(oldY-1).removeAura(movingPiece, "petrifyingAura");
            }

            if(oldY+1 < board.boardY && oldY+1 >= 0){
                board.getTiles().get(oldX+1).get(oldY+1).removeAura(movingPiece, "petrifyingAura");
            }
        }

        if(oldY -1 < board.boardY && oldY-1 >= 0){
            board.getTiles().get(oldX).get(oldY-1).removeAura(movingPiece, "petrifyingAura");
        }

        if(oldY +1 < board.boardY && oldY+1 >= 0){
            board.getTiles().get(oldX).get(oldY+1).removeAura(movingPiece, "petrifyingAura");
        }
        //Remove captured piece from the board
        board.getPieces().remove(capturedPiece);

        //Add the piece being moved to the new tile
        board.getTiles().get(newX).get(newY).setPiece(movingPiece);

        //Set the moving pieces location
        movingPiece.setCol(newX);
        movingPiece.setRow(newY);

        //Alter first move state
        movingPiece.setIsFirstMove(false);

        //Set en passant tile
        if(movingPiece.getName() == "pawn" && Math.abs(oldY-newY)==2){
            int[] enPassantTile = {newX, (oldY+newY)/2};
            board.setEnPassantTile(enPassantTile);
        } else{
            board.setEnPassantTile(null);
        }

        //Flip the turn
        if(board instanceof MatchBoard){
            board.setWhiteToMove(!board.getWhiteToMove());
        }

        if(capturedPiece != null){
            capturedPiece.onCapture(movingPiece);
            //System.out.println(movingPiece + " captured " + capturedPiece);
        }

        board.tick();

    }




    public void undo(){
        //Revert Tile ownership
        board.getTiles().get(newX).get(newY).setPiece(capturedPiece);

        //Adding aura to new tiles
        if(oldX-1 < board.boardX && oldX-1 >=0){
            board.getTiles().get(oldX -1).get(oldY).getAuras().add(new PetrifyingAura(movingPiece));

            if(oldY-1 < board.boardY && oldY-1 >= 0){
                board.getTiles().get(oldX-1).get(oldY-1).getAuras().add(new PetrifyingAura(movingPiece));
            }

            if(oldY+1 < board.boardY){
                board.getTiles().get(oldX-1).get(oldY+1).getAuras().add(new PetrifyingAura(movingPiece));
            }
        }

        if(oldX+1 < board.boardX && oldX+1 >= 0){
            board.getTiles().get(oldX +1).get(oldY).getAuras().add(new PetrifyingAura(movingPiece));

            if(oldY-1 < board.boardY && oldY-1 >= 0){
                board.getTiles().get(oldX+1).get(oldY-1).getAuras().add(new PetrifyingAura(movingPiece));
            }

            if(oldY+1 < board.boardY && oldY+1 >= 0){
                board.getTiles().get(oldX+1).get(oldY+1).getAuras().add(new PetrifyingAura(movingPiece));
            }
        }

        if(oldY -1 < board.boardY && oldY-1 >= 0){
            board.getTiles().get(oldX).get(oldY-1).getAuras().add(new PetrifyingAura(movingPiece));
        }

        if(oldY +1 < board.boardY && oldY+1 >= 0){
            board.getTiles().get(oldX).get(oldY+1).getAuras().add(new PetrifyingAura(movingPiece));
        }

        //Removing aura from tiles
        if(newX-1 < board.boardX && newX-1 >=0){
            board.getTiles().get(newX -1).get(newY).removeAura(movingPiece, "petrifyingAura");

            if(newY-1 < board.boardY && newY-1 >= 0){
                board.getTiles().get(newX-1).get(newY-1).removeAura(movingPiece, "petrifyingAura");
            }

            if(newY+1 < board.boardY){
                board.getTiles().get(newX-1).get(newY+1).removeAura(movingPiece, "petrifyingAura");
            }
        }

        if(newX+1 < board.boardX && newX+1 >= 0){
            board.getTiles().get(newX +1).get(newY).removeAura(movingPiece, "petrifyingAura");

            if(newY-1 < board.boardY && newY-1 >= 0){
                board.getTiles().get(newX+1).get(newY-1).removeAura(movingPiece, "petrifyingAura");
            }

            if(newY+1 < board.boardY && newY+1 >= 0){
                board.getTiles().get(newX+1).get(newY+1).removeAura(movingPiece, "petrifyingAura");
            }
        }

        if(newY -1 < board.boardY && newY-1 >= 0){
            board.getTiles().get(newX).get(newY-1).removeAura(movingPiece, "petrifyingAura");
        }

        if(newY +1 < board.boardY && newY+1 >= 0){
            board.getTiles().get(newX).get(newY+1).removeAura(movingPiece, "petrifyingAura");
        }

        //Re add captured pieces to the board
        if(capturedPiece != null) {
            board.getPieces().add(capturedPiece);
        }

        //Set the piece ownership back to old tile
        board.getTiles().get(oldX).get(oldY).setPiece(movingPiece);

        //Revert piece co ordinates to original location
        movingPiece.setCol(oldX);
        movingPiece.setRow(oldY);

        //Revert first move status
        movingPiece.setIsFirstMove(isFirstMove);

        //Revert en passant tile
        board.setEnPassantTile(enPassantTile);

        //Flip the turn
        if(board instanceof MatchBoard){
            board.setWhiteToMove(!board.getWhiteToMove());
        }

        board.untick();

        if(capturedPiece != null){
            capturedPiece.undoOnCapture(movingPiece);
        }


    }
}
