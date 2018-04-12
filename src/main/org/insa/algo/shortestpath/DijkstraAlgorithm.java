package org.insa.algo.shortestpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.insa.algo.AbstractSolution.Status;
import org.insa.algo.utils.BinaryHeap;
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
		}
		
		// Set source cost to zero and insert it on the heap
		labels[data.getOrigin().getId()].setCost(0);
		heap.insert(labels[data.getOrigin().getId()]);
		

		// Notify observers about the first event (origin processed).
		notifyOriginProcessed(data.getOrigin());

		// Initialize array of predecessors.
		//Arc[] predecessorArcs = new Arc[nbNodes];
		
		boolean stillUnmarkedNodes = true;
		
		
		// Node currently being marked
		//Node currentNode;
		
		// Label currently marked
		Label currentLabel;
		
		// Successor currently checked
		Node nextNode;
		
		while(stillUnmarkedNodes && !labels[data.getDestination().getId()].isMarked() && !heap.isEmpty()) {
			
			// Extracting the min Node from the the heap and marking it
			//currentNode = heap.deleteMin();
			//labels[currentNode.getId()].mark();
			currentLabel = heap.deleteMin();
			currentLabel.mark();
			notifyNodeMarked(currentLabel.getNode());
			
			// Iteration over every successor of currentNode
			for(Arc arc : currentLabel.getNode()) {
				
				// Small test to check allowed roads...
				// Not sure if necessary
				if (!data.isAllowed(arc)) {
					continue;
				}
				
				nextNode = arc.getDestination();
				
				notifyNodeReached(nextNode);
				
				if(!labels[nextNode.getId()].isMarked()) {
					
					if (labels[nextNode.getId()].getNode() == data.getDestination())
						notifyDestinationReached(arc.getDestination());
					
					// Update cost if necessary
					// If there is an update, insert this node on the heap
					if ( labels[nextNode.getId()].updateCost(
							currentLabel.getCost()+arc.getLength()) ) 
					{
						
						labels[nextNode.getId()].setFather(currentLabel.getNode());
						
						try {
							heap.remove(labels[nextNode.getId()]);
						}
						catch(Exception ElementNotFoundException) {}
						
						heap.insert(labels[nextNode.getId()]);
						
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

		// Destination has no predecessor, the solution is infeasible...
		if (labels[data.getDestination().getId()].getFather() == null) {
			solution = new ShortestPathSolution(data, Status.INFEASIBLE);
			System.out.println("oups");
		} else {

			// The destination has been found, notify the observers.
			notifyDestinationReached(data.getDestination());

			ArrayList<Node> nodes = new ArrayList<Node>();
			
			Label label = labels[data.getDestination().getId()];
			
			while (label.getFather() != null) {
				nodes.add(label.getNode());
				label = labels[label.getFather().getId()];
			}
			
			nodes.add(label.getNode());
			
			Collections.reverse(nodes);
			
			Path path = Path.createShortestPathFromNodes(graph, nodes);

			// Create the final solution.
			solution = new ShortestPathSolution(data, Status.OPTIMAL, path);
		}

		return solution;
		
    }

}
