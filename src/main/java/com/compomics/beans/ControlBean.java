package com.compomics.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import com.compomics.neo4j.database.Service;
import com.compomics.neo4j.model.dataTransferObjects.DiseaseDTO;
import com.compomics.neo4j.model.dataTransferObjects.PathwayDTO;
import com.compomics.neo4j.model.dataTransferObjects.ProteinDTO;

/**
 * Created by demet on 19/05/2017.
 */
@ManagedBean
@SessionScoped
public class ControlBean implements Serializable {

    private static final long serialVersionUID = -2282658692375654056L;

    private Service dbService;
    private List<ProteinDTO> proteinDTOs = new ArrayList<>();
    private List<PathwayDTO> pathwayDTOs = new ArrayList<>();
    private List<DiseaseDTO> diseaseDTOs = new ArrayList<>();
    private String gene1;
    private String gene2;
    private String species;

    public List<ProteinDTO> getProteinDTOs() {
        return proteinDTOs;
    }

    public void setProteinDTOs(List<ProteinDTO> proteinDTOs) {
        this.proteinDTOs = proteinDTOs;
    }

    public List<PathwayDTO> getPathwayDTOs() {
        return pathwayDTOs;
    }

    public void setPathwayDTOs(List<PathwayDTO> pathwayDTOs) {
        this.pathwayDTOs = pathwayDTOs;
    }

    public List<DiseaseDTO> getDiseaseDTOs() {
        return diseaseDTOs;
    }

    public void setDiseaseDTOs(List<DiseaseDTO> diseaseDTOs) {
        this.diseaseDTOs = diseaseDTOs;
    }

    @PostConstruct
    private void init() {
        dbService = new Service();
    }

    public void findProteinsByGene() {
        species = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("species");
        gene1 = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("gene").trim().split(",")[0].toUpperCase();
        proteinDTOs.clear();
        if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("gene").trim().split(",").length == 2) {
            gene2 = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("gene").trim().split(",")[1].toUpperCase();
        } else {
            gene2 = "";
        }
        proteinDTOs = dbService.findProteinsByGene(gene1, gene2, species);
        // 	for(int i=0; i<proteinDTOs.size(); i++){
        //		if(proteinDTOs.get(i).getProtein1().getGeneNames().get(0).equals(gene1)){
        //			java.util.Collections.swap(proteinDTOs, 0, i);
        // 		}
        //	}
    }

    public void findProteinsByGenes() {
        species = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("species");
        String geneArray = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("array1");
        proteinDTOs.clear();

        proteinDTOs = dbService.findProteinsByGenes(Arrays.asList(geneArray.split("\\s*,\\s*")), species);
    }

    public void findProteinsByName() {
        String proteinName1;

        if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("protein1").toUpperCase().split("[-\\s]")
                [FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("protein1").toUpperCase().split("[-\\s]").length - 1].
                equals("PROTEIN")) {
            proteinName1 = String.join(".*", Arrays.copyOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("protein1").toUpperCase().split("[-\\s]"),
                    FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("protein1").toUpperCase().split("[-\\s]").length - 1));

        } else {
            proteinName1 = String.join(".*", FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("protein1").toUpperCase().split("[-\\s]"));
        }

        proteinName1 = "(?i).*" + proteinName1 + ".*";
        String proteinName2 = "";
        proteinDTOs.clear();
        double jaccScore = 0;
        if (!FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("protein2").equals("")) {

            if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("protein2").toUpperCase().split("[-\\s]")
                    [FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("protein2").toUpperCase().split("[-\\s]").length - 1].
                    equals("PROTEIN")) {
                proteinName2 = String.join(".*", Arrays.copyOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("protein2").toUpperCase().split("[-\\s]"),
                        FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("protein2").toUpperCase().split("[-\\s]").length - 1));

            } else {
                proteinName2 = String.join(".*", FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("protein2").toUpperCase().split("[-\\s]"));
            }
            proteinName2 = "(?i).*" + proteinName2 + ".*";
        }
        if (!FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jacc").equals("")) {
            jaccScore = Double.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jacc"));
        }

        species = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("species");
        proteinDTOs = dbService.findProteinsByName(proteinName1, proteinName2, jaccScore, species);
    }

    public void findPathwayDTOs() {
        String pathway = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("pathway").trim().toUpperCase();
        if (pathway.startsWith("R-HSA")) {
            pathwayDTOs = dbService.findPathwayDTOs(pathway);
        } else {
            if (pathway.split(" ")[pathway.split(" ").length - 1].equals("PATHWAY")) {
                pathway = StringUtils.join(Arrays.copyOf(pathway.split(" "), pathway.split(" ").length - 1), " ");
            }
            pathwayDTOs = dbService.findPathwayDTOs("(?i).*" + pathway + ".*");
        }
    }

    public void findDiseaseDTOs() {
        String disease = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("disease").trim().toUpperCase();

        if (disease.startsWith("C") && disease.length() == 8) {
            diseaseDTOs = dbService.findDiseaseDTOs(disease, 0.4);
        } else {
            if (disease.split(" ")[disease.split(" ").length - 1].equals("DISEASE")) {
                disease = StringUtils.join(Arrays.copyOf(disease.split(" "), disease.split(" ").length - 1), " ");
            }
            diseaseDTOs = dbService.findDiseaseDTOs(".*" + disease + ".*", 0.4);
        }

        diseaseDTOs.sort((o1, o2) -> Integer.compare(o2.getProteinDTOs().size(), o1.getProteinDTOs().size()));
    }

    public void findProteinDTOsByTissue() {
        String tissue = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("tissue");
        Double jaccScore = 0.0;
        if (!FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jacc").equals("")) {
            jaccScore = Double.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jacc"));
        }
        proteinDTOs = dbService.getProteinDTOsByTissue(tissue, jaccScore);
    }
}
