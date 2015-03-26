package node;

import com.sun.javafx.geom.Vec2d;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Created by user on 3/24/2015.
 */
public class Node {
    Vec2d pos = new Vec2d();
    double size;
    final Rectangle2D.Double myShape = new Rectangle2D.Double();

    public Rectangle getBounds(){
        return new Rectangle((int) (pos.x - size / 2), (int) (pos.y - size /2),(int)size,(int)size);
    }

    public Node(double x, double y, double _size){
        this.setPos(x, y).setSize(_size);
    }

    public Node setPos(double x, double y){pos.set(x,y); return this;}
    public Node setSize(double s){size=s; return this;}

    public Node update(){
        setPos(pos.x+Math.random()-0.5, pos.y+Math.random()-0.5);
        return this;
    }

    public void draw(Graphics2D g){
        g.setColor(Color.GRAY);
        myShape.setFrame(getBounds());
        g.fill(myShape);
    }
}
