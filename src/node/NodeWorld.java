package node;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by user on 3/24/2015.
 */
public class NodeWorld {
    public static CopyOnWriteArrayList<Node> nodes = new CopyOnWriteArrayList<Node>();

    public static void buildWorld(){
        int totalNodes = 1000;
        for(int i=0; i<totalNodes; i++){
            nodes.add(new Node(Math.random()*512, Math.random()*512, Math.random()*24));
        }
    }

    public static void drawNodes(Graphics g){
        for(Node node : nodes){
            node.draw(g);
        }
    }

    public static void update(){

    }
}
