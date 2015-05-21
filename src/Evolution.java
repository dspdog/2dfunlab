import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by user on 5/20/2015.
 */
public class Evolution {


    static int attempts = 0;
    static int generations = 0;
    static int evolves = 0;
    static boolean resetShape = true;
    static String scoreString = "";
    static float polarity = 1.0f;
    static Random rnd = new Random();

    static double targetArea = 15000d;


    static double highestScore = 0;


    public static ArrayList<AffineTransform> trans;
    public static ArrayList<AffineTransform> recordTrans;
    //public static ArrayList<Shape> treeShape;

    public static float scaleDown = 1.0f;

    public static Area theSubArea;
    public static Area theArea;
    public static Shape theShape;
    public static Shape theScaledShape;

    public static Area buildTree(int depth, AffineTransform atAccum, Shape _theShape, ArrayList<AffineTransform> _trans){

        Area result = new Area(atAccum.createTransformedShape(_theShape));

        if(depth > 0 )
            for(AffineTransform tran : _trans){
                result.add(buildTree(depth - 1, MyTransformUtils.compose((AffineTransform)atAccum.clone(), tran),_theShape, _trans));
            }

        return result;
    }

    public static ArrayList<Shape> buildTreeShape(int depth, AffineTransform atAccum, Shape _theShape, ArrayList<AffineTransform> trans){

        ArrayList<Shape> result = new ArrayList<Shape>();
        result.add(atAccum.createTransformedShape(_theShape));

        if(depth > 0 )
            for(AffineTransform tran : trans){
                result.addAll(buildTreeShape(depth - 1, MyTransformUtils.compose((AffineTransform) atAccum.clone(), tran), _theShape, trans));
            }

        return result;
    }


    public static void updateTree(){
        MyTransformUtils.setTime();
        theArea = new Area();
        theSubArea = new Area();
        theShape = MyPolygonUtils.NGon(37);

        float rndScale = (float)rnd.nextGaussian()*0.01f; //random gaussian scaling is good at escaping local minima!

        int numberOfTransforms = 2; // = number of control points/ affines tranforms to choose from

        if(trans==null || resetShape){
            resetShape=false;
            highestScore=0;
            recordTrans = new ArrayList<AffineTransform>();
            trans = new ArrayList<AffineTransform>();
            for(int i=0; i<numberOfTransforms; i++)trans.add(MyTransformUtils.getRandomSmall(rndScale)); //use getRandom for more random pts
        }

        ArrayList<AffineTransform> scoreDeriv = getDerivTransforms(trans, 0.01f);

        //for(AffineTransform tran : trans){
        //    MyTransformUtils.compose(tran,MyTransformUtils.getRandomSmall(rndScale)); //nudge each transform
        //}

        double score = getScore(trans);


        if(score*polarity>=highestScore*polarity){
            highestScore=score;
            recordTrans = cloneList(trans);
            evolves++;
            scoreString = (
                    "SCORE: " +String.format("%1$.12f", highestScore) + ", "
                            + attempts + " attempts, " + evolves + " evolutions, "
                            + generations + " gens, " + (generations/evolves)
                            + " g/e cont: " + theArea.isSingular() + ", area: ");
            attempts=0;
        }else{
            if(recordTrans!=null)
                trans = cloneList(recordTrans);
            attempts++;
        }

        generations++;

        View.theAreaDrawn= theArea;
    }

    static ArrayList<AffineTransform> getDerivTransforms(ArrayList<AffineTransform> _trans, float scale){

        ArrayList<AffineTransform> baseTrans = cloneList(_trans);
        ArrayList<AffineTransform> derivTrans = new ArrayList<AffineTransform>();

        ArrayList<Double> nudgedScoresSX = new ArrayList<Double>(); //scaleX
        ArrayList<Double> nudgedScoresSY = new ArrayList<Double>(); //scaleY
        double baseScore = getScore(baseTrans);

        for(int i=0; i<_trans.size(); i++){
            nudgedScoresSX.add(getScore(MyTransformUtils.getNudgedList(_trans, scale, 0.0f, i)));
            nudgedScoresSY.add(getScore(MyTransformUtils.getNudgedList(_trans, 0.0f, scale, i)));
        }

        for(int i=0; i<_trans.size(); i++){
            derivTrans.add(new AffineTransform(nudgedScoresSX.get(i),0,0,nudgedScoresSY.get(i),0,0)); //new AffineTransform(scaleX,shearY,shearX,scaleY,translateX,translateY);
        }

        return derivTrans;
    }

    static double getScore(ArrayList<AffineTransform> _trans){
        theArea = Evolution.buildTree(9, new AffineTransform(), theShape, _trans);
        double startArea = MyAreaUtils.getAreaArea(theArea);
        scaleDown = (float)Math.sqrt(targetArea / startArea);
        //treeShape = Evolution.buildTreeShape(1, new AffineTransform(), theShape, trans);

        theArea.transform(AffineTransform.getScaleInstance(scaleDown,scaleDown));
        theScaledShape = AffineTransform.getScaleInstance(scaleDown,scaleDown).createTransformedShape(theShape);

        return MyAreaUtils.getAreaPerimeter(theArea) / MyAreaUtils.getAreaArea(theArea);
    }

    public static ArrayList<AffineTransform> cloneList(ArrayList<AffineTransform> list){
        ArrayList<AffineTransform> newList = new ArrayList<AffineTransform>();
        for(AffineTransform tran : list){
            newList.add((AffineTransform)tran.clone());
        }
        return newList;
    }


}

