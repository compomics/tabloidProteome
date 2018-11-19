package com.compomics.neo4j.model.dataTransferObjects;

import java.io.Serializable;
import java.util.List;

import com.compomics.neo4j.model.nodes.Complex;
import com.compomics.neo4j.model.nodes.Disease;
import com.compomics.neo4j.model.nodes.Go;
import com.compomics.neo4j.model.nodes.PathWay;
import com.compomics.neo4j.model.nodes.Project;
import com.compomics.neo4j.model.relationshipTypes.Associate;

/**
 * Created by demet on 12/19/2016.
 */
public class LinkDTO implements Serializable{

    private static final long serialVersionUID = -1191827964699580119L;

    private String source;

    private String target;

    private Associate associate;

    private List<Project> projects;
    
    private List<PathWay> pathWays;
    
    private List<Complex> complexes;
    
    private List<Go> mf;
    
    private List<Go> bp;
    
    private List<Go> cc;
    
    private List<Disease> diseases;
    
    private String edgeAnnotation;

	public LinkDTO() {
    }

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public Associate getAssociate() {
		return associate;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public List<PathWay> getPathWays() {
		return pathWays;
	}

	public List<Complex> getComplexes() {
		return complexes;
	}

	public List<Go> getMf() {
		return mf;
	}

	public List<Go> getBp() {
		return bp;
	}

	public List<Go> getCc() {
		return cc;
	}

	public List<Disease> getDiseases() {
		return diseases;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setAssociate(Associate associate) {
		this.associate = associate;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public void setPathWays(List<PathWay> pathWays) {
		this.pathWays = pathWays;
	}

	public void setComplexes(List<Complex> complexes) {
		this.complexes = complexes;
	}

	public void setMf(List<Go> mf) {
		this.mf = mf;
	}

	public void setBp(List<Go> bp) {
		this.bp = bp;
	}

	public void setCc(List<Go> cc) {
		this.cc = cc;
	}

	public void setDiseases(List<Disease> diseases) {
		this.diseases = diseases;
	}

	public String getEdgeAnnotation() {
		return edgeAnnotation;
	}

	public void setEdgeAnnotation(String edgeAnnotation) {
		this.edgeAnnotation = edgeAnnotation;
	}


}