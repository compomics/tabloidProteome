package com.compomics.neo4j.model.dataTransferObjects;

import java.io.Serializable;
import java.util.List;

import com.compomics.neo4j.model.nodes.Disease;

public class DiseaseDTO implements Serializable{

	private static final long serialVersionUID = -5034036789505045464L;

	private Disease disease;
	
	private List<ProteinDTO> proteinDTOs;

	public DiseaseDTO() {
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public List<ProteinDTO> getProteinDTOs() {
		return proteinDTOs;
	}

	public void setProteinDTOs(List<ProteinDTO> proteinDTOs) {
		this.proteinDTOs = proteinDTOs;
	}
	
	
}
