package edu.nyu.yz518.minesweeper.svc;

import edu.nyu.yz518.minesweeper.game.BoardState;
import edu.nyu.yz518.minesweeper.game.PlayerRecord;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MinesweeperRestClientImpl implements MinesweeperRestClient {
    public void saveBoardState(String uid, BoardState state){

    }

    public BoardState getBoardState(String uid){
        return null;
    }

    public void reportScore(String uid, int score){

    }

    public List<PlayerRecord> getTopPlayer(){
        return null;
    }
}
