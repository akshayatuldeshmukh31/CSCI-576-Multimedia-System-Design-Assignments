//import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.lang.*;
import java.awt.Graphics;
import java.awt.Graphics2D; 
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;

//First exercise of Homework 1
public class myPart1{

    static BufferedImage leftImage = null;
    static BufferedImage rightImage = null;
    static int xc = 256, yc = 256;
    static int leftImageWidth = 512, rightImageWidth = 256;
    static boolean start = true;

    public static void main(String[] args){
        //args[0] -> Number of lines
        //args[1] -> Scaling factor
        //args[2] -> Boolean value for aliasing
        
        float slope = 0;
        float degree = 0;
        int n = Integer.parseInt(args[0]);
        float step = 360.0f/(float)n;
        leftImage = new BufferedImage(leftImageWidth, leftImageWidth, BufferedImage.TYPE_INT_RGB);
        leftImage = whitenImage(leftImage, leftImageWidth);

        for(int i=0;i<n;i++){
            //Find a way to get points for all lines
            //Call drawLines
            if(degree==0){
                leftImage = drawLines(511, yc, leftImage, leftImageWidth, leftImageWidth);
            }
            else if(degree==45){
                leftImage = drawLines(511, 511, leftImage, leftImageWidth, leftImageWidth);
            }
            else if(degree==90){
                leftImage = drawLines(xc, 511, leftImage, leftImageWidth, leftImageWidth);
            }
            else if(degree==135){
                leftImage = drawLines(0, 511, leftImage, leftImageWidth, leftImageWidth);
            }
            else if(degree==180){
                leftImage = drawLines(0, yc, leftImage, leftImageWidth, leftImageWidth);
            }
            else if(degree==225){
                leftImage = drawLines(0, 0, leftImage, leftImageWidth, leftImageWidth);
            }
            else if(degree==270){
                leftImage = drawLines(xc, 0, leftImage, leftImageWidth, leftImageWidth);
            }
            else if(degree==315){
                leftImage = drawLines(511, 0, leftImage, leftImageWidth, leftImageWidth);
            }
            else if((0<degree && degree<45) || (315<degree && degree<360)){
                leftImage = drawLines(511, Math.round((255*slope) + 256), leftImage, leftImageWidth, leftImageWidth);
            }
            else if((45<degree && degree<90) || (90<degree && degree<135)){
                leftImage = drawLines(Math.round((255/slope) + 256), 511, leftImage, leftImageWidth, leftImageWidth);
            }
            else if((135<degree && degree<180) || (180<degree && degree<225)){
                leftImage = drawLines(0, Math.round((-255*slope) + 256), leftImage, leftImageWidth, leftImageWidth);
            }
            else if((225<degree && degree<270) || (270<degree && degree<315)){
                leftImage = drawLines(Math.round((-255/slope) + 256), 0, leftImage, leftImageWidth, leftImageWidth);
            }
            
            degree += step;
            slope = (float)Math.tan(degree*Math.PI/180);
        }

        float scaleFac = Float.valueOf(args[1].trim()).floatValue();

        if(scaleFac!=0.0)
            rightImageWidth = Math.round((float)leftImageWidth/scaleFac);
        else{
            rightImageWidth = leftImageWidth;
        }

        rightImage = new BufferedImage(rightImageWidth, rightImageWidth, BufferedImage.TYPE_INT_RGB);

        if(Integer.parseInt(args[2])==1 && scaleFac!=0.0){
            rightImage = scaleImage(antiAliasImage(leftImage, leftImageWidth), leftImageWidth, rightImageWidth, scaleFac);
        }
        else if(scaleFac!=0.0){
            rightImage = scaleImage(leftImage, leftImageWidth, rightImageWidth, scaleFac);
        }
        else
            rightImage = whitenImage(rightImage, rightImageWidth);

        // Use a panel and label to display the image
        JPanel  panel = new JPanel ();
        panel.add (new JLabel (new ImageIcon (leftImage)));
        panel.add (new JLabel (new ImageIcon (rightImage)));
        
        JFrame frame = new JFrame("Display images");
        
        frame.getContentPane().add (panel);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
    }

    //Function to draw lines
    public static BufferedImage drawLines(int x2, int y2, BufferedImage img, int width, int height){
        
        if(start==true){
            for(int y = 0; y < height; y++){
                for(int x = 0; x < width; x++){
                    byte r = (byte)255;
                    byte g = (byte)255;
                    byte b = (byte)255;
                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    img.setRGB(x,y,pix);
                }
            }
            start=false;
        }
        
        // two coordinates to connect a line, x is for height, y is for row
        int x1 = xc, y1 = yc;
        
        int start_x = x1, start_y = y1, end_x = x2, end_y = y2;
        int dx = x2 - x1;
        int dy = y2 - y1;
        double slope = 0;
        int dark_pix = 0xff000000 | ((0 & 0xff) << 16) | ((0 & 0xff) << 8) | (0 & 0xff);
        
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
        
        img.setRGB(start_x, start_y, dark_pix);
        
        if(anchor_x) {
            double y = start_y + 0.5;
            for(int x = start_x + 1; x <= end_x; x++) {
                y = y + slope;
                img.setRGB(x, (int)Math.floor(y), dark_pix);
            }
        }
        else {
            double x = start_x + 0.5;
            for(int y = start_y + 1; y <= end_y; y++) {
                x = x + slope;
                img.setRGB((int)Math.floor(x), y, dark_pix);
            }
        }
        return img;
    }

