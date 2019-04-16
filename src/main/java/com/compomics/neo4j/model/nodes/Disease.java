package com.compomics.neo4j.model.nodes;

import java.io.Serializable;

public class Disease implements Serializable {

    private static final long serialVersionUID = 5132682850178702654L;

    private String disgenetId;

    private String diseaseName;

    public String getDisgenetId() {
        return disgenetId;
    }

    public void setDisgenetId(String disgenetId) {
        this.disgenetId = disgenetId;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }
}
