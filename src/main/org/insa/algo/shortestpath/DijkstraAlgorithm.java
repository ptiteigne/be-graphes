package org.insa.algo.shortestpath;

import java.util.Arrays;

import org.insa.algo.utils.BinaryHeap;
import org.insa.graph.Arc;
import org.insa.graph.Graph;
import org.insa.graph.Node;

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
		
		for (Label label : labels) {
			
		}
		
		// Set source cost to zero and insert it on the heap
		labels[data.getOrigin().getId()].setCost(0);
		heap.insert(labels[data.getOrigin().getId()]);
		

		// Notify observers about the first event (origin processed).
		notifyOriginProcessed(data.getOrigin());

		// Initialize array of predecessors.
		Arc[] predecessorArcs = new Arc[nbNodes];
		
		boolean stillUnmarkedNodes = true;
		
		
		// Node currently being marked
		Node currentNode;
		
		// Successor currently checked
		Node nextNode;
		
		while(stillUnmarkedNodes) {
			
			// Extracting the min Node from the the heap and marking it
			currentNode = heap.deleteMin();
			labels[currentNode.getId()].mark();
			
			// Iteration over every successor of currentNode
			for(Arc arc : currentNode) {
				
				// Small test to check allowed roads...
				// Not sure if necessary
				if (!data.isAllowed(arc)) {
					continue;
				}
				
				nextNode = arc.getDestination();
				
				if(!labels[nextNode.getId()].isMarked()) {
					
					// Update cost if necessary
					// If there is an update, insert this node on the heap
					if ( labels[nextNode.getId()].updateCost(
							labels[currentNode.getId()].getCost()+arc.getLength()) ) 
					{
						try {
							heap.remove(nextNode);
						}
						catch(Exception ElementNotFoundException) {}
						
						heap.insert(nextNode);
						
					}
					
				}
				
			}
			
			
			
			stillUnmarkedNodes = false;
			
			// Check if there is still an unmarked node
			for (Label label : labels) {
				if (label.isMarked() == false){
					stillUnmarkedNodes = true;
					break;
				}
			}
			
		}
		
		
        
        
		ShortestPathSolution solution = null;
        
        return solution;
    }

}
