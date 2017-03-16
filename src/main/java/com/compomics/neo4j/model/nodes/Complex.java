package com.compomics.neo4j.model.nodes;

import java.io.Serializable;

/**
 * Created by demet on 12/19/2016.
 */
public class Complex implements Serializable{

    private static final long serialVersionUID = 4133258179319091230L;

    private String complexName;

    private String pubmedId;

    private String complexComment;

    private String cellLine;

    private String diseaseComment;

    private String subUnitComment;

    private String corumId;

    private String purificationMethod;

    private String synonymName;

    public Complex() {
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    public String getComplexComment() {
        return complexComment;
    }

    public void setComplexComment(String complexComment) {
        this.complexComment = complexComment;
    }

    public String getCellLine() {
        return cellLine;
    }

    public void setCellLine(String cellLine) {
        this.cellLine = cellLine;
    }

    public String getDiseaseComment() {
        return diseaseComment;
    }

    public void setDiseaseComment(String diseaseComment) {
        this.diseaseComment = diseaseComment;
    }

    public String getSubUnitComment() {
        return subUnitComment;
    }

    public void setSubUnitComment(String subUnitComment) {
        this.subUnitComment = subUnitComment;
    }

    public String getCorumId() {
        return corumId;
    }

    public void setCorumId(String corumId) {
        this.corumId = corumId;
    }

    public String getPurificationMethod() {
        return purificationMethod;
    }

    public void setPurificationMethod(String purificationMethod) {
        this.purificationMethod = purificationMethod;
    }

    public String getSynonymName() {
        return synonymName;
    }

    public void setSynonymName(String synonymName) {
        this.synonymName = synonymName;
    }
}
