import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by user on 5/21/2015.
 */
public class TransDescriptor implements Comparable<TransDescriptor>{
    ArrayList<AffineTransform> trans;
    double score;
    int attempts; //attempts it took to get here
    int generation; //doesnt account for parents removed from graph -- see generationsBeforeMe()
    int famNum;
    long myId = -1;

    final int MEMBERS_PER_FAMILY = 512;

    public static long familyMembers = 0;

    TransDescriptor parent;
    ArrayList<TransDescriptor> children = new ArrayList<TransDescriptor>();

    public TransDescriptor(ArrayList<AffineTransform> _trans, double _score, int _famNum){
        trans=Evolution.cloneList(_trans);
        score=_score;
        attempts = 1;
        generation = 0;
        famNum=_famNum;
    }

    public TransDescriptor(ArrayList<AffineTransform> _trans, double _score, TransDescriptor _parent, int _famNum){
        trans=Evolution.cloneList(_trans);
        score=_score;
        attempts=1;
        parent=_parent;
        famNum=_famNum;
        generation = parent.generation+1;

        familyMembers++;
        myId= familyMembers;

        if(familyMembers %MEMBERS_PER_FAMILY==0){
            Evolution.resetShape=true; //start new family
            familyMembers=0;
        }
    }

    public TransDescriptor randomAncestor(){
        int gens = (int)(Math.random()*generationsBeforeMe()+15); //+X just makes it reset a bit more
        return myNParent(gens);
    }

    public TransDescriptor myNParent(int gens){
        if(gens==0){
            return this;//.bestOfMySiblings();
        }else{
            if(parent==null)return this;
            return parent.myNParent(gens-1);
        }
    }

    public ArrayList<TransDescriptor> bestOfMySiblings(int n){
        ArrayList<TransDescriptor> res = new ArrayList<TransDescriptor>();

        if(parent==null){
            res.add(this);
            return res;
        }

        Collections.sort(parent.children);
        res = new ArrayList<TransDescriptor>(parent.children.subList(0,(int)Math.min(n, parent.children.size())));
        return res;
    }

    public int generationsBeforeMe(){
        return generationsBeforeMe(0);
    }

    public int generationsBeforeMe(int i){
        if(parent==null)return i;
        return parent.generationsBeforeMe(i + 1);
    }

    public void submitChild(ArrayList<AffineTransform> list1){
        double _score = Evolution.getScore(list1);
        if(_score>this.score){
            TransDescriptor addition = new TransDescriptor(list1, _score, this, famNum);
            this.children.add(addition);
            Evolution.scoreList.add(addition);
            Evolution.globalScoreList.add(addition);
        }
        else{
            attempts++;
            if(attempts>20)
            score*=0.9999d;
            //if(attempts>200){
            //    Evolution.deleteFromGraph(this);
            //}
        }
    }

    @Override

    /*public int compareTo(TransDescriptor o) { //compare by attempts ascending then score descending
        if(o.attempts==this.attempts)return this.compareToScore(o);
        return o.attempts > this.attempts ? -1 : 1;
    }*/

    public int compareTo(TransDescriptor o) { //compare by score only
        if(o.score==this.score)return 0;
        return o.score > this.score ? 1 : -1;
    }
}
