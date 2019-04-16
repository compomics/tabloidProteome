package com.compomics.neo4j.model.nodes;

import java.io.Serializable;

/**
 * Created by demet on 12/19/2016.
 */
public class Reactome implements Serializable {

    private static final long serialVersionUID = 2190691658950525996L;

    private String pathwayName;

    private String species;

    private String reactomeAccession;

    private String evidenceCode;

    private String reactomelink;

    public String getPathwayName() {
        return pathwayName;
    }

    public void setPathwayName(String pathwayName) {
        this.pathwayName = pathwayName;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getReactomeAccession() {
        return reactomeAccession;
    }

    public void setReactomeAccession(String reactomeAccession) {
        this.reactomeAccession = reactomeAccession;
    }

    public String getEvidenceCode() {
        return evidenceCode;
    }

    public void setEvidenceCode(String evidenceCode) {
        this.evidenceCode = evidenceCode;
    }

    public String getReactomelink() {
        return reactomelink;
    }

    public void setReactomelink(String reactomelink) {
        this.reactomelink = reactomelink;
    }
}
