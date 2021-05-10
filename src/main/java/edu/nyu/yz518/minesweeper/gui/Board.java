package edu.nyu.yz518.minesweeper.gui;

import edu.nyu.yz518.minesweeper.game.Tile;
import edu.nyu.yz518.minesweeper.game.TileState;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public class Board {
    private final MineTile[][] uiTiles;
    private final Tile[][] gameTiles;
    private final Container grid;

    private final int mineCount;
    private int flagLeft;
    private final int rowSize;
    private final int colSize;

    private final JFrame frame;

    public Board(int rowSize, int colSize, int mineCount) {
        this.mineCount = mineCount;
        this.flagLeft = mineCount;
        this.rowSize = rowSize;
        this.colSize = colSize;
        frame = new JFrame("Minesweeper by Yun Zhang, JAVA2021");
        JButton newGame = new JButton("New Game");
        newGame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                newGame();
            }
        });

        uiTiles = new MineTile[rowSize][colSize];
        gameTiles = new Tile[rowSize][colSize];
        grid = new Container();
        frame.setSize(17 * rowSize, 17 * colSize);
        frame.setLayout(new BorderLayout());
        frame.add(newGame, BorderLayout.SOUTH);

        grid.setLayout(new GridLayout(16, 16, -1, -1));

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

        frame.add(grid, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

    private void newGame() {
        gridForEach((i, j) -> {
            gameTiles[i][j] = new Tile(TileState.NEW, 0);
            uiTiles[i][j].setTile(gameTiles[i][j]);
        }
        );
        placeMines();
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
                gameTiles[x][y].setState(TileState.WRONG);
                uiTiles[x][y].setTile(gameTiles[x][y]);
            }
        });
        JOptionPane.showMessageDialog(frame, "You Lose!");
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
                if (gameTiles[x][y].getState() == TileState.NEW) {
                    gameTiles[x][y].setState(TileState.FLIPPED);
                    uiTiles[x][y].setTile(gameTiles[x][y]);
                }
            });
            JOptionPane.showMessageDialog(frame, "You Win!");
        }
    }
}
