import java.awt.*;
import java.awt.geom.Line2D;
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
    public final HashMap<Shape,Triangle> shape2Tri = new HashMap<>();
    public final HashSet<Shape> internalShapes = new HashSet<>();

    public void processTriangles(ArrayList<Shape> shapes, HashSet<Shape> _internalShapes){

        internalShapes.addAll(_internalShapes);

        vertsNonUnique = 0;
        vertsUnique = 0;
        polys = 0;

        vertsList.clear();
        vertsMap.clear();

        //building vertex-index list....
        for(Shape triangleShape : shapes){
            Triangle tri = new Triangle(triangleShape);
            shape2Tri.put(triangleShape,tri);
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

        HashMap<Triangle, HashMap<Triangle, Double>> pressures = new HashMap<>();

        double fullPressure = 0;
        boolean isInternal = false;
        Shape myShape;

        public Triangle(Shape shape){
            myShape = shape;
            myVerts = MyPolygonUtils.shape2Pts(shape);
            isInternal = internalShapes.contains(myShape);
            fullPressure=0;
        }

        public double getTotalPressure(){
            return 0d; //TODO 
        }

        public void putPressure(double voltage, Triangle originalSourceTriangle, Triangle comingFrom){ //coming from is the neighbor providing this contribution, origSource is starting location for this voltage source
            if(pressures.get(comingFrom)==null){
                HashMap<Triangle, Double> thePressure = new HashMap<>();
                pressures.put(comingFrom, thePressure);
            }

            if(pressures.get(comingFrom).get(originalSourceTriangle)==null){
                pressures.get(comingFrom).put(originalSourceTriangle, new Double(voltage));
                fullPressure+=voltage;

                double availableSharedFacesLength = 0;

                for(Triangle neighbor : myFaceNeighbors){
                    if(neighbor!=comingFrom){
                        availableSharedFacesLength+=getSharedFaceLength(neighbor); // TODO update -- internal vs external
                    }
                }

                for(Triangle neighbor : myFaceNeighbors){
                    if(neighbor!=comingFrom && neighbor.isInternal){
                        double partialVoltage = voltage*getSharedFaceLength(neighbor)/availableSharedFacesLength;
                        neighbor.putPressure(partialVoltage, originalSourceTriangle, this);
                    }
                }
            }else{
                //if we are here then this triangle has already been influenced by this source from this neighbor
                //pressures.get(comingFrom).put(sourceName, new Double(pressures.get(comingFrom).get(sourceName)+voltage));
            }
        }

        public double getSharedFaceLength(Triangle neighbor){
            Point2D p1=null;
            Point2D p2=null;
            for(Point2D vert : neighbor.myVerts){
                if(myVertsSet.contains(getVertexString(vert))){
                    if(p1==null){
                        p1=vert;
                    }else{
                        p2=vert;
                    }
                }
            }

            return p1.distance(p2);
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
                    if(myVertsSet.contains(getVertexString(vert))){
                        sharedVerts++;
                    }
                }

                if(sharedVerts==2){
                    myFaceNeighbors.add(neighbor);
                }
            }
        }

        public String getTriangleString(){
            String str= "";
            for(Point2D pt : myVerts){
                str+=getVertexString(pt);
            }
            return str;
        }
    }

    public String getVertexString(Point2D vertex){
        float roundToNearesth = 1000.0f;
        return  Float.toString((int)(vertex.getX()*roundToNearesth)/roundToNearesth) +
                Float.toString((int)(vertex.getY()*roundToNearesth)/roundToNearesth);
    }
}
