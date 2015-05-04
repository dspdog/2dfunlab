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

        float noise0 = (float) SimplexNoise.noise((seed + theTime - startTime) / 10000f, 0f) / 5f;
        float noise1 = (float) SimplexNoise.noise((10*seed + theTime - startTime) / 10000f, 10f+10f*seed) / 15f;
        float noise2 = (float) SimplexNoise.noise((20*seed + theTime - startTime) / 10000f, 20f+10f*seed) / 15f;
        float noise3 = (float) SimplexNoise.noise((30*seed + theTime - startTime) / 10000f, 30f+10f*seed) / 5f;
        float noise4 = (float) SimplexNoise.noise((40*seed + theTime - startTime) / 10000f, 40f+10f*seed) / 5f;
        float noise5 = (float) SimplexNoise.noise((50*seed + theTime - startTime) / 10000f, 50f+10f*seed) * 40f;
        float noise6 = (float) SimplexNoise.noise((60*seed + theTime - startTime) / 10000f, 60f+10f*seed) * 40f;

        at2.translate(noise5, noise6);
        at2.scale(1.1 + noise1, 1.1 + noise2);
        at2.rotate(noise0 * Math.PI);
        at2.shear(noise3, noise4);

        return at2;
    }

}
