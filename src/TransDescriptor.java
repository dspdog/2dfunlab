import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * Created by user on 5/21/2015.
 */
public class TransDescriptor {
    ArrayList<AffineTransform> trans;
    double score = 0;
    int attempts = 0; //attempts it took to get here

    //TODO parent
    //TODO children (w/ better scores)

    public TransDescriptor(ArrayList<AffineTransform> _trans, double _score, int _attempts){
        trans=Evolution.cloneList(_trans);
        score=_score;
        attempts=_attempts;
    }
}
