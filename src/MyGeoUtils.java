/**
 * Created by user on 6/6/2015.
 */

import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;

public class MyGeoUtils {

    private static final Geometry[] EMPTY_GEOM_ARRAY = new Geometry[0];

    public static ArrayList<Shape> triangulate(Area area, float edgeSegLength){ //TODO triangulate w edges not just points
        GeometryFactory gf = new GeometryFactory();

        final ArrayList<Coordinate> points = new ArrayList<Coordinate>();
        //final ArrayList<Coordinate> points2 = new ArrayList<Coordinate>();
        ArrayList<Line2D.Double> lines =  MyAreaUtils.getAreaSegmentsShort(area, edgeSegLength);

        for(Line2D line : lines){
            points.add(new Coordinate(line.getX1(), line.getY1()));
           //points2.add(new Coordinate(line.getX1()+Math.random()*100-50f, line.getY1()+Math.random()*100-50f));
        }

        //points.add(new Coordinate(lines.get(0).getX1(), lines.get(0).getY1())); //close the shape
        points.add(points.get(0)); //close the shape

        ConformingDelaunayTriangulationBuilder triangulationBuilder =
                new ConformingDelaunayTriangulationBuilder();


        ArrayList<Geometry> sites = new ArrayList<Geometry>();

        ArrayList<Geometry> constraints = new ArrayList<Geometry>(); //constraints.add(ShapeReader.read(area,0.001,gf)); //doesnt always work...
        //constraints.add(ShapeReader.read(area, 1, gf));
        sites.add(new LinearRing(new CoordinateArraySequence(points.toArray(new Coordinate[points.size()])), gf));

        triangulationBuilder.setSites(
                new GeometryCollection(sites.toArray(EMPTY_GEOM_ARRAY), gf));

        //triangulationBuilder.setConstraints(
        //        new GeometryCollection(constraints.toArray(EMPTY_GEOM_ARRAY), gf));

        triangulationBuilder.setTolerance(0.00000001);

    /* run triangulation */

        Geometry triangulationResult = triangulationBuilder.getTriangles(gf);

        ArrayList<Shape> result = new ArrayList<>();

        for(int i=0; i<triangulationResult.getNumGeometries(); i++){
            result.add(new ShapeWriter().toShape(triangulationResult.getGeometryN(i)));
        }

        return result;
    }
}
