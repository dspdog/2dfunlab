package node;

import com.sun.javafx.geom.Vec2d;

/**
 * Created by user on 4/2/2015.
 */
public class NodeBehaviors {
    public static void moveBrownian(Node node, float scale){
        node.setPos(node.pos.x+(Math.random()-0.5)*scale, node.pos.y+(Math.random()-0.5)*scale);
    }

    public static void moveToMaintainNeighborDensity(Node node, float targetDensity){
        float totalDensity = 0;
        Vec2d densityGradient = new Vec2d(0,0);

        for(Node neighbor : node.neighbors){
            if(neighbor!=node){
                float gConst = (float)node.diameter/2f;
                float distToNeighbor = (float)node.pos.distance(neighbor.pos);

                float neighborContrib = gConst/distToNeighbor/distToNeighbor;
                Vec2d neighborContribGrad = new Vec2d((neighbor.pos.x-node.pos.x)*neighborContrib, (neighbor.pos.y-node.pos.y)*neighborContrib);

                totalDensity+=neighborContrib;
                densityGradient.set(densityGradient.x + neighborContribGrad.x, densityGradient.y + neighborContribGrad.y);
            }
        }

        float error = totalDensity-targetDensity;
        float speedLimit = (float)node.diameter/2f;

        while(Math.abs(error*Vec2d.distanceSq(0,0,densityGradient.x,densityGradient.y))>speedLimit*speedLimit){
            error*=0.9f;
        }

        node.setPos(node.pos.x-densityGradient.x*error, node.pos.y-densityGradient.y*error);
    }

    public static void restrictToNodeWorld(Node node){
        float pad = (float)node.diameter;
        node.setPos(Math.max(node.pos.x, NodeWorld.myBounds.getMinX()+pad), Math.max(node.pos.y, NodeWorld.myBounds.getMinY()+pad));
        node.setPos(Math.min(node.pos.x, NodeWorld.myBounds.getMaxX()-pad), Math.min(node.pos.y, NodeWorld.myBounds.getMaxY()-pad));
    }

    public static void pullGravity(Node node){
        float step = 0.25f;
        node.setPos(node.pos.x,node.pos.y+step);
    }
}
