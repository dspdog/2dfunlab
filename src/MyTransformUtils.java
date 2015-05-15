import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

/**
 * Created by user on 5/3/2015.
 */
public class MyTransformUtils {

    static long startTime = System.currentTimeMillis();
    static long theTime = System.currentTimeMillis();

    static void setTime(){
        theTime = System.currentTimeMillis();
    }

    static AffineTransform compose(AffineTransform src, AffineTransform opp){
        src.translate(opp.getTranslateX(), opp.getTranslateY());
        src.scale(opp.getScaleX(), opp.getScaleY());
        src.shear(opp.getShearX(), opp.getShearY());
        return src;
    }

    static AffineTransform getRandomSmall() {

        float size = 0.01f;

        float scaleX =(float)(Math.random()-0.5f)*size+1f;
        float scaleY =(float)(Math.random()-0.5f)*size+1f;

        float shearX =(float)(Math.random()-0.5f)*size;
        float shearY =(float)(Math.random()-0.5f)*size;

        float translateX =(float)(Math.random()-0.5f)*size;
        float translateY =(float)(Math.random()-0.5f)*size;

        return new AffineTransform(scaleX,shearY,shearX,scaleY,translateX,translateY);
    }

    static AffineTransform getRandom() {

        float scaleX =(float)Math.random()*0.5f+ 0.3f;
        float scaleY =(float)Math.random()*0.5f+ 0.3f;

        float shearX =(float)(Math.random()-0.5f)*1.5f;
        float shearY =(float)(Math.random()-0.5f)*1.5f;

        float translateX =(float)(Math.random()-0.5f)*75f;
        float translateY =(float)(Math.random()-0.5f)*75f;

        return new AffineTransform(scaleX,shearY,shearX,scaleY,translateX,translateY);
    }



}
