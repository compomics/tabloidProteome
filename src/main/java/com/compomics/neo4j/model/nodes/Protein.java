package com.compomics.neo4j.model.nodes;

import java.io.Serializable;

/**
 * Created by demet on 12/19/2016.
 */
public class Protein implements Serializable {

    private static final long serialVersionUID = -1879461966536472891L;

    private String geneName;

    private String species;

    private String uniprotEntryName;

    private String proteinName;

    private String length;

    private String uniprotAccession;

    private String uniprotStatus;

    public Protein() {
    }

    public String getGeneName() {
        return geneName;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
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

    public String getLenghth() {
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

}
