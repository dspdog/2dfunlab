package mytree;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by user on 3/25/2015.
 */
public class MyTree<E extends Rectangle> { //a grid not a tree
    int widthInCells;
    int heightInCells;
    double maxNodeSize;

    Rectangle treeBounds;
    ArrayList<E>[][] cells; //TODO make this 1D

    public MyTree(Rectangle _bounds, double maxSize){
        treeBounds = _bounds;
        maxNodeSize = Math.max(maxSize, 8); //minimum grid size 8 pixels to avoid slowdowns
        init();
    }

    public void insert(E node){ //uses upper left corner as anchor?
        double x = node.getX();
        double y = node.getY();
        if(treeBounds.contains(x,y)){
            int quantizedX = (int)(widthInCells *(x- treeBounds.getMinX())/ treeBounds.width);
            int quantizedY = (int)(heightInCells *(y- treeBounds.getMinY())/ treeBounds.height);
            cells[quantizedX][quantizedY].add(node);
        }
    }

    public int quantizedX(double x){
        return Math.max(Math.min((int)(widthInCells *(x- treeBounds.getMinX())/ treeBounds.width), widthInCells-1),0);
    }

    public int quantizedY(double y){
        return Math.max(Math.min((int)(heightInCells *(y- treeBounds.getMinY())/ treeBounds.height), heightInCells-1),0);
    }

    public ArrayList<E> cellAt(double x, double y){return cells[quantizedX(x)][quantizedY(y)];}

    public ArrayList<E> nodesNear(E nodeBounds){ //TODO make list of cell indices

        if(quantizedX(nodeBounds.getMinX()) == quantizedX(nodeBounds.getMaxX()) && quantizedY(nodeBounds.getMinY()) == quantizedY(nodeBounds.getMaxY())){ //all within one cell
            return cellAt(nodeBounds.getMaxX(), nodeBounds.getMaxY());
        }else{
            ArrayList<E> cell0 = cellAt(nodeBounds.getMaxX(), nodeBounds.getMaxY());
            ArrayList<E> cell1 = cellAt(nodeBounds.getMaxX(),nodeBounds.getMinY());
            ArrayList<E> cell2 = cellAt(nodeBounds.getMinX(), nodeBounds.getMinY());
            ArrayList<E> cell3 = cellAt(nodeBounds.getMinX(),nodeBounds.getMaxY());
            if(cell1!=cell0){cell0.addAll(cell1);}
            if(cell2!=cell0){cell0.addAll(cell2);}
            if(cell3!=cell0){cell0.addAll(cell3);}

            return cell0;
        }
    }

    public void init(){
        widthInCells = (int)(treeBounds.getWidth()/maxNodeSize);
        heightInCells = (int)(treeBounds.getHeight()/maxNodeSize);

        cells = new ArrayList[widthInCells][heightInCells];
        for(int x=0; x< widthInCells; x++){
            for(int y=0; y< heightInCells; y++){
                cells[x][y]=new ArrayList<E>();
            }
        }
    }

    public void clear(){
        for(int x=0; x< widthInCells; x++){
            for(int y=0; y< heightInCells; y++){
                cells[x][y].clear();
            }
        }
    }
}
