import java.io.*;
import java.util.*;

/**
 * This is the current iteration of the neural network. It currently can take in weights and inputs, and evaluates the inputs into
 * an output. In addition to that, the network can learn for a a-b-1 configuration network given a set of input and output.
 * 
 * @author Aaron Lo
 * @version 2-11-20
 * 
 * 
 *          List of Methods: double activationFunction(double, boolean), double changeInWeight(int, int, int, double), void
 *          feedForward(), double getMaxError(), double getRandomNumber(double, double), void learningSet(), void
 *          loadInputs(String), void loadWeights(String), void main(String[]), Network(String[]), void printStateOfNetwork(), void
 *          printTestCases(), void printWeights(), void randomizeWeights(), void takeInInput()
 * 
 * 
 */
public class Network
{
   /**
    * This is the current tester for the neural network.
    * 
    * @param args the input from console
    */
   public static void main(String[] args) throws IOException
   {
      new Network(args);
   }

   /** The lower bound for the randomized weight. */
   public double lowerRandomizedWeight = -1.0;
   /** The higher bound for the randomized weight. */
   public double higherRandomizedWeight = 1.0;
   /** How much the network steps at every iteration. */
   public double learningFactor = 0.1;
   /** When the network should stop. */
   public double errorThreshold = 0.01;
   /** The maximum number of times the network should run. */
   public int maximumNumberOfIteration = 100000;

   private double[][][] weights;
   private double[][][] deltaWeights;
   private double[][] values;
   private double[][] input;
   private double[] output;
   private int[] sizes;

   private int numOfTrainingSets;
   private int totalNumberOfLayers;

   /**
    * This is the constructor for the neural network.
    * 
    * @param args the arguments from console; if one input, that signifies the configuration file, if there are more than one
    *             argument, that signifies the layers of the network
    * @throws IOException if the file called for is not found
    */
   public Network(String[] args) throws IOException
   {
      String configurationFile = "configuration.txt";
      String weightFile = "";
      String[] inputFileNames = null;

      int maxWidthForWeights = 0;

      if (args.length > 1)
      {
         totalNumberOfLayers = args.length;
         sizes = new int[totalNumberOfLayers];

         for (int layer = 0; layer < totalNumberOfLayers; layer++)
         {
            sizes[layer] = Integer.parseInt(args[layer]);

            if (sizes[layer] > maxWidthForWeights)
               maxWidthForWeights = sizes[layer];
         }
      } // if (args.length > 1)
      else
      {
         if (args.length == 1)
            configurationFile = args[0];

         BufferedReader configReader = new BufferedReader(new FileReader(new File("files/" + configurationFile)));
         int inputNodes = Integer.parseInt(configReader.readLine());

         totalNumberOfLayers = Integer.parseInt(configReader.readLine()) + 2;
         maxWidthForWeights = inputNodes;

         sizes = new int[totalNumberOfLayers];
         sizes[0] = inputNodes;

         for (int layer = 1; layer < totalNumberOfLayers - 1; layer++)
         {
            sizes[layer] = Integer.parseInt(configReader.readLine());

            if (sizes[layer] > maxWidthForWeights)
               maxWidthForWeights = sizes[layer];
         }

         sizes[totalNumberOfLayers - 1] = Integer.parseInt(configReader.readLine());

         if (sizes[totalNumberOfLayers - 1] > maxWidthForWeights)
            maxWidthForWeights = sizes[totalNumberOfLayers - 1];

         if (configReader.readLine().equals("true"))
         {
            lowerRandomizedWeight = Double.parseDouble(configReader.readLine());
            higherRandomizedWeight = Double.parseDouble(configReader.readLine());
            learningFactor = Double.parseDouble(configReader.readLine());
            errorThreshold = Double.parseDouble(configReader.readLine());
            maximumNumberOfIteration = Integer.parseInt(configReader.readLine());
         }

         if (configReader.readLine().equals("true"))
            weightFile = configReader.readLine();

         int inputFileNumber = Integer.parseInt(configReader.readLine());
         inputFileNames = new String[inputFileNumber];

         for (int inputFiles = 0; inputFiles < inputFileNumber; inputFiles++)
            inputFileNames[inputFiles] = configReader.readLine();

         configReader.close();
      } // else

      weights = new double[totalNumberOfLayers - 1][maxWidthForWeights][maxWidthForWeights];
      deltaWeights = new double[totalNumberOfLayers - 1][maxWidthForWeights][maxWidthForWeights];
      values = new double[totalNumberOfLayers][maxWidthForWeights];

      if (weightFile.equals(""))
         randomizeWeights();
      else
         loadWeights(weightFile);

      System.out.println("\nMaxNumberOfIteration: " + maximumNumberOfIteration);
      System.out.println("Weights from " + lowerRandomizedWeight + " to " + higherRandomizedWeight);
      System.out.println("Error Threshold: " + errorThreshold);
      System.out.println("Learning Factor: " + learningFactor);
      System.out.println("Number of Activations");

      for (int index = 0; index < totalNumberOfLayers; index++)
         System.out.print(sizes[index] + " ");

      System.out.println("");

      if (inputFileNames.length == 0)
      {
         takeInInput();
         learningSet();
         printStateOfNetwork();
      }
      else
         for (int inputFiles = 0; inputFiles < inputFileNames.length; inputFiles++)
         {
            System.out.println("\n\nNetwork for " + inputFileNames[inputFiles] + "\n");
            loadInputs(inputFileNames[inputFiles]);
            learningSet();
            printTestCases();
         }
   }

