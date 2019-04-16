package com.compomics.neo4j.model.dataTransferObjects;

import java.io.Serializable;
import java.util.List;

import com.compomics.neo4j.model.nodes.Protein;


/**
 * Created by demet on 12/19/2016.
 */
public class GraphDTO implements Serializable {

    private static final long serialVersionUID = -1191827964699580119L;

    private List<Protein> proteins;

    private List<LinkDTO> links;

    public GraphDTO() {
    }

    public List<Protein> getProteins() {
        return proteins;
    }

    public List<LinkDTO> getLinks() {
        return links;
    }

    public void setProteins(List<Protein> proteins) {
        this.proteins = proteins;
    }

    public void setLinks(List<LinkDTO> links) {
        this.links = links;
    }

}
