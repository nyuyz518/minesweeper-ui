package edu.nyu.yz518.minesweeper.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class PlayerRecord {
    private final String uid;
    private final int score;

    @JsonCreator
    public PlayerRecord(@JsonProperty("uid") String uid, @JsonProperty("score") int score) {
        this.uid = uid;
        this.score = score;
    }

    public String getUid() {
        return uid;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "PlayerRecord{" +
                "uid='" + uid + '\'' +
                ", score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerRecord that = (PlayerRecord) o;
        return score == that.score && uid.equals(that.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, score);
    }
}
