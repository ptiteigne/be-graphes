package org.insa.algo.shortestpath;

public class AlgoResults {
	
	private long execTime;
	private double cost;
	
	public AlgoResults(long execTime, double cost) {
		this.execTime = execTime;
		this.cost = cost;
	}
	
	public long getExecTime() {
		return this.execTime;
	}
	
	public double getCost() {
		return this.cost;
	}

}
