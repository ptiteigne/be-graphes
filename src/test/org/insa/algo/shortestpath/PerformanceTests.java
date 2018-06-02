package org.insa.algo.shortestpath;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ThreadLocalRandom;

import org.insa.algo.AbstractInputData.Mode;
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
	private static String mapMidiPyrenees= "/home/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/midi-pyrenees.mapgr";
	// private final static String mapMidiPyrenees = "D:/Biblio/T�l�chargements/midi-pyrenees.mapgr";
	
	
	// Path to the node pairs set file
	private final static String nodePairsSetFile = "/home/genoud/Bureau/Desktop/NodePairsSet.csv";
	
	// Path to the csv length results file
	private final static String lengthPerfsFile = "/home/genoud/Bureau/BEGraphesLENGTHPerfs.csv";
	
	// Path to the csv time results file
	private final static String timePerfsFile = "/home/genoud/Bureau/BEGraphesTIMEPerfs.csv";
	
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
        	System.exit(1);
    	}

        System.out.println("Pairs Generated!");
        	
    }
    
    // Read pairs of node from the given file
    private static void loadTestBatchFromCSV(String csvFile) {
	    	
        String line = "";
        String cvsSplitBy = ";";
        
        BufferedReader br;
        FileReader fr;
        
        if(graph == null) {
        	System.out.println("You cannot load a test batch before loading a graph.");
        	return;
        }

        try {
        	
        	fr = new FileReader(csvFile);
        	br = new BufferedReader(fr);
            
            // Count number of lines (= number of node pairs) in the csv
            int nbNodePairs = 0;
            while (br.readLine() != null) nbNodePairs++;
            
            fr.close();
            br.close();
            
            // Instantiating the testBatch
            testBatch = new NodePair[nbNodePairs];
            
            // Getting back to the beginning of the file
            fr = new FileReader(csvFile);
            br = new BufferedReader(fr);
            
            // Index of the NodePair currently being saved
            int iNodePair = 0;
            
            // Fill the testBatch
            while ((line = br.readLine()) != null) {

	                // use semicolon as separator
	                String[] columns = line.split(cvsSplitBy);
	                
	                testBatch[iNodePair] = new NodePair(graph.get(Integer.parseInt(columns[0])),
	                									graph.get(Integer.parseInt(columns[1])));
	                
	                iNodePair++;

	            }

	        } 
        
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Exiting the program to avoid any more mistakes...");
            System.exit(3);
        }
        catch (NullPointerException e) {
        	e.printStackTrace();
        	System.exit(3);
        }
        
        System.out.println("Pairs loaded from csv file!");
	    
    }
    
    
    // Print the pairs of node loaded from the test batch
    private static void printLoadedNodePairs() {
    	
    	try {
    		for(int i=0; i<testBatch.length; i++) {
    			System.out.println(testBatch[i].getStartNode().getId() + " ; " + testBatch[i].getEndNode().getId()); 
    		}
    	}
    	catch (NullPointerException e) {
    		e.printStackTrace();
    	}
    	
    }
    
    private static AlgoResults getAlgoResults(AlgoChoice algo, NodePair nodePair, ArcInspector filter) {
    	
    	AlgoResults algoResults = null;
    	
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
		   return null;
	   
	   startTime = System.nanoTime();
	   
	   solution = algorithm.doRun();
	   
	   endTime = System.nanoTime();
	   
	   
	   if (data.getMode() == Mode.LENGTH)
		   algoResults = new AlgoResults(endTime - startTime, solution.getPath().getLength());
	   else if (data.getMode() == Mode.TIME)
		   algoResults = new AlgoResults(endTime - startTime, solution.getPath().getMinimumTravelTime());
	   
	   return algoResults;
	   
    	
    }
    
    // Write the performance tests into the csv file given to the method, with the filter given
    private static void makePerfomanceTests(String csvFile, ArcInspector filter) {

		try {
			
			// Initializing the file descriptor of the results
			FileWriter fos = new FileWriter(csvFile);
			PrintWriter dos = new PrintWriter(fos);
			
			if(!debug)
				dos.println("costDijsktra;ExecTimeDijkstra;costAStar;ExecTimeAStar");
			else
				dos.println("NodeOne;NodeTwo;costDijsktra;ExecTimeDijkstra;costAStar;ExecTimeAStar");
			
			AlgoResults dijkstraResults = null;
			AlgoResults aStarResults = null;
			
			double progressPerCent;
			
			// loop through all your data and print it to the file
			for (int i=0; i < testBatch.length; i++)
			{
				
				// Execute the DIJKSTRA algorithm
				dijkstraResults = getAlgoResults(AlgoChoice.Dijsktra, testBatch[i], filter);
				
				
				// Execute the ASTAR algorithm
				aStarResults = getAlgoResults(AlgoChoice.AStar, testBatch[i], filter);
				
				
				// Print (or not if !debug) the node pairs
				if(debug)
					dos.print(testBatch[i].getStartNode().getId() + ";" + testBatch[i].getEndNode().getId() + ";");
				
				// Print dijkstra results
				if(dijkstraResults == null)
					dos.print("X;X;");
				else
					dos.print(dijkstraResults.getCost() + ";" + dijkstraResults.getExecTime() + ";");
				
				// Print aStar results
				if(aStarResults == null)
					dos.print("X;X;");
				else
					dos.print(aStarResults.getCost() + ";" + aStarResults.getExecTime() + ";");
				
				// Next line
				dos.println();
				
				//Print progress to the screen every 10 algorithm execution
				if (i%10 == 0) {
					progressPerCent = ((double) i / (double) testBatch.length) * 100; 
					System.out.println(filter.toString() + " : " + progressPerCent + "%");
				}
				
			}
			
			dos.close();
			fos.close();
			
			System.out.println("Finished the tests!");
			
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
			System.out.println("Error reading graph file.");
    		System.out.println("File path to the graph is " + mapMidiPyrenees);
    		System.out.println("Are you sure?");
			System.exit(1);
		}
    	
    	filterLengthAndAllArcs = ArcInspectorFactory.getAllFilters().get(0);
        
        filterTimeAndCar = ArcInspectorFactory.getAllFilters().get(2);
    	
    	// generateTestBatch(numberOfPairsToTest);
        loadTestBatchFromCSV("/home/genoud/Bureau/NodePairsSet.csv");
        // printLoadedNodePairs();
    	
        makePerfomanceTests("/home/genoud/Bureau/LengthPerfs.csv", filterTimeAndCar );
    	
    }

}
