import java.io.*;
import java.util.*;

public class ImageTraining
{
   private static HashMap<String, String> map;

   public static void grayScale(String input, String output, PrintWriter out)
   {
      String[] files = (new File(input)).list();
      out.println(files.length);
      DibDump dib = new DibDump();
      for (String name : files)
      {
         int[][] ar = dib.grayScaleImg(input + "/" + name, output + "/" + name);
         System.out.println(ar.length + " " + ar[0].length);
         for (int i = 0; i < ar.length; i++)
         {
            for (int j = 0; j < ar[0].length; j++)
            {
               out.println((double)((ar[i][j] >> 8) & 0x00FF) / 255);
            }
         }
         if(map.get(name) == null)
            System.out.println(name);

         out.println(map.get(name));
      }
   }

   public static void loadMap(String mapFile)
   {
      map = new HashMap<String, String>();
      try
      {
         BufferedReader br = new BufferedReader(new FileReader(new File("files/" + mapFile + ".txt")));
         int k = Integer.parseInt(br.readLine());
         for (int i = 0; i < k; i++)
         {
            String name = br.readLine();
            String output = br.readLine();
            map.put(name, output);
            System.out.println(name + " " + map.get(name));
         }
         br.close();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

   }


   public ImageTraining() throws IOException
   {
      String mapFile = "names";
      String input = "img";
      String middle = "input";
      String output = "test";
      String testCases = "testCases";
      String test = "run";
      
      PrintWriter pr = new PrintWriter(new File("files/" + output + ".txt"));
      PrintWriter tr = new PrintWriter(new File("files/" + test + ".txt"));
      
      loadMap(mapFile);

      grayScale(input, middle, pr);
      grayScale(testCases, middle, tr);

      pr.close();
      tr.close();
   }

   public static void main(String[] args) throws IOException
   {
      new ImageTraining();
   }
}