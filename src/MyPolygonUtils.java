
import java.awt.*;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by user on 5/9/2015.
 */
public class MyPolygonUtils {

    static public int worldScale = 2000000;
    public static Shape NGon(int N){ //http://www.java2s.com/Code/Java/2D-Graphics-GUI/DrawaPolygon.htm
        float radius = worldScale;
        float x = 0;
        float y = 0;
        java.awt.Polygon p = new java.awt.Polygon();

        for (int i = 0; i < N; i++){
            p.addPoint((int) (x + radius * Math.cos(i * 2 * Math.PI / N)),
                    (int) (y + radius * Math.sin(i * 2 * Math.PI / N)));

        }

        return p;
    }

    public static ArrayList<Point2D> shape2Pts(Shape shape){

        ArrayList<Point2D> pts = new ArrayList<>();

        PathIterator pi = shape.getPathIterator(null);
        double coords[] = new double[6];
        while (!pi.isDone()){
            int s = pi.currentSegment(coords);
            switch (s) {
                case PathIterator.SEG_MOVETO:
                    pts.add(new Point2D.Double(coords[0],coords[1]));
                    break;

                case PathIterator.SEG_LINETO:
                    pts.add(new Point2D.Double(coords[0],coords[1]));
                    break;

                case PathIterator.SEG_CLOSE:
                    // Ignore
                    break;
            }
            pi.next();
        }

        return pts;
    }
}
