package neuralnetwork.visualize;

import java.util.ArrayList;
import java.util.HashSet;
import neuralnetwork.Network;
import neuralnetwork.Node;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

public class Visualize {
    public static void visualize(Network NN) {
        
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
                
        String styleSheet =
    		"edge {"
                +"	size: 2px;"
                +"	fill-color: red;"
                +"	fill-mode: dyn-plain;"
                +"      text-alignment: under; text-color: white; text-style: bold; text-background-mode: rounded-box; text-background-color: #222C; text-padding: 5px, 4px; text-offset: 0px, 5px; }node#C {text-alignment:above; text-offset: 0px, -5px;"
                +"}"
                +"edge.pinedge {"
                +"	size: 2px;"
                +"	fill-color: black;"
                +"	fill-mode: dyn-plain;"
                +"}"
                +"edge.freeze {"
                +"	size: 2px;"
                +"	fill-color: blue;"
                +"	fill-mode: dyn-plain;"
                +"      text-alignment: under; text-color: white; text-style: bold; text-background-mode: rounded-box; text-background-color: #222C; text-padding: 5px, 4px; text-offset: 0px, 5px; }node#C {text-alignment:above; text-offset: 0px, -5px;"
                +"}"
                +"edge.pruned {"
                +"	size: 2px;"
                +"	fill-color: orange;"
                +"	fill-mode: dyn-plain;"
                +"      text-alignment: under; text-color: white; text-style: bold; text-background-mode: rounded-box; text-background-color: #222C; text-padding: 5px, 4px; text-offset: 0px, 5px; }node#C {text-alignment:above; text-offset: 0px, -5px;"
                +"}"
                +"node { "
                +"      size: 30px; "
                +"      fill-color: yellow, red; "
                +"      fill-mode: gradient-horizontal; "
                +"}"
                +"node.pinnode { "
                +"      size: 5px; "
                +"      fill-color: black, gray; "
                +"      fill-mode: gradient-horizontal; "
                +"}"
                +"node:clicked { "
                +"      size: 30px; "
                +"      fill-color: #7B920A, #ADD100; "
                +"      fill-mode: gradient-horizontal; "
                +"}"
                +"node.freezed { "
                +"      size: 30px; "
                +"      fill-color: #3a7bd5, #00d2ff; "
                +"      fill-mode: gradient-horizontal; "
                +"}";
                
        Graph graph = new SingleGraph("Neural Network");
        graph.setStrict(true);
        graph.addAttribute("ui.stylesheet", styleSheet);
        
        ArrayList<Node> inLayer = NN.getInputLayer().getNodes();
        ArrayList<Node> hidLayer = NN.getHiddenLayer().getNodes();
        ArrayList<Node> outLayer = NN.getOutputLayer().getNodes();
        
        for (int i = 0; i < inLayer.size(); i++) {
            String id = "Ip"+Integer.toString(i); 
            graph.addNode(id).setAttribute("ui.class", "pinnode");
            graph.getNode(id).setAttribute("xyz", 2, (i+1) * (double)100/(inLayer.size()+1), 0);
        }
        
        for (int i = 0; i < outLayer.size(); i++) {
            String id = "Op"+Integer.toString(i); 
            graph.addNode(id).setAttribute("ui.class", "pinnode");
            graph.getNode(id).setAttribute("xyz", 108, (i+1) * (double)100/(outLayer.size()+1), 0);
        }
        
        for (int i = 0; i < inLayer.size(); i++) {
            String id = "I"+Integer.toString(i); 
            graph.addNode(id).setAttribute("ui.label", id);
            graph.getNode(id).setAttribute("xyz", 10, (i+1) * (double)100/(inLayer.size()+1), 0);
        }
        
        for (int i = 0; i < hidLayer.size(); i++) {
            String id = "H"+Integer.toString(i); 
            graph.addNode(id).setAttribute("ui.label", id);
            if (hidLayer.get(i).isFreezed) {
                graph.getNode(id).addAttribute("ui.class", "freezed");
            }
            graph.getNode(id).setAttribute("xyz", 60, (i+1) * (double)100/(hidLayer.size()+1), 0);
        }
        
        for (int i = 0; i < outLayer.size(); i++) {
            String id = "O"+Integer.toString(i); 
            graph.addNode(id).setAttribute("ui.label", id);
            graph.getNode(id).setAttribute("xyz", 100, (i+1) * (double)100/(outLayer.size()+1), 0);
        }
        
        for (int i = 0; i < inLayer.size(); i++) {
            String id1 = "Ip"+Integer.toString(i);
            String id2 = "I"+Integer.toString(i);
            graph.addEdge(id1+id2, id1, id2, true).addAttribute("ui.class", "pinedge");
        }
        
        for (int i = 0; i < outLayer.size(); i++) {
            String id1 = "O"+Integer.toString(i);
            String id2 = "Op"+Integer.toString(i);
            graph.addEdge(id1+id2, id1, id2, true).addAttribute("ui.class", "pinedge");
        }
        
        for (int i = 0; i < inLayer.size(); i++) {
            String id1 = "I"+Integer.toString(i); 
            for (int j = 0; j < hidLayer.size(); j++) {
               String id2 = "H"+Integer.toString(j);
               double w = NN.Wih.get(inLayer.get(i)).get(hidLayer.get(j));
               String ws = String.format("%.3f", w);
               graph.addEdge(id1+id2, id1, id2, true).setAttribute("ui.label", ws);
               if (-0.000001 <= w && w <= 0.000001) {
                    graph.removeEdge(id1+id2);
               } else if (hidLayer.get(j).isFreezed) { 
                   graph.getEdge(id1+id2).addAttribute("ui.class", "freeze");
               }
            }
        }
        
        for (int i = 0; i < hidLayer.size(); i++) {
            String id1 = "H"+Integer.toString(i); 
            for (int j = 0; j < outLayer.size(); j++) {
               String id2 = "O"+Integer.toString(j);
               double w = NN.Who.get(hidLayer.get(i)).get(outLayer.get(j));
               String ws = String.format("%.3f", w);
               graph.addEdge(id1+id2, id1, id2, true).setAttribute("ui.label", ws);
               if (-0.000001 <= w && w <= 0.000001) {
                   graph.removeEdge(id1+id2);
               } 
            }
        }
        
        for (int i = 0; i < hidLayer.size(); i++) {
            boolean flag = false;
            String id = "H"+Integer.toString(i); 
            for (Node input: NN.getInputLayer().getNodes()) {
                double w = NN.Wih.get(input).get(hidLayer.get(i));
                if (Math.abs(w) > 1E-6) {
                    flag = true;
                    break;
                }
            }
            
            if (!flag) {
                graph.removeNode(id);
                continue;
            }
            
            flag = false;
            for (Node output: NN.getOutputLayer().getNodes()) {
                double w = NN.Who.get(hidLayer.get(i)).get(output);
                if (Math.abs(w) > 1E-6) {
                    flag = true;
                    break;
                }
            }
            
            if (!flag) {
                graph.removeNode(id);
            }
        }
        
        Viewer viewer = graph.display();
        viewer.disableAutoLayout();
    }    
}
