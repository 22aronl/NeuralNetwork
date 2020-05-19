import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.*;

public class Interface extends JComponent implements MouseMotionListener
{
   private static final long serialVersionUID = 1L;

   public static void main(String[] args)
   {
      new Interface();
   }

   private JLabel[] ar;
   private JComponent[] ar2;
   private String[] array = { "input", "hidden", "output", "random", "configurationFile" };
   private boolean[] isCheckMark = { false, false, false, true, true };
   private JFrame frame;
   private JPanel panel;

   private int input;
   private int output;
   private int[] hidden;

   public static final int NUM_PER_COLUMN = 5;

   public Interface()
   {
      ar = new JLabel[array.length];
      ar2 = new JComponent[array.length];

      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            createAndShowGUI();
         }
      });

      // Wait until display has been drawn
      try
      {
         while (frame == null || !frame.isVisible())
            Thread.sleep(1);
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   /**
    * Create the GUI and show it. For thread safety, this method should be invoked
    * from the event-dispatching thread.
    */
   private void createAndShowGUI()
   {
      // Create and set up the window.
      frame = new JFrame();
      panel = new JPanel();
      frame.getContentPane();
      panel.setLayout(null);

      firstPage();

      panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      frame.add(panel);
      this.addMouseMotionListener(this);
      frame.setSize(500, 300);
      frame.setVisible(true);
      
   }

   public void secondPage()
   {
      int interval = 25;
      int height = 25;
      int counter = 0;

      int width = 50;
      int winterval = 150;
      for (int i = 0; i <= array.length / NUM_PER_COLUMN; i++)
      {
         height = 25;

         for (int j = 0; j < NUM_PER_COLUMN && i * NUM_PER_COLUMN + j < ar.length; j++)
         {
         }
      }
   }

   public void firstPage()
   {
      int interval = 25;
      int height = 25;
      int counter = 0;

      int width = 50;
      int winterval = 150;
      for (int i = 0; i <= array.length / NUM_PER_COLUMN; i++)
      {
         height = 25;

         for (int j = 0; j < NUM_PER_COLUMN && i * NUM_PER_COLUMN + j < ar.length; j++)
         {
            JLabel label = new JLabel(array[i * NUM_PER_COLUMN + j]);
            Dimension size = label.getPreferredSize();
            label.setBounds(width, height, size.width, size.height);

            panel.add(label);
            ar[counter++] = label;

            if (isCheckMark[counter - 1])
            {
               JCheckBox button2 = new JCheckBox();
               button2.setBounds(width + 40, height - 15, 50, 50);
               panel.add(button2);
               ar2[counter - 1] = button2;
            }
            else
            {
               JTextField text = new JTextField();
               Dimension size2 = text.getPreferredSize();
               text.setBounds(width + 40, height - 4, 70, size2.height);
               ar2[counter - 1] = text;
               panel.add(text);
            }

            height += interval;
         }

         width += winterval;
      }
      JCheckBox button2 = new JCheckBox();
      button2.setBounds(300, 200, 50, 50);
      JButton button = new JButton("Press");
      button.setBounds(250, 150, 50, 50);
      button.setAction(new AbstractAction("Press")
      {
         public void actionPerformed(ActionEvent e)
         {
            System.out.println(((JTextField) ar2[0]).getText());
         }
      });

      panel.add(button);
      panel.add(button2);
   }

   public void mouseMoved(MouseEvent e)
   {
      System.out.println("YEE");
   }

   public void mouseDragged(MouseEvent e)
   {

   }

}