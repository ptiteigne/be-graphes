package org.insa.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Class representing a path between nodes in a graph.
 * 
 * A path is represented as a list of {@link Arc} and not a list of {@link Node}
 * due to the multigraph nature of the considered graphs.
 *
 */
public class Path {

    /**
     * Create a new path that goes through the given list of nodes (in order),
     * choosing the fastest route if multiple are available.
     * 
     * @param graph Graph containing the nodes in the list.
     * @param nodes List of nodes to build the path.
     * 
     * @return A path that goes through the given list of nodes.
     * 
     * @throws IllegalArgumentException If the list of nodes is not valid, i.e. two
     *         consecutive nodes in the list are not connected in the graph.
     */
    public static Path createFastestPathFromNodes(Graph graph, List<Node> nodes)
            throws IllegalArgumentException {
        List<Arc> arcs = new ArrayList<Arc>();
        
        ListIterator<Node> iteNodes = nodes.listIterator();
        Node nodeUn;
        Node nodeDeux;
        
        // If there are no nodes, we just return an empty path
        try {
        	nodeDeux = iteNodes.next();
        }
        catch(Exception NoSuchElementException) {
        	return new Path(graph);
        }
        
        // If there is any node...
        while(iteNodes.hasNext()) {
        	nodeUn = nodeDeux;
        	
        	if(iteNodes.hasNext() == true) {
        		nodeDeux = iteNodes.next();
        		
        		// Arc List from nodeUn
        		List<Arc> arcsFromUn = nodeUn.getSuccessors();
        		// The best arc that will be selected in nodeUn
        		Arc bestArc = null;
        		
        		// Look for the best arc
        		for(Arc unArc : arcsFromUn) {
        			if(unArc.getDestination() == nodeDeux) {
        				if(bestArc == null)
        					bestArc = unArc;
        				else if (unArc.getMinimumTravelTime() < bestArc.getMinimumTravelTime())
        					bestArc = unArc;
        			}
        		}
        		
        		// If we found no arc, the nodes aren't connected so we throw an Exception
        		if (bestArc == null)
        			throw new IllegalArgumentException();
        		
        		arcs.add(bestArc);
        		
        	}
        		
        }
        
        return new Path(graph, arcs);
    }

    /**
     * Create a new path that goes through the given list of nodes (in order),
     * choosing the shortest route if multiple are available.
     * 
     * @param graph Graph containing the nodes in the list.
     * @param nodes List of nodes to build the path.
     * 
     * @return A path that goes through the given list of nodes.
     * 
     * @throws IllegalArgumentException If the list of nodes is not valid, i.e. two
     *         consecutive nodes in the list are not connected in the graph.
     * 
     */
    public static Path createShortestPathFromNodes(Graph graph, List<Node> nodes)
            throws IllegalArgumentException {
        List<Arc> arcs = new ArrayList<Arc>();
        
        ListIterator<Node> iteNodes = nodes.listIterator();
        Node nodeUn;
        Node nodeDeux;
        
        // If there are no nodes, we just return an empty path
        try {
        	nodeDeux = iteNodes.next();
        }
        catch(Exception NoSuchElementException) {
        	return new Path(graph);
        }
        
        // If there is any node...
        while(iteNodes.hasNext()) {
        	nodeUn = nodeDeux;
        	
        	if(iteNodes.hasNext() == true) {
        		nodeDeux = iteNodes.next();
        		
        		// Arc List from nodeUn
        		List<Arc> arcsFromUn = nodeUn.getSuccessors();
        		// The best arc that will be selected in nodeUn
        		Arc bestArc = null;
        		
        		// Look for the best arc
        		for(Arc unArc : arcsFromUn) {
        			if(unArc.getDestination() == nodeDeux) {
        				if(bestArc == null)
        					bestArc = unArc;
        				else if (unArc.getLength() < bestArc.getLength())
        					bestArc = unArc;
        			}
        		}
        		
        		// If we found no arc, the nodes aren't connected so we throw an Exception
        		if (bestArc == null)
        			throw new IllegalArgumentException();
        		
        		arcs.add(bestArc);
        		
        	}
        		
        }
        
        return new Path(graph, arcs);
    }

    /**
     * Concatenate the given paths.
     * 
     * @param paths Array of paths to concatenate.
     * 
     * @return Concatenated path.
     * 
     * @throws IllegalArgumentException if the paths cannot be concatenated (IDs of
     *         map do not match, or the end of a path is not the beginning of the
     *         next).
     */
    public static Path concatenate(Path... paths) throws IllegalArgumentException {
        if (paths.length == 0) {
            throw new IllegalArgumentException("Cannot concatenate an empty list of paths.");
        }
        final String mapId = paths[0].getGraph().getMapId();
        for (int i = 1; i < paths.length; ++i) {
            if (!paths[i].getGraph().getMapId().equals(mapId)) {
                throw new IllegalArgumentException(
                        "Cannot concatenate paths from different graphs.");
            }
        }
        ArrayList<Arc> arcs = new ArrayList<>();
        for (Path path: paths) {
            arcs.addAll(path.getArcs());
        }
        Path path = new Path(paths[0].getGraph(), arcs);
        if (!path.isValid()) {
            throw new IllegalArgumentException(
                    "Cannot concatenate paths that do not form a single path.");
        }
        return path;
    }

