import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by user on 5/20/2015.
 */
public class Evolution {

    static int generations = 0;
    static boolean resetShape = true;
    static String scoreString = "";
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

        float startScale = 0.00f; //zero = no bias for upper-most parent

        if(trans==null || resetShape){
            resetShape=false;
            highestScore=0;
            recordTrans = new ArrayList<AffineTransform>();
            trans = new ArrayList<AffineTransform>();
            scoreList = new ArrayList<TransDescriptor>();
            for(int i=0; i<numberOfTransforms; i++)trans.add(MyTransformUtils.getRandomSmall(startScale)); //use getRandom for more random pts
            double score = getScore(trans);
            desc = new TransDescriptor(trans,score);
        }else{
            if(desc.children.size()>0){
                Collections.sort(desc.children);
                //int index = (int)(Math.min(desc.children.size()-1, Math.abs(rnd.nextGaussian()*3f))); //gaussian sibling index
                desc = desc.children.get(0);
            }else{
                desc = desc.randomAncestor();
                System.out.println("UP");
            }
        }
        if(desc.parent!=null){
            System.out.println("CURRENTLY #" + scoreList.indexOf(desc) + "/" + scoreList.size() + " GEN " + desc.generation + "(" + desc.generationsBeforeMe() + ") SIBS " + desc.parent.children.size());
        }

        trans = desc.trans;

        float max = 50f;
        for(int attempt = 0; attempt<max; attempt++){
            if(scoreList.size()>10)
            desc = scoreList.get(0); //using elements other than #1 doesnt converge as well

            float rndScale = (float)rnd.nextGaussian()*(attempt/max);//(float)rnd.nextGaussian()*0.1f; //random gaussian scaling is good at escaping local minima!
            testDerivTransforms(rndScale, desc);

            Collections.sort(scoreList);
        }

        generations++;
        View.theAreaDrawn= theArea;

        pruneList();
    }

    public static void pruneList(){
        int maxSize = 500;

        //remove all but top siblings from list
        HashSet<TransDescriptor> newList = new HashSet<TransDescriptor>();
        for(TransDescriptor tran : scoreList){
            newList.addAll(tran.bestOfMySiblings(1)); //converges best w/ 1
        }

        scoreList=new ArrayList<TransDescriptor>(newList);
        Collections.sort(scoreList);

        if(scoreList.size()>maxSize){
            for(TransDescriptor tran : scoreList.subList(maxSize, scoreList.size())){
                deleteFromGraph(tran);
            }
            scoreList.subList(maxSize, scoreList.size()).clear();
        }

        //TODO remove non-top sibs from families?
    }

    static public void deleteFromGraph(TransDescriptor tran){
        if(tran.parent!=null){
            tran.parent.children.remove(tran);
            //update family tree
            tran.parent.children.addAll(tran.children);
            for(TransDescriptor child : tran.children){
                child.parent = tran.parent;
            }
        }
    }

    static void testDerivTransforms(float scale, TransDescriptor parentTransform){
        for(int i=0; i<parentTransform.trans.size(); i++){
            parentTransform.submitChild(MyTransformUtils.getNudgedList(parentTransform.trans, scale, 0f, 0f,0f, i));
            parentTransform.submitChild(MyTransformUtils.getNudgedList(parentTransform.trans, 0f, scale, 0f,0f, i));

            parentTransform.submitChild(MyTransformUtils.getNudgedList(parentTransform.trans, 0f, 0f, scale,0f, i));
            parentTransform.submitChild(MyTransformUtils.getNudgedList(parentTransform.trans, 0f, 0f, 0f,scale, i));

            parentTransform.submitChild(MyTransformUtils.getRandomNudgedList(parentTransform.trans, scale, 0f,0f,0f));
            parentTransform.submitChild(MyTransformUtils.getRandomNudgedList(parentTransform.trans, 0f, scale,0f,0f));
            parentTransform.submitChild(MyTransformUtils.getRandomNudgedList(parentTransform.trans, 0f, 0f, scale, 0f));
            parentTransform.submitChild(MyTransformUtils.getRandomNudgedList(parentTransform.trans, 0f, 0f, 0f, scale));

            float rnd = (float)Math.random();
            float rnd2 = (float)Math.random();
            parentTransform.submitChild(MyTransformUtils.getRandomNudgedList(parentTransform.trans, scale * rnd, scale * (1 - rnd), scale * rnd2, scale * (1 - rnd2)));
        }
    }

    static double getScore(ArrayList<AffineTransform> _trans){

        //TODO place "limits" on transform strength

        theArea = Evolution.buildTree(4, new AffineTransform(), theShape, _trans);

        double startArea = MyAreaUtils.getAreaArea(theArea);
        scaleDown = (float)Math.sqrt(targetArea / startArea);
        //treeShape = Evolution.buildTreeShape(1, new AffineTransform(), theShape, trans);

        theArea.transform(AffineTransform.getScaleInstance(scaleDown,scaleDown));
        theScaledShape = AffineTransform.getScaleInstance(scaleDown,scaleDown).createTransformedShape(theShape);

        double score = MyAreaUtils.getAreaPerimeter(theArea) / MyAreaUtils.getAreaArea(theArea);
        View.theAreaDrawn = theArea;
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

