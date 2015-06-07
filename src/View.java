
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

public class View extends Panel
    implements MouseWheelListener, KeyListener, FocusListener, ActionListener, MouseMotionListener

{
    static long startTime = System.currentTimeMillis();

    public static Area theAreaDrawn;

    Point2D realMousePt;
    Point2D centerPt = new Point2D.Double(0,0);

    final UIThread game;
    boolean quit;
    final int screenwidth;
    final int screenheight;
    final Font screenFont = new Font(Font.MONOSPACED, Font.BOLD, 12);

    double pixelsData[];
    double dataMax = 0;

    int pixels[];
    BufferedImage render;
    Graphics2D rg;
    long fpsDraw;
    long fpsLogic;
    long framesThisSecondDrawn;
    long framesThisSecondLogic;
    long oneSecondAgo;

    long samplesThisFrame;
    double samplesNeeded;

    double zoom = 1.0;

    static int NUMEVOLVERS = 7;

    public ArrayList<Evolution> myEvolutions = new ArrayList<>();
    public static ArrayList<EvolutionThread> myEvolutionThreads = new ArrayList<>();
    public static ArrayList<TransDescriptor> theFullList = new ArrayList<>();
    //user params



        static int mousex;
        static int mousey;
        int mouseScroll;


    int maxPoints;
    int maxLineLength;

    //drag vars
        int mousemode; //current mouse button

    boolean started;


    public View(){

        for(int i=0; i<NUMEVOLVERS; i++){
            Evolution theEvolution = new Evolution();
            myEvolutions.add(theEvolution);
            myEvolutionThreads.add(new EvolutionThread(theEvolution));
        }

        started=false;
        oneSecondAgo =0;
        framesThisSecondDrawn = 0;
        framesThisSecondLogic = 0;
        game = new UIThread();

        quit = false;
        screenwidth = 1024;
        screenheight = 512;
        pixels = new int[screenwidth * screenheight];

        pixelsData = new double[screenwidth * screenheight];
        mousemode = 0;
        samplesNeeded = 1;
        maxLineLength = screenwidth;

        mouseScroll = 0;
    }

    static TransDescriptor selectedTrans = null;

    public static void main(String[] args) {
        JFrame f = new JFrame();

        f.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            };
        });

        final View is = new View();
        is.setSize(is.screenwidth, is.screenheight); // same size as defined in the HTML APPLET
        is.init();

        final TransDescriptor.TableModel model = new TransDescriptor.TableModel(theFullList, selectedTrans);
        final JTable table = new JTable(model);

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(table.getSelectedRow()!=-1){
                    selectedTrans = (TransDescriptor) table.getValueAt(table.getSelectedRow(),-1);
                }
            }
        });

        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                combinedLists(is);

                try{

                    Collections.sort(theFullList);
                }catch (Exception ex){
                    ex.printStackTrace();
                }


                final TransDescriptor.TableModel _model = new TransDescriptor.TableModel(theFullList, selectedTrans);
                table.setModel(_model);

                int index = theFullList.indexOf(_model.selected);
                if(index>=0)
                table.setRowSelectionInterval(index,index);
            }
        });
        timer.start();

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                is, new JScrollPane(table));

        splitPane.setDividerLocation(512);

        f.add(splitPane);

        f.pack();

        f.setSize(is.screenwidth, is.screenheight*2 + 20); // add 20, seems enough for the Frame title,
        f.show();
    }



    public static ArrayList<TransDescriptor> combinedLists( View is){
        ArrayList<TransDescriptor> result = new ArrayList<TransDescriptor>();

        for(Evolution e : is.myEvolutions){
            result.addAll(e.globalScoreList);
        }

        theFullList = result;

        return result;
    }

    public void init() {
        System.setProperty("sun.java2d.opengl","True");
        start();
    }

    public void actionPerformed(ActionEvent e) {

    }

    public class UIThread extends Thread{
        public void run(){
            while(!quit){

                Point2D mousePt = new Point2D.Float(mousex,mousey);
                Point2D _mousePt = new Point2D.Float(mousex,mousey);

                try {
                    _mousePt = cameraTransform.inverseTransform(mousePt,_mousePt);
                } catch (NoninvertibleTransformException e) {e.printStackTrace();}

                realMousePt = _mousePt;

                repaint();
                try {
                    sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public UIThread(){
        }
    }


    public class EvolutionThread extends Thread{

        Evolution evolution;

        public void run(){
            while(!quit){
                framesThisSecondLogic++;
                evolution.updateTree();
            }
        }

        public EvolutionThread(Evolution _evolution){
            evolution=_evolution;
        }
    }

    public void start(){
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        render =  new BufferedImage(screenwidth, screenheight, BufferedImage.TYPE_INT_RGB); //createImage(screenwidth, screenheight);
        rg = (Graphics2D)render.getGraphics();

        clearframe();

        for(EvolutionThread t : myEvolutionThreads){
            t.start();
        }

        game.start();

        started = true;
    }

    public void update(Graphics gr){

        paint((Graphics2D)gr);
    }

    AffineTransform cameraTransform = new AffineTransform();

    public void paint(Graphics2D gr){
        rg = (Graphics2D)render.getGraphics();

        rg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        framesThisSecondDrawn++;
        if(System.currentTimeMillis()- oneSecondAgo >=1000){
            oneSecondAgo = System.currentTimeMillis();
            fpsDraw = framesThisSecondDrawn;
            fpsLogic = framesThisSecondLogic;
            framesThisSecondDrawn =0;
            framesThisSecondLogic =0;
        }

        rg.setColor(new Color(1f,1f,1f));
        rg.fillRect(0, 0, screenwidth, screenheight);

        rg.setColor(Color.black);
        rg.setFont(screenFont);
        int row = 15;
       // rg.drawString(myEvolution1.scoreString, 5, row*1);
        rg.drawString("FPS DRAW " + String.valueOf(fpsDraw) + " ", 5, row*1);
        rg.drawString("FPS LOGIC " + String.valueOf(fpsLogic), 5, row*2);
        rg.drawString("INDIV #" + String.valueOf(Evolution.familyMembersGlobal) + " " + (int)(1.0f * Evolution.familyMembersGlobal / ((System.currentTimeMillis()-startTime)/1000f)) + "i/s", 5, row*3);
        rg.drawString("FAM #" + myEvolutions.get(0).familyNumber, 5, row*4);

        cameraTransform = new AffineTransform();
        cameraTransform.translate(screenwidth/2,screenheight/2);
        cameraTransform.scale(zoom, zoom);
        cameraTransform.translate(-centerPt.getX(), -centerPt.getY());

        rg.setTransform(cameraTransform);
        rg.setStroke(new BasicStroke(1.0f / (float)zoom));


        if(theAreaDrawn!=null){
            rg.setColor(Color.lightGray);
            //rg.draw(theAreaDrawn);

            if(selectedTrans!=null){
                rg.setColor(Color.black);

                TransDescriptor transToShow = selectedTrans.myNParent(selectedTrans.generationsBeforeMe()*mousey/512);
                Area areaToDraw = transToShow.getArea();

                rg.fill(areaToDraw);
                rg.setColor(Color.gray);

                for(Shape s : transToShow.organizedTriangles.internal){
                    rg.draw(s);
                }
            }

            rg.setColor(Color.red);
        }

        rg.drawRect((int)centerPt.getX(),(int)centerPt.getY(),20,20);
        gr.drawImage(render, 0, 0, screenwidth, screenheight, this);
    }

    /*
    public Area buildTree(Shape _theShape, AffineTransform atAccum, ArrayList<Integer> depthsList){ //new ArrayList<Integer>(listA);
        //TODO depth per-transform: (much slower)
        Area result = new Area(atAccum.createTransformedShape(_theShape));
        int numTrans = depthsList.size();

        for(int i=0; i<numTrans; i++){
            if(depthsList.get(i)>0){
                ArrayList<Integer> copyList = new ArrayList<Integer>(depthsList);
                copyList.set(i,copyList.get(i)-1);
                result.add(buildTree(_theShape, MyTransformUtils.compose((AffineTransform)atAccum.clone(), trans.get(i)), copyList));
            }
        }
        return result;
    }
     */

    public void clearframe(){
        for(int a = 0; a < screenwidth * screenheight; a++){
            pixels[a] = 0xff000000;
            pixelsData[a] = 0;
        }

        samplesThisFrame=0;
        dataMax = 0;


    }


    public void mouseClicked(MouseEvent mouseevent){
    }

    public void mousePressed(MouseEvent e){

    }

    public void mouseReleased(MouseEvent e){
        //setCursor (Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        mousemode = 0;
    }

    public void mouseEntered(MouseEvent e){
        //setCursor (Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    public void mouseExited(MouseEvent e){
        //setCursor (Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    public void mouseDragged(MouseEvent e){

        clearframe();


    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //aJTable.rowAtPoint(e.getPoint());
        mousex = e.getX();
        mousey = e.getY();
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseScroll += e.getWheelRotation();
        if(e.getWheelRotation()>0){ //scroll down

                zoom *=0.99;

        }else{ //scroll up

                zoom /=0.99;

        }

        clearframe();

    }

    public void keyTyped(KeyEvent e){
    }

    public void keyPressed(KeyEvent e){

        if(e.getKeyChar() == 'a')
            centerPt.setLocation(centerPt.getX()-10,centerPt.getY());
        if(e.getKeyChar() == 'd')
            centerPt.setLocation(centerPt.getX() + 10, centerPt.getY());
        if(e.getKeyChar() == 'w')
            centerPt.setLocation(centerPt.getX(),centerPt.getY()-10);
        if(e.getKeyChar() == 's'){
            centerPt.setLocation(centerPt.getX(),centerPt.getY()+10);
        }
        //if(e.getKeyChar() == 'r')
        //    myEvolution.resetShape=true;
        clearframe();
    }

    public void keyReleased(KeyEvent e){
    }

    public void focusGained(FocusEvent focusevent){}
    public void focusLost(FocusEvent focusevent){}
}
