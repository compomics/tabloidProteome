package com.compomics.neo4j.model.relationshipTypes;

import java.io.Serializable;

/**
 * Created by demet on 12/19/2016.
 */
public class Associate implements Serializable{


    private static final long serialVersionUID = -6840672278650359662L;

    private String paralog;

    private Double jaccSimScore;

    private String interact;
    
    private String intact;
    
    private String bioGrid;

    private String commonAssayCount;

    private String commonProject;
    
    private String intactConfidence;
    
    private String intactDetection;
    
    private String intactInteractionType;

    public Associate() {
    }

    public String getParalog() {
        return paralog;
    }

    public void setParalog(String paralog) {
        this.paralog = paralog;
    }

    public Double getJaccSimScore() {
        return jaccSimScore;
    }

    public void setJaccSimScore(Double jaccSimScore) {
        this.jaccSimScore = jaccSimScore;
    }

    public String getInteract() {
        return interact;
    }

    public void setInteract(String interact) {
        this.interact = interact;
    }

    public String getCommonAssayCount() {
        return commonAssayCount;
    }

    public void setCommonAssayCount(String commonAssayCount) {
        this.commonAssayCount = commonAssayCount;
    }

    public String getCommonProject() {
        return commonProject;
    }

    public void setCommonProject(String commonProject) {
        this.commonProject = commonProject;
    }

	public String getIntactConfidence() {
		return intactConfidence;
	}

	public String getIntactDetection() {
		return intactDetection;
	}

	public String getIntactInteractionType() {
		return intactInteractionType;
	}

	public void setIntactConfidence(String intactConfidence) {
		this.intactConfidence = intactConfidence;
	}

	public void setIntactDetection(String interactionDetection) {
		this.intactDetection = interactionDetection;
	}

	public void setIntactInteractionType(String interactionType) {
		this.intactInteractionType = interactionType;
	}

	public String getIntact() {
		return intact;
	}

	public String getBioGrid() {
		return bioGrid;
	}

	public void setIntact(String intact) {
		this.intact = intact;
	}

	public void setBioGrid(String bioGrid) {
		this.bioGrid = bioGrid;
	} 
}
