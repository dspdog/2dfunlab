package qtree;

import java.awt.*;
import java.util.ArrayList;
//http://gamedevelopment.tutsplus.com/tutorials/quick-tip-use-quadtrees-to-detect-likely-collisions-in-2d-space--gamedev-374
/**
 * Created by user on 3/23/2015.
 */
public class Quadtree<E extends Rectangle> {

    private int MAX_OBJECTS = 10;
    private int MAX_LEVELS = 16;

    private int level;
    private ArrayList<E> objects;
    private Rectangle bounds;
    private Quadtree[] nodes;

    /*
     * Constructor
     */
    public Quadtree(int pLevel, Rectangle pBounds) {
        level = pLevel;
        objects = new ArrayList();
        bounds = pBounds;
        nodes = new Quadtree[4];
    }

    /*
     * Clears the quadtree
     */
    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    /*
     * Splits the node into 4 subnodes
     */
    private void split() {
        int subWidth = (int)(bounds.getWidth() / 2);
        int subHeight = (int)(bounds.getHeight() / 2);
        int x = (int)bounds.getX();
        int y = (int)bounds.getY();

        nodes[0] = new Quadtree(level+1, new Rectangle(x + subWidth, y, subWidth, subHeight));
        nodes[1] = new Quadtree(level+1, new Rectangle(x, y, subWidth, subHeight));
        nodes[2] = new Quadtree(level+1, new Rectangle(x, y + subHeight, subWidth, subHeight));
        nodes[3] = new Quadtree(level+1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
    }

    /*
    * Determine which node the object belongs to. -1 means
    * object cannot completely fit within a child node and is part
    * of the parent node
    */
    private int getIndex(E pRect) {
        int index = -1;
        double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
        double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

        // Object can completely fit within the top quadrants
        boolean topQuadrant = (pRect.getY() < horizontalMidpoint && pRect.getY() + pRect.getHeight() < horizontalMidpoint);
        // Object can completely fit within the bottom quadrants
        boolean bottomQuadrant = (pRect.getY() > horizontalMidpoint);

        // Object can completely fit within the left quadrants
        if (pRect.getX() < verticalMidpoint && pRect.getX() + pRect.getWidth() < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            }
            else if (bottomQuadrant) {
                index = 2;
            }
        }
        // Object can completely fit within the right quadrants
        else if (pRect.getX() > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            }
            else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }

    /*
     * Insert the object into the quadtree. If the node
     * exceeds the capacity, it will split and add all
     * objects to their corresponding nodes.
     */
    public void insert(E obj) {
        if (nodes[0] != null) {
            int index = getIndex(obj);

            if (index != -1) {
                nodes[index].insert(obj);

                return;
            }
        }

        objects.add(obj);

        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }

            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                }
                else {
                    i++;
                }
            }
        }
    }

    /*
 * Return all objects that could collide with the given object
 */
    public ArrayList<Rectangle> retrieve(ArrayList<Rectangle> returnObjects, E object) {
        int index = getIndex(object);
        if (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(returnObjects, object);
        }
        returnObjects.addAll(objects);
        return returnObjects;
    }
}