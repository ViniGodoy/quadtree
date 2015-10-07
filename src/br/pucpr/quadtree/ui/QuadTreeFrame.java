package br.pucpr.quadtree.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class QuadTreeFrame extends JFrame {
    private JPanel pnlNorth;
    private JPanel pnlSouth;
    private JQuadTree quadTree = new JQuadTree();

    public QuadTreeFrame() {
        setSize(new Dimension(800, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("QuadTree demonstration - by Vinícius G. Mendonça");
        setLayout(new BorderLayout());
        add(getPnlNorth(), BorderLayout.NORTH);
        add(getPnlSouth(), BorderLayout.SOUTH);
        add(quadTree, BorderLayout.CENTER);
    }

    public static void main(String args[]) {
        EventQueue.invokeLater(() -> new QuadTreeFrame().setVisible(true));
    }

    private JPanel getPnlNorth() {
        if (pnlNorth != null) {
            return pnlNorth;
        }

        pnlNorth = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlNorth.add(new JLabel("Right click to create a circle. Left click to remove. Middle click to test collision."));
        return pnlNorth;
    }

    private JPanel getPnlSouth() {
        if (pnlSouth != null) {
            return pnlSouth;
        }

        JButton btnRandom = new JButton("Random");
        btnRandom.addActionListener(this::onRandom);
        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(this::onClear);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(this::onClose);

        pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSouth.add(btnRandom);
        pnlSouth.add(btnClear);
        pnlSouth.add(btnClose);
        return pnlSouth;
    }

    private void onRandom(ActionEvent evt) {
        quadTree.random(200);
    }


    private void onClear(ActionEvent evt) {
        quadTree.clear();
    }

    private void onClose(ActionEvent evt) {
        System.exit(0);
    }
}
