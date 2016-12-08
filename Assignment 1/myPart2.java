import java.awt.image.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.List;

//Second exercise of Homework 1
public class myPart2 extends JPanel{
    
    private final float LEFT_IMAGE_FPS = 30;
    private float RIGHT_IMAGE_FPS;
    final int LEFT_IMAGE_DELAY = Math.round(1000.0f/LEFT_IMAGE_FPS);
    private int RIGHT_IMAGE_DELAY;
    JLabel leftPane, rightPane;
    private int xc = 256, yc = 256;
    private int imageSize = 512;
    private Timer leftTimer, rightTimer;
    private int n;
    private float speedOfRotation;
    private float leftCounter = 0, rightCounter = 0, leftStartDegreeStep, rightStartDegreeStep, spokeStep;
    private ImageIcon leftTempImage, rightTempImage;

    public myPart2(String[] arguments){
        //args[0] -> Number of lines
        //args[1] -> Speed of rotations
        //args[2] -> Frames per second
        
        n = Integer.parseInt(arguments[0]);
        speedOfRotation = Float.parseFloat(arguments[1]);
        RIGHT_IMAGE_FPS = Float.parseFloat(arguments[2]);
        RIGHT_IMAGE_DELAY = Math.round(1000.0f/RIGHT_IMAGE_FPS);

        leftStartDegreeStep = (360/LEFT_IMAGE_FPS)*speedOfRotation;
        rightStartDegreeStep = (360/RIGHT_IMAGE_FPS)*speedOfRotation;
        spokeStep = 360.0f/(float)n;

        leftPane = new JLabel(makeImage(leftCounter));
        rightPane = new JLabel(makeImage(rightCounter));
        this.add(leftPane);
        this.add(rightPane);

        leftTimer = new Timer(LEFT_IMAGE_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateLeftPaneImage();
            }
        });
        leftTimer.start();

        rightTimer = new Timer(RIGHT_IMAGE_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRightPaneImage();
            }
        });
        rightTimer.start();
    }

    private void display(){
        JFrame frame = new JFrame("Display videos");
        frame.getContentPane().add(this);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void updateLeftPaneImage(){
        leftCounter = (leftCounter + leftStartDegreeStep)%360;
        leftTempImage = makeImage(leftCounter);
        leftTempImage.getImage().flush();
        leftPane.setIcon(leftTempImage);
        leftPane.revalidate();
        leftPane.repaint();
    }

    public void updateRightPaneImage(){
        rightCounter = (rightCounter + rightStartDegreeStep)%360;
        rightTempImage = makeImage(rightCounter);
        rightTempImage.getImage().flush();
        rightPane.setIcon(rightTempImage);
        rightPane.revalidate();
        rightPane.repaint();
    }


    public ImageIcon makeImage(float degreeStep){
        float degree = degreeStep , slope = 0;
        degree = degree%360; 
        slope = (float)Math.tan(degree*Math.PI/180);
        int redLineStarter = 0;

        BufferedImage tempImage = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);
        tempImage = whitenImage(tempImage, imageSize, imageSize);
        for(int i=0;i<n;i++){
            if(degree==0){
                tempImage = drawLines(511, yc, tempImage, imageSize, imageSize, redLineStarter);
            }
            else if(degree==45){
                tempImage = drawLines(511, 511, tempImage, imageSize, imageSize, redLineStarter);
            }
            else if(degree==90){
                tempImage = drawLines(xc, 511, tempImage, imageSize, imageSize, redLineStarter);
            }
            else if(degree==135){
                tempImage = drawLines(0, 511, tempImage, imageSize, imageSize, redLineStarter);
            }
            else if(degree==180){
                tempImage = drawLines(0, yc, tempImage, imageSize, imageSize, redLineStarter);
            }
            else if(degree==225){
                tempImage = drawLines(0, 0, tempImage, imageSize, imageSize, redLineStarter);
            }
            else if(degree==270){
                tempImage = drawLines(xc, 0, tempImage, imageSize, imageSize, redLineStarter);
            }
            else if(degree==315){
                tempImage = drawLines(511, 0, tempImage, imageSize, imageSize, redLineStarter);
            }
            else if((0<degree && degree<45) || (315<degree && degree<360)){
                tempImage = drawLines(511, Math.round((255*slope) + 256), tempImage, imageSize, imageSize, redLineStarter);
            }
            else if((45<degree && degree<90) || (90<degree && degree<135)){
                tempImage = drawLines(Math.round((255/slope) + 256), 511, tempImage, imageSize, imageSize, redLineStarter);
            }
            else if((135<degree && degree<180) || (180<degree && degree<225)){
                tempImage = drawLines(0, Math.round((-255*slope) + 256), tempImage, imageSize, imageSize, redLineStarter);
            }
            else if((225<degree && degree<270) || (270<degree && degree<315)){
                tempImage = drawLines(Math.round((-255/slope) + 256), 0, tempImage, imageSize, imageSize, redLineStarter);
            }
                
            degree += spokeStep;
            degree = degree%360;
            slope = (float)Math.tan(degree*Math.PI/180);
            if(redLineStarter==0)
                redLineStarter = 1;
        }
        return (new ImageIcon(tempImage));
    }

    //Function to paint the whole image white
    public BufferedImage whitenImage(BufferedImage img, int width, int height){
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                byte r = (byte)255;
                byte g = (byte)255;
                byte b = (byte)255;
                int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                img.setRGB(x,y,pix);
            }
        }
        return img;
    }

    //Function to draw lines
    public BufferedImage drawLines(int x2, int y2, BufferedImage img, int width, int height, int redLineStarter){
        
        // two coordinates to connect a line, x is for height, y is for row
        int x1 = xc, y1 = yc;
        
        int start_x = x1, start_y = y1, end_x = x2, end_y = y2;
        int dx = x2 - x1;
        int dy = y2 - y1;
        double slope = 0;
        int pixelColor;
        if(redLineStarter==0){
            pixelColor = 0xff000000 | ((255 & 0xff) << 16) | ((0 & 0xff) << 8) | (0 & 0xff);
            redLineStarter = 1;
        }
        else
            pixelColor = 0xff000000 | ((0 & 0xff) << 16) | ((0 & 0xff) << 8) | (0 & 0xff);
        
        boolean anchor_x;
        if(dx != 0) slope = dy/(double)dx;
        
        if(Math.abs(slope) <= 1 && dx !=0) {
            anchor_x = true;
            if(dx < 0) {
                start_x = x2;
                start_y = y2;
                end_x = x1;
                end_y = y1;
            }
            slope = (end_y - start_y)/(double)(end_x - start_x);
        }
        else {
            anchor_x = false;
            if(dy < 0) {
                start_x = x2;
                start_y = y2;
                end_x = x1;
                end_y = y1;
            }
            if(dx == 0) {slope = 0;}
            else {slope = (end_x - start_x)/(double)(end_y - start_y);}
        }
        
        img.setRGB(start_x, start_y, pixelColor);
        
        if(anchor_x) {
            double y = start_y + 0.5;
            for(int x = start_x + 1; x <= end_x; x++) {
                y = y + slope;
                img.setRGB(x, (int)Math.floor(y), pixelColor);
            }
        }
        else {
            double x = start_x + 0.5;
            for(int y = start_y + 1; y <= end_y; y++) {
                x = x + slope;
                img.setRGB((int)Math.floor(x), y, pixelColor);
            }
        }
        return img;
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new myPart2(args).display();
            }
        });
    }
}