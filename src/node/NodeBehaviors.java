package node;

import com.sun.javafx.geom.Vec2d;

/**
 * Created by user on 4/2/2015.
 */
public class NodeBehaviors {
    static float maxDistance=0f;

    public static void splitMe(Node node){
        NodeWorld.nodes.add(NodeBehaviors.moveBrownian(node.clone(), 0.1f));
    }

    public static void removeMe(Node node){
        NodeWorld.nodes.remove(node);
    }

    public static Node moveBrownian(Node node, float scale){
        return node.setPos(node.pos.x+(Math.random()-0.5)*scale, node.pos.y+(Math.random()-0.5)*scale);
    }

    public static void pushVoltageFrom(Node startNode){
        for(Node _node : NodeWorld.nodes){_node.distToBase=999999f; _node.visited=false; maxDistance=0;} //clear all distances
        startNode.distToBase=0;
        startNode.visited=true;
        startNode.voltageAccumulator += 100f;
        pushVoltages(startNode);
    }

    private static void pushVoltages(Node node){
        float voltageThrottle = 0.1f; // voltage throttle = current?
        float numFreeNeighbors = 0;// node.neighbors.size();

        for(Node neighbor : node.neighbors){if(!neighbor.visited){numFreeNeighbors++;}}

        for(Node neighbor : node.neighbors){
            if(!neighbor.visited){
                neighbor.voltageAccumulator += voltageThrottle*(node.voltageAccumulator-neighbor.voltageAccumulator) / numFreeNeighbors;
                neighbor.distToBase=Math.min(neighbor.distToBase, node.distToBase+(float)neighbor.pos.distance(node.pos));
                maxDistance=Math.max(maxDistance,neighbor.distToBase);
            }
        }

        node.visited=true;
        for(Node neighbor : node.neighbors){
            if(!neighbor.visited) {
                pushVoltages(neighbor);
            }
        }

    }

    public static void moveToMaintainNeighborDensity(Node node, float targetDensity){
        float totalDensity = 0;
        Vec2d densityGradient = new Vec2d(0,0);

        for(Node neighbor : node.neighbors){
            if(neighbor!=node){
                float gConst = (float)node.diameter/2f;
                float distToNeighbor = (float)Math.max(node.pos.distance(neighbor.pos), 4f);

                float neighborContrib = (float)(gConst/Math.pow(distToNeighbor, NodeWorld.distGamma));
                Vec2d neighborContribGrad = new Vec2d((neighbor.pos.x-node.pos.x)*neighborContrib, (neighbor.pos.y-node.pos.y)*neighborContrib);

                totalDensity+=neighborContrib;
                densityGradient.set(densityGradient.x + neighborContribGrad.x, densityGradient.y + neighborContribGrad.y);
            }
        }

        node.density = totalDensity;

        float error = totalDensity-targetDensity;
        float speedLimit = (float)node.diameter/8f;

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

    public static void suckVoltageIfEdgeNode(Node node){
        float ambientVoltage = 0.1f;
        float colorNeighbors = (float)Math.max(0,Math.min(node.neighbors.size()/Node.maxNeighbors,1.0));
        if(colorNeighbors<0.5f && node.voltageAccumulator>ambientVoltage)node.voltageAccumulator-=5f;
    }

    public static void pullGravity(Node node, int mode){
        float step = 0.25f;
        float length = (float)node.pos.distance(512,512)/step;
        Vec2d directionToCenter = new Vec2d((node.pos.x-512)/length, (node.pos.y-512)/length);


        switch(mode){
            case 0: //no gravity
                break;
            case 1: //gravity down
                node.setPos(node.pos.x,node.pos.y+step);
                break;
            case 2: //gravity in
                if(length<512){
                    directionToCenter = new Vec2d(0,0);
                }
                node.setPos(node.pos.x-directionToCenter.x,node.pos.y-directionToCenter.y);
                break;
            case 3: //gravity out
                if(length>512){
                    directionToCenter = new Vec2d(0,0);
                }
                node.setPos(node.pos.x+directionToCenter.x,node.pos.y+directionToCenter.y);
                break;
        }
    }

    public static float getMaxDistance(){
        return maxDistance;
    }
}
