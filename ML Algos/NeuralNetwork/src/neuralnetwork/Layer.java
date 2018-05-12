package neuralnetwork;

import java.util.ArrayList;

public class Layer {
    ArrayList<Node> nodes = new ArrayList<>();

    public ArrayList<Node> getNodes() {
        return nodes;
    }
     
    public int getSize() {
        return nodes.size();
    }
    
    public void setNodes(int numNodes, NodeType nodeType) {
        nodes.clear();
        for (int i = 0; i < numNodes; i++) {
            nodes.add(new Node(nodeType));
        }
    }
    
    public Node addNode(NodeType nodeType) {
        Node newNode = new Node(nodeType); 
        nodes.add(newNode);
        return newNode;
    }
}
