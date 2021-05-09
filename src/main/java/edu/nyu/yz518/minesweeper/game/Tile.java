package edu.nyu.yz518.minesweeper.game;

public class Tile {
    private TileState state;
    private int content;
    public static final int MINE = 9;

    public Tile(TileState state, int content) {
        this.state = state;
        this.content = content;
    }

    public TileState getState() {
        return state;
    }

    public int getContent() {
        return content;
    }
}
