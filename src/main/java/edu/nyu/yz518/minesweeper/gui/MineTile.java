package edu.nyu.yz518.minesweeper.gui;

import edu.nyu.yz518.minesweeper.game.Tile;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Objects;

public class MineTile extends JButton {
    private final int col;
    private final int row;
    private static final ImageIcon ICONS[] = {
            new ImageIcon(Objects.requireNonNull(MineTile.class.getResource("/img/0.png"))),
            new ImageIcon(Objects.requireNonNull(MineTile.class.getResource("/img/1.png"))),
            new ImageIcon(Objects.requireNonNull(MineTile.class.getResource("/img/2.png"))),
            new ImageIcon(Objects.requireNonNull(MineTile.class.getResource("/img/3.png"))),
            new ImageIcon(Objects.requireNonNull(MineTile.class.getResource("/img/4.png"))),
            new ImageIcon(Objects.requireNonNull(MineTile.class.getResource("/img/5.png"))),
            new ImageIcon(Objects.requireNonNull(MineTile.class.getResource("/img/6.png"))),
            new ImageIcon(Objects.requireNonNull(MineTile.class.getResource("/img/7.png"))),
            new ImageIcon(Objects.requireNonNull(MineTile.class.getResource("/img/8.png"))),
            new ImageIcon(Objects.requireNonNull(MineTile.class.getResource("/img/9.png"))),
            new ImageIcon(Objects.requireNonNull(MineTile.class.getResource("/img/10.png"))),
            new ImageIcon(Objects.requireNonNull(MineTile.class.getResource("/img/11.png"))),
            new ImageIcon(Objects.requireNonNull(MineTile.class.getResource("/img/12.png")))
    };

    public MineTile(int col, int row){
        this.col = col;
        this.row = row;
        this.setContentAreaFilled(false);
        this.setPreferredSize(new Dimension(16, 16));
        this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public void setTile(Tile tile){
        switch (tile.getState()){
            case NEW:
                this.setIcon(ICONS[10]);
                break;
            case FLAGGED:
                this.setIcon(ICONS[11]);
            default:
                this.setIcon(ICONS[tile.getContent() <= 9? tile.getContent() : 0]);
        }
    }
}