   /**
    * This prints the state of the network (the results of test cases, and weights).
    */
   public void printStateOfNetwork()
   {
      printTestCases();
      printWeights();
   }

   /**
    * This prints the test cases of the network, it runs the input into the network and prints out the output.
    */
   private void printTestCases()
   {
      for (int i = 0; i < numOfTrainingSets; i++)
      {
         System.out.println("Test Number " + i);
         System.out.print("The inputs are ");
         values[0] = input[i];

         for (int j = 0; j < sizes[0]; j++)
            System.out.print(values[0][j] + " ");

         System.out.println("");
         feedForward();
         System.out.println("The output is " + values[totalNumberOfLayers - 1][0]);
         System.out.println("The expected output is " + output[i]);
         System.out.println("The error is " + Math.abs(output[i] - values[totalNumberOfLayers - 1][0]) + "\n");
      } // for (int i = 0; i < numOfTrainingSets; i++)
   }

   /**
    * This prints out the weights in the network.
    */
   private void printWeights()
   {
      for (int layer = 0; layer < totalNumberOfLayers - 1; layer++) // Prints out weights
         for (int toNode = 0; toNode < sizes[layer + 1]; toNode++)
            for (int fromNode = 0; fromNode < sizes[layer]; fromNode++)
               System.out.println("Weight " + layer + " " + fromNode + " " + toNode + " : " + weights[layer][fromNode][toNode]);
   }

   /**
    * This loads in the inputs from the inputFile and the outputs.
    * 
    * @param inputFile the file with this information
    * @throws IOException if the file is not found
    */
   public void loadInputs(String inputFile) throws IOException
   {
      BufferedReader inputReader = new BufferedReader(new FileReader(new File("files/" + inputFile)));
      numOfTrainingSets = Integer.parseInt(inputReader.readLine());

      input = new double[numOfTrainingSets][sizes[0]];
      output = new double[numOfTrainingSets];

      for (int set = 0; set < numOfTrainingSets; set++)
      {
         for (int inputs = 0; inputs < sizes[0]; inputs++)
            input[set][inputs] = Double.parseDouble(inputReader.readLine());

         output[set] = Double.parseDouble(inputReader.readLine());
      }

      inputReader.close();
   }

   /**
    * This loads in the weights from a file.
    * 
    * @param weightFile the name of the file with the weights
    * @throws IOException thrown if the file name is not found
    */
   public void loadWeights(String weightFile) throws IOException
   {
      BufferedReader weightsReader = new BufferedReader(new FileReader(new File("files/" + weightFile)));

      for (int layer = 0; layer < totalNumberOfLayers - 1; layer++)
         for (int fromNode = 0; fromNode < sizes[layer]; fromNode++)
            for (int toNode = 0; toNode < sizes[layer + 1]; toNode++)
               weights[layer][fromNode][toNode] = Double.parseDouble(weightsReader.readLine());

      weightsReader.close();
   }

   /**
    * This trains the network to the test cases. This requires that all weights and inputs created already.
    */
   public void learningSet()
   {
      int iteration = 0;
      double maxError = getMaxError();
      double curError;
      int percentage = maximumNumberOfIteration / 20;
      int current = percentage;

      while (maxError > errorThreshold && iteration++ < maximumNumberOfIteration)
      {
         if (iteration > current)
         {
            System.out.print("#");
            current += percentage;
         }

         for (int trainingSet = 0; trainingSet < numOfTrainingSets; trainingSet++)
         {
            values[0] = input[trainingSet];
            feedForward();

            for (int layer = totalNumberOfLayers - 2; layer >= 0; layer--)
               for (int startNode = 0; startNode < sizes[layer]; startNode++)
                  for (int toNode = 0; toNode < sizes[layer + 1]; toNode++)
                     deltaWeights[layer][startNode][toNode] = changeInWeight(layer, startNode, toNode, output[trainingSet]);

            for (int layer = totalNumberOfLayers - 1; layer >= 1; layer--)
               for (int startNode = 0; startNode < sizes[layer - 1]; startNode++)
                  for (int toNode = 0; toNode < sizes[layer]; toNode++)
                     weights[layer - 1][startNode][toNode] += deltaWeights[layer - 1][startNode][toNode];
         } // for (int trainingSet = 0; trainingSet < numOfTrainingSets; trainingSet++)

         curError = getMaxError();

         maxError = curError;
      } // while (maxError > ERROR_THRESHOLD && iteration++ < MAXIMUM_NUMBER_OF_ITERATION)

      System.out.println("\nIterations " + (iteration - 1));
      System.out.println("Max Error Reached " + maxError);
      System.out.println("Final Learning Factor " + learningFactor + "\n");
   }

