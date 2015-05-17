import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.Random;

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

    static Random rnd = new Random();

    static AffineTransform getRandomSmall(float size) {

        float scaleX =(float)(rnd.nextGaussian())*size+1f;
        float scaleY =(float)(rnd.nextGaussian())*size+1f;

        float shearX =(float)(rnd.nextGaussian())*size;
        float shearY =(float)(rnd.nextGaussian())*size;

        float translateX =(float)(rnd.nextGaussian())*size;
        float translateY =(float)(rnd.nextGaussian())*size;

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
