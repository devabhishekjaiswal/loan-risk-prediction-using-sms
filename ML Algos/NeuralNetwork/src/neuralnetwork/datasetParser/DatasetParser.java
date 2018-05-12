package neuralnetwork.datasetParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import neuralnetwork.dataset.Dataset;
import neuralnetwork.Pair;
import neuralnetwork.dataset.Sample;

public abstract class DatasetParser {
   
    public Pair<Dataset, Dataset> parse(String fileName, double p) {
        Dataset trainset = new Dataset();
        Dataset validationSet = new Dataset();
        try {
            InputStream instream = new FileInputStream(fileName);
            Scanner scanner = new Scanner(instream);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                Sample sample = extractSample(line);
                if (Math.random() < p) {
                    validationSet.addSample(sample);
                } else {
                    trainset.addSample(sample);
                }
            }
            return new Pair(trainset, validationSet);
        } catch (FileNotFoundException ex) {
            System.err.println("File Not Present");
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    public abstract Sample extractSample(String line); 
    public abstract String getLabel(ArrayList<Double> output); 
}
