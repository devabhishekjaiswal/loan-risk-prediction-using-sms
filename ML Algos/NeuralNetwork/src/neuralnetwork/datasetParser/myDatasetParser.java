package neuralnetwork.datasetParser;

import java.util.ArrayList;
import java.util.StringTokenizer;
import neuralnetwork.dataset.Sample;

public class myDatasetParser extends DatasetParser {
    
    @Override
    public Sample extractSample(String line) {
    //This part is specific to the dataset being parsed
        StringTokenizer tokenizer = new StringTokenizer(line, ",");
        
        ArrayList<Double> input = new ArrayList<>();
        ArrayList<Double> output = new ArrayList<>();
        
        int col = 1;
        while (tokenizer.hasMoreTokens()) {
            String attr = tokenizer.nextToken();
            if (col == 1) {
                double amount = Double.parseDouble(attr);
                input.add(amount*0.000001);
            } else 
            if (col == 2) {
                double credit = Double.parseDouble(attr);
                input.add(credit*0.000001);
            } else 
            if (col == 3) {
                double debit = Double.parseDouble(attr);
                input.add(debit*0.0001);
            } else 
            if (col == 4) {
                switch(attr) { 
                    case "NO": output.add(0.0); break;
                    case "YES": output.add(1.0); break;
                }
            } 
            col++;
        }
        
        Sample sample = new Sample(input, output);
        return sample;
    }
    
    @Override
    public String getLabel(ArrayList<Double> output) {
        double v1 = output.get(0);
      
        if (v1 <= 0.5) {
            return "NO";
        } else {
            return "YES";
        } 
       
    }
}
