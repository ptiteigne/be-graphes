package org.insa.algo.shortestpath;

import java.util.Arrays;

import org.insa.graph.Arc;
import org.insa.graph.Graph;

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
        
		// Initialize array of labels.
		Label[] labels = new Label[nbNodes];
		
		// Set source cost to zero and insert it on the heap
		labels[data.getOrigin().getId()].setCost(0);

		// Notify observers about the first event (origin processed).
		notifyOriginProcessed(data.getOrigin());

		// Initialize array of predecessors.
		Arc[] predecessorArcs = new Arc[nbNodes];
        
        
		ShortestPathSolution solution = null;
        
        return solution;
    }

}
