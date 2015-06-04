import javax.swing.table.AbstractTableModel;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

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
    Area myArea= null;

    Date myTime = new Date();

    public Evolution evolution;

    public static final int MEMBERS_PER_FAMILY = 200;

    TransDescriptor parent;
    ArrayList<TransDescriptor> children = new ArrayList<TransDescriptor>();

    public Area getArea(){
        if(myArea==null){
            myArea = evolution.buildTree(4, new AffineTransform(), evolution.theShape, trans);
            double startArea = MyAreaUtils.getAreaArea(myArea);
            double scaleDown = (float)Math.sqrt(Evolution.targetArea / startArea);
            myArea.transform(AffineTransform.getScaleInstance(scaleDown,scaleDown));
        }
        return myArea;
    }

    public TransDescriptor(ArrayList<AffineTransform> _trans, double _score, Evolution _evolution){
        trans=Evolution.cloneList(_trans);
        score=_score;
        attempts = 1;
        generation = 0;
        evolution=_evolution;
        famNum=evolution.familyNumber;
    }

    public TransDescriptor(ArrayList<AffineTransform> _trans, double _score, TransDescriptor _parent, Evolution _evolution){
        evolution=_evolution;
        trans=Evolution.cloneList(_trans);
        score=_score;
        attempts=1;
        parent=_parent;
        generation = parent.generation+1;
        famNum=evolution.familyNumber;

        evolution.familyMembers++;
        Evolution.familyMembersGlobal++;
        myId=Evolution.familyMembersGlobal;

        if(evolution.familyMembers %MEMBERS_PER_FAMILY==0){
            evolution.resetShape=true; //start new family
            evolution.familyNumber++;
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
        double _score = evolution.getScore(list1);
        if(_score>this.score){
            TransDescriptor addition = new TransDescriptor(list1, _score, this, evolution);
            this.children.add(addition);
            evolution.scoreList.add(addition);
            evolution.globalScoreList.add(addition);
        }
        else{
            attempts++;
            //staleness limiter:
            // if(attempts>20)
            //score*=0.9999d;
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


    public static class TableModel extends AbstractTableModel {

        public ArrayList<TransDescriptor> descs;
        private String[] columnNames= {"generation", "famNum", "score", "id", "attempts", "time"};

        public TransDescriptor selected=null;

        public TableModel(ArrayList<TransDescriptor> descs, TransDescriptor _selected){
            this.descs = descs;
            if(_selected!=null){
                selected=_selected;
            }
        }

        @Override
        public int getRowCount() {
            return descs.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        public String getColumnName(int column) {
            return columnNames[column];
        }

        public void add(TransDescriptor desc) {
            descs.add(desc);
        }

        public void remove(TransDescriptor desc) {
            if (descs.contains(desc)) {
                descs.remove(desc);
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            TransDescriptor desc = descs.get(rowIndex);
            switch (columnIndex){
                case -1:
                    return desc;
                case 0:
                    return desc.generation;
                case 1:
                    return desc.famNum;
                case 2:
                    return desc.score;
                case 3:
                    return desc.myId;
                case 4:
                    return desc.attempts;
                case 5:
                    return new SimpleDateFormat("h:mm:ss a yyyy-MM-dd ").format(desc.myTime);
            }
            return "";
        }
    }

}
