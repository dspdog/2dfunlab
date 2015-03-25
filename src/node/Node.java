package node;

import com.sun.javafx.geom.Vec2d;

import java.awt.*;

/**
 * Created by user on 3/24/2015.
 */
public class Node {
    Vec2d pos = new Vec2d();
    double size = 16;

    public Node(double x, double y, double _size){
        this.setPos(x, y).setSize(_size);
    }

    public Node setPos(double x, double y){pos.set(x,y); return this;}
    public Node setSize(double s){size=s; return this;}

    public Node draw(Graphics g){
        pos.set(pos.x+Math.random()-0.5, pos.y+Math.random()-0.5);
        g.setColor(Color.GRAY);
        g.drawOval((int)(pos.x-size/2),(int)(pos.y-size/2),(int)size,(int)size);
        return this;
    }
}
