package com.compomics.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.compomics.neo4j.database.Service;
import com.compomics.neo4j.model.dataTransferObjects.ProteinDTO;
import com.compomics.neo4j.model.nodes.Complex;
import com.compomics.neo4j.model.nodes.Go;
import com.compomics.neo4j.model.nodes.PathWay;
import com.compomics.neo4j.model.nodes.Project;
import com.compomics.neo4j.model.nodes.Protein;
import com.google.gson.Gson;

/**
 * Created by demet on 12/12/2016.
 */
@ManagedBean
@SessionScoped
public class GraphDbManagedBean implements Serializable{

    private static final long serialVersionUID = 9222685086844376613L;
    
    private List<ProteinDTO> proteinDTOS = new ArrayList<>();
    private List<String> associations = new ArrayList<>();
    private List<String> commonProjects = new ArrayList<>();
    private List<String> pathways = new ArrayList<>();
    private List<String> complexes = new ArrayList<>();
    private List<String> mfs = new ArrayList<>();
    private List<String> bps = new ArrayList<>();
    private List<String> ccs = new ArrayList<>();
    private Service dbService;
    private String accession1;
    private String accession2;
    private String selectionType;
    
    private static final String SEPERATOR = "*";
    
    // queries
    private static final String SINGLE_PROTEIN_QUERY = "singleProteinSearch";
    private static final String DOUBLE_PROTEIN_QUERY = "doubleProteinSearch";

    @ManagedProperty(value="#{navigationBean}")
    private NavigationBean navigationBean;
    
    @ManagedProperty(value="#{visualisationBean}")
    private VisualisationBean visualisationBean;

    public List<ProteinDTO> getProteinDTOS() {
        return proteinDTOS;
    }

	public String getAccession1() {
        return accession1;
    }

    public String getAccession2() {
        return accession2;
    }

    public void setAccession1(String accession1) {
		this.accession1 = accession1;
	}

	public void setAccession2(String accession2) {
		this.accession2 = accession2;
	}

	public String getSelectionType() {
		return selectionType;
	}

	public void setSelectionType(String selectionType) {
		this.selectionType = selectionType;
	}

	public NavigationBean getNavigationBean() {
		return navigationBean;
	}

	public void setNavigationBean(NavigationBean navigationBean) {
		this.navigationBean = navigationBean;
	}

	public Service getDbService() {
		return dbService;
	}

	public VisualisationBean getVisualisationBean() {
		return visualisationBean;
	}

	public void setVisualisationBean(VisualisationBean visualisationBean) {
		this.visualisationBean = visualisationBean;
	}


	public List<String> getAssociations() {
		return associations;
	}

	public List<String> getCommonProjects() {
		return commonProjects;
	}

	public List<String> getPathways() {
		return pathways;
	}

	public List<String> getComplexes() {
		return complexes;
	}

	public List<String> getMfs() {
		return mfs;
	}

	public List<String> getBps() {
		return bps;
	}

	public List<String> getCcs() {
		return ccs;
	}

	@PostConstruct
    private void init(){
		dbService = new Service();
    }

	public void load(){
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> requestParams = context.getExternalContext().getRequestParameterMap();
		if(requestParams.containsKey("accession")){
			if(requestParams.get("accession")!=null && !requestParams.get("accession").equals("")){
				accession1 = requestParams.get("accession");
				accession2 = null;
				setSelectionType("single");
				getProteinDTOs();
			}
		}else if(requestParams.containsKey("accession1") && requestParams.containsKey("accession2")){
			if(requestParams.get("accession1")!=null && !requestParams.get("accession1").equals("") && requestParams.get("accession2")!=null && !requestParams.get("accession2").equals(""))
			{
				accession1 = requestParams.get("accession1");
				accession2 = requestParams.get("accession2");
				setSelectionType("double");
				getProteinDTOs();
			}
		}
	}
	
    public void getProteinDTOs(){
    	proteinDTOS.clear();
    	FacesMessage msg = null;
        if(selectionType.equals("single")){
        	if(accession1 == null || accession1.equals("")){
        		msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Accession field cannot be empty.");
        		FacesContext.getCurrentInstance().addMessage(null, msg);
        	}else{
        		getSingleProteinDTOs();
        		visualisationBean.load(this);
        	}
        }else if(selectionType.equals("double")){
        	if(accession1 == null || accession1.equals("")){
        		msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Accession 1 field cannot be empty.");
        		FacesContext.getCurrentInstance().addMessage(null, msg);  
        	}else if(accession2 == null || accession2.equals("")){
        		msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Accession 2 field cannot be empty.");
        		FacesContext.getCurrentInstance().addMessage(null, msg);  
        	}else{
        		getDoubleProteinDTOs();
        		visualisationBean.load(this);
        	}	
        }
    }
    
