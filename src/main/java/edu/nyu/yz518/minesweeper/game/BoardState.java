package edu.nyu.yz518.minesweeper.game;

import java.util.Arrays;
import java.util.Objects;

public class BoardState {
    private final String uid;
    private final Tile[] gameBoard;
    private final int secondLeft;

    public BoardState(String uid, Tile[] gameBoard, int secondLeft) {
        this.uid = uid;
        this.gameBoard = gameBoard;
        this.secondLeft = secondLeft;
    }

    public String getUid() {
        return uid;
    }

    public Tile[] getGameBoard() {
        return gameBoard;
    }

    public int getSecondLeft() {
        return secondLeft;
    }

    @Override
    public String toString() {
        return "BoardState{" +
                "uid='" + uid + '\'' +
                ", gameBoard=" + Arrays.toString(gameBoard) +
                ", secondLeft=" + secondLeft +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardState that = (BoardState) o;
        return secondLeft == that.secondLeft && uid.equals(that.uid) && Arrays.equals(gameBoard, that.gameBoard);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(uid, secondLeft);
        result = 31 * result + Arrays.hashCode(gameBoard);
        return result;
    }

}
