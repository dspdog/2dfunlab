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
    int generation;

    TransDescriptor parent;
    ArrayList<TransDescriptor> children = new ArrayList<TransDescriptor>();

    public TransDescriptor(ArrayList<AffineTransform> _trans, double _score, int _attempts){
        trans=Evolution.cloneList(_trans);
        score=_score;
        attempts=_attempts;
        generation = 0;
    }

    public TransDescriptor(ArrayList<AffineTransform> _trans, double _score, int _attempts, TransDescriptor _parent){
        trans=Evolution.cloneList(_trans);
        score=_score;
        attempts=_attempts;
        parent=_parent;
        generation = parent.generation+1;
    }

    public TransDescriptor randomAncestor(){
        int gens = (int)(Math.random()*generation);
        return myNParent(gens);
    }

    public TransDescriptor myNParent(int gens){
        if(gens==0){
            return this;
        }else{
            return parent.myNParent(gens-1);
        }
    }

    public void submitChild(ArrayList<AffineTransform> list1){
        double _score = Evolution.getScore(list1);
        if(_score>this.score){
            TransDescriptor addition = new TransDescriptor(list1, _score, 0, this);
            this.children.add(addition);
            Evolution.scoreList.add(addition);
        }
        else{attempts++;}
    }

    @Override
    public int compareTo(TransDescriptor o) {
        return o.score > this.score ? 1 : -1;
    }
}
