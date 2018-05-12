package neuralnetwork;

import neuralnetwork.dataset.Sample;
import neuralnetwork.dataset.Dataset;
import neuralnetwork.prune.Prune;
import neuralnetwork.visualize.Visualize;
import neuralnetwork.datasetParser.DatasetParser;
import java.util.ArrayList;
import neuralnetwork.datasetParser.myDatasetParser;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;

public class NeuralNetwork {
    
    /*public void start() {
        Network NN = new Network(2, 2, 0.5);
        
        ArrayList<Double> input = new ArrayList<>();
        input.add(0.05); input.add(0.10);
        
        ArrayList<Double> target = new ArrayList<>();
        target.add(0.01); target.add(0.99);
        
        double[][] Wih = {{0.15, 0.25}, {0.20, 0.30}};
        double[][] Who = {{0.40, 0.50}, {0.45, 0.55}};
        
        NN.setInitialWeights(Wih, Who);
        NN.trainNNSingle(input,target);
        
        PrintWriter writer = new PrintWriter(System.err, true);
        NN.logWho(writer);
        NN.logWih(writer);
    }*/
    
     private String predict(Network NN, double[] inp, DatasetParser parser) {
        ArrayList<Double> input = new ArrayList<>();
        for (int i = 0; i < inp.length; i++) input.add(inp[i]);
       
        ArrayList<Double> output = NN.getOutput(input);
        String label = parser.getLabel(output);
        return label;
    }
     
    public int getCorrectCount(Network NN, Dataset validateset, DatasetParser parser){
        int correctLabelCnt = 0;
        for (Sample sample: validateset.samples) {
            double[] testInput = new double[sample.input.size()];
            for (int i = 0; i < sample.input.size(); i++) {
                testInput[i] = sample.input.get(i);
            }
            String predictedLabel = predict(NN, testInput, parser);
            String actualLabel = parser.getLabel(sample.output);
            
            /*System.out.println("Predicted = " + predictedLabel);
            System.out.println("Actual    = " + actualLabel);
            System.out.println();*/
            
            boolean correct = (actualLabel.equals(predictedLabel));
            if (correct) correctLabelCnt++;
        }
        return correctLabelCnt;
    }
   
    public void evaluateData() {
        
    
        Network NN = new Network(3, 1, 0.1);
        NN.setRandomWeights();
        
        String fileName = "out.csv";
        DatasetParser parser = new myDatasetParser();
        Pair<Dataset, Dataset> trainNvalidate = parser.parse(fileName, 0.2);
        Dataset trainset = trainNvalidate.first;
        Dataset validateset = trainNvalidate.second;
        
        System.out.println(trainset.samples);
        //System.out.println(validateset.samples);
        
        
        //Constants
        final int MAX_PASSES = 500;
        final int MAX_HIDDEN_NODES = 10;
        final double BATCH_ERROR_THRESHOLD = 0.000001;
        final double WEIGHT_FREEZE_THRESHOLD = 1e-8;
        final double SINGLE_ERROR_THRESHOLD = 1e-9;
            
        for (Sample sample: trainset.get()) {
            validateset.addSample(sample);
        }
        
        NN.addHiddenNode();
        double error = NN.trainBatchWeightFreeze(trainset, MAX_PASSES, SINGLE_ERROR_THRESHOLD, WEIGHT_FREEZE_THRESHOLD);
        Visualize.visualize(NN);
        
        double prevError=error, deltaError;
        int maxHiddenNode = MAX_HIDDEN_NODES;
        do {
            NN.addHiddenNode();
            error = NN.trainBatchWeightFreeze(trainset, MAX_PASSES, SINGLE_ERROR_THRESHOLD, WEIGHT_FREEZE_THRESHOLD);
        
            deltaError = Math.abs(error - prevError);
            prevError = error;
            
            Visualize.visualize(NN);
          
            if (NN.getHiddenLayer().getSize() > maxHiddenNode) break;
            
        } while (deltaError > BATCH_ERROR_THRESHOLD);
      
        System.out.printf("Results ::  Artificial Neural Networks \n======\n\n");
        {
            int correctLabelCnt = getCorrectCount(NN, validateset, parser);
            System.out.printf("Results\n======\n\n");
            System.out.printf("Number of Hidden Nodes         %10d\n", NN.getHiddenLayer().getSize());
            double accuracy = (double)correctLabelCnt/validateset.samples.size();
            System.out.printf("Correctly Predicted            %10d %10.3f %%\n", correctLabelCnt, (accuracy*100));
            System.out.printf("Incorrectly Predicted          %10d %10.3f %%\n", (validateset.samples.size() - correctLabelCnt), (1.0-accuracy)*100);
            System.out.printf("Total vectors in ValidationSet %10d\n", validateset.samples.size());
            System.out.printf("Mean Error                     %10.3f\n", Math.sqrt(error));
            System.out.println();
        }
        
        {
            int correctLabelCnt = getCorrectCount(NN, validateset, parser);
            double accuracy = (double)correctLabelCnt/validateset.samples.size();
            
            Prune prune = new Prune(validateset, parser);
            double nW = prune.prune(NN, accuracy, 0.1, 0.02);
      
            System.out.printf("Results Pruned\n======\n\n");
            System.out.printf("Number of Hidden Nodes         %10d\n", NN.getHiddenLayer().getSize());
            correctLabelCnt = getCorrectCount(NN, validateset, parser);
            accuracy = (double)correctLabelCnt/validateset.samples.size();
            System.out.printf("Correctly Predicted            %10d %10.3f %%\n", correctLabelCnt, (accuracy*100));
            System.out.printf("Incorrectly Predicted          %10d %10.3f %%\n", (validateset.samples.size() - correctLabelCnt), (1.0-accuracy)*100);
            System.out.printf("Total vectors in ValidationSet %10d\n", validateset.samples.size());
            System.out.printf("Mean Error                     %10.3f\n", Math.sqrt(error));
            System.out.printf("Thresholding Limit             %10.3f\n", nW);
        }
        
        Visualize.visualize(NN);
        
    }
    
    
    public static void main(String[] args) {
        new NeuralNetwork().evaluateData();
    }
    
}
