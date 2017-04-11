package com.compomics.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.compomics.export.TSVExporter;
import com.compomics.neo4j.database.Service;
import com.compomics.neo4j.model.dataTransferObjects.ProteinDTO;

/**
 * Created by demet on 12/12/2016.
 */
@ManagedBean
@SessionScoped
public class GraphDbManagedBean implements Serializable{

    private static final long serialVersionUID = 9222685086844376613L;
    
    private List<ProteinDTO> proteinDTOS = new ArrayList<>();
    private Service dbService;
    private String accession1;
    private String accession2;
    private String selectionType;
    private String control;
    
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

	public String getControl() {
		return control;
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
				accession1 = requestParams.get("accession").toUpperCase();
				accession2 = null;
				control= "single";
				setSelectionType("single");
				getProteinDTOs();
			}
		}else if(requestParams.containsKey("accession1") && requestParams.containsKey("accession2")){
			if(requestParams.get("accession1")!=null && !requestParams.get("accession1").equals("") && requestParams.get("accession2")!=null && !requestParams.get("accession2").equals(""))
			{
				accession1 = requestParams.get("accession1").toUpperCase();
				accession2 = requestParams.get("accession2").toUpperCase();
				controlRelation();
				setSelectionType("double");
				getProteinDTOs();
			}
		}
	}
	
	private void controlRelation(){
		control = dbService.controlRelation(accession1, accession2);
	}
	
	public void getProteinDTOs() {
		proteinDTOS.clear();
		if (selectionType.equals("single")) {
			getSingleProteinDTOs();
			visualisationBean.load(this);
		} else if (selectionType.equals("double")) {
			getDoubleProteinDTOs();
			visualisationBean.load(this);
		}
	}
    
    public void getSingleProteinDTOs(){

        proteinDTOS = dbService.getProteinDTOList(SINGLE_PROTEIN_QUERY, accession1, accession2);
    }
    
    public void getDoubleProteinDTOs(){

        proteinDTOS = dbService.getProteinDTOList(DOUBLE_PROTEIN_QUERY, accession1, accession2);
    }
   
    public void setSelectionType(){
    	selectionType = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectionType");
    }
    
    public void tsvExport(){
    	TSVExporter tsvExporter = new TSVExporter();
    	tsvExporter.tsvExport(proteinDTOS);
    }
}
