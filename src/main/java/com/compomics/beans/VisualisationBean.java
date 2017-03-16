package com.compomics.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.primefaces.context.RequestContext;
import org.primefaces.model.mindmap.DefaultMindmapNode;
import org.primefaces.model.mindmap.MindmapNode;

import com.compomics.neo4j.model.dataTransferObjects.ProteinDTO;
import com.compomics.neo4j.model.nodes.Complex;
import com.compomics.neo4j.model.nodes.Go;
import com.compomics.neo4j.model.nodes.PathWay;
import com.compomics.neo4j.model.nodes.Project;
import com.compomics.neo4j.model.nodes.Protein;
import com.compomics.neo4j.model.relationshipTypes.Associate;

@ManagedBean
@SessionScoped
public class VisualisationBean implements Serializable{

	private static final long serialVersionUID = -1246774333059415835L;

	private List<String> nodes = new ArrayList<>();
	private List<Integer> edges = new ArrayList<>();;
	private String selectedNode;
	private int selectedEdge;
	private Protein selectedProtein;
	private boolean visible = false;
	private Protein protein1;
	private Protein protein2;
	private List<Project> projects = new ArrayList<>();
	private List<Associate> association = new ArrayList<>();
	private List<PathWay> pathWays = new ArrayList<>();
	private List<Complex> complexs = new ArrayList<>();
	private List<Go> mf = new ArrayList<>();
    private List<Go> bp = new ArrayList<>();
    private List<Go> cc = new ArrayList<>();
	private GraphDbManagedBean graphDbManagedBean;
	

	public List<String> getNodes() {
		return nodes;
	}

	public List<Integer> getEdges() {
		return edges;
	}

	public String getSelectedNode() {
		return selectedNode;
	}

	public int getSelectedEdge() {
		return selectedEdge;
	}

	public Protein getSelectedProtein() {
		return selectedProtein;
	}

	public boolean isVisible() {
		return visible;
	}

	public Protein getProtein1() {
		return protein1;
	}

	public Protein getProtein2() {
		return protein2;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public List<Associate> getAssociation() {
		return association;
	}

	public List<PathWay> getPathWays() {
		return pathWays;
	}

	public List<Complex> getComplexs() {
		return complexs;
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

	public GraphDbManagedBean getGraphDbManagedBean() {
		return graphDbManagedBean;
	}

	public void load(GraphDbManagedBean bean) {
		visible = false;
		graphDbManagedBean = bean;
		nodes = new ArrayList<>();
		edges = new ArrayList<>();
		
		if(!graphDbManagedBean.getProteinDTOS().isEmpty()){
			nodes.add(graphDbManagedBean.getProteinDTOS().get(0).getProtein1().getUniprotAccession());
			int counter = 0;
			for(ProteinDTO proteinDTO : graphDbManagedBean.getProteinDTOS()) {
				nodes.add(proteinDTO.getProtein2().getUniprotAccession());
				edges.add(counter);
				counter++;
			}
		}	
	}

	public void onNodeSelect(){
		selectedProtein = new Protein();
		if(graphDbManagedBean.getProteinDTOS().get(0).getProtein1().getUniprotAccession().equals(selectedNode.trim())){			
			selectedProtein = graphDbManagedBean.getProteinDTOS().get(0).getProtein1();
		}else{
			for(ProteinDTO proteinDTO : graphDbManagedBean.getProteinDTOS()) {
				if(proteinDTO.getProtein2().getUniprotAccession().equals(selectedNode.trim())){
					selectedProtein = proteinDTO.getProtein2();
				}
			}
		}

	}
	
	public void getNode(){
		selectedNode = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedNode");  
		onNodeSelect();
	}
	
	public void onEdgeSelect(){

		clearRelations();
		if (graphDbManagedBean.getProteinDTOS().get(selectedEdge) != null) {
			projects.addAll(graphDbManagedBean.getProteinDTOS().get(selectedEdge).getProjects());
			association.add(graphDbManagedBean.getProteinDTOS().get(selectedEdge).getAssociate());
			pathWays.addAll(graphDbManagedBean.getProteinDTOS().get(selectedEdge).getPathWays());
			complexs.addAll(graphDbManagedBean.getProteinDTOS().get(selectedEdge).getComplexes());
			mf.addAll(graphDbManagedBean.getProteinDTOS().get(selectedEdge).getMf());
			bp.addAll(graphDbManagedBean.getProteinDTOS().get(selectedEdge).getBp());
			cc.addAll(graphDbManagedBean.getProteinDTOS().get(selectedEdge).getCc());
			protein1 = graphDbManagedBean.getProteinDTOS().get(selectedEdge).getProtein1();
			protein2 = graphDbManagedBean.getProteinDTOS().get(selectedEdge).getProtein2();
		}
		visible = true;

	}
	
	public void getEdge(){
		selectedEdge = Integer.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedEdge").trim());  
		onEdgeSelect();
	}
	
	public void clearRelations(){
		projects.clear();
		association.clear();
		pathWays.clear();
		complexs.clear();
		mf.clear();
		bp.clear();
		cc.clear();
		protein1 = new Protein();
		protein2 = new Protein();
	}
}
