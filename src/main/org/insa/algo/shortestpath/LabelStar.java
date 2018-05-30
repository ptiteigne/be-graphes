package org.insa.algo.shortestpath;

import org.insa.algo.AbstractInputData;
import org.insa.graph.Node;

public class LabelStar extends Label {
	
	private double estimatedCost;
	
	public LabelStar() {
		
		super();
		this.estimatedCost = Double.POSITIVE_INFINITY;
		
	}
	
	public void setEstimatedCost(double estimatedCost) {
		
		this.estimatedCost = estimatedCost;
		
	}
	
	public double getEstimatedCost() {
		return this.estimatedCost;
	}
	
	@Override
	public double getTotalCost() {
		return this.getCost() + this.estimatedCost;
	}
	
	@Override
	public int compareTo(Label o) {
		
		LabelStar oStar = (LabelStar)o;
		
		if(o == null)
			throw new NullPointerException();
		
		else if(this.getTotalCost() > oStar.getTotalCost())
			return 1;
		else if (this.getTotalCost() < oStar.getTotalCost())
			return -1;
		
		else {
			
			if(this.getEstimatedCost() > oStar.getEstimatedCost())
				return 1;
			else if (this.getEstimatedCost() < oStar.getEstimatedCost())
				return -1;
			
			else
				return 0;
			
		}
		
	}

}
