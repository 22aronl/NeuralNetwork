import java.io.*;
import java.util.*;


/**
 * This is the current iteration of the neural network (not really). It
 * currently can take in weights and inputs, and evaluates the inputs into an
 * output.
 * 
 * @author Aaron Lo
 * @version 2-11-20
 * 
 * 
 *          List of Methods:
 * 
 *          feedForward() : feeds the value from input to output
 * 
 *          randomizeWeights() : randomizes the weights
 * 
 *          takeInInput() : This takes in the input for the network
 * 
 *          activationFunction(double input, boolean derivative) : the
 *          activation for the function,
 * 
 *          getRandomNumber(): Gets a random number between range
 * 
 */
public class Network
{
   /**
    * This is the current tester for the neural netwrok.
    * 
    * @param args the input
    */
   public static void main(String[] args) throws IOException
   {
      int input = 2;
      int[] hidden = { 2 };
      int output = 1;
      if (args.length != 0)
      {
         input = Integer.parseInt(args[0]);
         hidden = new int[args.length - 2];

         for (int i = 1; i < args.length - 1; i++)
            hidden[i - 1] = Integer.parseInt(args[i]);
            
         output = Integer.parseInt(args[args.length - 1]);
      }

      new Network(input, hidden, output);
   }

   /** The lower bound for the randomized weight. */
   public static final double LOWER_RANDOMIZED_WEIGHT = -1.0;
   /** The higher bound for the randomized weight. */
   public static final double HIGHER_RANDOMIZED_WEIGHT = 1.0;
   /** How much the network steps at every iteration. */
   public static final double LEARNING_FACTOR = 1.0;
   /**
    * When the network should stop learning after hte error is below this
    * threshold.
    */
   public static final double ERROR_THRESHOLD = 0.5;
   /** How many times the network should run. */
   public static final int MAXIMUM_NUMBER_OF_ITERATION = 10000;

   private double[][][] weights;
   private double[][] values;
   private double[][] input;

   private int inputNodes;
   private int[] hiddenLayerNodes;
   private int outputNodes;
   private int totalNumberOfLayers;
   private int numberOfHiddenLayers;

   /**
    * This is the constructor for this neural network. This sets the weights, takes
    * in the input and evaluates them.
    * 
    * @param inputNodes       the number of inputNodes the network has
    * @param hiddenLayerNodes this represents how many hidden layers of nodes there
    *                         are as well as how many nodes per layer
    * @param outputNodes      the number of ouput nodes the network has
    */
   public Network(int inputNodes, int[] hiddenLayerNodes, int outputNodes) throws IOException
   {
      this.inputNodes = inputNodes;
      this.hiddenLayerNodes = hiddenLayerNodes;
      this.outputNodes = outputNodes;
      this.numberOfHiddenLayers = hiddenLayerNodes.length;
      this.totalNumberOfLayers = numberOfHiddenLayers + 2;
      int maxWidthForWeights = 0;

      for (int i = 0; i < hiddenLayerNodes.length; i++)
      {
         if (maxWidthForWeights < hiddenLayerNodes[i])
            maxWidthForWeights = hiddenLayerNodes[i];
      }

      if (maxWidthForWeights < outputNodes)
         maxWidthForWeights = outputNodes;

      weights = new double[totalNumberOfLayers - 1][maxWidthForWeights][maxWidthForWeights];

      if (inputNodes > maxWidthForWeights)
         maxWidthForWeights = inputNodes;

      values = new double[totalNumberOfLayers][maxWidthForWeights];

      randomizeWeights();
      int numOfInputs = takeInInput();

      for (int i = 0; i < numOfInputs; i++)
      {
         System.out.println("Test Number " + i);
         System.out.print("The inputs are ");

         for (int j = 0; j < inputNodes; j++)
         {
            values[0][j] = input[i][j];
            System.out.print(input[i][j] + " ");
         }

         System.out.println("");
         feedForward();
         System.out.println("The output is " + values[numberOfHiddenLayers + 1][0] + "\n");
      }
   }

   /**
    * This is the activation function.
    * 
    * @param input      the input into the activation function
    * @param derivative true if you want the derivative of activation function;
    *                   false if you want the activation function
    * @return the value of the the activation Function with the input
    */
   public double activationFunction(double input, boolean derivative)
   {
      double output = 1;

      if (derivative)
         output = 1;
      else
         output = input;

      return output;
   }

