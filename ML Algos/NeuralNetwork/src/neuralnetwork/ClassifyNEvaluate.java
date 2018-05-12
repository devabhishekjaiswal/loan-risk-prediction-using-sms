package neuralnetwork;

import weka.core.Instances;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;

public class ClassifyNEvaluate {
    Classifier classifier;
    
    ClassifyNEvaluate(Classifier classifer) {
        this.classifier = classifer;
    }
    
    public void classifyNevaluate(String fileName) {
        try {
            Instances train;
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                train = new Instances(reader);
            }
        
            train.setClassIndex(train.numAttributes() - 1);
            
            try {
                classifier.buildClassifier(train);
                System.out.println(classifier);
            
                Evaluation eval = new Evaluation(train);
                eval.evaluateModel(classifier, train);
                        
                System.out.println(eval.toSummaryString("\nResults\n======\n", false));        
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
     
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(System.err);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
