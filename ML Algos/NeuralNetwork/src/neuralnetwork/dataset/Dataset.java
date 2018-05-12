package neuralnetwork.dataset;

import java.util.ArrayList;

public class Dataset {
    public ArrayList<Sample> samples = new ArrayList<>();
    
    public ArrayList<Sample> get() {
        return this.samples;
    }
    
    public int getSize() {
        return samples.size();
    }
    
    public Dataset addSample(Sample sample) {
        this.samples.add(sample);
        return this;
    }
}
