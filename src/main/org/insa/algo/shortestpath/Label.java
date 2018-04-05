package org.insa.algo.shortestpath;

import org.insa.graph.Node;

/**
 * A label is associated to a node.
 * When evaluating a shortest path with the Dijkstra Algorithm,
 * a label is associated to every node.
 * 
 * The label has the following attributes :
 * 	<li> a boolean telling if the node has been marked </li>
 *	<li> the father of this node </li>
 *	<li> the cost to reach the node </li>
 * @author Julien and Quentin
 *
 */
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

}
