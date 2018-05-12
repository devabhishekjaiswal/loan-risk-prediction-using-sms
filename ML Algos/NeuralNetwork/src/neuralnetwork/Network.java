package neuralnetwork;

import neuralnetwork.dataset.Dataset;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Network implements Cloneable{
    private final Layer inputLayer = new Layer();
    private final Layer outputLayer = new Layer();
    private final Layer hiddenLayer = new Layer();
    private double learningRate;
    
    
    public Network(int numInput, int numOutput, double learningRate) {
        inputLayer.setNodes(numInput, NodeType.INPUT);
        outputLayer.setNodes(numOutput, NodeType.OUTPUT);
        
        //Keep this part commented for DNC
            //int numHidden = 1;
            //hiddenLayer.setNodes(numHidden, NodeType.HIDDEN);
        //
        this.learningRate = learningRate;
    }
   
    public Network setLearningRate(double rate) {
        this.learningRate = rate;
        return this;
    }
    
    
    public HashMap<Node, HashMap<Node, Double>> Wih = new HashMap<>();
    public HashMap<Node, HashMap<Node, Double>> Who = new HashMap<>();
    
    public Network setInitialWeights(double[][] inpWih, double[][] inpWho ) {
        for (int i = 0; i < inputLayer.getSize(); i++) {
            Node input= inputLayer.getNodes().get(i);
            Wih.put(input, new HashMap<Node, Double>());
            for (int j = 0; j < hiddenLayer.getSize(); j++) {
                Node hidden = hiddenLayer.getNodes().get(j);
                Wih.get(input).put(hidden, inpWih[i][j]);
            }
        }
        
        for (int i = 0; i < hiddenLayer.getSize(); i++) {
            Node hidden = hiddenLayer.getNodes().get(i);
            Who.put(hidden, new HashMap<Node, Double>());
            for (int j= 0; j < outputLayer.getSize(); j++) {
                Node output = outputLayer.getNodes().get(j);
                Who.get(hidden).put(output, inpWho[i][j]);
            }
        }
        
        return this;
    }
    
    public Network setRandomWeights() {
        for (int i = 0; i < inputLayer.getSize(); i++) {
            Node input= inputLayer.getNodes().get(i);
            Wih.put(input, new HashMap<Node, Double>());
            for (int j = 0; j < hiddenLayer.getSize(); j++) {
                Node hidden = hiddenLayer.getNodes().get(j);
                Wih.get(input).put(hidden, Math.random());
            }
        }
        
        for (int i = 0; i < hiddenLayer.getSize(); i++) {
            Node hidden = hiddenLayer.getNodes().get(i);
            Who.put(hidden, new HashMap<Node, Double>());
            for (int j= 0; j < outputLayer.getSize(); j++) {
                Node output = outputLayer.getNodes().get(j);
                Who.get(hidden).put(output, Math.random());
            }
        }
        
        return this;
    }
    
    public void addHiddenNode(){
        Node addedNode = hiddenLayer.addNode(NodeType.HIDDEN);
        for (Node input: inputLayer.getNodes()) {
            assignRandom(input, addedNode);
        }
        for (Node output: outputLayer.getNodes()) {
            if (!Who.containsKey(addedNode)) {
                Who.put(addedNode, new HashMap<Node, Double>());
            }
            assignRandom(addedNode, output);
        }
    }
    
    //Assign Random Weights
    private void assignRandom(Node from, Node to) {
        try {
            double randWeight = Math.random();
            if (from.getType() == NodeType.INPUT) {
                Wih.get(from).put(to, randWeight);
            } else if (to.getType() == NodeType.OUTPUT){
                Who.get(from).put(to, randWeight);
            } 
        } catch (NullPointerException ex) {
            System.out.println("Invalid Node");
            ex.printStackTrace(System.err);
        }
    }
    
    //Layers Getter
    public Layer getInputLayer() {return this.inputLayer;}
    public Layer getHiddenLayer() {return this.hiddenLayer;}
    public Layer getOutputLayer() {return this.outputLayer;}
    
    
    //Forward Pass Neural Network
    public ArrayList<Double> getOutput(ArrayList<Double> input) {
        assert(input.size() == inputLayer.getNodes().size());
        for (int i=0; i<input.size(); i++) {
            double val = input.get(i);
            inputLayer.getNodes().get(i).setInput(val);
        }
        
        generateOutput();
        
        ArrayList<Double> output = new ArrayList<>();
        for (Node out: outputLayer.getNodes()) {
            output.add(out.getOutput());
        }
        return output;
    }
    
    private void generateOutput() {
        for (Node hidden: hiddenLayer.getNodes()) {
            double sum = 0;   
            for (Node input: inputLayer.getNodes()) {
                double weight =  Wih.get(input).get(hidden);
                sum += weight * input.getOutput();
            }
            hidden.setInput(sum);
            //hidden.addBias(0.35);
        }
        
        for (Node output: outputLayer.getNodes()) {
            double sum = 0;
            for (Node hidden: hiddenLayer.getNodes()) {
                double weight = Who.get(hidden).get(output);
                sum += weight * hidden.getOutput();
            }
            output.setInput(sum);
            //output.addBias(0.60);
        }
    }
    
    //Calculate Error
    private ArrayList<Double> getError(ArrayList<Double> output, ArrayList<Double> target) {
        ArrayList<Double> error = new ArrayList<>();
        for (int i = 0; i < output.size(); i++) {
            double err = output.get(i)-target.get(i);
            error.add(0.5 * Math.pow(err, 2));
        }
        return error;
    }
    
    private double getTotalError(ArrayList<Double> error) {
        double totalError = 0;
        for (Double e : error) {
            totalError += e;
        }
        return totalError;
    }
   
    //Backward Pass
    private void adjustWho(ArrayList<Double> target) {
        for (Node hidden: hiddenLayer.getNodes()) {
            for (int j = 0; j < outputLayer.getSize(); j++) {
                Node output = outputLayer.getNodes().get(j);
                
                double delta = 1.0;
                delta *= hidden.getOutput();
                delta *= (output.getOutput() - target.get(j));
                delta *= output.getOutput() * (1.0 - output.getOutput());
                
                double prevW = Who.get(hidden).get(output);
                double newW = prevW - this.learningRate*delta;
                Who.get(hidden).put(output, newW);
            }
        }
    }
    
    private void adjustWih(ArrayList<Double> target) {
        for (Node input: inputLayer.getNodes()) {
            for (Node hidden: hiddenLayer.getNodes()) {
                
                if (hidden.isFreezed) continue;
                
                double delta = 1.0;
                delta *= hidden.getOutput() * (1.0 - hidden.getOutput());
                delta *= input.getOutput();
            
                double sum = 0;
                for (int k = 0; k < outputLayer.getSize(); k++) {
                    double d = 1;
                    Node output = outputLayer.getNodes().get(k);
                    d *= output.getOutput() - target.get(k);
                    d *= Who.get(hidden).get(output);
                    sum += d;
                }
                
                delta *= sum;
                double prevW = Wih.get(input).get(hidden);
                double newW = prevW - this.learningRate*delta;
                Wih.get(input).put(hidden, newW);
            }
        }     
    } 
    
    
    //Train Neural Network
    public double trainNNSingle(ArrayList<Double> input, ArrayList<Double> target) {
        
        ArrayList<Double> output = getOutput(input);
        ArrayList<Double> error = getError(output, target);
         
        double E = getTotalError(error);
        
        this.adjustWho(target); this.adjustWih(target);
        return E;
    }
    
    private final double INF = 1e6;
    private double preverror = INF;
    public double trainBatch(Dataset dataSet, int maxIterations, double THRESHHOLD) {
             
        for (int k = 0; k < maxIterations; k++) {
            
            double dataSetError = 0;
            for (int i = 0; i < dataSet.get().size(); i++) {
                double E = trainNNSingle(dataSet.samples.get(i).input, dataSet.samples.get(i).output);
                dataSetError += E;
            }
            
            //Logging
            System.out.printf("Iteration %d\n", k);
            this.logWih(new PrintWriter(System.out, true)); this.logWho(new PrintWriter(System.out, true));
            System.out.printf("Error in %d iteration is %10.3f\n", k, dataSetError);
            System.out.printf("Average Error per Sample in %d iteration is %10.6f\n\n", k, (double)dataSetError/dataSet.getSize());
            //
            
            if (Math.abs(this.preverror - dataSetError) <= THRESHHOLD) break;
            this.preverror = dataSetError;
        }
        return preverror/dataSet.getSize();
    }
    
    public double trainBatchWeightFreeze(Dataset dataSet, int maxIterations, double THRESHHOLD, double tau) {
        
        for (int k = 0; k < maxIterations; k++) {
            
            HashMap<Node, Double> hiddenOutput = new HashMap<>();
            for (Node hidden: hiddenLayer.getNodes()) {
                hiddenOutput.put(hidden, hidden.getOutput());
            }
            
            double dataSetError = 0;
            for (int i = 0; i < dataSet.get().size(); i++) {
                double E = trainNNSingle(dataSet.samples.get(i).input, dataSet.samples.get(i).output);
                dataSetError += E;
            }
            
            for (Node hidden: hiddenLayer.getNodes()) {
                if (Math.abs(hidden.getOutput() - hiddenOutput.get(hidden)) <= tau) {
                    hidden.isFreezed = true;
                }
            }
            
            //Logging
            System.out.printf("Iteration %d\n", k);
            this.logWih(new PrintWriter(System.out, true)); this.logWho(new PrintWriter(System.out, true));
            System.out.printf("Error in %d iteration is %10.3f\n", k, dataSetError);
            System.out.printf("Average Error per Sample in %d iteration is %10.6f\n\n", k, (double)dataSetError/dataSet.getSize());
            //
            
            if (Math.abs(this.preverror - dataSetError) <= THRESHHOLD) break;
            this.preverror = dataSetError;
        
            
        }
        return preverror/dataSet.getSize();
    }
    
    //Logging
    public void logWih(PrintWriter writer) {
        writer.println("Weight Input to Hidden:");
        for (Node input: inputLayer.getNodes()) {
            for (Node hidden: hiddenLayer.getNodes()) {
                writer.printf("%10.3f ", Wih.get(input).get(hidden));
            }
            writer.println();
        }
    }
    
    public void logWho(PrintWriter writer) {
        writer.println("Weight Hidden to Output:");
        for (Node hidden: hiddenLayer.getNodes()) {
            for (Node out: outputLayer.getNodes()) {
                writer.printf("%10.3f ", Who.get(hidden).get(out));
            }
            writer.println();
        }
    }    
    
    public double[][] getWih() {
        double[][] w = new double[inputLayer.getSize()][hiddenLayer.getSize()];
        for (int i = 0; i < inputLayer.getSize(); i++) {
            Node input = inputLayer.getNodes().get(i);
            for (int j = 0; j < hiddenLayer.getSize(); j++) {
                Node hidden = hiddenLayer.getNodes().get(j);
                w[i][j] = Wih.get(input).get(hidden);
            }
        }
        return w;
    }
    
    public double[][] getWho() {
        double[][] w = new double[hiddenLayer.getSize()][outputLayer.getSize()];
        for (int i = 0; i < hiddenLayer.getSize(); i++) {
            Node hidden = hiddenLayer.getNodes().get(i);
            for (int j = 0; j < outputLayer.getSize(); j++) {
                Node output = outputLayer.getNodes().get(j);
                w[i][j] = Who.get(hidden).get(output);
            }
        }
        return w;
    }
    
    public void setWih(double[][] w) {
        for (int i = 0; i < inputLayer.getSize(); i++) {
            Node input = inputLayer.getNodes().get(i);
            for (int j = 0; j < hiddenLayer.getSize(); j++) {
                Node hidden = hiddenLayer.getNodes().get(j);
                Wih.get(input).put(hidden, w[i][j]);
            }
        }
    }
    
    public void setWho(double[][] w) {
        for (int i = 0; i < hiddenLayer.getSize(); i++) {
            Node hidden = hiddenLayer.getNodes().get(i);
            for (int j = 0; j < outputLayer.getSize(); j++) {
                Node output = outputLayer.getNodes().get(j);
                Who.get(hidden).put(output, w[i][j]);
            }
        }
    }
}