import java.awt.*;

/**
 * Created by user on 5/9/2015.
 */
public class MyPolygonUtils {
    public static Shape NGon(int N){ //http://www.java2s.com/Code/Java/2D-Graphics-GUI/DrawaPolygon.htm
        float radius = 20;
        float x = 0;
        float y = 0;
        Polygon p = new Polygon();

        for (int i = 0; i < N; i++){
            p.addPoint((int) (x + radius * Math.cos(i * 2 * Math.PI / N)),
                    (int) (y + radius * Math.sin(i * 2 * Math.PI / N)));

        }

        return p;
    }
}