    // Graph containing this path.
    private final Graph graph;

    // Origin of the path
    private final Node origin;

    // List of arcs in this path.
    private final List<Arc> arcs;

    /**
     * Create an empty path corresponding to the given graph.
     * 
     * @param graph Graph containing the path.
     */
    public Path(Graph graph) {
        this.graph = graph;
        this.origin = null;
        this.arcs = new ArrayList<>();
    }

    /**
     * Create a new path containing a single node.
     * 
     * @param graph Graph containing the path.
     * @param node Single node of the path.
     */
    public Path(Graph graph, Node node) {
        this.graph = graph;
        this.origin = node;
        this.arcs = new ArrayList<>();
    }

    /**
     * Create a new path with the given list of arcs.
     * 
     * @param graph Graph containing the path.
     * @param arcs Arcs to construct the path.
     */
    public Path(Graph graph, List<Arc> arcs) {
        this.graph = graph;
        this.arcs = arcs;
        this.origin = arcs.size() > 0 ? arcs.get(0).getOrigin() : null;
    }

    /**
     * @return Graph containing the path.
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * @return First node of the path.
     */
    public Node getOrigin() {
        return origin;
    }

    /**
     * @return Last node of the path.
     */
    public Node getDestination() {
        return arcs.get(arcs.size() - 1).getDestination();
    }

    /**
     * @return List of arcs in the path.
     */
    public List<Arc> getArcs() {
        return Collections.unmodifiableList(arcs);
    }

    /**
     * Check if this path is empty (it does not contain any node).
     * 
     * @return true if this path is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.origin == null;
    }

    /**
     * Get the number of <b>nodes</b> in this path.
     * 
     * @return Number of nodes in this path.
     */
    public int size() {
        return isEmpty() ? 0 : 1 + this.arcs.size();
    }

    /**
     * Check if this path is valid.
     * 
     * A path is valid if any of the following is true:
     * <ul>
     * <li>it is empty;</li>
     * <li>it contains a single node (without arcs);</li>
     * <li>the first arc has for origin the origin of the path and, for two
     * consecutive arcs, the destination of the first one is the origin of the
     * second one.</li>
     * </ul>
     * 
     * @return true if the path is valid, false otherwise.
     */
    public boolean isValid() {
    	
    	List<Arc> arcs = this.getArcs();
        
    	// If path is empty, return true
    	if(this.isEmpty() == true)
    		return true;
    	
    	// If there is no arc and there is one node (origin == destination), return true
    	else if (arcs.isEmpty() && (this.origin == this.getDestination()))
    		return true;
    	
    	// If the origin of the first arc is indeed the origin of the path...
    	else if (arcs.get(0).getOrigin() == this.origin){
    		
    		Iterator<Arc> iteArcs = arcs.iterator();
    		Arc arcUn;
    		Arc arcDeux = iteArcs.next();
    		
    		// We check if every two consecutive arcs are valid
    		while(iteArcs.hasNext()) {
    			arcUn = arcDeux;
    			arcDeux = iteArcs.next();
    			
    			// If any two consecutive arcs are invalid, we return false
    			if(arcUn.getDestination() != arcDeux.getOrigin())
    				return false;
    			
    		}
    		
    		return true;    		
    		
    	}
    	else
    		return false;
    }

    /**
     * Compute the length of this path (in meters).
     * 
     * @return Total length of the path (in meters).
     * 
.
     */
    public float getLength() {
    	float result = 0 ;
    	for(Arc unarc : arcs)
    	{
    		result = result + unarc.getLength();
    	}
        return result;
    }

    /**
     * Compute the time required to travel this path if moving at the given speed.
     * 
     * @param speed Speed to compute the travel time.
     * 
     * @return Time (in seconds) required to travel this path at the given speed (in
     *         kilometers-per-hour).
     */
    public double getTravelTime(double speed) {
        
        return ((this.getLength()/1000)/speed)/3600;
        
    }

    /**
     * Compute the time to travel this path if moving at the maximum allowed speed
     * on every arc.
     * 
     * @return Minimum travel time to travel this path (in seconds).
     * 
     * 
     */
    public double getMinimumTravelTime() {
    	double result = 0 ;
    	for(Arc unarc : arcs)
    	{
    		result = result + unarc.getMinimumTravelTime();
    	}
        return result;
    }

}
