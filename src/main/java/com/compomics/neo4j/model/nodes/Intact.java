package com.compomics.neo4j.model.nodes;

import java.io.Serializable;

public class Intact implements Serializable{

	private static final long serialVersionUID = 455932561039572361L;

	private String intactId;
    
    private String confidenceValue;
    
    private String detection;
    
    private String interactionType;

	public Intact() {
	}

	public String getIntactId() {
		return intactId;
	}

	public String getConfidenceValue() {
		return confidenceValue;
	}

	public String getDetection() {
		return detection;
	}

	public String getInteractionType() {
		return interactionType;
	}

	public void setIntactId(String intactId) {
		this.intactId = intactId;
	}

	public void setConfidenceValue(String confidenceValue) {
		this.confidenceValue = confidenceValue;
	}

	public void setDetection(String detection) {
		this.detection = detection;
	}

	public void setInteractionType(String interactionType) {
		this.interactionType = interactionType;
	}
    
    
}
