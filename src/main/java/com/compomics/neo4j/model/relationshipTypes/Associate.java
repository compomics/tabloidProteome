package com.compomics.neo4j.model.relationshipTypes;

import java.io.Serializable;
import java.math.BigDecimal;

import org.neo4j.driver.internal.value.FloatValue;

/**
 * Created by demet on 12/19/2016.
 */
public class Associate implements Serializable{


    private static final long serialVersionUID = -6840672278650359662L;

    private String paralog;

    private Double jaccSimScore;

    private String interact;

    private String commonAssayCount;

    private String commonProject;
    
    private String intactConfidence;
    
    private String interactionDetection;
    
    private String interactionType;

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

	public String getInteractionDetection() {
		return interactionDetection;
	}

	public String getInteractionType() {
		return interactionType;
	}

	public void setIntactConfidence(String intactConfidence) {
		this.intactConfidence = intactConfidence;
	}

	public void setInteractionDetection(String interactionDetection) {
		this.interactionDetection = interactionDetection;
	}

	public void setInteractionType(String interactionType) {
		this.interactionType = interactionType;
	}
    
    
}
