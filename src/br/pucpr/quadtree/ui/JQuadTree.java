package br.pucpr.quadtree.ui;

import br.pucpr.quadtree.AxisAlignable;
import br.pucpr.quadtree.QuadTree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class JQuadTree extends JComponent {
    private static final int SIZE = 480;
    private QuadTree<Circle> quadTree = new QuadTree<>(new Rectangle2D.Double(0,0, SIZE, SIZE));
    private List<Rectangle2D> hit = new ArrayList<>();
    private List<Rectangle2D> tested = new ArrayList<>();

    public JQuadTree() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onMouseClick(e);
            }
        });
    }

    public void clear() {
        quadTree = new QuadTree<>(new Rectangle2D.Double(0,0, SIZE, SIZE));
        clearMarks();
        repaint();
    }

    private Rectangle2D mouseBounds(MouseEvent evt) {
        return new Rectangle2D.Double(evt.getX(), evt.getY(), 1, 1);
    }

    private void clearMarks() {
        quadTree.list().forEach(JQuadTree.Circle::clear);
        Circle.touchCount = 0;
        tested.clear();
        hit.clear();
    }

    private void onMouseClick(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            quadTree.add(new Circle(evt.getX(), evt.getY(), QuadTree.MIN_SIZE));
            clearMarks();
        } else if (evt.getButton() == 2) {
            clearMarks();
            quadTree.debugFindCollisions(mouseBounds(evt), tested, hit);
        } else if (evt.getButton() == MouseEvent.BUTTON3) {
            Set<Circle> objects = quadTree.findCollisions(mouseBounds(evt));
            objects.forEach(quadTree::remove);
            clearMarks();
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, SIZE, SIZE);
        g2d.setColor(Color.LIGHT_GRAY);

        for (int i = 0; i <= SIZE; i += QuadTree.MIN_SIZE) {
            g2d.drawLine(i, 0,    i, SIZE);
            g2d.drawLine(0, i, SIZE,    i);
        }

        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRect(0, 0, SIZE, SIZE);
        for (Rectangle2D bound : quadTree.getNodeBounds()) {
            g2d.setColor(Color.BLACK);
            g2d.draw(bound);
        }

        for (Rectangle2D box : hit) {
            Color green = new Color(10, 50, 10, 50);
            g2d.setColor(green);
            g2d.fill(box);
        }

        for (Rectangle2D box : tested) {
            g2d.setColor(Color.RED);
            g2d.draw(box);
        }

        for (Circle c : quadTree.list()) {
            c.paint(g2d);
        }

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, SIZE, SIZE, 30);
        g2d.setColor(Color.WHITE);
        int hits = hit.size() + tested.size();

        g2d.drawString(
                String.format("Objects: %d Collisions: Boxes: %d Objects: %d Total: %d",
                        quadTree.list().size(),
                        hits, Circle.touchCount,
                        hits + Circle.touchCount), 0, SIZE + 20);

        g2d.dispose();
    }



    @Override
    public Dimension getPreferredSize() {
        return new Dimension(SIZE, SIZE+50);
    }

    public void random(int count) {
        quadTree = new QuadTree<>(new Rectangle2D.Double(0,0, SIZE, SIZE));

        double d = (double)SIZE / count;
        List<Double> xs = new ArrayList<>();
        List<Double> ys = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            xs.add(i * d);
            ys.add(i * d);
        }
        Collections.shuffle(xs);
        Collections.shuffle(ys);
        for (int i = 0; i < count; i++) {
            quadTree.add(new Circle(xs.get(i), ys.get(i), QuadTree.MIN_SIZE));
        }

        clearMarks();
        repaint();
    }

    private static class Circle implements AxisAlignable {
        public static int touchCount = 0;

        private boolean found = false;
        private boolean touched = false;
        private Ellipse2D circle;


        public Circle(double x, double y, double radius) {
            double hr = radius / 2.0;
            circle = new Ellipse2D.Double(x - hr, y - hr, radius, radius);
        }

        public void paint(Graphics2D g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(found ? Color.GREEN : (touched ? Color.RED : Color.BLUE));
            g2d.fill(circle);
            g2d.dispose();
        }

        @Override
        public boolean intersects(Rectangle2D aabb) {
            found = circle.intersects(aabb);
            touched = true;
            touchCount++;
            return found;
        }

        public void clear() {
            found = false;
            touched = false;
        }

    }
}
