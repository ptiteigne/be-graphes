package org.insa.algo.shortestpath;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ThreadLocalRandom;

import org.insa.algo.ArcInspector;
import org.insa.algo.ArcInspectorFactory;
import org.insa.graph.Arc;
import org.insa.graph.Graph;
import org.insa.graph.Node;
import org.insa.graph.NodePair;
import org.insa.graph.io.BinaryGraphReader;
import org.insa.graph.io.GraphReader;

public class PerformanceTests {
	
	private enum AlgoChoice{
		Dijsktra,
		AStar
	}
	
	// Path(s) of the map(s) used for the tests
	//private static String mapMidiPyrenees= "/home/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/midi-pyrenees.mapgr";
	private final static String mapMidiPyrenees = "D:/Biblio/Téléchargements/midi-pyrenees.mapgr";
	
	
	// Path to the node pairs set file
	private final static String nodePairsSetFile = "C:/Users/j-640/Desktop/NodePairsSet.csv";
	
	// Path to the csv length results file
	private final static String lengthPerfsFile = "C:/Users/j-640/Desktop/BEGraphesLENGTHPerfs.csv";
	
	// Path to the csv time results file
	private final static String timePerfsFile = "C:/Users/j-640/Desktop/BEGraphesTIMEPerfs.csv";
	
	// Number of pairs of Node to test
	private final static int numberOfPairsToTest = 10;
	
	// Graph used for the tests
    private static Graph graph;

    // Boolean setting the need to print debug informations to the screen
    private static boolean debug = false;
    
    // List of nodes pair (to test)
    private static NodePair[] testBatch;
    
    // Arc filter (looking for minimum length, all arcs permitted)
    private static ArcInspector filterLengthAndAllArcs;
    
    // Arc filter (looking for minimum time, only cars)
    private static ArcInspector filterTimeAndCar;
    
    
    private static void initializeWithMidiPyrenees() throws IOException{
    	
		GraphReader reader = new BinaryGraphReader(
	             new DataInputStream(new BufferedInputStream(new FileInputStream(mapMidiPyrenees))));
	     
	    graph = reader.read();
    	
    }

    // Generate pairs of node according to the graph used and size given to the method
    private static void generateTestBatch(int size) {
    	
        // 
    	testBatch = new NodePair[size];
        
        System.out.println("Generating node pairs...");
        
        
        
        try {
        	// Tab delimited file will be written to data with the name tab-file.csv
        	FileWriter fos = new FileWriter(nodePairsSetFile);
        	PrintWriter dos = new PrintWriter(fos);
        	
        	
        	 for (int i = 0; i < size; ++i) {
             	
                 Node startNode;
                 Node endNode;
                 AStarAlgorithm testAStar;
                 
                 do {
                 	
                     startNode = graph.get(ThreadLocalRandom.current().nextInt(0, graph.size()));
                     endNode = graph.get(ThreadLocalRandom.current().nextInt(0, graph.size()));
                     testAStar = new AStarAlgorithm(new ShortestPathData(graph, startNode, endNode, ArcInspectorFactory.getAllFilters().get(0)));
                     
                 } while (!testAStar.doRun().isFeasible());
                 testBatch[i] = new NodePair(startNode, endNode);
                 
                 dos.print(startNode.getId() + ";");
                 dos.println(endNode.getId());
                 
                 if(debug) {
                    //System.out.println("Node pair: Start=" + testBatch[i].getStartNode() + " and End=" + testBatch[i].getEndNode());
                 	System.out.println(((double)i/(double)size) * 100 + "%");
                 }
                 
             }
        	 
	    	dos.close();
	    	fos.close();
	    	
    	} 
        
        catch (IOException e) {
        	System.out.println("Error Printing Tab Delimited File");
        	System.out.println("The file path is " + nodePairsSetFile);
        	System.out.println("Are you sure?");
    	}

        System.out.println("Pairs Generated!");
        	
    }
    
    private static long getExecutionTimeOfTheAlgorithm(AlgoChoice algo, NodePair nodePair, ArcInspector filter) {
    	
    	ShortestPathData data;
  	    
  	    ShortestPathAlgorithm algorithm = null;
  	    ShortestPathSolution solution;
  	    
  	    long startTime;
  	    long endTime;

  	   // Designing the data
  	   data = new ShortestPathData(graph, graph.get(nodePair.getStartNode().getId()), 
  			   										graph.get(nodePair.getEndNode().getId()), 
  			   										filter);
	    
	   if (algo == AlgoChoice.Dijsktra) {
		   algorithm = new DijkstraAlgorithm(data);
	   }
	   else if (algo == AlgoChoice.AStar)
		   algorithm = new AStarAlgorithm(data);
	   else
		   return -1;
	   
	   startTime = System.nanoTime();
	   
	   solution = algorithm.doRun();
	   
	   endTime = System.nanoTime();

	   return endTime - startTime;
    	
    }
    
    private static void makeLengthPerfomanceTests() {

		try {
			
			// Initializing the file descriptor of the results
			FileWriter fos = new FileWriter(lengthPerfsFile);
			PrintWriter dos = new PrintWriter(fos);
			dos.println("ExecTime;Dijkstra;AStar");
			
			long execTime;
			
			// loop through all your data and print it to the file
			for (int i=0; i < numberOfPairsToTest; i++)
			{
				
				//Print the number of the test
				dos.print(i + ";");
				
				//(We could also print the Ids of the nodes)
				
				// Execute the DIJKSTRA algorithm
				execTime = getExecutionTimeOfTheAlgorithm(AlgoChoice.Dijsktra, testBatch[i], filterLengthAndAllArcs);
				// Print the execution time in the file
				dos.print(execTime+";");
				
				// Execute the DIJKSTRA algorithm
				execTime = getExecutionTimeOfTheAlgorithm(AlgoChoice.AStar, testBatch[i], filterLengthAndAllArcs);
				// Print the execution time in the file
				dos.print(execTime);
				
				dos.println();
				
			}
			dos.close();
			fos.close();
		} 
		
		catch (IOException e) {
			System.out.println("Error Printing Tab Delimited File");
			System.out.println("The file path is " + lengthPerfsFile);
        	System.out.println("Are you sure?");
		}
   
    }
    
    public static void main(String args[]) {
    	
    	debug = true;
    	
    	
    	//Initialize the performance tests with MidiPyrenees
    	try {
			initializeWithMidiPyrenees();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Problem while loading the map.");
			return;
		}
    	
    	filterLengthAndAllArcs = ArcInspectorFactory.getAllFilters().get(0);
        
        filterTimeAndCar = ArcInspectorFactory.getAllFilters().get(2);
    	
    	generateTestBatch(numberOfPairsToTest);
    	
    	makeLengthPerfomanceTests();    	
    	
    }

}
