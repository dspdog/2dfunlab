import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Created by user on 5/9/2015.
 */
public class MyAreaUtils {

    static double getAreaPerimeter(Area area){
        if(area.getBounds2D().getWidth()>500 || area.getBounds2D().getHeight()>500 || !area.isSingular())return 0; //TODO use max distance instead of width/height
        return polygonPerimeter(getAreaSegments(area));
    }

    static double getAreaArea(Area area){
        return polygonArea(getAreaSegments(area));
    }

    //http://stackoverflow.com/questions/8144156/using-pathiterator-to-return-all-line-segments-that-constrain-an-area
    static public ArrayList<Line2D.Double> getAreaSegments(Area area){
        ArrayList<double[]> areaPoints = new ArrayList<double[]>();
        ArrayList<Line2D.Double> areaSegments = new ArrayList<Line2D.Double>();
        double[] coords = new double[6];

        for (PathIterator pi = area.getPathIterator(null); !pi.isDone(); pi.next()) {
            // The type will be SEG_LINETO, SEG_MOVETO, or SEG_CLOSE
            // Because the Area is composed of straight lines
            int type = pi.currentSegment(coords);
            // We record a double array of {segment type, x coord, y coord}
            double[] pathIteratorCoords = {type, coords[0], coords[1]};
            areaPoints.add(pathIteratorCoords);
        }

        double[] start = new double[3]; // To record where each polygon starts

        for (int i = 0; i < areaPoints.size(); i++) {
            // If we're not on the last point, return a line from this point to the next
            double[] currentElement = areaPoints.get(i);

            // We need a default value in case we've reached the end of the ArrayList
            double[] nextElement = {-1, -1, -1};
            if (i < areaPoints.size() - 1) {
                nextElement = areaPoints.get(i + 1);
            }

            // Make the lines
            if (currentElement[0] == PathIterator.SEG_MOVETO) {
                start = currentElement; // Record where the polygon started to close it later
            }

            if (nextElement[0] == PathIterator.SEG_LINETO) {
                areaSegments.add(
                        new Line2D.Double(
                                currentElement[1], currentElement[2],
                                nextElement[1], nextElement[2]
                        )
                );
            } else if (nextElement[0] == PathIterator.SEG_CLOSE) {
                areaSegments.add(
                        new Line2D.Double(
                                currentElement[1], currentElement[2],
                                start[1], start[2]
                        )
                );
            }
        }
        // areaSegments now contains all the line segments
        return areaSegments;
    }

    public static double polygonArea(ArrayList<Line2D.Double> areaSegments) { //http://www.shodor.org/~jmorrell/interactivate/org/shodor/util11/PolygonUtils.java
        double area = 0;
        for (Line2D segment : areaSegments) {area += segment.getP1().getX() * segment.getP2().getY() - segment.getP1().getY() * segment.getP2().getX();}
        return (Math.abs(area/2d));
    }

    public static double polygonPerimeter(ArrayList<Line2D.Double> areaSegments) {
        double perim = 0;
        for (Line2D segment : areaSegments) {perim+=Math.hypot(segment.getX2()-segment.getX1(), segment.getY2()-segment.getY1());}
        return perim;
    }

    public static double polygonPerimeterWeighted(ArrayList<Line2D.Double> areaSegments) {
        double perim = 0;

        for (Line2D segment : areaSegments) {
            double distToCenter1 = Math.max(Math.hypot(segment.getX1(), segment.getY1()), 1f);//centered at origin instead of bounds
            double distToCenter2 = Math.max(Math.hypot(segment.getX2(), segment.getY2()), 1f);
            double weight1 = MyPolygonUtils.worldScale/(distToCenter1*distToCenter1);
            double weight2 = MyPolygonUtils.worldScale/(distToCenter2*distToCenter2);
            double weight = Math.min(weight1,weight2);
            perim+=weight*Math.hypot(segment.getX2()-segment.getX1(), segment.getY2()-segment.getY1());
        }
        return perim;
    }
}
