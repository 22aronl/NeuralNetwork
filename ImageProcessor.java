import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.*;

public class ImageProcessor
{
   public static void main(String[] args) throws IOException
   {
      new ImageProcessor();
   }

   public ImageProcessor() throws IOException
   {
      BufferedImage image = ImageIO.read(this.getClass().getResource("img/download.jpg"));
      BufferedImage i = marchThroughImage(image);

      
   }

   public void printRGB(int pixel)
   {
      int alpha = (pixel >> 24) & 0xff;
      int red = (pixel >> 16) & 0xff;
      int green = (pixel >> 8) & 0xff;
      int blue = (pixel) & 0xff;
      System.out.println(pixel + ", " + alpha + ", " + red + ", " + green + ", " + blue);
   }

   public int greyScalePixel(int pixel)
   {
      int alpha = (pixel >> 24) & 0xff;
      int red = (pixel >> 16) & 0xff;
      int green = (pixel >> 8) & 0xff;
      int blue = (pixel) & 0xff;

      int lum = (int) Math.round(0.3 * (double) red + 0.589 * (double) green + 0.11 * (double) blue);

      return (lum << 16) | (lum << 8) | lum;
   }

   private BufferedImage marchThroughImage(BufferedImage image) throws IOException
   {
      BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
      int w = image.getWidth();
      int h = image.getHeight();
      System.out.println("width, height: " + w + ", " + h);

      for (int i = 0; i < h; i++)
      {
         for (int j = 0; j < w; j++)
         {
 
            int pixel = image.getRGB(j, i);
            System.out.println("x,y: " + j + ", " + i + ", " + pixel);
            //printRGB(pixel);
            img.setRGB(j, i, pixel);
            //img.setRGB(j, i, greyScalePixel(pixel));
            System.out.println("");
         }
      }

      ImageIO.write(img, "bmp", new File("img/test3.bmp"));
      return img;
   }
}