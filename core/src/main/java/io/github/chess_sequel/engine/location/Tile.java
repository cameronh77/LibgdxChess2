package io.github.chess_sequel.engine.location;


import io.github.chess_sequel.engine.auras.Aura;
import io.github.chess_sequel.engine.interactables.Interactable;
import io.github.chess_sequel.engine.pieces.Piece;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class Tile {
    private int xord;
    private int yord;

    private Piece piece;
    private Interactable interactable;
    private ArrayList<Aura> auras = new ArrayList<>();

    public Tile(int xord, int yord){
        this.xord=xord;
        this.yord=yord;
    }


    public int getXord() {
        return xord;
    }

    public int getYord(){
        return yord;
    }

    public Piece getPiece(){
        return piece;
    }

    public void setPiece(Piece piece){
        this.piece = piece;
    }

    public Interactable getInteractable(){
        return interactable;
    }

    public void setInteractable(Interactable interactable){
        this.interactable = interactable;
    }

    public ArrayList<Aura> getAuras(){
        return auras;
    }

    public void removeAura(Piece owner, String name){
        for(Aura aura: auras){
            if(owner == aura.getOwner() && name == aura.getName()){
                auras.remove(aura);
                break;
            }
        }
    }
}
