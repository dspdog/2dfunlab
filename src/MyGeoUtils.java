/**
 * Created by user on 6/6/2015.
 */

import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;

public class MyGeoUtils {

    private static final Geometry[] EMPTY_GEOM_ARRAY = new Geometry[0];

    public static ArrayList<Geometry> triangulate(Area area){ //TODO triangulate w edges not just points
        final GeometryFactory gf = new GeometryFactory();
        final ArrayList<Coordinate> points = new ArrayList<Coordinate>();
        ArrayList<Line2D.Double> lines =  MyAreaUtils.getAreaSegments(area);
        for(Line2D line : lines){
            points.add(new Coordinate(line.getX1(), line.getY1()));
        }

        points.add(new Coordinate(lines.get(0).getX1(), lines.get(0).getY1()));

        final Polygon polygon = gf.createPolygon(new LinearRing(new CoordinateArraySequence(points
                .toArray(new Coordinate[points.size()])), gf), null);

        ConformingDelaunayTriangulationBuilder triangulationBuilder =
                new ConformingDelaunayTriangulationBuilder();

        List<Geometry> constraints =
                new ArrayList<Geometry>();

        constraints.add(polygon);

        triangulationBuilder.setSites(
                 new GeometryCollection(constraints.toArray(EMPTY_GEOM_ARRAY), gf));
        //triangulationBuilder.setConstraints(
        //        new GeometryCollection(constraints.toArray(EMPTY_GEOM_ARRAY), gf));
        triangulationBuilder.setTolerance(0.01);

    /* run triangulation */

        Geometry triangulationResult = triangulationBuilder.getTriangles(gf);

        ArrayList<Geometry> result = new ArrayList<>();

        for(int i=0; i<triangulationResult.getNumGeometries(); i++){
            result.add(triangulationResult.getGeometryN(i));
        }

        return result;
    }
}
