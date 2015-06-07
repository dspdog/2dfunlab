import java.awt.geom.*;
import java.util.ArrayList;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.HashSet;

public class MyAreaUtils {

    static double getAreaPerimeter(Area area){
        if(area.getBounds2D().getWidth()>500 || area.getBounds2D().getHeight()>500 || !area.isSingular())return 0; //TODO use max distance instead of width/height
        return polygonPerimeter(getAreaSegments(area));
    }

    static double getAreaArea(Area area){
        return polygonArea(getAreaSegments(area));
    }

    public static class TriangleOrganizer {
        public ArrayList<Shape> internal= new ArrayList<>();
        public ArrayList<Shape> external = new ArrayList<>();
        public HashSet<Shape> internalMap = new HashSet<>();
        public HashSet<Shape> externalMap = new HashSet<>();
        public TriangleOrganizer(Area holder, ArrayList<Shape> shapes){
            for(Shape s: shapes){
                if(numPts(s)==3)
                    if(holder.contains(computeCenter(s))){
                        internal.add(s);
                        internalMap.add(s);
                    }else{
                        external.add(s);
                        externalMap.add(s);
                    }
            }
        };
    }

    public static int numPts(Shape shape){
        PathIterator pi = shape.getPathIterator(null);
        double coords[] = new double[6];
        int numPoints = 0;
        while (!pi.isDone()){
            int s = pi.currentSegment(coords);
            switch (s) {
                case PathIterator.SEG_MOVETO:
                    numPoints++;
                    break;

                case PathIterator.SEG_LINETO:
                    numPoints++;
                    break;

                case PathIterator.SEG_CLOSE:
                    // Ignore
                    break;
            }
            pi.next();
        }

        return numPoints;
    }

    public static Point2D computeCenter(Shape shape)//http://stackoverflow.com/questions/21973875/java-pathiterator-how-do-i-accurately-calculate-center-of-shape-object
    {
        PathIterator pi = shape.getPathIterator(null);
        double coords[] = new double[6];
        double sumX = 0;
        double sumY = 0;
        int numPoints = 0;
        while (!pi.isDone()){
            int s = pi.currentSegment(coords);
            switch (s) {
                case PathIterator.SEG_MOVETO:
                    sumX += coords[0];
                    sumY += coords[1];
                    numPoints++;
                    break;

                case PathIterator.SEG_LINETO:
                    sumX += coords[0];
                    sumY += coords[1];
                    numPoints++;
                    break;

                case PathIterator.SEG_CLOSE:
                    // Ignore
                    break;
            }
            pi.next();
        }
        double x = sumX / numPoints;
        double y = sumY / numPoints;

        //if(numPoints!=3){
        //    System.out.println(numPoints + " pts?");
        //}
        return new Point2D.Double(x,y);
    }

    static public ArrayList<Line2D.Double> lineList(Line2D.Double line, float len){ //TODO chop line up into segs of length len
        ArrayList<Line2D.Double> res = new ArrayList<>();
        //res.add(line);

        Point2D startPt = line.getP1();
        Point2D endPt = line.getP2();

        while (startPt.distance(endPt)>len){
            Point2D shiftedStartPt = shiftedTowards(startPt,endPt,len);
            res.add(new Line2D.Double(startPt,shiftedStartPt));
            startPt=shiftedStartPt;
        }

        res.add(new Line2D.Double(startPt,endPt));

        return res;
    }

    static public Point2D shiftedTowards(Point2D mover, Point2D dest, float shiftDist){
        double fracDist = shiftDist/mover.distance(dest);
        return new Point2D.Double(mover.getX()-(mover.getX()-dest.getX())*fracDist, mover.getY()-(mover.getY()-dest.getY())*fracDist);
    }

    static public ArrayList<Line2D.Double> getAreaSegmentsShort(Area area, float linesLen){
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
                areaSegments.addAll(lineList(new Line2D.Double(
                        currentElement[1], currentElement[2],
                        nextElement[1], nextElement[2]
                ), linesLen));
            } else if (nextElement[0] == PathIterator.SEG_CLOSE) {
                areaSegments.addAll(lineList(new Line2D.Double(
                                currentElement[1], currentElement[2],
                                start[1], start[2]
                        ), linesLen)
                );
            }
        }
        // areaSegments now contains all the line segments
        return areaSegments;
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
