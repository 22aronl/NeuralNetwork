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
   public static final double LEARNING_FACTOR = 0.1;
   /**
    * When the network should stop learning after hte error is below this
    * threshold.
    */
   public static final double ERROR_THRESHOLD = 0.0001;
   /** How many times the network should run. */
   public static final int MAXIMUM_NUMBER_OF_ITERATION = 100000;

   private double[][][] weights;
   private double[][] values;
   private double[][] input;
   private double[] output;

   private int inputNodes;
   private int[] hiddenLayerNodes;
   private int outputNodes;
   private int[] sizes;
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
      sizes = new int[totalNumberOfLayers];


      for (int index = 0; index < hiddenLayerNodes.length; index++)
      {
         if (maxWidthForWeights < hiddenLayerNodes[index])
            maxWidthForWeights = hiddenLayerNodes[index];
         sizes[index + 1] = hiddenLayerNodes[index];
      }

      if (maxWidthForWeights < outputNodes)
         maxWidthForWeights = outputNodes;
      sizes[totalNumberOfLayers - 1] = outputNodes;

      weights = new double[totalNumberOfLayers - 1][maxWidthForWeights][maxWidthForWeights];

      if (inputNodes > maxWidthForWeights)
         maxWidthForWeights = inputNodes;
      sizes[0] = inputNodes;

      values = new double[totalNumberOfLayers][maxWidthForWeights];

      randomizeWeights();

      int numOfInputs = takeInInput();
      double maxError = 0.0;

      learningSet();

      for (int i = 0; i < numOfInputs; i++)
      {
         values[0] = input[i];
         feedForward();
         maxError = Math.max(maxError, Math.abs(output[i] - values[totalNumberOfLayers - 1][0]));
      }

      System.out.println(weights[0][0][0] + " " + weights[0][1][0] + " " + weights[0][0][1] + " " + weights[0][1][1]);
      System.out.println(weights[1][0][0] + " " + weights[1][1][0]);
      System.out.println(maxError);
      int iteration = 0;
      int percent = MAXIMUM_NUMBER_OF_ITERATION / 200;
      for (int i = 0; i < numOfInputs; i++)
      {
         iteration = 0;
         values[0] = input[i];
         maxError = Math.abs(output[i] - values[totalNumberOfLayers - 1][0]);
         while (maxError > ERROR_THRESHOLD && iteration++ < MAXIMUM_NUMBER_OF_ITERATION)
         {
            if (iteration == percent)
            {
               System.out.print("#");
               percent += MAXIMUM_NUMBER_OF_ITERATION / 200;
            }
            maxError = 0.0;
            // System.out.println(values[0][0] + " " + values[0][1]);
            feedForward();
            learn(i);
            // System.out.println(weights[0][0][0] + " " + weights[0][1][0] + " " +
            // weights[0][0][1] + " " + weights[0][1][1]);
            // System.out.println(weights[1][0][0] + " " + weights[1][1][0]);

            if (Math.abs(output[i] - values[totalNumberOfLayers - 1][0]) > maxError)
               maxError = Math.abs(output[i] - values[totalNumberOfLayers - 1][0]);
         }
         // System.out.println(maxError);
         // System.out.println(iteration);
      }

      System.out.println(maxError);
      System.out.println(iteration);

      System.out.println(weights[0][0][0] + " " + weights[0][1][0] + " " + weights[0][0][1] + " " + weights[0][1][1]);
      System.out.println(weights[1][0][0] + " " + weights[1][1][0]);

      for (int i = 0; i < numOfInputs; i++)
      {
         System.out.println("Test Number " + i);
         System.out.print("The inputs are ");
         values[0] = input[i];
         for (int j = 0; j < inputNodes; j++)
         {
            System.out.print(values[0][j] + " ");
         }

         System.out.println("");
         feedForward();
         System.out.println("The output is " + values[numberOfHiddenLayers + 1][0]);
         System.out.println("The expected output is " + output[i] + "\n");
      }
   }

   public void learningSet(int numOfTrainingSets)
   {
      int iteration = 0;
      double maxError = 0;

      while (maxError > ERROR_THRESHOLD && iteration < MAXIMUM_NUMBER_OF_ITERATION)
      {
         for (int trainingSet = 0; trainingSet < numOfTrainingSets; trainingSet++)
         {
            // Load into network
            loadIntoNetwork(trainingSet);
            learn(trainingSet);
         }
      }
   }

   public void loadIntoNetwork(int trial)
   {

   }

   public int learn(int trial)
   {
      int iteration = 0;
      double curChange = 0.0;
      double previousChange = 0.0;
      double totalChange = 0.0;

      for (int startNode = 0; startNode < hiddenLayerNodes[numberOfHiddenLayers - 1]; startNode++)
         for (int toNode = 0; toNode < outputNodes; toNode++)
         {
            curChange = changeInWeight(totalNumberOfLayers - 1, startNode, toNode, output[trial],
                  values[totalNumberOfLayers - 1][0]);
            totalChange = curChange;

            while (curChange - previousChange <= totalChange)
            {
               weights[totalNumberOfLayers - 1][startNode][toNode] += curChange;
               feedForward();
               totalChange = curChange - previousChange;
               previousChange = curChange;
               curChange = changeInWeight(totalNumberOfLayers - 1, startNode, toNode, output[trial],
                     values[totalNumberOfLayers - 1][0]);
               iteration++;
            }
         }

      for (int startNode = 0; startNode < inputNodes; startNode++)
         for (int toNode = 0; toNode < hiddenLayerNodes[0]; toNode++)
         {
            curChange = changeInWeight(0, startNode, toNode, output[trial], values[0][0]);
            totalChange = curChange;

            while (curChange - previousChange <= totalChange)
            {
               weights[0][startNode][toNode] += curChange;
               feedForward();
               totalChange = curChange - previousChange;
               previousChange = curChange;
               curChange = changeInWeight(0, startNode, toNode, output[trial], values[totalNumberOfLayers - 1][0]);
               iteration++;
            }
         }

      return iteration;
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
      double output = 0.0;

      if (derivative)
         output = 1;
      else
         output = input;

      return output;
   }

   /**
    * This NEEDS TO BE CHANGED LATER, IS NOT GENERALIZED.
    * 
    * @param layer          the layer in which the weight starts
    * @param currentNode    the node from which the weight starts from
    * @param nextNode       the node on the next layer in which the the weight goes
    *                       to
    * @param expectedOutput the expected output for the training
    * @param realOutput     the output that was received
    * @return how much the weight should change
    */
   public double changeInWeight(int layer, int currentNode, int nextNode, double expectedOutput, double realOutput)
   {
      double output = 0.0;
      if (layer == 1)
         output = -LEARNING_FACTOR * -(expectedOutput - realOutput) * activationFunction(values[2][0], true)
               * values[layer][currentNode];
      else if (layer == 0)
         output = -LEARNING_FACTOR * -(expectedOutput - realOutput) * activationFunction(values[2][0], true)
               * values[0][currentNode] * activationFunction(values[1][nextNode], true) * weights[1][nextNode][0];

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
         for (int startNode = 0; startNode < inputNodes; startNode++)
            for (int toNode = 0; toNode < hiddenLayerNodes[0]; toNode++)
            {
               System.out.println("Please input weight values for layer: 0" + " node " + startNode + " to layer 1 node " + toNode);
               weights[0][startNode][toNode] = sc.nextDouble();
            }

         for (int layer = 1; layer < numberOfHiddenLayers; layer++)
            for (int startNode = 0; startNode < hiddenLayerNodes[layer - 1]; startNode++)
               for (int toNode = 0; toNode < hiddenLayerNodes[layer]; toNode++)
               {
                  System.out.println("Please input weight values for layer:" + layer + " node " + startNode + " to layer "
                        + (layer + 1) + " node " + toNode);
                  weights[layer][startNode][toNode] = sc.nextDouble();
               }

         for (int startNode = 0; startNode < hiddenLayerNodes[numberOfHiddenLayers - 1]; startNode++)
            for (int toNode = 0; toNode < outputNodes; toNode++)
            {
               System.out.println("Please input weight values for layer:" + (totalNumberOfLayers - 2) + " node " + startNode
                     + " to layer " + (totalNumberOfLayers - 1) + " node " + toNode);
               weights[totalNumberOfLayers - 2][startNode][toNode] = sc.nextDouble();
            }

      } // if (sc.nextBoolean())

      System.out.println("How many different tests would you like?");
      int num = sc.nextInt();

      input = new double[num][inputNodes];
      output = new double[num];

      for (int test = 0; test < num; test++)
      {
         for (int j = 0; j < inputNodes; j++)
         {
            System.out.println("Please input the value node " + j + " for test number " + test);
            input[test][j] = sc.nextDouble();
         }

         System.out.println("What is the output for this trial: " + test);
         output[test] = sc.nextDouble();
      }
      for (int test = 0; test < num; test++)

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

      for (int startNode = 0; startNode < inputNodes; startNode++)
         for (int toNode = 0; toNode < hiddenLayerNodes[0]; toNode++) // Sets the weights from layer 0 to 1
            weights[0][startNode][toNode] = getRandomNumber(LOWER_RANDOMIZED_WEIGHT, HIGHER_RANDOMIZED_WEIGHT);

      for (int layer = 1; layer < totalNumberOfLayers - 2; layer++)
         for (int startNode = 0; startNode < hiddenLayerNodes[layer - 1]; startNode++)
            for (int toNode = 0; toNode < hiddenLayerNodes[layer]; toNode++) // weights that go from layer 1 to the layer output
               weights[layer][startNode][toNode] = getRandomNumber(LOWER_RANDOMIZED_WEIGHT, HIGHER_RANDOMIZED_WEIGHT);

      for (int startNode = 0; startNode < hiddenLayerNodes[numberOfHiddenLayers - 1]; startNode++)
         for (int toNode = 0; toNode < outputNodes; toNode++) // Sets the weights for the last layer that goes the output
            weights[totalNumberOfLayers - 2][startNode][toNode] = getRandomNumber(LOWER_RANDOMIZED_WEIGHT,
                  HIGHER_RANDOMIZED_WEIGHT);

   }

   /**
    * This gets the random number between LOWER_RANDOMIZED_WEIGHT and
    * HIGHER_RANDOMIZED_WEIGHT.
    * 
    * @param lower  the lower bound of randomization
    * @param higher the higher bound of randomization
    * @return the random number
    */
   public double getRandomNumber(double lower, double higher)
   {
      return (Math.random() * (higher - lower)) + lower;
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
      { // j represents the output node, k the node leading into that
         sum = 0.f;

         for (int k = 0; k < hiddenLayerNodes[numberOfHiddenLayers - 1]; k++)
            sum += values[numberOfHiddenLayers][k] * weights[numberOfHiddenLayers][k][j];

         values[numberOfHiddenLayers + 1][j] = activationFunction(sum, false);
      }
   }
}