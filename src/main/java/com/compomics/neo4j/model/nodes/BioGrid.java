package com.compomics.neo4j.model.nodes;

import java.io.Serializable;

public class BioGrid implements Serializable{

	private static final long serialVersionUID = 4312534717290370998L;
	
	private String biogridId;
	
	private String experimentName;
	
	private String experimentType;
	
	private String modification;
	
	private double score;
	
	private String throughput;

	public BioGrid() {
	}

	public String getBiogridId() {
		return biogridId;
	}

	public String getExperimentName() {
		return experimentName;
	}

	public String getExperimentType() {
		return experimentType;
	}

	public String getModification() {
		return modification;
	}

	public double getScore() {
		return score;
	}

	public String getThroughput() {
		return throughput;
	}

	public void setBiogridId(String biogridId) {
		this.biogridId = biogridId;
	}

	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}

	public void setExperimentType(String experimentType) {
		this.experimentType = experimentType;
	}

	public void setModification(String modification) {
		this.modification = modification;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public void setThroughput(String throughput) {
		this.throughput = throughput;
	}
	
	

}
