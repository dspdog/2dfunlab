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

    TransDescriptor parent;
    ArrayList<TransDescriptor> children = new ArrayList<TransDescriptor>();

    public TransDescriptor(ArrayList<AffineTransform> _trans, double _score){
        trans=Evolution.cloneList(_trans);
        score=_score;
        attempts = 1;
        generation = 0;
    }

    public TransDescriptor(ArrayList<AffineTransform> _trans, double _score, TransDescriptor _parent){
        trans=Evolution.cloneList(_trans);
        score=_score;
        attempts=1;
        parent=_parent;
        generation = parent.generation+1;
    }

    public TransDescriptor randomAncestor(){
        int gens = (int)(Math.random()*generationsBeforeMe()+1); //+1 just makes it reset a bit more
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

    public TransDescriptor bestOfMySiblings(){
        if(parent==null)return this;
        Collections.sort(parent.children);
        return parent.children.get(0);
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
            TransDescriptor addition = new TransDescriptor(list1, _score, this);
            this.children.add(addition);
            Evolution.scoreList.add(addition);
        }
        else{
            attempts++;
            score*=0.99999d;
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
