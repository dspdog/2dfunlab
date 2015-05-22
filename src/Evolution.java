import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collections;
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
    //static float polarity = 1.0f;
    static Random rnd = new Random();

    static double targetArea = 15000d;


    static double highestScore = 0;


    public static ArrayList<TransDescriptor> scoreList = new ArrayList<TransDescriptor>();

    public static ArrayList<AffineTransform> trans;
    public static TransDescriptor desc;
    public static ArrayList<AffineTransform> recordTrans;
    //public static ArrayList<Shape> treeShape;

    public static float scaleDown = 1.0f;

    public static Area theSubArea;
    public static Area theArea;
    public static Area theRecordArea;
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
        theShape = MyPolygonUtils.NGon(13);

        int numberOfTransforms = 2; // = number of control points/ affines tranforms to choose from

        float startScale = 0.01f;

        if(trans==null || resetShape){
            resetShape=false;
            highestScore=0;
            recordTrans = new ArrayList<AffineTransform>();
            trans = new ArrayList<AffineTransform>();
            for(int i=0; i<numberOfTransforms; i++)trans.add(MyTransformUtils.getRandomSmall(startScale)); //use getRandom for more random pts
            double score = getScore(trans);
            desc = new TransDescriptor(trans,score,0);
        }else{
            if(desc.children.size()>0){
                Collections.sort(desc.children);
                desc = desc.children.get(0);
                System.out.println("GEN " + desc.generation);
            }else{
                desc = desc.randomAncestor();
                System.out.println("---GEN " + desc.generation);
            }
        }

        trans = desc.trans;

        float max = 100f;
        for(int attempt = 0; attempt<max; attempt++){
            float rndScale = (float)rnd.nextGaussian()*attempt/max;//(float)rnd.nextGaussian()*0.1f; //random gaussian scaling is good at escaping local minima!
            testDerivTransforms(rndScale, desc);
        }

        System.out.println(desc.children.size() + " offspring");
        generations++;
        View.theAreaDrawn= theArea;

        Collections.sort(scoreList);
    }

    static void testDerivTransforms(float scale, TransDescriptor parentTransform){
        for(int i=0; i<parentTransform.trans.size(); i++){
            parentTransform.submitChild(MyTransformUtils.getNudgedList(parentTransform.trans, scale, 0.0f, i));
            parentTransform.submitChild(MyTransformUtils.getNudgedList(parentTransform.trans, 0.0f, scale, i));
            parentTransform.submitChild(MyTransformUtils.getRandomNudgedList(parentTransform.trans, scale, 0.0f));
            parentTransform.submitChild(MyTransformUtils.getRandomNudgedList(parentTransform.trans, 0.0f, scale));
            float rnd = (float)Math.random();
            parentTransform.submitChild(MyTransformUtils.getRandomNudgedList(parentTransform.trans, scale * rnd, scale * (1 - rnd)));
        }
    }

    static double getScore(ArrayList<AffineTransform> _trans){

        //TODO place "limits" on transform strength

        theArea = Evolution.buildTree(4, new AffineTransform(), theShape, _trans);
        View.theAreaDrawn = theArea;
        double startArea = MyAreaUtils.getAreaArea(theArea);
        scaleDown = (float)Math.sqrt(targetArea / startArea);
        //treeShape = Evolution.buildTreeShape(1, new AffineTransform(), theShape, trans);

        theArea.transform(AffineTransform.getScaleInstance(scaleDown,scaleDown));
        theScaledShape = AffineTransform.getScaleInstance(scaleDown,scaleDown).createTransformedShape(theShape);

        double score = MyAreaUtils.getAreaPerimeter(theArea) / MyAreaUtils.getAreaArea(theArea);

        //TODO reduce score according to variance^2

        return score;
    }

    public static ArrayList<AffineTransform> cloneList(ArrayList<AffineTransform> list){
        ArrayList<AffineTransform> newList = new ArrayList<AffineTransform>();
        for(AffineTransform tran : list){
            newList.add((AffineTransform)tran.clone());
        }
        return newList;
    }


}

