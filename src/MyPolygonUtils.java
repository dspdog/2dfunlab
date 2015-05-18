import org.poly2tri.geometry.polygon.PolygonPoint;
//import org.poly2tri.geometry.polygon.Polygon;
import java.awt.*;

/**
 * Created by user on 5/9/2015.
 */
public class MyPolygonUtils {
    static public int worldScale = 2000;
    public static Shape NGon(int N){ //http://www.java2s.com/Code/Java/2D-Graphics-GUI/DrawaPolygon.htm
        float radius = worldScale;
        float x = 0;
        float y = 0;
        Polygon p = new Polygon();

        for (int i = 0; i < N; i++){
            p.addPoint((int) (x + radius * Math.cos(i * 2 * Math.PI / N)),
                    (int) (y + radius * Math.sin(i * 2 * Math.PI / N)));

        }

        return p;
    }

    //TODO area --> Polygon 
    private org.poly2tri.geometry.polygon.Polygon createCirclePolygon( int n,
                                         double scale,
                                         double radius,
                                         double x,
                                         double y )
    {
        if( n < 3 ) n=3;

        PolygonPoint[] points = new PolygonPoint[n];
        for( int i=0; i<n; i++ )
        {
            points[i] = new PolygonPoint( scale*(x + radius*Math.cos( (2.0*Math.PI*i)/n )),
                    scale*(y + radius*Math.sin( (2.0*Math.PI*i)/n ) ));
        }
        return new org.poly2tri.geometry.polygon.Polygon( points );

        //Poly2Tri.triangulate( circle );
    }
}
