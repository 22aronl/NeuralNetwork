import java.util.*;

/**
 * This is the current iteration of the neural network (not really). It
 * currently can take in weights and inputs, and evaluates the inputs into an
 * output.
 * 
 * @author Aaron Lo
 * @version 2-11-20
 */
public class Network
{
   /**
    * This is the current tester for the neural netwrok.
    * 
    * @param args the input
    */
   public static void main(String[] args)
   {
      int input = 2;
      int[] hidden =
      { 2 };
      int output = 1;
      new Network(input, hidden, output);
   }

   /** The lower bound for the randomized weight. */
   public static final float LOWER_RANDOMIZED_WEIGHT = 0.0f;
   /** The higher bound for the randomized weight. */
   public static final float HIGHER_RANDOMIZED_WEIGHT = 1.0f;
   /** How much the network steps at every iteration. */
   public static final float LEARNING_FACTOR = 1.0f;
   /**
    * When the network should stop learning after hte error is below this
    * threshold.
    */
   public static final float ERROR_THRESHOLD = 0.5f;
   /** How many times the network should run. */
   public static final int MAXIMUM_NUMBER_OF_ITERATION = 10000;

   private float[][][] weights;
   private float[][] values;
   private float[][] input;

   private int inputNodes;
   private int[] hiddenLayerNodes;
   private int outputNodes;
   private int totalNumberOfLayers;
   private int numberOfHiddenLayers;

   /**
    * This is the constructor for this neural network.
    * 
    * @param inputNodes       the number of inputNodes the network has
    * @param hiddenLayerNodes this represents how many hidden layers of nodes there
    *                         are as well as how many nodes per layer
    * @param outputNodes      the number of ouput nodes the network has
    */
   public Network(int inputNodes, int[] hiddenLayerNodes, int outputNodes)
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

      weights = new float[totalNumberOfLayers - 1][maxWidthForWeights][maxWidthForWeights];
      values = new float[totalNumberOfLayers][Math.max(maxWidthForWeights, inputNodes)];

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
   public float activationFunction(float input, boolean derivative)
   {
      if (derivative)
         return 1;
      return input;
   }

   /**
    * This takes in the input for the inputs of the neural and potentially weights.
    * 
    * @return how many inputs there are
    */
   public int takeInInput()
   {
      Scanner sc = new Scanner(System.in);
      System.out.println("Would you like to set the weights: True for yes, False for no");
      if (sc.nextBoolean())
      {
         for (int i = 0; i < inputNodes; i++)
            for (int j = 0; j < hiddenLayerNodes[0]; j++)
            {
               System.out.println("Please input weight values for layer: 0" + " node " + i + " to layer 1 node " + j);
               weights[0][i][j] = sc.nextFloat();
            }

         for (int layer = 1; layer < numberOfHiddenLayers; layer++)
            for (int j = 0; j < hiddenLayerNodes[layer - 1]; j++)
               for (int k = 0; k < hiddenLayerNodes[layer]; k++)
               {
                  System.out.println(
                        "Please input weight values for layer:" + layer + " node " + j + " to layer " + (layer + 1) + " node " + k);
                  weights[layer][j][k] = sc.nextFloat();
               }

         for (int i = 0; i < hiddenLayerNodes[numberOfHiddenLayers - 1]; i++)
            for (int j = 0; j < outputNodes; j++)
            {
               System.out.println("Please input weight values for layer:" + (totalNumberOfLayers - 2) + " node " + i + " to layer "
                     + (totalNumberOfLayers - 1) + " node " + j);
               weights[totalNumberOfLayers - 2][i][j] = sc.nextFloat();
            }

      } // if (sc.nextBoolean())
      System.out.println("How many different inputs would you like?");
      int num = sc.nextInt();

      input = new float[num][inputNodes];

      for (int i = 0; i < num; i++)
      {
         for (int j = 0; j < inputNodes; j++)
         {
            System.out.println("Please input the value node " + j + " for test number " + i);
            input[i][j] = sc.nextFloat();
         }
      }
      sc.close();
      return num;
   }

   /**
    * This randomizes the weights of the neural network between two integers.
    */
   public void randomizeWeights()
   {
      for (int i = 0; i < inputNodes; i++)
         for (int j = 0; j < hiddenLayerNodes[0]; j++)
            weights[0][i][j] = (float) (Math.random() * HIGHER_RANDOMIZED_WEIGHT) + LOWER_RANDOMIZED_WEIGHT;

      for (int i = 1; i < totalNumberOfLayers - 2; i++)
         for (int j = 0; j < hiddenLayerNodes[i - 1]; j++)
            for (int k = 0; k < hiddenLayerNodes[i]; k++)
               weights[i][j][k] = (float) (Math.random() * HIGHER_RANDOMIZED_WEIGHT) + LOWER_RANDOMIZED_WEIGHT;

      for (int i = 0; i < hiddenLayerNodes[numberOfHiddenLayers - 1]; i++)
         for (int j = 0; j < outputNodes; j++)
            weights[totalNumberOfLayers - 2][i][j] = (float) (Math.random() * HIGHER_RANDOMIZED_WEIGHT) + LOWER_RANDOMIZED_WEIGHT;

   }

   /**
    * This evalutes the values in the network(in the values array) from the first
    * layer to the last layer, given that the inputs are in the first layer
    * already.
    */
   public void feedForward()
   {
      float sum = 0.f;
      for (int j = 0; j < hiddenLayerNodes[0]; j++) // From layer 0 to layer 1
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
      for (int j = 0; j < outputNodes; j++)
      {
         sum = 0.f;
         for (int k = 0; k < hiddenLayerNodes[numberOfHiddenLayers - 1]; k++)
            sum += values[numberOfHiddenLayers][k] * weights[numberOfHiddenLayers][k][j];
         values[numberOfHiddenLayers + 1][j] = activationFunction(sum, false);
      }
   }
}