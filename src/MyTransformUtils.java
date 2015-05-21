import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
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
        //src.translate(opp.getTranslateX(), opp.getTranslateY());
        //src.scale(opp.getScaleX(), opp.getScaleY());
        //src.shear(opp.getShearX(), opp.getShearY());
        src.concatenate(opp);
        return src;
    }

    static Random rnd = new Random();

    static AffineTransform getRandomSmall(float size) {

        float scaleX =(float)(rnd.nextGaussian())*size+1f;
        float scaleY =(float)(rnd.nextGaussian())*size+1f;

        float shearX = 0; //(float)(rnd.nextGaussian())*size;
        float shearY = 0; //(float)(rnd.nextGaussian())*size;

        float translateX =0;//(float)(rnd.nextGaussian())*size*MyPolygonUtils.worldScale;
        float translateY =0;//(float)(rnd.nextGaussian())*size*MyPolygonUtils.worldScale;

        return new AffineTransform(scaleX,shearY,shearX,scaleY,translateX,translateY);
    }

    static ArrayList<AffineTransform> getNudgedList(ArrayList<AffineTransform> trans, float _scaleX, float _scaleY, int nudgeIndex) {

        ArrayList<AffineTransform> nudgedList = new ArrayList<AffineTransform>();

        for(int i=0; i<trans.size(); i++){
            if(i==nudgeIndex){
                nudgedList.add(compose(new AffineTransform(trans.get(i)),getNudge(_scaleX,_scaleY)));
            }else{
                nudgedList.add(new AffineTransform(trans.get(i)));
            }
        }

        return nudgedList;
    }

    static ArrayList<AffineTransform> getRandomNudgedList(ArrayList<AffineTransform> trans, float _scaleX, float _scaleY) {

        ArrayList<AffineTransform> nudgedList = new ArrayList<AffineTransform>();

        for(int i=0; i<trans.size(); i++){
            nudgedList.add(compose(new AffineTransform(trans.get(i)),getNudge((float)(_scaleX*Math.random()),(float)(_scaleY*Math.random()))));
        }

        return nudgedList;
    }

    static ArrayList<AffineTransform> nudgeThisWithThat(ArrayList<AffineTransform> _this, ArrayList<AffineTransform> _that, float nudgeScale){

        ArrayList<AffineTransform> res = new ArrayList<AffineTransform>();

        for(int i=0; i<_this.size(); i++){
            AffineTransform thisAt = new AffineTransform(_this.get(i));
            AffineTransform thatAt = new AffineTransform(_that.get(i));
            float rnd = (float)Math.random();
            compose(thisAt, getNudge(thatAt.getScaleX() > 0 ? nudgeScale : -nudgeScale, thatAt.getScaleY() > 0 ? nudgeScale : -nudgeScale));
            res.add(thisAt);
        }

        return res;
    }

    static AffineTransform getNudge(float _scaleX, float _scaleY) {

        float scaleX =1f+_scaleX;
        float scaleY =1f+_scaleY;

        float shearX = 0; //(float)(rnd.nextGaussian())*size;
        float shearY = 0; //(float)(rnd.nextGaussian())*size;

        float translateX =0;//(float)(rnd.nextGaussian())*size*MyPolygonUtils.worldScale;
        float translateY =0;//(float)(rnd.nextGaussian())*size*MyPolygonUtils.worldScale;

        return new AffineTransform(scaleX,shearY,shearX,scaleY,translateX,translateY);
    }

    static AffineTransform getRandom() {

        float scaleX =(float)Math.random()*0.5f+ 0.3f;
        float scaleY =(float)Math.random()*0.5f+ 0.3f;

        float shearX =(float)(Math.random()-0.5f)*1.5f;
        float shearY =(float)(Math.random()-0.5f)*1.5f;

        float translateX =(float)(Math.random()-0.5f)*2000f*4;
        float translateY =(float)(Math.random()-0.5f)*2000f*4;

        return new AffineTransform(scaleX,shearY,shearX,scaleY,translateX,translateY);
    }
}
