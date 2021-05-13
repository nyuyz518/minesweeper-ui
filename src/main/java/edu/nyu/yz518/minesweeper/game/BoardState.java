package edu.nyu.yz518.minesweeper.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BoardState {
    private final Tile[][] gameBoard;
    private final int secondLeft;

    public BoardState(){
        this(null, 0);
    }
    public BoardState(Tile[][] gameBoard, int secondLeft) {
        this.gameBoard = gameBoard;
        this.secondLeft = secondLeft;
    }

    public Tile[][] getGameBoard() {
        return gameBoard;
    }

    public int getSecondLeft() {
        return secondLeft;
    }

    @Override
    public String toString() {
        return "BoardState{" +
                "gameBoard=" + Arrays.toString(gameBoard) +
                ", secondLeft=" + secondLeft +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardState that = (BoardState) o;
        return secondLeft == that.secondLeft && Arrays.equals(gameBoard, that.gameBoard);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(secondLeft);
        result = 31 * result + Arrays.hashCode(gameBoard);
        return result;
    }

}
