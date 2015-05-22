import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * Created by user on 5/21/2015.
 */
public class TransDescriptor implements Comparable<TransDescriptor>{
    ArrayList<AffineTransform> trans;
    double score = 0;
    int attempts = 0; //attempts it took to get here

    TransDescriptor parent;
    ArrayList<TransDescriptor> children = new ArrayList<TransDescriptor>();

    public TransDescriptor(ArrayList<AffineTransform> _trans, double _score, int _attempts){
        trans=Evolution.cloneList(_trans);
        score=_score;
        attempts=_attempts;
    }

    public TransDescriptor(ArrayList<AffineTransform> _trans, double _score, int _attempts, TransDescriptor _parent){
        trans=Evolution.cloneList(_trans);
        score=_score;
        attempts=_attempts;
        parent=_parent;
    }

    public void submitChild(ArrayList<AffineTransform> list1){
        double _score = Evolution.getScore(list1);
        if(_score>this.score){this.children.add(new TransDescriptor(list1, _score, 0, this)); }
        else{attempts++;}
    }

    @Override
    public int compareTo(TransDescriptor o) {
        return o.score > this.score ? 1 : -1;
    }
}
