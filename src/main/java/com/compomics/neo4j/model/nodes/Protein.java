package com.compomics.neo4j.model.nodes;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by demet on 12/19/2016.
 */
public class Protein implements Serializable {

    private static final long serialVersionUID = -1879461966536472891L;
    
    private List<String> geneNames;
    
    private List<String> geneIds;

    private String species;

    private String uniprotEntryName;

    private String proteinName;

    private String length;

    private String uniprotAccession;

    private String uniprotStatus;
    
    private String associationId;
    
    private int group;
    

    public Protein() {
    }

    public List<String> getGeneNames() {
        return geneNames;
    }

    public void setGeneNames(List<String> geneName) {
        this.geneNames = geneName;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getUniprotEntryName() {
        return uniprotEntryName;
    }

    public void setUniprotEntryName(String uniprotEntryName) {
        this.uniprotEntryName = uniprotEntryName;
    }

    public String getProteinName() {
        return proteinName;
    }

    public void setProteinName(String proteinName) {
        this.proteinName = proteinName;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getUniprotAccession() {
        return uniprotAccession;
    }

    public void setUniprotAccession(String uniprotAccession) {
        this.uniprotAccession = uniprotAccession;
    }

    public String getUniprotStatus() {
        return uniprotStatus;
    }

    public void setUniprotStatus(String uniprotStatus) {
        this.uniprotStatus = uniprotStatus;
    }

	public List<String> getGeneIds() {
		return geneIds;
	}

	public void setGeneIds(List<String> geneIds) {
		this.geneIds = geneIds;
	}

	public String getAssociationId() {
		return associationId;
	}

	public void setAssociationId(String associationId) {
		this.associationId = associationId;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uniprotAccession == null) ? 0 : uniprotAccession.hashCode());
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
		Protein other = (Protein) obj;
		if (uniprotAccession == null) {
			if (other.uniprotAccession != null)
				return false;
		} else if (!uniprotAccession.equals(other.uniprotAccession))
			return false;
		return true;
	}

}