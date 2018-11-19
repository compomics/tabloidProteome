package com.compomics.neo4j.model.nodes;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by demet on 12/19/2016.
 */
public class Project implements Serializable {

    private static final long serialVersionUID = 4549615759659498054L;

    private String instruments;

    private String keywords;

    private String projectAccession;

    private String tissue;

    private String experimentType;

    private String tags;

    public Project() {
    }

    public String getInstruments() {
        return instruments;
    }

    public void setInstruments(String instruments) {
        this.instruments = instruments;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getProjectAccession() {
        return projectAccession;
    }

    public void setProjectAccession(String projectAccession) {
        this.projectAccession = projectAccession;
    }

    public String getTissue() {
        return tissue;
    }

    public void setTissue(String tissue) {
        this.tissue = tissue;
    }

    public String getExperimentType() {
        return experimentType;
    }

    public void setExperimentType(String experimentType) {
        this.experimentType = experimentType;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((projectAccession == null) ? 0 : projectAccession.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Project other = (Project) obj;
		if (projectAccession == null) {
			if (other.projectAccession != null)
				return false;
		} else if (!projectAccession.equals(other.projectAccession))
			return false;
		return true;
	}

	
}
