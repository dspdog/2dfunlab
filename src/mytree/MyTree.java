package mytree;

import node.Node;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by user on 3/25/2015.
 */
public class MyTree<E extends Rectangle> { //a grid not a tree
    int width;
    int height;
    double maxNodeSize;

    Rectangle bounds;
    ArrayList<E>[][] nodes;// = new ArrayList[width][height];

    public MyTree(Rectangle _bounds, double maxSize){
        bounds = _bounds;
        maxNodeSize = Math.max(maxSize, 8); //minimum grid size 8 pixels to avoid slowdowns
        init();
    }

    public void insert(E node){ //uses upper left corner as anchor?
        double x = node.getX();
        double y = node.getY();
        if(bounds.contains(x,y)){
            int quantizedX = (int)(width*(x-bounds.getMinX())/bounds.width);
            int quantizedY = (int)(height*(y-bounds.getMinY())/bounds.height);
            nodes[quantizedX][quantizedY].add(node);
        }
    }

    public void init(){
        width = (int)(bounds.getWidth()/maxNodeSize);
        height = (int)(bounds.getHeight()/maxNodeSize);

        nodes = new ArrayList[width][height];
        for(int x=0; x<width; x++){
            for(int y=0; y<height; y++){
                nodes[x][y]=new ArrayList<E>();
            }
        }
    }

    public void clear(){
        for(int x=0; x<width; x++){
            for(int y=0; y<height; y++){
                nodes[x][y].clear();
            }
        }
    }
}
