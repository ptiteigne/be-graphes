package org.insa.algo.shortestpath;

import org.insa.graph.Node;

/**
 * A label is associated to a node.
 * When evaluating a shortest path with the Dijkstra Algorithm,
 * a label is associated to every node.
 * 
 * The label has the following attributes :
 * 	<li> the node Id associated to the label </li>
 * 	<li> a boolean telling if the node has been marked </li>
 *	<li> the father of this node </li>
 *	<li> the cost to reach the node </li>
 * @author Julien and Quentin
 *
 */
public class Label implements Comparable<Label> {
	
	private int nodeId;
	
	private Node currentNode;
	
	private double cost;
	
	private Node father;
	
	private boolean isMarked;
	
	/**
	 * Constructor of class Label
	 * The label is initialized like this :
	 * <ul>
     *	<li> the node is NOT marked </li>
     *	<li> the node has no father (father = null) </li>
     *	<li> the cost to reach the node is Infinity.MAX_VALUE </li>
	 * </ul>
	 * 
	 */
	public Label() {
		this.cost = Double.POSITIVE_INFINITY;
		this.father = null;
		this.isMarked = false;
		this.currentNode =null;
	}
	
	// Get the Id of the node associated to this label
	public int getId() {
		return this.nodeId;
	}
	
	// Set the Id of the node associated to this label
	public void setId(int id) {
		this.nodeId = id;
	}
	
	// Get the cost to reach this node
	public double getCost() {
		return this.cost;
	}
	
	// Get the TOTAL cost to reach this node (useless in normal Labels)
	public double getTotalCost() {
		return this.cost;
	}
	
	// Set the cost to reach this node
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	// get the node associated to the label
	public Node getNode()
	{
		return this.currentNode;
	}
	
	//set the node of the label
	public void setNode(Node node)
	{
		this.currentNode = node;	
	}
	
	
	public boolean isMarked() {
		return this.isMarked;
	}
	
	public void mark()
	{
		this.isMarked = true;
	}
	
	public Node getFather()
	{
		return this.father;
	}
	
	public void setFather(Node father)
	{
		this.father = father;
	}

	@Override
	public int compareTo(Label o) {
		if(o == null)
			throw new NullPointerException();
		else if(this.getTotalCost() > o.cost)
			return 1;
		else if (this.getTotalCost() < o.cost)
			return -1;
		else
			return 0;
	}
	

}
