package edu.nyu.yz518.minesweeper.svc;

import edu.nyu.yz518.minesweeper.game.BoardState;
import edu.nyu.yz518.minesweeper.game.PlayerRecord;

import java.util.List;

public interface MinesweeperRestClient {
    List<Integer> getSavedBoards();
    Integer saveBoardState(BoardState state);
    BoardState getBoardState(int bid);
    void reportScore(PlayerRecord record);
    List<PlayerRecord> getTopPlayers();
}
