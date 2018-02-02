package decisiontree;


public class NodeAnalysisResult{
	private String attributeAtNode;
	private String entropyAtNode;
	private boolean isNodeOutputNode;
	private String outputNode;

	public NodeAnalysisResult() {
		this.attributeAtNode = null;
		this.entropyAtNode = null;
		this.isNodeOutputNode = false;
		this.outputNode = null;
	}

	public String getAttributeAtNode() {
		return attributeAtNode;
	}

	public void setAttributeAtNode(String attributeAtNode) {
		this.attributeAtNode = attributeAtNode;
	}

	public String getEntropyAtNode() {
		return entropyAtNode;
	}

	public void setEntropyAtNode(String entropyAtNode) {
		this.entropyAtNode = entropyAtNode;
	}

	public boolean isNodeOutputNode() {
		return isNodeOutputNode;
	}

	public void setNodeOutputNode(boolean isNodeOutputNode) {
		this.isNodeOutputNode = isNodeOutputNode;
	}

	public String getOutputNode() {
		return outputNode;
	}

	public void setOutputNode(String outputNode) {
		this.outputNode = outputNode;
	}
	
	
	
}
