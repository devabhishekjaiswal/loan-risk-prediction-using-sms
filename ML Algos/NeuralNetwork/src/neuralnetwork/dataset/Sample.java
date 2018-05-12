package neuralnetwork.dataset;

import java.util.ArrayList;

public class Sample {
    public ArrayList<Double> input = new ArrayList<>();
    public ArrayList<Double> output = new ArrayList<>();
    
    public Sample(ArrayList<Double> input, ArrayList<Double> output) {
        this.input = input;
        this.output = output;
    }
    
    public ArrayList<Double> getInput(){
        return input;
    }
    
    public ArrayList<Double> getOutput() {
        return output;
    }
    
    @Override
    public String toString() {
        String s = "";
        s += input;
        s += " => ";
        s += output;
        return s;
    }
}
