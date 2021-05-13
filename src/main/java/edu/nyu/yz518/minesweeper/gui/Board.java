package edu.nyu.yz518.minesweeper.gui;

import edu.nyu.yz518.minesweeper.game.BoardState;
import edu.nyu.yz518.minesweeper.game.PlayerRecord;
import edu.nyu.yz518.minesweeper.game.Tile;
import edu.nyu.yz518.minesweeper.game.TileState;
import edu.nyu.yz518.minesweeper.svc.MinesweeperRestClient;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

@Component
public class Board {
    private final MinesweeperRestClient restClient;
    private final MineTile[][] uiTiles;
    private Tile[][] gameTiles = null;
    private final Container grid;

    private final int mineCount;
    private final int timeLimit;
    private final ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture<?> currentTimer;
    private boolean blocked;
    private int flagLeft;
    private int secondsLeft;
    private final int rowSize;
    private final int colSize;

    private final JFrame frame;
    private final JLabel status;

    class Updater implements Runnable {
        @Override
        public void run() {
            if(--secondsLeft == 0){
                SwingUtilities.invokeLater(Board.this::loseGame);
            }
            SwingUtilities.invokeLater(Board.this::updateStatus);
        }
    };

    public Board(
            @Value("${edu.nyu.yz518.minesweeper-ui.row-count}")int rowSize,
            @Value("${edu.nyu.yz518.minesweeper-ui.col-count}")int colSize,
            @Value("${edu.nyu.yz518.minesweeper-ui.mine-count}")int mineCount,
            @Value("${edu.nyu.yz518.minesweeper-ui.time-limit}")int timeLimit,
            MinesweeperRestClient restClient) {
        this.restClient = restClient;
        this.mineCount = mineCount;
        this.timeLimit = timeLimit;
        this.secondsLeft = timeLimit;
        this.flagLeft = mineCount;
        this.rowSize = rowSize;
        this.colSize = colSize;
        frame = new JFrame("Yun Zhang, JAVA2021");
        status = new JLabel("Ready");

        JButton newGame = new JButton("New Game");
        newGame.addMouseListener((new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                newGame();
            }
        }));

        JButton top = new JButton("Top Player");
        top.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JDialog topPlayer = new TopPlayers(frame, restClient.getTopPlayers()) ;
                topPlayer.pack();
                topPlayer.setVisible(true);
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(newGame);
        panel.add(top);
        JPanel sidePanel = new JPanel();
        JComboBox<Integer> savedGames
                = new JComboBox<Integer>(restClient.getSavedBoards().toArray(new Integer[]{}));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));

        JButton save = new JButton("Save Game State");
        save.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                if(!blocked) {
                    BoardState current = new BoardState(gameTiles, secondsLeft);
                    restClient.saveBoardState(current);
                }
            }
        });
        JButton refresh = new JButton("Refresh Saved");
        refresh.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                savedGames.removeAllItems();
                for(Integer item : restClient.getSavedBoards()){
                    savedGames.addItem(item);
                }

            }
        });

        JButton load = new JButton("Load Saved Game");
        load.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                Integer i = (Integer) savedGames.getSelectedItem();
                if(i != null) {
                    newGame();
                    BoardState saved = restClient.getBoardState(i);
                    if(saved == null || saved.getGameBoard() == null){
                        JOptionPane.showMessageDialog(null, "Incompatible Saved Data", "Alert", JOptionPane.ERROR_MESSAGE);
                    } else if(Board.this.rowSize == saved.getGameBoard().length &&
                        Board.this.colSize == saved.getGameBoard()[0].length) {
                        gameTiles = saved.getGameBoard();
                        secondsLeft = saved.getSecondLeft();

                        gridForEach((x, y) -> {
                            uiTiles[x][y].setTile(gameTiles[x][y]);
                        });
                    } else {
                        JOptionPane.showMessageDialog(null, "Incompatible Dimension", "Alert", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        buttons.add(refresh);
        buttons.add(load);
        sidePanel.add(save);
        sidePanel.add(savedGames);
        sidePanel.add(buttons);

        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        currentTimer = scheduledExecutorService.scheduleAtFixedRate(new Updater(), 0, 1, TimeUnit.SECONDS);

        uiTiles = new MineTile[rowSize][colSize];
        gameTiles = new Tile[rowSize][colSize];
        grid = new Container();
        grid.setSize(17 * colSize, 17 * rowSize);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.NORTH);
        frame.add(status, BorderLayout.SOUTH);
        frame.add(sidePanel, BorderLayout.EAST);

        grid.setLayout(new GridLayout(rowSize, colSize, -1, -1));

        gridForEach((i, j) -> {
                    uiTiles[i][j] = new MineTile(j, i);
                    gameTiles[i][j] = new Tile(TileState.NEW, 0);
                    uiTiles[i][j].setTile(gameTiles[i][j]);
                    uiTiles[i][j].addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            tileClicked(e);
                        }
                    });
                    grid.add(uiTiles[i][j]);
                }
        );
        placeMines();
        blocked = false;

        frame.add(grid, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void gridForEach(BiConsumer<Integer, Integer> callback) {
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                callback.accept(i, j);
            }
        }
    }

    private void tileClicked(MouseEvent e) {
        if(blocked){
            return;
        }
        MineTile t = (MineTile) e.getSource();
        int x = t.getRow();
        int y = t.getCol();

        if (SwingUtilities.isLeftMouseButton(e)
        && gameTiles[x][y].getState() == TileState.NEW) {
            if (gameTiles[x][y].getContent() == Tile.MINE) {
                loseGame();
            } else {
                Queue<Pair<Integer, Integer>> toClear = new LinkedList<>();
                toClear.add(new ImmutablePair<>(x, y));
                clearZeroCell(toClear);
                winGame();
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            if (gameTiles[x][y].getState() == TileState.FLAGGED) {
                gameTiles[x][y].setState(TileState.NEW);
                this.flagLeft ++;
            } else if (gameTiles[x][y].getState() == TileState.NEW && this.flagLeft > 0) {
                gameTiles[x][y].setState(TileState.FLAGGED);
                this.flagLeft --;
                if(flagLeft == 0){
                    winGame();
                }
            }
            uiTiles[x][y].setTile(gameTiles[x][y]);
        }
    }

    private void updateStatus(){
        status.setText("Time Left: " + this.secondsLeft + " Flags Left: " + this.flagLeft);
    }

    private void newGame() {
        if(currentTimer != null) {
            currentTimer.cancel(false);
            currentTimer = null;
        }
        gridForEach((i, j) -> {
            gameTiles[i][j] = new Tile(TileState.NEW, 0);
            uiTiles[i][j].setTile(gameTiles[i][j]);
        });
        placeMines();
        blocked = false;
        this.secondsLeft = timeLimit;
        currentTimer = scheduledExecutorService.scheduleAtFixedRate(new Updater(), 0, 1, TimeUnit.SECONDS);
    }

    private List<Pair<Integer, Integer>> getAdjacentCells(Pair<Integer, Integer> cell) {
        int x = cell.getLeft();
        int y = cell.getRight();
        List<Pair<Integer, Integer>> ret = new ArrayList<>();
        for (int i = Math.max(x - 1, 0); i <= Math.min(x + 1, rowSize - 1); i++) {
            for (int j = Math.max(y - 1, 0); j <= Math.min(y + 1, colSize - 1); j++) {
                if (i != x || j != y) {
                    ret.add(new ImmutablePair<>(i, j));
                }
            }
        }
        return ret;
    }

    private void placeMines() {
        int[] shuffle = new int[rowSize * colSize];
        Random rand = new Random();
        for (int i = 0; i < mineCount; i++) {
            shuffle[i] = 1;
        }
        for (int i = 0; i < mineCount; i++) {
            int shuffleTo = rand.nextInt(rowSize * colSize);
            int t = shuffle[i];
            shuffle[i] = shuffle[shuffleTo];
            shuffle[shuffleTo] = t;
        }
        gridForEach((x, y) -> {
            if (shuffle[x * colSize + y] == 1) {
                gameTiles[x][y].setContent(Tile.MINE);
            }
        });
        gridForEach((x, y) -> {
            if (gameTiles[x][y].getContent() != Tile.MINE) {
                int adjacentMine = getAdjacentCells(new ImmutablePair<>(x, y)).stream()
                        .map(c -> gameTiles[c.getLeft()][c.getRight()].getContent() == Tile.MINE ? 1 : 0)
                        .reduce(0, Integer::sum);
                gameTiles[x][y].setContent(adjacentMine);
            }
        });
    }

    private void clearZeroCell(Queue<Pair<Integer, Integer>> toClear) {
        while (toClear.size() != 0) {
            Pair<Integer, Integer> curr = toClear.poll();
            int x = curr.getLeft();
            int y = curr.getRight();
            if (gameTiles[x][y].getState() == TileState.NEW) {
                gameTiles[x][y].setState(TileState.FLIPPED);
                uiTiles[x][y].setTile(gameTiles[x][y]);
                if (gameTiles[x][y].getContent() == 0) {
                    List<Pair<Integer, Integer>> adjacent = getAdjacentCells(curr);
                    adjacent.forEach(toClear::offer);
                }
            }
        }
    }

    public void loseGame() {
        gridForEach((x, y) -> {
            if (gameTiles[x][y].getState() == TileState.NEW) {
                gameTiles[x][y].setState(TileState.FLIPPED);
                uiTiles[x][y].setTile(gameTiles[x][y]);
            } else if (gameTiles[x][y].getState() == TileState.FLAGGED
            && gameTiles[x][y].getContent() != Tile.MINE) {
                gameTiles[x][y].setState(TileState.WRONGFLAG);
                uiTiles[x][y].setTile(gameTiles[x][y]);
            }
        });
        if(currentTimer != null) {
            currentTimer.cancel(true);
            currentTimer = null;
        }
        status.setText("You Lose!");
        blocked = true;
    }

    public void winGame() {
        AtomicBoolean win = new AtomicBoolean(true);
        if(flagLeft == 0){
            gridForEach((x, y) -> {
                if (gameTiles[x][y].getContent() != Tile.MINE && gameTiles[x][y].getState() == TileState.FLAGGED) {
                    win.set(false);
                }
            });
        } else {
            gridForEach((x, y) -> {
                if (gameTiles[x][y].getContent() != Tile.MINE && gameTiles[x][y].getState() == TileState.NEW) {
                    win.set(false);
                }
            });
        }
        if (win.get()) {
            gridForEach((x, y) -> {
                if (gameTiles[x][y].getState() == TileState.NEW && gameTiles[x][y].getContent() != Tile.MINE) {
                    gameTiles[x][y].setState(TileState.FLIPPED);
                    uiTiles[x][y].setTile(gameTiles[x][y]);
                }
            });
            if(currentTimer != null) {
                currentTimer.cancel(true);
                currentTimer = null;
            }
            status.setText("You Win!");
            JDialog winDialog = new JDialog(frame, "Record your score");
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
            panel.add(new JLabel("Score: "+Board.this.secondsLeft));
            panel.add(new JLabel("Name"));
            JTextField field = new JTextField();
            panel.add(field);
            JButton ok = new JButton("OK");
            ok.addMouseListener(new MouseAdapter(){
                @Override
                public void mousePressed(MouseEvent e) {
                    if(!"".equals(field.getText())) {
                        restClient.reportScore(new PlayerRecord(field.getText(), Board.this.secondsLeft));
                        winDialog.dispose();
                    }
                }
            });
            JButton cancel = new JButton("Cancel");
            cancel.addMouseListener(new MouseAdapter(){
                @Override
                public void mousePressed(MouseEvent e) {
                    winDialog.dispose();
                }
            });
            JPanel panel2 = new JPanel();
            panel2.setLayout(new FlowLayout());
            panel2.add(ok);
            panel2.add(cancel);
            panel.add(panel2);
            winDialog.add(panel);
            winDialog.pack();
            winDialog.setVisible(true);

            blocked = true;
        }
    }
}
