package com.compomics.neo4j.model.nodes;

import java.io.Serializable;

/**
 * Created by demet on 12/19/2016.
 */
public class Assay implements Serializable{

    private static final long serialVersionUID = 5092894809184023398L;

    private String assayAccession;

    private String tissue;

    public Assay() {
    }

    public String getAssayAccession() {
        return assayAccession;
    }

    public void setAssayAccession(String assayAccession) {
        this.assayAccession = assayAccession;
    }

    public String getTissue() {
        return tissue;
    }

    public void setTissue(String tissue) {
        this.tissue = tissue;
    }
}
