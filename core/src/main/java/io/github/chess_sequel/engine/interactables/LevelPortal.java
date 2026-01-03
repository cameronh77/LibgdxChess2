package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.Game;
import io.github.chess_sequel.engine.player.BotPlayer;

public class LevelPortal extends Interactable{

    private String level;
    private Game game;

    public LevelPortal(String level, Game game, int col, int row){
        this.level = level;
        this.game = game;
        this.col = col;
        this.row = row;
        this.filePath = "level-portal.png";
    }


    public void interaction(){
        game.progressGame(level);
    };

}
