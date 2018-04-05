package org.insa.algo.shortestpath;

import org.insa.graph.Node;

public class Label {
	
	private int cost;
	
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
		this.cost = Integer.MAX_VALUE;
		this.father = null;
		this.isMarked = false;
	}
	
	// Get the cost to reach this node
	public int getCost() {
		return this.cost;
	}
	
	// Set the cost to reach this node
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public boolean isMarked() {
		return this.isMarked;
	}
	
	public void Mark()
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
	

}
