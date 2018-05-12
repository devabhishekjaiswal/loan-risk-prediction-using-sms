package neuralnetwork;

import static java.lang.Math.exp;

public class Node {
    double input;
    double output;
    
    //exclusively for hidden nodes;
    public boolean isFreezed = false;
    
    Node setInput(double input) {
        this.input = input;
        return this;
    }
    
    Node addBias(double bias) {
        this.input += bias;
        return this;
    }
    
    double getOutput() {
        if (this.getType() == NodeType.INPUT) {
            this.output = this.input;
        } else {
            this.output = ActivationFunction.actvFn(this.input);
        }
        return this.output;
    }
    
    
    NodeType type;
    Node(NodeType type) {
        this.type = type;
    }
    
    NodeType getType() {
        return this.type;
    }
}

class ActivationFunction {
    public static double actvFn(double x) {
        return 1.0/(1+exp(-x));
    }
}