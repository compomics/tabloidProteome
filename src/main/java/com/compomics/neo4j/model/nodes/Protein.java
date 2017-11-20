package com.compomics.neo4j.model.nodes;

import java.io.Serializable;
import java.util.List;

/**
 * Created by demet on 12/19/2016.
 */
public class Protein implements Serializable {

    private static final long serialVersionUID = -1879461966536472891L;

    private List<Object> geneNames;
    
    private List<Object> geneIds;

    private String species;

    private String uniprotEntryName;

    private String proteinName;

    private String length;

    private String uniprotAccession;

    private String uniprotStatus;
    
    private int group;

    public Protein() {
    }

    public List<Object> getGeneNames() {
        return geneNames;
    }

    public void setGeneNames(List<Object> geneName) {
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

	public List<Object> getGeneIds() {
		return geneIds;
	}

	public void setGeneIds(List<Object> geneIds) {
		this.geneIds = geneIds;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

}
