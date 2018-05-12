package neuralnetwork;

public enum NodeType {
    INPUT("INPUT"),
    HIDDEN("HIDDEN"),
    OUTPUT("OUTPUT");
      
    private final String type;
    
    private NodeType(String type){
        this.type = type;
    }
    
    @Override
    public String toString(){
        return this.type;
    }
}
