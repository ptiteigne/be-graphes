package org.insa.graph;

public class NodePair {
	
	private Node startNode;
	private Node endNode;
	
	public NodePair(Node startNode, Node endNode) {
		this.startNode = startNode;
		this.endNode = endNode;
	}
	
	public Node getStartNode() {
		return this.startNode;
	}
	
	public void setStartNode(Node startNode) {
		this.startNode = startNode;
	}
	
	public Node getEndNode() {
		return this.endNode;
	}
	
	public void setEndNode(Node endNode) {
		this.endNode = endNode;
	}

}
