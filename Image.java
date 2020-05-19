import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * The display for Solitarie
 * 
 * @author Aaron Lo
 * @version 11-7-18
 */
public class Image extends JComponent implements KeyListener, MouseMotionListener
{

   public static void main(String[] args)
   {
      new Image();
   }

   private JFrame frame;
   private int[][] ar;
   private int width = 500;
   private int height = 500;

   /**
    * The constructor for SolitaireDisplay
    * 
    * @param game the game of Solitaire
    */
   public Image()
   {

      frame = new JFrame("Minesweeper");

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.getContentPane().add(this);

      this.setPreferredSize(new Dimension(width, height));
      this.addMouseMotionListener(this);
      this.addKeyListener(this);
      frame.pack();
      frame.setVisible(true);

      ar = new int[100][100];

      JLabel label = new JLabel("Test");
      Dimension size = label.getPreferredSize();
      label.setBounds(200, 200, size.width, size.height);
      frame.add(label);
      
      repaint();
   }

   /**
    * Paints the display
    * 
    * @param g the graphics
    */
   public void paintComponent(Graphics g)
   {
      for (int i = 0; i < width/ar[0].length; i++)
      {
         for(int j = 0; j < height / ar.length; j++)
         {
            
         }
      }
   }

   /**
    * Key Typed
    * 
    * @param e stst
    */
   public void keyTyped(KeyEvent e)
   {

   }

   /**
    * Key released
    * 
    * @param e ladjf
    */
   public void keyReleased(KeyEvent e)
   {

   }

   /**
    * The key pressed
    * 
    * @param e the key even
    */
   public void keyPressed(KeyEvent e)
   {

   }

   public void mouseMoved(MouseEvent e)
   {

   }

   public void mouseDragged(MouseEvent e)
   {
      System.out.println(e.getX() + " " + e.getY());
   }  
}