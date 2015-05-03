
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.net.URL;

public class ifsys extends Panel
    implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, FocusListener, ActionListener,
        ItemListener
{

    Point2D realMousePt;
    Point2D centerPt = new Point2D.Double(512,512);

    final mainthread game;
    boolean quit;
    final int screenwidth;
    final int screenheight;
    final Font screenFont = new Font(Font.MONOSPACED, Font.BOLD, 12);

    int samplePixels[];
    int sampleWidth;
    int sampleHeight;

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

    Image sampleImage;

    //user params

        boolean antiAliasing;
        boolean trailsHidden;
        boolean spokesHidden;
        boolean infoHidden;
        boolean imgSamples;
        boolean guidesHidden;
        boolean ptsHidden;
        boolean invertColors;
        int sampletotal;
        int iterations;
        int pointselected;


        boolean shiftDown;
        boolean ctrlDown;
        boolean altDown;
        static int mousex;
        static int mousey;
        int mouseScroll;

    double shapeArea;
    double shapeAreaDelta;

    int maxPoints;
    int maxLineLength;

    //drag vars
        int mousemode; //current mouse button
        double startDragX;
        double startDragY;
        double startDragPX;
        double startDragPY;
        double startDragCenterX;
        double startDragCenterY;
        double startDragDist;
        double startDragAngle;
        double startDragScale;

    String presetstring;
    boolean started;
    int preset;

    public ifsys(){
        started=false;
        samplesThisFrame=0;
        oneSecondAgo =0;
        framesThisSecondDrawn = 0;
        framesThisSecondLogic = 0;
        altDown=false;
        ctrlDown=false;
        shiftDown=false;
        game = new mainthread();
        quit = false;
        antiAliasing = true;
        infoHidden = false;
        imgSamples = true;
        guidesHidden = false;
        ptsHidden = false;
        invertColors = false;
        screenwidth = 1024;
        screenheight = 1024;
        pixels = new int[screenwidth * screenheight];
        samplePixels = new int[screenwidth * screenheight];
        pixelsData = new double[screenwidth * screenheight];
        sampletotal = 1000;
        iterations = 2;
        mousemode = 0;
        samplesNeeded = 1;
        maxLineLength = screenwidth;

        mouseScroll = 0;

        pointselected=-1;
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();

        f.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            };
        });

        ifsys is = new ifsys();
        is.setSize(is.screenwidth, is.screenheight); // same size as defined in the HTML APPLET
        f.add(is);
        f.pack();
        is.init();
        f.setSize(is.screenwidth, is.screenheight + 20); // add 20, seems enough for the Frame title,
        f.show();

        /*
        MenuBar menuBar;
        Menu fileMenu, renderMenu, shapeMenu, guidesMenu, viewMenu;

        menuBar = new MenuBar();
        renderMenu = new Menu("Render");


        //RENDER MENU
            CheckboxMenuItem aaButton = new CheckboxMenuItem("Anti-Aliasing"); //anti-aliasing toggle
            aaButton.setState(is.antiAliasing);
            aaButton.addItemListener(is);
            renderMenu.add(aaButton);

            CheckboxMenuItem inButton = new CheckboxMenuItem("Invert"); //invert toggle
            inButton.setState(is.invertColors);
            inButton.addItemListener(is);
            renderMenu.add(inButton);

        menuBar.add(renderMenu);
        f.setMenuBar(menuBar);*/
    }

    public void init() {
        System.setProperty("sun.java2d.opengl","True");
        start();
    }


    public void findSelectedPoint(){

    }

    public void actionPerformed(ActionEvent e) {

    }

    public void itemStateChanged(ItemEvent e) {
        //RENDER MENU
            if(e.getItem()=="Anti-Aliasing"){
                antiAliasing = e.getStateChange()==1;
            }
            if(e.getItem()=="Invert"){
                invertColors = e.getStateChange()==1;
            }
    }


    public class mainthread extends Thread{
        public void run(){
            while(!quit){
                framesThisSecondLogic++;
                Point2D mousePt = new Point2D.Float(mousex,mousey);
                Point2D _mousePt = new Point2D.Float(mousex,mousey);

                try {
                    _mousePt = cameraTransform.inverseTransform(mousePt,_mousePt);
                } catch (NoninvertibleTransformException e) {e.printStackTrace();}

                realMousePt = _mousePt;

                repaint();
                try {
                    sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public mainthread(){
        }
    }

    public void start(){
        //setCursor (Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);
        render =  new BufferedImage(screenwidth, screenheight, BufferedImage.TYPE_INT_RGB); //createImage(screenwidth, screenheight);
        rg = (Graphics2D)render.getGraphics();

        clearframe();
        game.start();

        setSampleImg("meerkat.jpg");
        started = true;
        //NodeWorld.resetWorld(1);
    }

    public void update(Graphics gr){

        paint((Graphics2D)gr);
    }

    AffineTransform cameraTransform = new AffineTransform();

    public void paint(Graphics2D gr){
        rg = (Graphics2D)render.getGraphics();

        framesThisSecondDrawn++;
        if(System.currentTimeMillis()- oneSecondAgo >=1000){
            oneSecondAgo = System.currentTimeMillis();
            fpsDraw = framesThisSecondDrawn;
            fpsLogic = framesThisSecondLogic;
            framesThisSecondDrawn =0;
            framesThisSecondLogic =0;
        }

        //generatePixels();
        //rg.drawImage(createImage(new MemoryImageSource(screenwidth, screenheight, pixels, 0, screenwidth)), 0, 0, screenwidth, screenheight, this);
        //rg.drawImage(sampleImage, getWidth() - 50, 0, 50, 50, this);

        rg.setColor(new Color(0.1f,0.1f,0.1f));
        rg.fillRect(0, 0, screenwidth, screenheight);

        rg.setColor(Color.white);
        rg.setFont(screenFont);
        int row = 15;
        rg.drawString("FPS DRAW " + String.valueOf(fpsDraw) + " ", 5, row*2);
        rg.drawString("FPS LOGIC " + String.valueOf(fpsLogic), 5, row*3);

        cameraTransform = new AffineTransform();

        //cameraTransform.scale(zoom, zoom);
        cameraTransform.translate(-centerPt.getX()+(screenwidth/2), -centerPt.getY()+(screenheight/2));
      
        rg.setTransform(cameraTransform);
        rg.setStroke(new BasicStroke(1.0f / (float)zoom));

        rg.drawRect(0,0,100,100);
        rg.drawRect(100,100,200,200);
        rg.drawRect(200,200,300,300);

        rg.setColor(Color.red);
        rg.drawRect((int)centerPt.getX(),(int)centerPt.getY(),20,20);

        gr.drawImage(render, 0, 0, screenwidth, screenheight, this);
    }

    public void generatePixels(){
        double scaler = 255/dataMax;
        double area = 0;
        int scaledColor = 0;
        for(int a = 0; a < screenwidth * screenheight; a++){
            int argb = 255;
            scaledColor = (int)(scaler*pixelsData[a]);
            argb = (argb << 8) + scaledColor;
            argb = (argb << 8) + scaledColor;
            argb = (argb << 8) + scaledColor;
            pixels[a] = argb;
        }
    }

    public void clearframe(){
        for(int a = 0; a < screenwidth * screenheight; a++){
            pixels[a] = 0xff000000;
            pixelsData[a] = 0;
        }

        samplesThisFrame=0;
        dataMax = 0;


    }

    public boolean putPixel(double x, double y, double alpha){ 
        double decX, decY; //decimal parts of coordinates

        if(x < (double)(screenwidth - 1) &&
            y < (double)(screenheight - 1) &&
            x > 0.0D && y > 0.0D){

            decX = x - Math.floor(x);
            decY = y - Math.floor(y);

            if(antiAliasing){
                //each point contributes to 4 pixels

                pixelsData[(int)(x) + (int)(y) * screenwidth]+=alpha*(1.0-decX)*(1.0-decY);
                pixelsData[(int)(x+1) + (int)(y) * screenwidth]+=alpha*decX*(1.0-decY);
                pixelsData[(int)(x) + (int)(y+1) * screenwidth]+=alpha*decY*(1.0-decX);
                pixelsData[(int)(x+1) + (int)(y+1) * screenwidth]+=alpha*decY*decX;

                if(dataMax<pixelsData[(int)x + (int)y * screenwidth]/ zoom){dataMax = pixelsData[(int)x + (int)y * screenwidth]/ zoom;}
            }else{
                pixelsData[(int)(x) + (int)(y) * screenwidth]=1;
            }

            samplesThisFrame++;

            return true; //pixel is in screen bounds
        }else{
            return false; //pixel outside of screen bounds
        }

    }

    public void setSampleImg(String filename){
        sampleImage = loadImage(filename);

        try {
            PixelGrabber grabber =
                    new PixelGrabber(sampleImage, 0, 0, -1, -1, false);

            if (grabber.grabPixels()) {
                sampleWidth = grabber.getWidth();
                sampleHeight = grabber.getHeight();
                samplePixels = (int[]) grabber.getPixels();

                for(int i=0; i<sampleHeight*sampleWidth; i++){
                    samplePixels[i] = samplePixels[i]&0xFF;
                }
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public double getSampleValue(double x, double y){ //TODO bilinear filtering
        int index = (int)x+sampleWidth*(int)y;
        if(index < sampleWidth*sampleHeight && index>0){
            return samplePixels[(int)x + (int)y*sampleWidth] / 255.0;
        }else{
            return 0;
        }
    }

    public Image loadImage(String name){
        try{
            URL theImgURL = new URL("file:/C:/Users/user/workspace/instant-ifs/instant-ifs/img/" + name);//file:/C:/Users/Labrats/Documents/GitHub/
            //URL theImgURL = new URL("file:/C:/Users/Labrats/Documents/GitHub/instant-ifs/instant-ifs/img/" + name);
            return ImageIO.read(theImgURL);
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void mouseClicked(MouseEvent mouseevent){
    }

    public void mousePressed(MouseEvent e){
        mousemode = e.getButton();

        mousex = e.getX();
        mousey = e.getY();
        findSelectedPoint();

        if(e.getClickCount()==2){
            if(mousemode == 1){ //add point w/ double click

                clearframe();

            }else if(mousemode == 3){ //remove point w/ double right click

                clearframe();

            }
        }else{
            startDragX = e.getX();
            startDragY = e.getY();


            if(ctrlDown || shiftDown){


                startDragScale = 1.0;
            }else{

            }

            requestFocus();
        }
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

    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseScroll += e.getWheelRotation();
        if(e.getWheelRotation()>0){ //scroll down
            if(shiftDown){

            }else if(ctrlDown){

            }else if(altDown){

            }else{
                zoom *=0.99;
            }
        }else{ //scroll up
            if(shiftDown){

            }else if(ctrlDown){

            }else if(altDown){

            }else{//increase point opacity
                zoom /=0.99;
            }
        }

        clearframe();

    }

    public void mouseMoved(MouseEvent e){
        findSelectedPoint();
        mousex = e.getX();
        mousey = e.getY();
    }

    public void keyTyped(KeyEvent e){
    }

    public void keyPressed(KeyEvent e){
        if(e.getKeyCode()==KeyEvent.VK_ALT)
            altDown=true;
        if(e.getKeyCode()==KeyEvent.VK_CONTROL)
            ctrlDown=true;
        if(e.getKeyCode()==KeyEvent.VK_SHIFT)
            shiftDown=true;
        if(e.getKeyChar() == 'a')
            centerPt.setLocation(centerPt.getX()-10,centerPt.getY());
        if(e.getKeyChar() == 'd')
            centerPt.setLocation(centerPt.getX() + 10, centerPt.getY());
        if(e.getKeyChar() == 'w')
            centerPt.setLocation(centerPt.getX(),centerPt.getY()-10);
        if(e.getKeyChar() == 's'){
            centerPt.setLocation(centerPt.getX(),centerPt.getY()+10);
        }
        clearframe();


    }

    public void keyReleased(KeyEvent e){
        if(e.getKeyCode()==KeyEvent.VK_ALT)
            altDown=false;
        if(e.getKeyCode()==KeyEvent.VK_CONTROL)
            ctrlDown=false;
        if(e.getKeyCode()==KeyEvent.VK_SHIFT)
            shiftDown=false;

    }

    public void focusGained(FocusEvent focusevent){}
    public void focusLost(FocusEvent focusevent){}
}
