package node;

import mytree.MyTree;
import qtree.Quadtree;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by user on 3/24/2015.
 */
public class NodeWorld {
    private final static double maxNodeSize = 2;
    private final static MyTree myTree = new MyTree(new Rectangle(0,0,1024,1024), maxNodeSize);

    public static CopyOnWriteArrayList<Node> nodes = new CopyOnWriteArrayList<Node>();
    public static int totalNodes = 100000;

    public static void buildWorld(){
        for(int i=0; i<totalNodes; i++){
            nodes.add(new Node(Math.random()*512 + 256, Math.random()*512 + 256, maxNodeSize));
        }
    }

    public static void drawNodes(Graphics2D g){
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //anti aliasing on
        for(Node node : nodes){
            node.draw(g);
        }
    }

    public static void update(){
        myTree.clear();
        for(Node node : nodes) {
            myTree.insert(node.getBounds());
            node.update();
        }
    }
}
