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

    static AffineTransform getRandom(AffineTransform at, int seed) {

        AffineTransform at2 = new AffineTransform(at);

        //translate
        float noise0 = (float) SimplexNoise.noise(seed*5, (seed*10000 + theTime - startTime) / 10000f, 0f);
        float noise1 = (float) SimplexNoise.noise(seed*6, (seed*20000 + theTime - startTime) / 10000f, 10f*seed) ;

        //scale
        float noise2 = (float) SimplexNoise.noise(seed*7, (seed*30000 + theTime - startTime) / 10000f, 20f*seed) ;
        float noise3 = (float) SimplexNoise.noise(seed*8, (seed*40000 + theTime - startTime) / 10000f, 30f*seed) ;

        //shear
        float noise4 = (float) SimplexNoise.noise(seed*9, (0 + theTime - startTime) / 10000f, 40f*seed) ;
        float noise5 = (float) SimplexNoise.noise(seed*10, (seed*35000 + theTime - startTime) / 10000f, 50f*seed) ;

        at2.translate(noise0*40, noise1*40);
        at2.scale(noise2/3+0.5, noise3/3+0.5);
        at2.shear(noise4, noise5);
        //at2.rotate(noise0 * Math.PI);

        return at2;
    }



}
