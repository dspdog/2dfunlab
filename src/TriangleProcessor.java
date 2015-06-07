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
    public final ArrayList<Triangle> trisList = new ArrayList<>();
    public final HashMap<String, Point2D> vertsMap = new HashMap<>();
    public final HashMap<String, HashSet<Triangle>> vertToTriangleSet = new HashMap<>();

    public void processTriangles(ArrayList<Shape> triangles){
        vertsNonUnique = 0;
        vertsUnique = 0;
        polys = 0;

        vertsList.clear();
        vertsMap.clear();

        //building vertex-index list....
        for(Shape triangleShape : triangles){
            Triangle tri = new Triangle(triangleShape);
            trisList.add(tri);

            for(Point2D vertex : tri.myVerts){
                vertsNonUnique++;

                String vertexString = getVertexString(vertex);
                Point2D putResult = vertsMap.put(vertexString, vertex);

                if(putResult==null){//place unique vert -- null means its the first with this value
                    //PUTTING UNIQUE VERTS INTO ARRAY...
                    vertsUnique++;
                    vertsList.add(vertex);

                    HashSet<Triangle> hash = new HashSet<Triangle>();
                    hash.add(tri);
                    vertToTriangleSet.put(vertexString, hash);
                }else{
                    vertToTriangleSet.get(vertexString).add(tri);
                }
            }

            polys++;
        }

        for(Triangle tri : trisList){tri.updateNeighbors();}
    }

    public class Triangle{
        HashSet<Triangle> myVertNeighbors = new HashSet<Triangle>();
        HashSet<Triangle> myFaceNeighbors = new HashSet<Triangle>();
        ArrayList<Point2D> myVerts = new ArrayList<>();
        HashSet<String> myVertsSet = new HashSet<>();

        Shape myShape;

        public Triangle(Shape shape){
            myShape = shape;
            myVerts = MyPolygonUtils.shape2Pts(shape);
        }

        public void updateNeighbors(){
            for(Point2D vert : myVerts){
                myVertNeighbors.addAll(vertToTriangleSet.get(getVertexString(vert)));
                myVertsSet.add(getVertexString(vert));
            }

            myVertNeighbors.remove(this);

            for(Triangle neighbor : myVertNeighbors){
                int sharedVerts = 0;

                for(Point2D vert : neighbor.myVerts){
                    if(myVertsSet.contains(vert)){
                        sharedVerts++;
                    }
                }

                if(sharedVerts==2){
                    myFaceNeighbors.add(neighbor);
                }
            }
        }
    }

    public String getVertexString(Point2D vertex){
        float roundToNearesth = 1000.0f;
        return  Float.toString((int)(vertex.getX()*roundToNearesth)/roundToNearesth) +
                Float.toString((int)(vertex.getY()*roundToNearesth)/roundToNearesth);
    }
}
