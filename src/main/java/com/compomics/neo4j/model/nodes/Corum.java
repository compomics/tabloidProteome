package com.compomics.neo4j.model.nodes;

import java.io.Serializable;

/**
 * Created by demet on 12/19/2016.
 */
public class Corum implements Serializable{

    private static final long serialVersionUID = 7947668784255424910L;

    private String complexName;

    private int pubmedId;

    private String complexComment;

    private String cellLine;

    private String diseaseComment;

    private String subUnitComment;

    private int corumId;

    private String purificationMethod;

    private String synonymName;

    public Corum() {
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public int getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(int pubmedId) {
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

    public int getCorumId() {
        return corumId;
    }

    public void setCorumId(int corumId) {
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
