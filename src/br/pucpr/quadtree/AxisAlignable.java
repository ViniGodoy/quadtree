package br.pucpr.quadtree;

import java.awt.geom.Rectangle2D;

public interface AxisAlignable {
    boolean intersects(Rectangle2D aabb);
}