    public void getSingleProteinDTOs(){

        proteinDTOS = dbService.getProteinDTOList(SINGLE_PROTEIN_QUERY, accession1, accession2);
        setRelations();
    }
    
    public void getDoubleProteinDTOs(){

        proteinDTOS = dbService.getProteinDTOList(DOUBLE_PROTEIN_QUERY, accession1, accession2);
        setRelations();
    }

    public void setRelations(){
    	clearRelations();
    	for(ProteinDTO proteinDTO : proteinDTOS){
    		StringBuilder associateSB = new StringBuilder();
    		associateSB.append(proteinDTO.getAssociate().getIntactConfidence()).append(SEPERATOR).append(proteinDTO.getAssociate().getInteractionDetection())
    		.append(SEPERATOR).append(proteinDTO.getAssociate().getInteractionType());

    		associations.add(associateSB.toString());
    		
    		StringBuilder prjSB = new StringBuilder();
    		for(Project project : proteinDTO.getProjects()){
    			prjSB.append(project.getProjectAccession()).append(SEPERATOR).append(project.getKeywords()).append(SEPERATOR).append( project.getTissue())
    				.append(SEPERATOR).append(project.getTags()).append(SEPERATOR);
    		}
    		if(prjSB.length()>0){prjSB.setLength(prjSB.length() - 1);}
    		
    		commonProjects.add(prjSB.toString());
    		
    		StringBuilder pthwySB = new StringBuilder();
    		for(PathWay pathway : proteinDTO.getPathWays()){
    			pthwySB.append(pathway.getReactomeAccession()).append(SEPERATOR).append(pathway.getPathwayName()).append(SEPERATOR).append(pathway.getEvidenceCode())
					.append(SEPERATOR);
    		}
    		if(pthwySB.length()>0){pthwySB.setLength(pthwySB.length() - 1);}
    		
    		pathways.add(pthwySB.toString());
    		
    		StringBuilder cmplxSB = new StringBuilder();
    		for(Complex complex : proteinDTO.getComplexes()){
    			cmplxSB.append(complex.getCorumId()).append(SEPERATOR).append(complex.getComplexName()).append(SEPERATOR).append(complex.getComplexComment())
					.append(SEPERATOR).append(complex.getCellLine()).append(SEPERATOR).append(complex.getDiseaseComment()).append(SEPERATOR).append(complex.getSubUnitComment())
					.append(SEPERATOR).append(complex.getPubmedId()).append(SEPERATOR).append(complex.getPurificationMethod()).append(SEPERATOR);
    		}
    		if(cmplxSB.length()>0){cmplxSB.setLength(cmplxSB.length() - 1);}
    		complexes.add(cmplxSB.toString());
    		
    		StringBuilder mfSB = new StringBuilder();
    		for(Go mf : proteinDTO.getMf()){
    			mfSB.append(mf.getId()).append(SEPERATOR).append(mf.getName()).append(SEPERATOR);
    		}
    		if(mfSB.length()>0){mfSB.setLength(mfSB.length() - 1);}
    		
    		mfs.add(mfSB.toString());
    		
    		StringBuilder bpSB = new StringBuilder();
    		for(Go bp : proteinDTO.getBp()){
    			bpSB.append(bp.getId()).append(SEPERATOR).append(bp.getName()).append(SEPERATOR);
    		}
    		if(bpSB.length()>0){bpSB.setLength(bpSB.length() - 1);}
    		
    		bps.add(bpSB.toString());
    		
    		StringBuilder ccSB = new StringBuilder();
    		for(Go cc : proteinDTO.getCc()){
    			ccSB.append(cc.getId()).append(SEPERATOR).append(cc.getName()).append(SEPERATOR);
    		}
    		if(ccSB.length()>0){ccSB.setLength(ccSB.length() - 1);}
    		
    		ccs.add(ccSB.toString());
    	}
    }
    
    private void clearRelations(){
		commonProjects.clear();
		associations.clear();
		pathways.clear();
		complexes.clear();
		mfs.clear();
		bps.clear();
		ccs.clear();
	}
    
    public void setSelectionType(){
    	selectionType = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectionType");
    }
}
