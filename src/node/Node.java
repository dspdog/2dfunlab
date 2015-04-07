package node;

import com.sun.javafx.geom.Vec2d;
import mytree.MyTree;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by user on 3/24/2015.
 */
public class Node{
    public Vec2d pos = new Vec2d();
    double diameter; //diameter
    double density =0f;
    public int index;
    public float distToBase=999999f;
    public float healthPts = 50f;
    public boolean visited =false;

    final Ellipse2D.Double myShape = new Ellipse2D.Double();
    MyTree tree;
    CopyOnWriteArrayList<Node> neighbors = new CopyOnWriteArrayList<Node>();

    public Rectangle getBounds(){return new Rectangle((int) (pos.x - diameter / 2), (int) (pos.y - diameter /2),(int) diameter,(int) diameter);}
    public Rectangle getBoundsScaled(float scale){return new Rectangle((int) (pos.x - diameter / 2*scale), (int) (pos.y - diameter /2*scale),(int)(diameter*scale),(int)( diameter*scale));}
    public Node(double x, double y, double s, MyTree t){this.setPos(x, y).setDiameter(s).setTree(t).setIndex();}
    public Node(double s, MyTree t){this.setDiameter(s).setTree(t).respawn().setIndex();}
    public Node setPos(double x, double y){pos.set(x,y); return this;}
    public Node setIndex(){index=nextIndex(); return this;}
    public Node setDiameter(double s){diameter =s; return this;}
    public Node setTree(MyTree t){tree=t; return this;}
    public Node respawn(){this.setPos(tree.myBounds.getMinX()+Math.random()*tree.myBounds.width, tree.myBounds.getMinX()+Math.random()*tree.myBounds.height); healthPts=200f*(float)Math.random(); return this;}

    public void updatePos(){
        NodeBehaviors.moveBrownian(this, NodeWorld.temperature);
        NodeBehaviors.moveToMaintainNeighborDensity(this, NodeWorld.pressure);
        NodeBehaviors.pullGravity(this, NodeWorld.gravityMode);
        NodeBehaviors.restrictToNodeWorld(this);
        if(!this.visited){
            this.healthPts*=0.95;
            //if(this.healthPts<1f){
            //    this.respawn();
            //}
        }else{
            //TODO increase health pts according to dist
            float rnd = (float)Math.random();

            if(rnd<0.001f){
                NodeBehaviors.splitMe(this);
            }
        }

    }

    public void updateNeighbors(){
        neighbors.clear();
        for(Node node : tree.nodesNear(this.getBounds())){
            if(node.pos.distance(this.pos)< diameter && node!=this){
                neighbors.add(node);
            }
        }
    }

    public void drawNeighbors(Graphics2D g){
        g.setColor(colorByHealth());
        for(Node neighbor: neighbors){
            g.drawLine((int)pos.x,(int)pos.y,(int)neighbor.pos.x,(int)neighbor.pos.y);
        }
    }

    public void draw(Graphics2D g){
        g.setColor(colorByHealth());
        if(index==0)g.setColor(new Color(255,0,0));
        myShape.setFrame(getBoundsScaled((float) ((density + 1) / (NodeWorld.pressure * 8f + 1))));
        g.fill(myShape);
    }

    public Color colorByHealth(){
        healthPts=Math.max(0,Math.min(healthPts,100f));
        return new Color(healthPts/100f,healthPts/100f,healthPts/100f);
    }

    public Color colorByStemDist(){
        if(visited){
            float gray = 1.0f-Math.max(Math.min(distToBase/NodeBehaviors.getMaxDistance(), 1.0f),0f);
            return new Color(gray,gray,gray);
        }else{
            return new Color(0,0,64);
        }
    }

    public Node clone(){
        return new Node(pos.x,pos.y,diameter,tree);
    }

    private static int __index =0;
    public static int nextIndex(){
        return __index++;
    }
}
