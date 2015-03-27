package mytree;

import node.Node;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by user on 3/25/2015.
 */
public class MyTree { //a grid not a tree
    int widthInCells;
    int heightInCells;
    double maxSlotSize;
    double maxNodeSize;

    Rectangle treeBounds;
    ArrayList<Node>[][] cells; //TODO make this 1D

    public MyTree(Rectangle _bounds, double maxSize){
        treeBounds = _bounds;
        maxSlotSize = Math.max(maxSize, 8); //minimum grid size 8 pixels to avoid slowdowns
        maxNodeSize = maxSize;
        init();
    }

    public void insert(Node node){ //uses upper left corner as anchor?
        double x = node.pos.x;
        double y = node.pos.y;
        if(treeBounds.contains(x,y)){
            cellAt(x,y).add(node);
        }
    }

    public int quantizedX(double x){
        return Math.max(Math.min((int)(widthInCells *(x- treeBounds.getMinX())/ treeBounds.width), widthInCells-1),0);
    }

    public int quantizedY(double y){
        return Math.max(Math.min((int)(heightInCells *(y- treeBounds.getMinY())/ treeBounds.height), heightInCells-1),0);
    }

    public int cellIndex(double x, double y){
        return quantizedX(x)+quantizedY(y)*heightInCells;
    }

    public ArrayList<Node> cellAt(double x, double y){return cells[quantizedX(x)][quantizedY(y)];}
    public ArrayList<Node> cellAt(int i){
        int y = i/widthInCells;
        int x = i%widthInCells;
        return cells[x][y];
    }

    public ArrayList<Node> nodesNear(Rectangle nodeBounds){
        ArrayList<Node> result = new ArrayList<Node>();
        int minX = Math.max(quantizedX(nodeBounds.getMinX()-maxNodeSize/2),0);
        int maxX = Math.min(quantizedX(nodeBounds.getMaxX()+maxNodeSize/2),widthInCells-1);
        int minY = Math.max(quantizedY(nodeBounds.getMinY()-maxNodeSize/2),0);
        int maxY = Math.min(quantizedY(nodeBounds.getMaxY()+maxNodeSize/2), heightInCells-1);
        for(int x=minX; x<=maxX; x++){
            for(int y=minY; y<=maxY; y++){
                result.addAll(cells[x][y]);
            }
        }
        return result;
    }

    public void init(){
        widthInCells = (int)(treeBounds.getWidth()/maxSlotSize);
        heightInCells = (int)(treeBounds.getHeight()/maxSlotSize);

        cells = new ArrayList[widthInCells][heightInCells];
        for(int x=0; x< widthInCells; x++){
            for(int y=0; y< heightInCells; y++){
                cells[x][y]=new ArrayList<Node>();
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

    public void drawGrid(Graphics2D g){
        g.setColor(Color.BLUE);
        for(int x=0; x<widthInCells; x++){
            for(int y=0; y<heightInCells; y++){
                g.drawString(""+cells[x][y].size(),(int)(x*maxNodeSize+maxNodeSize/2),(int)(y*maxNodeSize+maxNodeSize/2));
                if(cells[x][y].size()>0){
                    g.drawRect((int) (x * maxNodeSize), (int) (y * maxNodeSize), (int) maxNodeSize, (int)maxNodeSize);
                }
            }
        }
    }
}
