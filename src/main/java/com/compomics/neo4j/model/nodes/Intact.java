package com.compomics.neo4j.model.nodes;

import java.io.Serializable;

public class Intact implements Serializable {

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

    public void setIntactId(String intactId) {
        this.intactId = intactId;
    }

    public String getConfidenceValue() {
        return confidenceValue;
    }

    public void setConfidenceValue(String confidenceValue) {
        this.confidenceValue = confidenceValue;
    }

    public String getDetection() {
        return detection;
    }

    public void setDetection(String detection) {
        this.detection = detection;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(String interactionType) {
        this.interactionType = interactionType;
    }
}
