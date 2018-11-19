package com.compomics.neo4j.model.relationshipTypes;

import java.io.Serializable;
import java.util.List;

import com.compomics.neo4j.model.nodes.BioGrid;
import com.compomics.neo4j.model.nodes.Intact;


/**
 * Created by demet on 12/19/2016.
 */
public class Associate implements Serializable{


    private static final long serialVersionUID = -6840672278650359662L;

    private String paralog;

    private Double jaccSimScore;

    private String interact;
    
    private List<Intact> intact;
    
    private List<BioGrid> bioGrid;

    private String commonAssayCount;

    private String commonProject;
    
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

	public List<Intact> getIntact() {
		return intact;
	}

	public List<BioGrid> getBioGrid() {
		return bioGrid;
	}

	public void setIntact(List<Intact> intact) {
		this.intact = intact;
	}

	public void setBioGrid(List<BioGrid> bioGrid) {
		this.bioGrid = bioGrid;
	}

	
}
