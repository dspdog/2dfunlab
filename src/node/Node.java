package node;

import com.sun.javafx.geom.Vec2d;
import mytree.MyTree;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Created by user on 3/24/2015.
 */
public class Node {
    Vec2d pos = new Vec2d();
    double size;
    final Rectangle2D.Double myShape = new Rectangle2D.Double();
    MyTree tree;
    ArrayList<Node> nodesNearby;

    public Rectangle getBounds(){
        return new Rectangle((int) (pos.x - size / 2), (int) (pos.y - size /2),(int)size,(int)size);
    }

    public Node(double x, double y, double s, MyTree t){this.setPos(x, y).setSize(s).setTree(t);}
    public Node setPos(double x, double y){pos.set(x,y); return this;}
    public Node setSize(double s){size=s; return this;}
    public Node setTree(MyTree t){tree=t; return this;}

    public Node update(){
        setPos(pos.x+Math.random()-0.5, pos.y+Math.random()-0.5);
        nodesNearby = tree.nodesNear(getBounds()); //TODO get list of gridCells not individual nodes?
        return this;
    }

    public void draw(Graphics2D g){
        g.setColor(Color.GRAY);
        myShape.setFrame(getBounds());
        g.fill(myShape);
    }
}