   /**
    * This takes in the input for the inputs of the neural and potentially weights.
    * 
    * @return how many inputs there are
    */
   public int takeInInput() throws IOException
   {
      Scanner sc = new Scanner(System.in);
      System.out.println("Would you like to read in input from a file?");
      if (sc.next().equalsIgnoreCase("Yes"))
      {
         System.out.println("What file would you like?");
         String fileName = sc.next();
         sc = new Scanner(new File("files/" + fileName));
         /*
          * Must be in this order; true or false for weights; if true -> enter in the
          * weights; # of inputs tests inputs for each
          */
      }

      System.out.println("Would you like to set the weights: True for yes, False for no");

      if (sc.nextBoolean())
      {
         for (int i = 0; i < inputNodes; i++)
            for (int j = 0; j < hiddenLayerNodes[0]; j++)
            {
               System.out.println("Please input weight values for layer: 0" + " node " + i + " to layer 1 node " + j);
               weights[0][i][j] = sc.nextDouble();
            }

         for (int layer = 1; layer < numberOfHiddenLayers; layer++)
            for (int j = 0; j < hiddenLayerNodes[layer - 1]; j++)
               for (int k = 0; k < hiddenLayerNodes[layer]; k++)
               {
                  System.out.println(
                        "Please input weight values for layer:" + layer + " node " + j + " to layer " + (layer + 1) + " node " + k);
                  weights[layer][j][k] = sc.nextDouble();
               }

         for (int i = 0; i < hiddenLayerNodes[numberOfHiddenLayers - 1]; i++)
            for (int j = 0; j < outputNodes; j++)
            {
               System.out.println("Please input weight values for layer:" + (totalNumberOfLayers - 2) + " node " + i + " to layer "
                     + (totalNumberOfLayers - 1) + " node " + j);
               weights[totalNumberOfLayers - 2][i][j] = sc.nextDouble();
            }

      } // if (sc.nextBoolean())

      System.out.println("How many different tests would you like?");
      int num = sc.nextInt();

      input = new double[num][inputNodes];

      for (int i = 0; i < num; i++)
      {
         for (int j = 0; j < inputNodes; j++)
         {
            System.out.println("Please input the value node " + j + " for test number " + i);
            input[i][j] = sc.nextDouble();
         }
      }

      System.out.println("");
      sc.close();
      return num;
   }

   /**
    * This randomizes the weights of the neural network between two integers.
    */
   public void randomizeWeights()
   {
      // Weights works with first index specifying the layer, the second the specific
      // node on that layer, and the third the node on the next layer

      for (int i = 0; i < inputNodes; i++)
         for (int j = 0; j < hiddenLayerNodes[0]; j++) // Sets the weights from layer 0 to 1
            weights[0][i][j] = getRandomNumber();

      for (int i = 1; i < totalNumberOfLayers - 2; i++)
         for (int j = 0; j < hiddenLayerNodes[i - 1]; j++)
            for (int k = 0; k < hiddenLayerNodes[i]; k++) // Sets the weights that go from layer 1 to the layer ouput
               weights[i][j][k] = getRandomNumber();

      for (int i = 0; i < hiddenLayerNodes[numberOfHiddenLayers - 1]; i++)
         for (int j = 0; j < outputNodes; j++) // Sets the weights for the last layer that goes the output
            weights[totalNumberOfLayers - 2][i][j] = getRandomNumber();

   }

   /**
    * This gets the random number between LOWER_RANDOMIZED_WEIGHT and HIGHER_RANDOMIZED_WEIGHT.
    * @return the random number
    */
   public double getRandomNumber()
   {
      return (Math.random() * (HIGHER_RANDOMIZED_WEIGHT - LOWER_RANDOMIZED_WEIGHT)) + LOWER_RANDOMIZED_WEIGHT;
   }

   /**
    * This evalutes the values in the network(in the values array) from the first
    * layer to the last layer, given that the inputs are in the first layer
    * already.
    */
   public void feedForward()
   {
      double sum = 0.f;
      for (int j = 0; j < hiddenLayerNodes[0]; j++) // Feeding values from layer 0 to 1
      {
         sum = 0.f;

         for (int k = 0; k < inputNodes; k++)
            sum += values[0][k] * weights[0][k][j];

         values[1][j] = activationFunction(sum, false);
      }

      for (int layer = 2; layer < numberOfHiddenLayers + 1; layer++)
      {
         for (int j = 0; j < hiddenLayerNodes[layer - 1]; j++) // j represents node on the next layer
         {
            sum = 0f;
            
            for (int k = 0; k < hiddenLayerNodes[layer - 2]; k++) // k represents the perceptron that points to node j
               sum += values[layer - 2][k] * weights[layer - 2][k][j];

            values[layer - 1][j] = activationFunction(sum, false);
         }
      } // for (int layer = 2; layer < numberOfHiddenLayers + 1; layer++)

      for (int j = 0; j < outputNodes; j++) // Feeding values into an output layer
      {
         sum = 0.f;

         for (int k = 0; k < hiddenLayerNodes[numberOfHiddenLayers - 1]; k++)
            sum += values[numberOfHiddenLayers][k] * weights[numberOfHiddenLayers][k][j];

         values[numberOfHiddenLayers + 1][j] = activationFunction(sum, false);
      }
   }
}