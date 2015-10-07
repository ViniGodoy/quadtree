package br.pucpr.quadtree;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class QuadTree<T extends AxisAlignable> {
    public static final double MIN_SIZE = 15.0;

    private static class QuadNode<T extends AxisAlignable> {
        private Rectangle2D bounds;
        private QuadNode<T>[] children;

        private List<T> objects;

        public QuadNode(Rectangle2D bounds) {
            this.bounds = bounds;
        }

        private boolean hasMinimumSize() {
            return bounds.getWidth() <= MIN_SIZE || bounds.getHeight() <= MIN_SIZE;
        }

        private void subdivide() {
            double hw = bounds.getWidth() / 2.0;
            double hh = bounds.getHeight() / 2.0;

            Rectangle2D NW = new Rectangle2D.Double(
                    bounds.getMinX(), bounds.getMinY(), hw, hh);
            Rectangle2D NE = new Rectangle2D.Double(
                    bounds.getMinX() + hw, bounds.getMinY(), hw, hh);
            Rectangle2D SW = new Rectangle2D.Double(
                    bounds.getMinX(), bounds.getMinY() + hh, hw, hh);
            Rectangle2D SE = new Rectangle2D.Double(
                    bounds.getMinX() + hw, bounds.getMinY() + hh, hw, hh);

            children = new QuadNode[4];
            children[0] = new QuadNode<>(NW);
            children[1] = new QuadNode<>(NE);
            children[2] = new QuadNode<>(SW);
            children[3] = new QuadNode<>(SE);

            for (QuadNode<T> child : children) {
                child.add(objects.get(0));
            }
            objects = null;
        }

        public boolean add(T object) {
            //Does not intersect? So can't add.
            if (!object.intersects(bounds)) {
                return false;
            }

            //If intersects and hold no objects, add the object
            if (isEmpty()) {
                objects = new ArrayList<>();
                objects.add(object);
                return true;
            }

            //A minimum sized node will hold any number of objects
            if (objects != null && hasMinimumSize()) {
                objects.add(object);
                return true;
            }

            //If has no children and a second object is added, subdivide
            if (children == null) {
                subdivide();
            }

            //Add the object to the children
            for (QuadNode<T> child : children) {
                child.add(object);
            }
            return true;
        }

        public void find(Rectangle2D area, Set<T> result) {
            if (!bounds.intersects(area)) {
                return;
            }

            //If there are objects in this node, test if they colide.
            if (objects != null) {
                result.addAll(objects.stream()
                        .filter(nodeObject -> nodeObject.intersects(area))
                        .collect(Collectors.toList())
                );
                return;
            }

            //Otherwise, find if the children have colliding objects.
            if (children != null) {
                for (QuadNode<T> child : children) {
                    child.find(area, result);
                }
            }
        }

        public void debugFind(Rectangle2D area, Set<T> result, List<Rectangle2D> tested, List<Rectangle2D> hit) {
            if (!bounds.intersects(area)) {
                tested.add(bounds);
                return;
            }
            hit.add(bounds);

            //If there are objects in this node, test if they colide.
            if (objects != null) {
                result.addAll(objects.stream()
                        .filter(nodeObject -> nodeObject.intersects(area))
                        .collect(Collectors.toList())
                );
                return;
            }

            //Otherwise, find if the children have colliding objects.
            if (children != null) {
                for (QuadNode<T> child : children) {
                    child.debugFind(area, result, tested, hit);
                }
            }
        }

        public void remove(T object) {
            if (objects != null) {
                objects.remove(object);

                if (objects.isEmpty()) {
                    objects = null;
                }
            }

            if (children != null) {
                for (QuadNode<T> child : children) {
                    child.remove(object);
                }

                Set<T> objs = list();
                if (objs.size() <= 1) {
                    children = null;

                    objects = new ArrayList<>();
                    objects.addAll(objs);
                }
            }
        }

        public void getNodeBounds(List<Rectangle2D> nodeBounds) {
            nodeBounds.add(bounds);
            if (children != null) {
                for (QuadNode<T> child : children) {
                    child.getNodeBounds(nodeBounds);
                }
            }
        }

        public Set<T> list() {
            Set<T> result = new HashSet<>();
            list(result);
            return result;
        }

        private void list(Set<T> values) {
            if (objects != null) {
                values.addAll(objects);
            }

            if (children != null) {
                for (QuadNode<T> child : children) {
                    child.list(values);
                }
            }
        }

        public boolean isEmpty() {
            return objects == null && children == null;
        }
    }

    private QuadNode<T> root;

    public QuadTree(Rectangle2D bounds) {
        root = new QuadNode<>(bounds);
    }

    public void add(T object) {
        root.add(object);
    }

    public void remove(T object) {
        root.remove(object);
    }

    public Set<T> findCollisions(Rectangle2D area) {
        Set<T> result = new HashSet<>();
        root.find(area, result);
        return result;
    }

    public Set<T> debugFindCollisions(Rectangle2D area, List<Rectangle2D> tested, List<Rectangle2D> hit) {
        Set<T> result = new HashSet<>();
        root.debugFind(area, result, tested, hit);
        return result;
    }


    public List<Rectangle2D> getNodeBounds() {
        List<Rectangle2D> nodeBounds = new ArrayList<>();
        root.getNodeBounds(nodeBounds);
        return nodeBounds;
    }

    public Set<T> list() {
        return root.list();

    }
}
