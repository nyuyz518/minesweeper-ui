package edu.nyu.yz518.minesweeper.svc;

import edu.nyu.yz518.minesweeper.game.BoardState;
import edu.nyu.yz518.minesweeper.game.PlayerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class MinesweeperRestClientImpl implements MinesweeperRestClient {
    private final Logger LOG = LoggerFactory.getLogger(MinesweeperRestClientImpl.class);

    private final RestTemplate restTemplate;
    private final String svcLocation;
    public MinesweeperRestClientImpl(
            RestTemplate restTemplate,
            @Value("${edu.nyu.yz518.svc.url}") String svcLocation){
        this.restTemplate = restTemplate;
        this.svcLocation = svcLocation;
    }

    public List<Integer> getSavedBoards(){
        ResponseEntity<List<Integer>> response
                = this.restTemplate.exchange(
                svcLocation+ "board",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Integer>>(){}
        );
        return response.getBody();
    }

    public Integer saveBoardState(BoardState state){
        return this.restTemplate.postForObject(svcLocation + "board", state, Integer.class);
    }

    public BoardState getBoardState(int bid){
        return this.restTemplate.getForObject(svcLocation + "board/" + bid, BoardState.class);
    }

    public void reportScore(PlayerRecord record){
        this.restTemplate.postForObject(svcLocation + "score", record, Object.class);
    }

    public List<PlayerRecord> getTopPlayers(){
        ResponseEntity<List<PlayerRecord>> response
                = this.restTemplate.exchange(
                        svcLocation+ "score",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<PlayerRecord>>(){}
                );
        return response.getBody();
    }
}
