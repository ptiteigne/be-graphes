package org.insa.algo.shortestpath;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.insa.algo.ArcInspector;
import org.insa.algo.ArcInspectorFactory;
import org.insa.graph.Arc;
import org.insa.graph.Graph;
import org.insa.graph.Node;
import org.insa.graph.Path;
import org.insa.graph.RoadInformation;
import org.insa.graph.RoadInformation.RoadType;
import org.insa.graph.io.BinaryGraphReader;
import org.insa.graph.io.GraphReader;
import org.junit.BeforeClass;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AStarTest {
	

	 // Small graph use for tests
    private static Graph graph;
    
    // graph used for the map
    private static Graph graph_map;

    // List of nodes
    private static Node[] nodes;

    // List of arcs in the graph
    @SuppressWarnings("unused")
    private static Arc 	x1_x2, x1_x3,
    					x2_x4,x2_x5, x2_x6, 
    					x3_x1, x3_x2, x3_x6, 
    					x5_x3, x5_x4, x5_x6,
    					x6_x5;
    
    // Arc filter (looking for minimum length, all arcs permitted)
    private static ArcInspector filterLengthAndAllArcs;
    
    // Arc filter (looking for minimum time, only cars)
    private static ArcInspector filterTimeAndCar;

//    // Some paths...
//    private static Path emptyPath, singleNodePath, shortPath, longPath, loopPath, longLoopPath,
//            invalidPath;

    @BeforeClass
    public static void initAll() throws IOException {

        // 10 and 20 meters per seconds
        RoadInformation speed10 = new RoadInformation(RoadType.MOTORWAY, null, true, 36, "");
               

        // Create nodes
        nodes = new Node[6];
        for (int i = 0; i < nodes.length; ++i) {
            nodes[i] = new Node(i, null);
        }

        // Add arcs...
        x1_x2 = Node.linkNodes(nodes[0], nodes[1], 7, speed10, null);
        x1_x3 = Node.linkNodes(nodes[0], nodes[2], 8, speed10, null);
		x2_x4 = Node.linkNodes(nodes[1], nodes[3], 4, speed10, null);
		x2_x5 = Node.linkNodes(nodes[1], nodes[4], 1, speed10, null);
		x2_x6 = Node.linkNodes(nodes[1], nodes[5], 5, speed10, null);
		x3_x1 = Node.linkNodes(nodes[2], nodes[0], 7, speed10, null);
		x3_x2 = Node.linkNodes(nodes[2], nodes[1], 2, speed10, null);
		x3_x6 = Node.linkNodes(nodes[2], nodes[5], 2, speed10, null);
		x5_x3 = Node.linkNodes(nodes[4], nodes[2], 2, speed10, null);
		x5_x4 = Node.linkNodes(nodes[4], nodes[3], 2, speed10, null);
		x5_x6 = Node.linkNodes(nodes[4], nodes[5], 3, speed10, null); 
		x6_x5 = Node.linkNodes(nodes[5], nodes[4], 3, speed10, null);
        

        graph = new Graph("ID", "", Arrays.asList(nodes), null);
        
        
        filterLengthAndAllArcs = ArcInspectorFactory.getAllFilters().get(0);
        
        filterTimeAndCar = ArcInspectorFactory.getAllFilters().get(2);
        
        // Initialization for oracle test with map

        //String mapName = "/home/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/guadeloupe.mapgr";
        String mapName = "D:/Biblio/Téléchargements/guadeloupe.mapgr";
        
        GraphReader reader = new BinaryGraphReader(
                new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));
        
        graph_map = reader.read();


    }

    
    public static void displayDijsktraLengths() {
    	
    	ShortestPathData data;
	    
	    AStarAlgorithm aStar;
	    ShortestPathSolution solAStar;
	    
    //	BellmanFordAlgorithm bellman;
	//    ShortestPathSolution solBellman;
	    
	    System.out.print("\t| ");
	    
	    for(int i = 0; i < 6; i++) {
	    	
	    	System.out.print("x" + (i+1) + " \t| ");
	    	
	    }
	    
	    System.out.println();
	    
	    for(int i = 0; i < 6; i++) {
	    	
	    	System.out.print("x" + (i+1) + " \t| ");
	    	
	    	for (int j = 0; j < 6; j++) {
	    	
	    		
	    		if (i==j) {
	    			System.out.print("-\t| ");
	    			continue;
	    		}
	    		
		    	data = new ShortestPathData(graph, nodes[i], nodes[j], filterLengthAndAllArcs);
			    
			    aStar = new AStarAlgorithm(data);
			    solAStar = aStar.doRun();
			    
			    if (!solAStar.isFeasible())
			    	System.out.print("00\t| ");
			    else	    
			    	System.out.print((int)solAStar.getPath().getLength() + "\t| ");
			    
	    	}
	    	
	    	System.out.println();
		    
	    }
    	
    }
    
    
    @Test
    public void verifyAStarLengthWhenOriginIsDestination() throws IOException {
    	
    	ShortestPathData data;
  	    
  	    AStarAlgorithm aStar;
  	    ShortestPathSolution solAStar;
  	    
      	BellmanFordAlgorithm bellman;
  	    ShortestPathSolution solBellman;
  	    

  	   // Test destination = origin 
  	   data = new ShortestPathData(graph_map, graph_map.get(7623), graph_map.get(7623), filterLengthAndAllArcs);
	    
	   aStar = new AStarAlgorithm(data);
	   solAStar = aStar.doRun();
	    
	   bellman = new BellmanFordAlgorithm(data);
	   solBellman = bellman.doRun();
	   
	    if (!solAStar.isFeasible() || !solBellman.isFeasible()) {
	    	assertEquals(solAStar.isFeasible(), solBellman.isFeasible());
	    }
	    
	    else {
		    assertEquals(solAStar.getPath().getLength(), solBellman.getPath().getLength(),0.1);
	    }
  	    
    	
    }
    
    @Test
    public void verifyAStarLengthWithALongPath() throws IOException {
    	
    	ShortestPathData data;
  	    
  	    AStarAlgorithm aStar;
  	    ShortestPathSolution solAStar;
  	    
      	BellmanFordAlgorithm bellman;
  	    ShortestPathSolution solBellman;
  	    
  	   data = new ShortestPathData(graph_map, graph_map.get(10546), graph_map.get(12368), filterLengthAndAllArcs);
	    
	   aStar = new AStarAlgorithm(data);
	   solAStar = aStar.doRun();
	    
	   bellman = new BellmanFordAlgorithm(data);
	   solBellman = bellman.doRun();
	   
	    if (!solAStar.isFeasible() || !solBellman.isFeasible()) {
	    	assertEquals(solAStar.isFeasible(), solBellman.isFeasible());
	    }
	    
	    else {
		    assertEquals(solAStar.getPath().getLength(), solBellman.getPath().getLength(),0.1);
	    }
	    
    }
    
    @Test
    public void verifyAStarLengthWithAMediumPath() throws IOException {
    	
    	ShortestPathData data;
  	    
  	    AStarAlgorithm aStar;
  	    ShortestPathSolution solAStar;
  	    
      	BellmanFordAlgorithm bellman;
  	    ShortestPathSolution solBellman;
  	    
  	   data = new ShortestPathData(graph_map, graph_map.get(21851), graph_map.get(14918), filterLengthAndAllArcs);
	    
	   aStar = new AStarAlgorithm(data);
	   solAStar = aStar.doRun();
	    
	   bellman = new BellmanFordAlgorithm(data);
	   solBellman = bellman.doRun();
	   
	    if (!solAStar.isFeasible() || !solBellman.isFeasible()) {
	    	assertEquals(solAStar.isFeasible(), solBellman.isFeasible());
	    }
	    
	    else {
		    assertEquals(solAStar.getPath().getLength(), solBellman.getPath().getLength(),0.1);
	    }
	    
    }
    
    @Test
    public void verifyAStarLengthWithAShortPath() throws IOException {
    	
    	ShortestPathData data;
  	    
  	    AStarAlgorithm aStar;
  	    ShortestPathSolution solAStar;
  	    
      	BellmanFordAlgorithm bellman;
  	    ShortestPathSolution solBellman;
  	    
  	    
  	   data = new ShortestPathData(graph_map, graph_map.get(12256), graph_map.get(8107), filterLengthAndAllArcs);
	    
	   aStar = new AStarAlgorithm(data);
	   solAStar = aStar.doRun();
	    
	   bellman = new BellmanFordAlgorithm(data);
	   solBellman = bellman.doRun();
	   
	    if (!solAStar.isFeasible() || !solBellman.isFeasible()) {
	    	assertEquals(solAStar.isFeasible(), solBellman.isFeasible());
	    }
	    
	    else {
		    assertEquals(solAStar.getPath().getLength(), solBellman.getPath().getLength(),0.1);
	    }
	    
    }
    
    @Test
    public void verifyAStarLengthImpossiblePath() throws IOException {
    	
    	ShortestPathData data;
  	    
  	    AStarAlgorithm aStar;
  	    ShortestPathSolution solAStar;
  	    
      	BellmanFordAlgorithm bellman;
  	    ShortestPathSolution solBellman;
  	    

  	   data = new ShortestPathData(graph_map, graph_map.get(15656), graph_map.get(15046), filterLengthAndAllArcs);
	    
	   aStar = new AStarAlgorithm(data);
	   solAStar = aStar.doRun();
	    
	   bellman = new BellmanFordAlgorithm(data);
	   solBellman = bellman.doRun();
	   
	    if (!solAStar.isFeasible() || !solBellman.isFeasible()) {
	    	assertEquals(solAStar.isFeasible(), solBellman.isFeasible());
	    }
	    
	    else {
		    assertEquals(solAStar.getPath().getLength(), solBellman.getPath().getLength(),0.1);
	    }
	    
    }
    
    @Test
    public void verifyAStarTimeWhenOriginIsDestination() throws IOException {
    	
    	ShortestPathData data;
  	    
  	    AStarAlgorithm aStar;
  	    ShortestPathSolution solAStar;
  	    
      	BellmanFordAlgorithm bellman;
  	    ShortestPathSolution solBellman;
  	    

  	   data = new ShortestPathData(graph_map, graph_map.get(7623), graph_map.get(7623), filterTimeAndCar);
	    
	   aStar = new AStarAlgorithm(data);
	   solAStar = aStar.doRun();
	    
	   bellman = new BellmanFordAlgorithm(data);
	   solBellman = bellman.doRun();
	   
	    if (!solAStar.isFeasible() || !solBellman.isFeasible()) {
	    	assertEquals(solAStar.isFeasible(), solBellman.isFeasible());
	    }
	    
	    else {
		    assertEquals(solAStar.getPath().getMinimumTravelTime(), solBellman.getPath().getMinimumTravelTime(),0.1);
	    }
  	    
    	
    }
    
    @Test
    public void verifyAStarMinTimeWithALongPath() throws IOException {
    	
    	ShortestPathData data;
  	    
  	    AStarAlgorithm aStar;
  	    ShortestPathSolution solAStar;
  	    
      	BellmanFordAlgorithm bellman;
  	    ShortestPathSolution solBellman;
  	    

  	   data = new ShortestPathData(graph_map, graph_map.get(10546), graph_map.get(12368), filterTimeAndCar);
	    
	   aStar = new AStarAlgorithm(data);
	   solAStar = aStar.doRun();
	    
	   bellman = new BellmanFordAlgorithm(data);
	   solBellman = bellman.doRun();
	   
	    if (!solAStar.isFeasible() || !solBellman.isFeasible()) {
	    	assertEquals(solAStar.isFeasible(), solBellman.isFeasible());
	    }
	    
	    else {
		    assertEquals(solAStar.getPath().getMinimumTravelTime(), solBellman.getPath().getMinimumTravelTime(),0.1);
	    }
	    
    }
    
    @Test
    public void verifyAStarMinTimeWithAMediumPath() throws IOException {
    	
    	ShortestPathData data;
  	    
  	    AStarAlgorithm aStar;
  	    ShortestPathSolution solAStar;
  	    
      	BellmanFordAlgorithm bellman;
  	    ShortestPathSolution solBellman;
  	    

  	   data = new ShortestPathData(graph_map, graph_map.get(21851), graph_map.get(14918), filterTimeAndCar);
	    
	   aStar = new AStarAlgorithm(data);
	   solAStar = aStar.doRun();
	    
	   bellman = new BellmanFordAlgorithm(data);
	   solBellman = bellman.doRun();
	   
	    if (!solAStar.isFeasible() || !solBellman.isFeasible()) {
	    	assertEquals(solAStar.isFeasible(), solBellman.isFeasible());
	    }
	    
	    else {
		    assertEquals(solAStar.getPath().getMinimumTravelTime(), solBellman.getPath().getMinimumTravelTime(),0.1);
	    }
	    
    }
    
    @Test
    public void verifyAStarMinTimeWithAShortPath() throws IOException {
    	
    	ShortestPathData data;
  	    
  	    AStarAlgorithm aStar;
  	    ShortestPathSolution solAStar;
  	    
      	BellmanFordAlgorithm bellman;
  	    ShortestPathSolution solBellman;
  	    

  	   data = new ShortestPathData(graph_map, graph_map.get(12256), graph_map.get(8107), filterTimeAndCar);
	    
	   aStar = new AStarAlgorithm(data);
	   solAStar = aStar.doRun();
	    
	   bellman = new BellmanFordAlgorithm(data);
	   solBellman = bellman.doRun();
	   
	    if (!solAStar.isFeasible() || !solBellman.isFeasible()) {
	    	assertEquals(solAStar.isFeasible(), solBellman.isFeasible());
	    }
	    
	    else {
		    assertEquals(solAStar.getPath().getMinimumTravelTime(), solBellman.getPath().getMinimumTravelTime(),0.1);
	    }
	    
    }
    
    @Test
    public void verifyAStarMinTimeImpossiblePath() throws IOException {
    	
    	ShortestPathData data;
  	    
  	    AStarAlgorithm aStar;
  	    ShortestPathSolution solAStar;
  	    
      	BellmanFordAlgorithm bellman;
  	    ShortestPathSolution solBellman;
  	    

  	   data = new ShortestPathData(graph_map, graph_map.get(15656), graph_map.get(15046), filterTimeAndCar);
	    
	   aStar = new AStarAlgorithm(data);
	   solAStar = aStar.doRun();
	    
	   bellman = new BellmanFordAlgorithm(data);
	   solBellman = bellman.doRun();
	   
	    if (!solAStar.isFeasible() || !solBellman.isFeasible()) {
	    	assertEquals(solAStar.isFeasible(), solBellman.isFeasible());
	    }
	    
	    else {
		    assertEquals(solAStar.getPath().getMinimumTravelTime(), solBellman.getPath().getMinimumTravelTime(),0.1);
	    }
	    
    }
    
    @Test
    public void testwithoutOracleTriangularInequality() throws IOException {
    	
    	//we imagine a "triangle path" ABC with AB as hypotenuse, we check that AB =< AC + BC 
    	
    	ShortestPathData dataAB, dataAC, dataCB;
  	    
  	    AStarAlgorithm aStarAB, aStarAC, aStarCB;
  	    ShortestPathSolution solAStarAB, solAStarAC, solAStarCB;
  	    
  	    
  	   dataAB = new ShortestPathData(graph_map, graph_map.get(19208), graph_map.get(12304), filterLengthAndAllArcs);
	    
	   aStarAB = new AStarAlgorithm(dataAB);
	   solAStarAB = aStarAB.doRun();
	   
	   
	   dataAC = new ShortestPathData(graph_map, graph_map.get(19208), graph_map.get(9918), filterLengthAndAllArcs); 
	   aStarAC = new AStarAlgorithm(dataAC);
	   solAStarAC = aStarAC.doRun();
	   
	   
	   dataCB = new ShortestPathData(graph_map, graph_map.get(9918), graph_map.get(12304), filterLengthAndAllArcs); 
	   aStarCB = new AStarAlgorithm(dataCB);
	   solAStarCB = aStarCB.doRun();
	    
	   // if AB is not feasible,we test if one of AC or BC is not feasible
	    if (!solAStarAB.isFeasible() || !solAStarAC.isFeasible() || !solAStarCB.isFeasible()) {
	    	assertEquals(solAStarAB.isFeasible(), solAStarCB.isFeasible() && solAStarCB.isFeasible());
	    }
	    
	    else {
		    assertTrue(solAStarAB.getPath().getLength() <=(solAStarAC.getPath().getLength()+solAStarCB.getPath().getLength()));
	    }
    	
    }

}
