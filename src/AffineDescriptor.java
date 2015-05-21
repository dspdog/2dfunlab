import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * Created by user on 5/21/2015.
 */
public class AffineDescriptor {
    ArrayList<AffineTransform> trans;
    double score = 0;

    public AffineDescriptor(ArrayList<AffineTransform> _trans, double _score){
        trans=Evolution.cloneList(_trans);
        score=_score;
    }
}