   /**
    * This gets the maximum error of all the test cases.
    * 
    * @return the maximum error
    */
   public double getMaxError()
   {
      values[0] = input[0];
      feedForward();
      double maxError = Math.abs(output[0] - values[totalNumberOfLayers - 1][0]);
      double curError;

      for (int trial = 1; trial < numOfTrainingSets; trial++)
      {
         values[0] = input[trial];
         feedForward();
         curError = Math.abs(output[trial] - values[totalNumberOfLayers - 1][0]);

         if (curError > maxError)
            maxError = curError;
      }

      return maxError;
   }

   /**
    * This is the activation function. If you want the normal function, call this with (input, false). If you want the derivative,
    * enter into the input param the value that has already been activated, and true into the derivative param. You do want to keep
    * in mind that when calling for the derivative of the activation funciton, the input should be already evaluated by the non
    * derivative activation function.
    * 
    * @param input      the input into the activation function
    * @param derivative true if you want the derivative of activation function; false if you want the activation function
    * @return the value of the the activation Function with the input
    */
   public double activationFunction(double input, boolean derivative)
   {
      double output = 0.0;

      if (derivative)
         output = input * (1.0 - input);
      else
         output = 1.0 / (1.0 + Math.exp(-input));

      return output;
   }

   /**
    * This NEEDS TO BE CHANGED LATER, IS NOT GENERALIZED.
    * 
    * @param layer          the layer in which the weight starts
    * @param currentNode    the node from which the weight starts from
    * @param nextNode       the node on the next layer in which the the weight goes to
    * @param expectedOutput the expected output for the training
    * @return how much the weight should change
    */
   public double changeInWeight(int layer, int currentNode, int nextNode, double expectedOutput)
   {
      double output = 0.0;
      double psi = (expectedOutput - values[2][0]) * activationFunction(values[2][0], true);

      if (layer == 1)
         output = -values[1][currentNode] * psi;
      else if (layer == 0)
      {
         double omega = psi * weights[1][nextNode][0];
         output = -values[0][currentNode] * omega * activationFunction(values[1][nextNode], true);
      }

      return -learningFactor * output;
   }

   /**
    * This calculates the sum of the nodes from layer to the next layer's node. This is not used right now, maybe later.
    * 
    * @param layer the layer in which you are starting from
    * @param node  the node to which you are going towards
    * @return the sum of this
    */
   public double calc(int layer, int node)
   {
      double sum = 0.0;

      for (int i = 0; i < sizes[layer]; i++)
         sum += weights[layer][i][node] * values[layer][i];

      return sum;
   }

   /**
    * This takes in the input and outputs from the user for the inputs of the neural.
    */
   public void takeInInput()
   {
      Scanner sc = new Scanner(System.in);

      System.out.println("How many different tests would you like?");
      numOfTrainingSets = sc.nextInt();

      input = new double[numOfTrainingSets][sizes[0]];
      output = new double[numOfTrainingSets];

      for (int test = 0; test < numOfTrainingSets; test++)
      {
         for (int j = 0; j < sizes[0]; j++)
         {
            System.out.println("Please input the value node " + j + " for test number " + test);
            input[test][j] = sc.nextDouble();
         }

         System.out.println("What is the output for this trial: " + test);
         output[test] = sc.nextDouble();
      }

      sc.close();
   }

   /**
    * This randomizes the weights of the neural network between two integers, the two integers specified by lowerRandomizedWeight
    * and higherRandomizedWeight.
    */
   public void randomizeWeights()
   {
      for (int layer = 1; layer < totalNumberOfLayers; layer++)
         for (int startNode = 0; startNode < sizes[layer - 1]; startNode++)
            for (int toNode = 0; toNode < sizes[layer]; toNode++)
               weights[layer - 1][startNode][toNode] = getRandomNumber(lowerRandomizedWeight, higherRandomizedWeight);
   }

   /**
    * This gets the random number between its parameters lower and higher.
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
    * This evalutes the values in the network(in the values array) from the first layer to the last layer, given that the inputs are
    * in the first layer already.
    */
   public void feedForward()
   {
      double sum = 0.0;

      for (int layer = 1; layer < totalNumberOfLayers; layer++)
         for (int toNode = 0; toNode < sizes[layer]; toNode++)
         {
            sum = 0.0;

            for (int startNode = 0; startNode < sizes[layer - 1]; startNode++)
               sum += values[layer - 1][startNode] * weights[layer - 1][startNode][toNode];

            values[layer][toNode] = activationFunction(sum, false);
         }
   }
}