package node;

import com.sun.javafx.geom.Vec2d;
import mytree.MyTree;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

/**
 * Created by user on 3/24/2015.
 */
public class Node{
    public Vec2d pos = new Vec2d();
    public Vec2d force = new Vec2d();
    double diameter; //diameter
    final Ellipse2D.Double myShape = new Ellipse2D.Double();
    MyTree tree;
   // ArrayList<Node> nodesNearby = new ArrayList<Node>();
    ArrayList<Node> neighbors = new ArrayList<Node>();

    public Rectangle getBounds(){
        return new Rectangle((int) (pos.x - diameter / 2), (int) (pos.y - diameter /2),(int) diameter,(int) diameter);
    }

    public Node(double x, double y, double s, MyTree t){this.setPos(x, y).setDiameter(s).setTree(t);}
    public Node setPos(double x, double y){pos.set(x,y); return this;}
    public Node setDiameter(double s){
        diameter =s; return this;}
    public Node setTree(MyTree t){tree=t; return this;}

    public void updatePos(){
        NodeBehaviors.moveBrownian(this, 0.1f); //corresponds to "temperature"?
        NodeBehaviors.moveToMaintainNeighborDensity(this, NodeWorld.pressure);
        if(NodeWorld.gravityEnabled)NodeBehaviors.pullGravity(this);
        NodeBehaviors.restrictToNodeWorld(this);
    }

    public void updateNeighbors(){
        neighbors.clear();
        for(Node node : tree.nodesNear(this.getBounds())){
            if(node.pos.distance(this.pos)< diameter && node!=this){
                neighbors.add(node);
            }
        }
    }

    public void draw(Graphics2D g){
        g.setColor(Color.GRAY);
        myShape.setFrame(getBounds());
        //g.drawString("" + neighbors.diameter(), (float) pos.x, (float) pos.y);
        g.fill(myShape);

        g.setColor(Color.BLACK);
        g.draw(myShape);
    }
}