    //Function to whiten image
    public static BufferedImage whitenImage(BufferedImage newImage, int newWidth){
        for(int y = 0; y < newWidth; y++){
            for(int x = 0; x < newWidth; x++){
                byte r = (byte)255;
                byte g = (byte)255;
                byte b = (byte)255;
                int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                newImage.setRGB(x,y,pix);
            }
        }
        return newImage;
    }

    //Function to scale images
    public static BufferedImage scaleImage(BufferedImage original, int originalWidth, int newWidth, float scalingFactor){
        BufferedImage newImage = null;
        newImage = new BufferedImage(newWidth, newWidth, BufferedImage.TYPE_INT_RGB);

        newImage = whitenImage(newImage, newWidth);

        int newx = 0, newy = 0;
        
        for(float i = 0;i<originalWidth;i+=scalingFactor){
            newy = 0;
            for(float j = 0;j<originalWidth;j+=scalingFactor){
                if(newx<newWidth && newy<newWidth)
                    newImage.setRGB(newx, newy, original.getRGB(Math.round(i), Math.round(j)));
                else
                    break;
                newy++;
            }
            newx++;
        }
        
        return newImage;
    }

    //To apply 3x3 Gaussian average filter for prefiltering image
    public static BufferedImage antiAliasImage(BufferedImage original, int newWidth){
        int clr;
        float red = 0, green = 0, blue = 0;
        BufferedImage newImage = new BufferedImage(newWidth, newWidth, BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<newWidth;i++){
            for(int j=0;j<newWidth;j++){
                clr = original.getRGB(i,j);
                red   = 4*((clr & 0x00ff0000) >> 16);
                green = 4*((clr & 0x0000ff00) >> 8);
                blue  = 4*(clr & 0x000000ff);
                if((i+1)<newWidth){
                    clr = original.getRGB(i+1,j);
                    red   += 2*((clr & 0x00ff0000) >> 16);
                    green += 2*((clr & 0x0000ff00) >> 8);
                    blue  += 2*(clr & 0x000000ff);
                }
                if((i-1)>=0){
                    clr = original.getRGB(i-1,j);
                    red   += 2*((clr & 0x00ff0000) >> 16);
                    green += 2*((clr & 0x0000ff00) >> 8);
                    blue  += 2*(clr & 0x000000ff);
                }
                if((j+1)<newWidth){
                    clr = original.getRGB(i,j+1);
                    red   += 2*((clr & 0x00ff0000) >> 16);
                    green += 2*((clr & 0x0000ff00) >> 8);
                    blue  += 2*(clr & 0x000000ff);
                }
                if((j-1)>=0){
                    clr = original.getRGB(i,j-1);
                    red   += 2*((clr & 0x00ff0000) >> 16);
                    green += 2*((clr & 0x0000ff00) >> 8);
                    blue  += 2*(clr & 0x000000ff);
                }
                if((i+1)<newWidth && (j+1)<newWidth){
                    clr = original.getRGB(i+1,j+1);
                    red   += (clr & 0x00ff0000) >> 16;
                    green += (clr & 0x0000ff00) >> 8;
                    blue  +=  clr & 0x000000ff;
                }
                if((i+1)<newWidth && (j-1)>=0){
                    clr = original.getRGB(i+1,j-1);
                    red   += (clr & 0x00ff0000) >> 16;
                    green += (clr & 0x0000ff00) >> 8;
                    blue  +=  clr & 0x000000ff;
                }
                if((i-1)>=0 && (j+1)<newWidth){
                    clr = original.getRGB(i-1,j+1);
                    red   += (clr & 0x00ff0000) >> 16;
                    green += (clr & 0x0000ff00) >> 8;
                    blue  +=  clr & 0x000000ff;
                }
                if((i-1)>=0 && (j-1)>=0){
                    clr = original.getRGB(i-1,j-1);
                    red   += (clr & 0x00ff0000) >> 16;
                    green += (clr & 0x0000ff00) >> 8;
                    blue  +=  clr & 0x000000ff;
                }

                clr = (Math.round(red/16) << 16) | (Math.round(green/16) << 8) | Math.round(blue/16);

                newImage.setRGB(i,j,clr);
            }
        }
        return newImage;
    }
}