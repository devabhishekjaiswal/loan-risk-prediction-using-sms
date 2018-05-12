package neuralnetwork.prune;

import neuralnetwork.datasetParser.DatasetParser;
import java.util.ArrayList;
import neuralnetwork.dataset.Dataset;
import neuralnetwork.Network;
import neuralnetwork.NeuralNetwork;
import neuralnetwork.Node;
import neuralnetwork.dataset.Sample;

public class Prune {
    Dataset validateset;
    DatasetParser parser;
    
    public Prune(Dataset validateset, DatasetParser parser) {
        this.validateset = validateset;
        this.parser = parser;
    }
    
    public double prune(Network NN, double initialAccuracy, final double stepSize, final double tolerance) {
        double threshold=stepSize;
        double accuracy;
        
        double[][] Wih = NN.getWih(), Who = NN.getWho();
        do {
            
           Wih = NN.getWih(); Who = NN.getWho();
            
            for (Node inputNode: NN.getInputLayer().getNodes()) {
                for (Node hiddenNode: NN.getHiddenLayer().getNodes()) {
                    double w = NN.Wih.get(inputNode).get(hiddenNode);
                    final double INF = 1e9;
                    double maxw = -INF;
                    for (Node outputNode: NN.getOutputLayer().getNodes()) {
                        maxw = Math.max(maxw, NN.Who.get(hiddenNode).get(outputNode));
                    }
                    w = Math.abs(w*maxw);
                    if (w < threshold) {
                        NN.Wih.get(inputNode).put(hiddenNode, 0.0);
                    }
                }
            }

            for (Node hiddenNode: NN.getHiddenLayer().getNodes()) {
                for (Node outputNode: NN.getOutputLayer().getNodes()) {
                    double w = NN.Who.get(hiddenNode).get(outputNode);
                    w = Math.abs(w);
                    if (w < threshold) {
                        NN.Who.get(hiddenNode).put(outputNode, 0.0);
                    }
                }
            }

            int correctLabelCnt = getCorrectCount(NN, validateset, parser);
            int size = validateset.samples.size();
            accuracy = (double)correctLabelCnt/size;

            threshold += stepSize;
           
        } while (Math.abs(initialAccuracy - accuracy) <= tolerance);
        
        NN.setWih(Wih); NN.setWho(Who);
        
        return threshold;

        
    }
        
    private static String predict(Network NN, double[] inp, DatasetParser parser) {
        ArrayList<Double> input = new ArrayList<>();
        for (int i = 0; i < inp.length; i++) input.add(inp[i]);
       
        ArrayList<Double> output = NN.getOutput(input);
        String label = parser.getLabel(output);
        return label;
    }
    
    private int getCorrectCount(Network NN, Dataset validateset, DatasetParser parser){
        int correctLabelCnt = 0;
        for (Sample sample: validateset.samples) {
            double[] testInput = new double[sample.getInput().size()];
            for (int i = 0; i < sample.getInput().size(); i++) {
                testInput[i] = sample.getInput().get(i);
            }
            String predictedLabel = predict(NN, testInput, parser);
            String actualLabel = parser.getLabel(sample.getOutput());
            
            /*System.out.println("Predicted = " + predictedLabel);
            System.out.println("Actual    = " + actualLabel);
            System.out.println();*/
            
            boolean correct = (actualLabel.equals(predictedLabel));
            if (correct) correctLabelCnt++;
        }
        return correctLabelCnt;
    }
}
