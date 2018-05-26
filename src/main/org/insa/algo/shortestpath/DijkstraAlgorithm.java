package org.insa.algo.shortestpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.insa.algo.AbstractSolution.Status;
import org.insa.algo.utils.BinaryHeap;
import org.insa.algo.utils.ElementNotFoundException;
import org.insa.graph.Arc;
import org.insa.graph.Graph;
import org.insa.graph.Node;
import org.insa.graph.Path;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }

    @Override
    protected ShortestPathSolution doRun() {
    	
    	// Retrieve the graph.
		ShortestPathData data = getInputData();
		Graph graph = data.getGraph();
	    
	    final int nbNodes = graph.size();
	    
	    // Initialize the heap
	    BinaryHeap<Label> heap = new BinaryHeap<Label>();
	    
		// Initialize array of labels.
		Label[] labels = new Label[nbNodes];
		
		//Initialize labels, but check if Nodes id are correct
		for (int i=0; i<nbNodes ; i++) {
			labels[i] = new Label();
			labels[i].setNode(graph.get(i));
			labels[i].setId(i);
		}
    	
		// Set source cost to zero and insert it on the heap
		labels[data.getOrigin().getId()].setCost(0);
		heap.insert(labels[data.getOrigin().getId()]);
		
		notifyOriginProcessed(data.getOrigin());
		
		// Node currently being marked
		Node currentNode;
		
		// Label currently marked
		Label currentLabel;
		
		// Successor currently checked
		Node nextNode;
		
		// We iterate while the heap is not empty AND the destination hasn't been reached
		while(!heap.isEmpty() && !labels[data.getDestination().getId()].isMarked()) {
			
			// Getting the node with minimum cost from the heap
			currentLabel = heap.deleteMin();
			
			currentNode = currentLabel.getNode();
			
			if (currentLabel.getId() == data.getDestination().getId()) {
				notifyDestinationReached(data.getDestination());
			}
			
			// Marking the node
			currentLabel.mark();
			notifyNodeMarked(currentNode);
			
			// Iterating over the arcs of this node
			for(Arc arc : currentNode) {
				
				
				// Check if the arc is allowed or not, according to the inspector
				// if not, go to next iteration
				if(!data.isAllowed(arc))
					continue;
				
				nextNode = arc.getDestination();
				
				// If the following node hasn't been marked...
				//if(!labels[nextNode.getId()].isMarked()) {
					
				notifyNodeReached(nextNode);
				
				// If we found a cheaper path to this node... we update its cost and set his new father
				if (labels[nextNode.getId()].getCost() >
						currentLabel.getCost() + data.getCost(arc) ) {
					
					try {
						heap.remove(labels[nextNode.getId()]);
					}
					catch (ElementNotFoundException ignored) {}
					
					labels[nextNode.getId()].setCost(
							currentLabel.getCost() + data.getCost(arc));
					labels[nextNode.getId()].setFather(currentNode);
					
					heap.insert(labels[nextNode.getId()]);
					
				}
					
					
				
			}
			
		}
    	
    	ShortestPathSolution solution = null;
    	
    	// If destination has no predecessors, the solution is infeasible
    	if (labels[data.getDestination().getId()].getFather() == null)
    		return new ShortestPathSolution(data, Status.INFEASIBLE);
    	
    	else
    	{
    		ArrayList<Node> nodes = new ArrayList<Node>();
    		
    		currentLabel = labels[data.getDestination().getId()];
    		
    		// Building the list of nodes forming the path
    		while (currentLabel.getNode() != data.getOrigin() && currentLabel.getFather() != null) {
    			nodes.add(currentLabel.getNode());
    			currentLabel = labels[currentLabel.getFather().getId()];
    		}
    		
    		// Adding the origin to the list
    		nodes.add(currentLabel.getNode());
    		
    		// Inverting the list
    		Collections.reverse(nodes);
    		
    		Path path = Path.createShortestPathFromNodes(graph, nodes);
    		
    		solution = new ShortestPathSolution(data, Status.OPTIMAL, path);
    			
    		
    	}
    		
    	
    	return solution;
		
    }

}
