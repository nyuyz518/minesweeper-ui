package edu.nyu.yz518.minesweeper.gui;

import edu.nyu.yz518.minesweeper.game.PlayerRecord;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TopPlayers extends JDialog {
    public TopPlayers(JFrame frame, List<PlayerRecord> players){
        super(frame, "Top Player" );
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        DefaultListModel<String> model = new DefaultListModel<>();
        for(PlayerRecord rec : players){
            model.addElement(rec.getUid() + "   --   " + rec.getScore() + "sec left             ");
        }
        JList<String> list = new JList<>(model);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(list);
        JButton ok = new JButton("OK");
        ok.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                dispose();
            }
        });
        panel.add(ok);
        this.add(panel);
    }
}
