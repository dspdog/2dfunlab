import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

/**
 * Created by user on 5/3/2015.
 */
public class MyTransformUtils {

    static long startTime = System.currentTimeMillis();
    static long theTime = 0;

    static void setTime(){
        theTime = System.currentTimeMillis();
    }

    static AffineTransform compose(AffineTransform src, AffineTransform opp){

        src.translate(opp.getTranslateX(), opp.getTranslateY());
        src.scale(opp.getScaleX(), opp.getScaleY());
        src.shear(opp.getShearX(), opp.getShearY());

        return new AffineTransform(src);
    }

    static AffineTransform getRandom(AffineTransform at, int seed) {

        AffineTransform at2 = new AffineTransform(at);

        //translate
        float noise0 = (float) SimplexNoise.noise((seed + theTime - startTime) / 10000f, 0f);
        float noise1 = (float) SimplexNoise.noise((10*seed + theTime - startTime) / 10000f, 10f+10f*seed) ;

        //scale
        float noise2 = (float) SimplexNoise.noise((20*seed + theTime - startTime) / 10000f, 20f+10f*seed) ;
        float noise3 = (float) SimplexNoise.noise((30*seed + theTime - startTime) / 10000f, 30f+10f*seed) ;

        //shear
        float noise4 = (float) SimplexNoise.noise((40*seed + theTime - startTime) / 10000f, 40f+10f*seed) ;
        float noise5 = (float) SimplexNoise.noise((50*seed + theTime - startTime) / 10000f, 50f+10f*seed) ;

        at2.translate(noise0*200, noise1*200);
        at2.scale(noise2/10+0.8, noise3/10+0.8);
        at2.shear(noise4, noise5);
        //at2.rotate(noise0 * Math.PI);

        return at2;
    }

}
