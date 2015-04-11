package node;

import mytree.MyTree;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by user on 3/24/2015.
 */
public class NodeWorld {
    private final static double maxNodeSize = 16; //diameter
    public final static Rectangle myBounds = new Rectangle(0,0,1024,800);
    private final static MyTree myTree = new MyTree(myBounds, 32);

    public static CopyOnWriteArrayList<Node> nodes = new CopyOnWriteArrayList<Node>();

    public static float zoom = 1.0f;
    public static float pressure = 0.7550f;
    public static float temperature = 0.1550f;
    public static float distGamma = 2f;
    public static int gravityMode=3;


    public static void resetWorld(int totalNodes){
        nodes = new CopyOnWriteArrayList<Node>();
        for(int i=0; i<totalNodes; i++){
            nodes.add(new Node(maxNodeSize, myTree));
        }
    }

    public static void drawNodes(Graphics2D g){
        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //anti aliasing on
        //myTree.drawGrid(g);

        for(Node node : nodes){
            node.drawNeighbors(g);
        }

        for(Node node : nodes){
            node.draw(g);
        }

        g.setColor(Color.RED);
        g.draw(myBounds);
    }

    public static void update(double x, double y){

        Node.maxVoltage *=0.9f;
        Node.maxNeighbors*=0.999f;

        myTree.clear();
        for(Node node : nodes) {
            node.updatePos();
            myTree.insert(node);
        }
        for(Node node : nodes) {
            node.updateNeighbors();
        }

        if(nodes.size()>0){
            //nodes.get(0).setPos(x-20,y-20);
            NodeBehaviors.findDistancesTo(nodes.get(0));
        }
    }
}
