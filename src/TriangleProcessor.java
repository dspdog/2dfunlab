import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by user on 6/7/2015.
 */
public class TriangleProcessor {
    public int vertsNonUnique = 0;
    public int vertsUnique = 0;
    public int polys = 0;

    //public final ArrayList<Triangle> triangles = new ArrayList<>();
    public final ArrayList<Point2D> vertsList = new ArrayList<>();

    public final HashMap<String, Point2D> vertsMap = new HashMap<>();
    public final HashMap<String, HashSet<Integer>> vertToTriangleSet = new HashMap<>();

    public void processTriangles(ArrayList<Shape> triangles){
        vertsNonUnique = 0;
        vertsUnique = 0;
        polys = 0;

        vertsList.clear();
        //this.triangles.clear();
        vertsMap.clear();

        int triangleIndex = 0;

        //building vertex-index list....
        for(Shape triangleShape : triangles){
            for(Point2D vertex : MyPolygonUtils.shape2Pts(triangleShape)){
                vertsNonUnique++;

                String vertexString = getVertexString(vertex);
                Point2D putResult = vertsMap.put(vertexString, vertex);

                if(putResult==null){//place unique vert -- null means its the first with this value
                    //PUTTING UNIQUE VERTS INTO ARRAY...
                    vertsUnique++;
                    vertsList.add(vertex);
                    vertToTriangleSet.put(vertexString, new HashSet<Integer>());
                }else{
                    vertToTriangleSet.get(vertexString).add(new Integer(triangleIndex));
                }
            }
            polys++;
            triangleIndex++;
        }

        //GETTING TRIS, ADDING TO ARRAY...
        /*for(Shape poly : csg){
            int size = poly.vertsList.size();
            for(int v = 1; v < size - 1; v++) {
                Triangle triangle = new Triangle(
                        vertsMap.get(getVertexString(convertCSGVert2myVert(poly.vertsList.get(0)))),
                        vertsMap.get(getVertexString(convertCSGVert2myVert(poly.vertsList.get(v)))),
                        vertsMap.get(getVertexString(convertCSGVert2myVert(poly.vertsList.get(v+1))))
                );
                if(triangle.myAreaSquared()>0){
                    //building edges
                    triangle.verts[0].addNext(triangle.verts[1]).addTriangle(triangle);
                    triangle.verts[1].addNext(triangle.verts[2]).addTriangle(triangle);
                    triangle.verts[2].addNext(triangle.verts[0]).addTriangle(triangle);
                    triangle.getNormal();
                    triangle.getArea();
                    triangles.add(triangle);
                }
            }
        }*/
    }

    public String getVertexString(Point2D vertex){
        float roundToNearesth = 1000.0f;
        return  Float.toString((int)(vertex.getX()*roundToNearesth)/roundToNearesth) +
                Float.toString((int)(vertex.getY()*roundToNearesth)/roundToNearesth);
    }
}
