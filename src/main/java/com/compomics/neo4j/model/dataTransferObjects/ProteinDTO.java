package com.compomics.neo4j.model.dataTransferObjects;

import com.compomics.neo4j.model.nodes.Complex;
import com.compomics.neo4j.model.nodes.Go;
import com.compomics.neo4j.model.nodes.PathWay;
import com.compomics.neo4j.model.nodes.Project;
import com.compomics.neo4j.model.nodes.Protein;
import com.compomics.neo4j.model.relationshipTypes.Associate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by demet on 12/19/2016.
 */
public class ProteinDTO implements Serializable{

    private static final long serialVersionUID = -1191827964699580119L;

    private Protein protein1;

    private Protein protein2;

    private Associate associate;

    private List<Project> projects;
    
    private List<PathWay> pathWays;
    
    private List<Complex> complexes;
    
    private List<Go> mf;
    
    private List<Go> bp;
    
    private List<Go> cc;

    private int distinctComplexCount;

    private int distinctPathCount;
    
    private int commonProjectSize;

    public ProteinDTO() {
    }

	public Protein getProtein1() {
		return protein1;
	}

	public Protein getProtein2() {
		return protein2;
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

	public int getDistinctComplexCount() {
		return distinctComplexCount;
	}

	public int getDistinctPathCount() {
		return distinctPathCount;
	}

	public int getCommonProjectSize() {
		return commonProjectSize;
	}

	public void setProtein1(Protein protein1) {
		this.protein1 = protein1;
	}

	public void setProtein2(Protein protein2) {
		this.protein2 = protein2;
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

	public void setDistinctComplexCount(int distinctComplexCount) {
		this.distinctComplexCount = distinctComplexCount;
	}

	public void setDistinctPathCount(int distinctPathCount) {
		this.distinctPathCount = distinctPathCount;
	}

	public void setCommonProjectSize(int commonProjectSize) {
		this.commonProjectSize = commonProjectSize;
	}

    

    
}
