package edu.nyu.yz518.minesweeper.game;

import java.util.Objects;

public class PlayerRecord {
    private final String uid;
    private final String name;
    private final int score;

    public PlayerRecord(String uid, String name, int score) {
        this.uid = uid;
        this.name = name;
        this.score = score;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "PlayerRecord{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerRecord that = (PlayerRecord) o;
        return score == that.score && uid.equals(that.uid) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, name, score);
    }
}
