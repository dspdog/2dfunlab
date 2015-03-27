package node;

import com.sun.javafx.geom.Vec2d;
import mytree.MyTree;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by user on 3/24/2015.
 */
public class Node{
    public Vec2d pos = new Vec2d();
    public Vec2d force = new Vec2d();
    double size;
    final Ellipse2D.Double myShape = new Ellipse2D.Double();
    MyTree tree;
    ArrayList<Node> nodesNearby = new ArrayList<Node>();
    ArrayList<Node> neighbors = new ArrayList<Node>();

    public Rectangle getBounds(){
        return new Rectangle((int) (pos.x - size / 2), (int) (pos.y - size /2),(int)size,(int)size);
    }

    public Node(double x, double y, double s, MyTree t){this.setPos(x, y).setSize(s).setTree(t);}
    public Node setPos(double x, double y){pos.set(x,y); return this;}
    public Node setSize(double s){size=s; return this;}
    public Node setTree(MyTree t){tree=t; return this;}

    public void updatePos(){
        setPos(pos.x+Math.random()-0.5, pos.y+Math.random()-0.5);
    }

    public void updateNeighbors(){
        neighbors.clear();
        for(Node node : tree.nodesNear(this.getBounds())){
            if(node.pos.distance(this.pos)<size && node!=this){
                neighbors.add(node);
            }
        }
    }

    public void draw(Graphics2D g){
        g.setColor(Color.GRAY);
        myShape.setFrame(getBounds());
        //g.drawString("" + neighbors.size(), (float) pos.x, (float) pos.y);
        g.draw(myShape);
    }
}
