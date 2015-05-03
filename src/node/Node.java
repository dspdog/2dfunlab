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

    public float voltageAccumulator =0f;
    public float healthPts = 50f;
    public boolean visited =false;

    static float maxVoltage = 100f;
    static float maxNeighbors = 1f;

    final Ellipse2D.Double myShape = new Ellipse2D.Double();
    MyTree tree;
    CopyOnWriteArrayList<Node> neighbors = new CopyOnWriteArrayList<Node>();

    public Rectangle getBounds(){return new Rectangle((int) (pos.x - diameter / 2), (int) (pos.y - diameter /2),(int) diameter,(int) diameter);}
    public Rectangle getBoundsScaled(float scale){return new Rectangle((int) (pos.x - diameter / 2*scale), (int) (pos.y - diameter /2*scale),(int)(diameter*scale),(int)( diameter*scale));}
    public Node(double x, double y, double s, MyTree t){this.setPos(x, y).setDiameter(s).setTree(t).setIndex();
        voltageAccumulator =0f;}
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
        NodeBehaviors.suckVoltageIfEdgeNode(this);
        NodeBehaviors.restrictToNodeWorld(this);


        float rnd = (float)Math.random();

        /*if(voltageChange>5f){
            if(rnd<0.001f && NodeWorld.nodes.size()<1000){
                NodeBehaviors.splitMe(this);
            }
        }else{
            if(rnd<0.000001f && voltageChange<0.00001f){
                NodeBehaviors.removeMe(this);
            }
        }*/

        if(voltageAccumulator >5f){
            if(rnd<0.001f && NodeWorld.nodes.size()<10000){
                NodeBehaviors.splitMe(this);
            }
        }else{
            if(rnd<0.1f && NodeWorld.nodes.size()>5 && voltageAccumulator <1f){
                NodeBehaviors.removeMe(this);
            }
        }


        voltageAccumulator *=0.995f;

        maxVoltage =Math.max(maxVoltage, voltageAccumulator);
        maxNeighbors =Math.max(maxNeighbors, neighbors.size());
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
        g.setColor(colorByNeighbors());
        for(Node neighbor: neighbors){
            g.drawLine((int)pos.x,(int)pos.y,(int)neighbor.pos.x,(int)neighbor.pos.y);
        }
    }

    public void draw(Graphics2D g){
        //g.setColor(new Color(0.0f,0.0f,0.0f,0.25f));
        g.setColor(colorByNeighbors());
        if(NodeWorld.nodes.get(0)==this){
            g.setColor(new Color(255,0,0));
           // System.out.println(voltageChange);
        }
        //myShape.setFrame(getBoundsScaled((float) ((density + 1) / (NodeWorld.pressure * 8f + 1))));
        myShape.setFrame(getBoundsScaled(0.5f));

        for(Node neighbor: neighbors){
            g.drawLine((int)pos.x,(int)pos.y,(int)neighbor.pos.x,(int)neighbor.pos.y);
        }

        g.fill(myShape);
    }

    public Color colorByNeighbors() {
        //float mod = maxVoltage/50f;
        float mod = maxVoltage /50f;
        float color = Math.max(0,Math.min(voltageAccumulator,mod))/mod;
        float colorNeighbors = (float)Math.max(0,Math.min(this.neighbors.size()/maxNeighbors,1.0));

        if(colorNeighbors<0.5f)return new Color(0.0f,0f,0f);

        return new Color(colorNeighbors/2f,color,color);//, 0.25f);
    }


    public Color colorByVoltage() {
        float mod = maxVoltage /50f;
        float color = Math.max(0,Math.min(voltageAccumulator,mod))/mod;
        return new Color(color,color,color);
    }

    public Color colorByLogVoltage() {
        float mod = maxVoltage;
        float color = (float)(-Math.log10(Math.max(0,Math.min(voltageAccumulator,mod))/mod)%10f)/10f;

        color = Math.max(0,Math.min(color, 1.0f));

        return new Color(color,color,color);
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
