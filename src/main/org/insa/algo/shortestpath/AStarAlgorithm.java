package org.insa.algo.shortestpath;

import java.util.ArrayList;
import java.util.Collections;

import org.insa.algo.AbstractInputData;
import org.insa.algo.AbstractSolution.Status;
import org.insa.algo.utils.BinaryHeap;
import org.insa.algo.utils.ElementNotFoundException;
import org.insa.graph.Arc;
import org.insa.graph.Graph;
import org.insa.graph.Node;
import org.insa.graph.Path;

public class AStarAlgorithm extends DijkstraAlgorithm {

    public AStarAlgorithm(ShortestPathData data) {
        super(data);
    }
    
    private double calculateEstimatedCostToDestination(ShortestPathData data, LabelStar label) {
    	
    	double estimatedCost = Double.POSITIVE_INFINITY;
		
		if (data.getMode() == AbstractInputData.Mode.LENGTH)
			estimatedCost = label.getNode().getPoint().distanceTo(data.getDestination().getPoint());
		
		else if (data.getMode() == AbstractInputData.Mode.TIME) {
			
			// Get the maximum speed
			int maxSpeedInt = data.getMaximumSpeed();
			double maxSpeed;
			
			// If there is no maximum speed, set it to 130
			// In both cases we need to cast it to a double
			if (maxSpeedInt == -1)
				maxSpeed = 130.0;
			else
				maxSpeed = (double)maxSpeedInt;
			
			// Put the speed in m/s
			maxSpeed /= 3.6;
			
			estimatedCost = label.getNode().getPoint().distanceTo(data.getDestination().getPoint())
								/ maxSpeed;
			
		}
		
		return estimatedCost;
    	
    }
    
    private LabelStar instantiateLabelStar(Node node, ShortestPathData data) {
    	
    	LabelStar label = new LabelStar();

		label.setNode(node);
		label.setId(node.getId());
		
		// Set the estimated cost to the destination
		label.setEstimatedCost(calculateEstimatedCostToDestination(data, label));
			
    	return label;
    	
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
		LabelStar[] labels = new LabelStar[nbNodes];
		
		// Array telling if the corresponding label (with the node id) has been initialized
		boolean[] labelIsInitialized = new boolean[nbNodes];
		
		// Setting all the labels to "not initialized"
		for(int i = 0; i < nbNodes; i++)
			labelIsInitialized[i] = false;
		
		// UPDATE :
		/*
		 * We don't initialize labels anymore!
		 * They will be initialized later...
		 * ... ONLY WHEN NEEDED
		 */
		//Initialize labels, but check if Nodes id are correct
//		for (int i=0; i<nbNodes ; i++) {
//			labels[i] = new LabelStar();
//			labels[i].setNode(graph.get(i));
//			labels[i].setId(i);
//			
//			// Set the estimated cost to the destination
//			labels[i].setEstimatedCost(calculateEstimatedCostToDestination(data, labels[i]));
//			
//		}
    	
		
		// Instantiating the label of the origin
		labels[data.getOrigin().getId()] = instantiateLabelStar(data.getOrigin(),data);
		labelIsInitialized[data.getOrigin().getId()] = true;
		
		// Instantiating the the label of the destination
		labels[data.getDestination().getId()] = instantiateLabelStar(data.getDestination(),data);
		labelIsInitialized[data.getDestination().getId()] = true;
		
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
				
				//DEBUG
//				if(nextNode.getId() == data.getDestination().getId()) {
//					System.out.println("Destination touchée!");
//				}
				
				// If the following node hasn't been marked...
				//if(!labels[nextNode.getId()].isMarked()) {
					
				notifyNodeReached(nextNode);
				
				// Instantiate the label of the next node if it hasn't been done
				if(labelIsInitialized[nextNode.getId()] == false) {
					labels[nextNode.getId()] = instantiateLabelStar(nextNode, data);
					labelIsInitialized[nextNode.getId()] = true;
				}
				
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
					
					//DEBUG
//					if(nextNode.getId() == data.getDestination().getId()) {
//						System.out.println("Destination INSEREE!");
//					}
					
				}
					
					
				
			}
			
		}
		
		//System.out.println("Fini!");
    	
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
