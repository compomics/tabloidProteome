package com.compomics.neo4j.model.dataTransferObjects;

import java.io.Serializable;
import java.util.List;

import com.compomics.neo4j.model.nodes.PathWay;


public class PathwayDTO implements Serializable{

	private static final long serialVersionUID = 4683311680811942209L;
	private PathWay pathWay;
	private List<ProteinDTO> proteinDTOs;
	
	public PathwayDTO() {
	}
	
	public PathWay getPathWay() {
		return pathWay;
	}
	
	public void setPathWay(PathWay pathWay) {
		this.pathWay = pathWay;
	}
	
	public List<ProteinDTO> getProteinDTOs() {
		return proteinDTOs;
	}
	
	public void setProteinDTOs(List<ProteinDTO> proteinDTOs) {
		this.proteinDTOs = proteinDTOs;
	}
	
}
