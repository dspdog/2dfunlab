import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

/**
 * Created by user on 5/3/2015.
 */
public class RandomTransform {

    static long startTime = System.currentTimeMillis();
    static long theTime = 0;

    static void setTime(){
        theTime = System.currentTimeMillis();
    }

    static AffineTransform getRandom(AffineTransform at, int seed) {

        AffineTransform at2 = new AffineTransform(at);

        float noise0 = (float) SimplexNoise.noise((seed + theTime - startTime) / 10000f, 0f);
        float noise1 = (float) SimplexNoise.noise((10*seed + theTime - startTime) / 10000f, 10f+10f*seed) ;
        float noise2 = (float) SimplexNoise.noise((20*seed + theTime - startTime) / 10000f, 20f+10f*seed) ;
        float noise3 = (float) SimplexNoise.noise((30*seed + theTime - startTime) / 10000f, 30f+10f*seed) ;
        float noise4 = (float) SimplexNoise.noise((40*seed + theTime - startTime) / 10000f, 40f+10f*seed) ;
        float noise5 = (float) SimplexNoise.noise((50*seed + theTime - startTime) / 10000f, 50f+10f*seed) ;

        at2.translate(noise0*250, noise1*250);
        at2.scale(noise2/10+1, noise3/10+1);
        at2.shear(noise4, noise5);
        //at2.rotate(noise0 * Math.PI);

        return at2;
    }

}
