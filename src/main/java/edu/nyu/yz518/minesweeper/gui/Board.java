package edu.nyu.yz518.minesweeper.gui;

import edu.nyu.yz518.minesweeper.game.Tile;
import edu.nyu.yz518.minesweeper.game.TileState;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;

public class Board {
    private JFrame frame;
    private JButton newGame;
    private MineTile[][] uiTiles;
    private Tile[][] gameTiles;
    private Container grid;

    public Board(int rowSize, int colSize) {
        frame = new JFrame("Minesweeper by Yun Zhang, JAVA2021");
        newGame = new JButton("New Game");
        uiTiles = new MineTile[colSize][rowSize];
        gameTiles = new Tile[colSize][rowSize];
        grid = new Container();
        frame.setSize(17*rowSize, 17*colSize);
        frame.setLayout(new BorderLayout());
        frame.add(newGame, BorderLayout.SOUTH);

        grid.setLayout(new GridLayout(16,16, -1, -1));

        for(int i = 0; i < uiTiles.length; i++){
            for (int j = 0; j<uiTiles[0].length; j++){
                uiTiles[i][j] = new MineTile(j, i);
                gameTiles[i][j] = new Tile(TileState.NEW, 10);
                uiTiles[i][j].setTile(gameTiles[i][j]);
                grid.add(uiTiles[i][j]);
            }
        }

        frame.add(grid, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void mineButtonClicked(MouseEvent e){

    }
}
