package io.github.chess_sequel.engine.roster;

import io.github.chess_sequel.engine.pieces.Piece;

import java.util.List;
import java.util.function.Supplier;

public class TeamPreset {

    public final String name;
    private final Supplier<List<Piece>> factory;

    public TeamPreset(String name, Supplier<List<Piece>> factory) {
        this.name = name;
        this.factory = factory;
    }

    public List<Piece> build() {
        return factory.get();
    }
}
