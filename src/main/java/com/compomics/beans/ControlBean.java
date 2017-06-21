package com.compomics.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.compomics.neo4j.database.Service;
import com.compomics.neo4j.model.dataTransferObjects.DiseaseDTO;
import com.compomics.neo4j.model.dataTransferObjects.PathwayDTO;
import com.compomics.neo4j.model.dataTransferObjects.ProteinDTO;

/**
 * Created by demet on 19/05/2017.
 */
@ManagedBean
@SessionScoped
public class ControlBean implements Serializable{

	private static final long serialVersionUID = -2282658692375654056L;
	
	private Service dbService;
	private List<ProteinDTO> proteinDTOs = new ArrayList<>();
	private List<PathwayDTO> pathwayDTOs = new ArrayList<>();
	private List<DiseaseDTO> diseaseDTOs = new ArrayList<>();
    private String gene1;
    private String gene2;
    
    public Service getDbService() {
		return dbService;
	}

	public void setDbService(Service dbService) {
		this.dbService = dbService;
	}

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

	public String getGene1() {
		return gene1;
	}

	public void setGene1(String gene1) {
		this.gene1 = gene1;
	}

	public String getGene2() {
		return gene2;
	}

	public void setGene2(String gene2) {
		this.gene2 = gene2;
	}

	@PostConstruct
    private void init(){
		dbService = new Service();
    }
	
	public void findProteins(){
		dbService.startSession();
		gene1 =  FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("gene").trim().split(",")[0].toUpperCase(); 
		if(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("gene").trim().split(",").length == 2){
			gene2 = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("gene").trim().split(",")[1].toUpperCase(); 
		}else{
			gene2 = "";
		}
    	proteinDTOs = dbService.findProteinsByGene(gene1, gene2);
    	for(int i=0; i<proteinDTOs.size(); i++){
    		if(proteinDTOs.get(i).getProtein1().getGeneNames().get(0).equals(gene1)){
    			java.util.Collections.swap(proteinDTOs, 0, i);
    		}
    	}
    	dbService.closeSession();
    }
	
	public void findPathwayDTOs(){
		dbService.startSession();
		String pathway = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("pathway").trim().toUpperCase(); 
		if(pathway.startsWith("R-HSA")){
			pathwayDTOs = dbService.findPathwayDTOs(pathway);
		}else{
			pathwayDTOs = dbService.findPathwayDTOs(".*"+pathway+".*");
		}
		dbService.closeSession();
	}
	
	public void findDiseaseDTOs(){
		dbService.startSession();
		String disease = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("disease").trim().toUpperCase(); 
		if(disease.startsWith("C") && disease.length()==8){
			diseaseDTOs = dbService.findDiseaseDTOs(disease);
		}else{
			diseaseDTOs = dbService.findDiseaseDTOs(".*"+disease+".*");
		}
		dbService.closeSession();
	}
}
