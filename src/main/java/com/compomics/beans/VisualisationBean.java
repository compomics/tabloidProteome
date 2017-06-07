package com.compomics.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.compomics.neo4j.model.dataTransferObjects.ProteinDTO;
import com.compomics.neo4j.model.nodes.Complex;
import com.compomics.neo4j.model.nodes.Disease;
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
	private String edges;
	private String selectedNode;
	private String selectedEdge;
	private Protein selectedProtein;
	private boolean visible = false;
	private boolean buttonVisible = true;
	private String graphType = "proteinView";
	private List<ProteinDTO> proteinDTOS;
	private Protein protein1;
	private Protein protein2;
	private List<Project> projects = new ArrayList<>();
	private List<Associate> association = new ArrayList<>();
	private List<PathWay> pathWays = new ArrayList<>();
	private List<Complex> complexs = new ArrayList<>();
	private List<Go> mf = new ArrayList<>();
    private List<Go> bp = new ArrayList<>();
    private List<Go> cc = new ArrayList<>();
    private List<Disease> diseases = new ArrayList<>();
    
	private GraphDbManagedBean graphDbManagedBean;

	private String jsonString;

	public List<String> getNodes() {
		return nodes;
	}

	public String getEdges() {
		return edges;
	}

	public String getSelectedNode() {
		return selectedNode;
	}

	public String getSelectedEdge() {
		return selectedEdge;
	}

	public Protein getSelectedProtein() {
		return selectedProtein;
	}

	public boolean isVisible() {
		return visible;
	}
	
	public boolean isButtonVisible() {
		return buttonVisible;
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

	public List<Disease> getDiseases() {
		return diseases;
	}

	public String getJsonString() {
		return jsonString;
	}

	public GraphDbManagedBean getGraphDbManagedBean() {
		return graphDbManagedBean;
	}
	
	public void load2(){
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> requestParams = context.getExternalContext().getRequestParameterMap();
		if(requestParams.containsKey("graph")){
			graphType = requestParams.get("graph");
			switchGraph();
		}
	}
	
	public void load(GraphDbManagedBean bean) {
		proteinDTOS = new ArrayList<>();
		visible = false;
		graphDbManagedBean = bean;
		nodes = new ArrayList<>();

		proteinDTOS = graphDbManagedBean.getProteinDTOS();
		controlGeneNames();
		switchGraph();

	}

	private void controlGeneNames(){
		proteinDTOS.forEach(proteinDTO ->{
			if(proteinDTO.getProtein1().getGeneNames().size() ==0 || proteinDTO.getProtein2().getGeneNames().size() ==0){
				buttonVisible = false;
			}
		});
	}
	
	private void switchGraph(){
		nodes.clear();
		jsonString = "";
		if(graphType.equals("geneView")){
			geneGraph();
		}else if(graphType.equals("proteinView")){
			uniprotGraph();
		}
	}
	
	private void uniprotGraph(){
		StringBuilder edge = new StringBuilder();
		if(!proteinDTOS.isEmpty()){
			edge.append("[");	
			int counter = 0;
			for(ProteinDTO proteinDTO : proteinDTOS) {
				if(!nodes.contains(proteinDTO.getProtein1().getUniprotAccession())){
					nodes.add(proteinDTO.getProtein1().getUniprotAccession());
				}
				if(!nodes.contains(proteinDTO.getProtein2().getUniprotAccession())){
					nodes.add(proteinDTO.getProtein2().getUniprotAccession());
				}
				
				if(proteinDTO.getAssociate().get(0).getJaccSimScore() >= 0.4){
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getUniprotAccession()).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getUniprotAccession()).append("\",\"label\":\"").append(proteinDTO.getAssociate().get(0).getJaccSimScore().toString())
					.append("\",\"faveColor\":\"black\",\"faveStyle\":\"solid\"}}*");
				}else{
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getUniprotAccession()).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getUniprotAccession()).append("\",\"label\":\"").append(proteinDTO.getAssociate().get(0).getJaccSimScore().toString())
					.append("\",\"faveColor\":\"gray\",\"faveStyle\":\"solid\"}}*");
				}
				
				if(proteinDTO.getAssociate().get(0).getInteract().equals("yes")){
					counter++;
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getUniprotAccession()).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getUniprotAccession()).append("\",\"label\":\"Interact\",\"faveColor\":\"purple\",\"faveStyle\":\"dotted\"}}*");
				}
				if(proteinDTO.getAssociate().get(0).getParalog().equals("yes")){
					counter++;
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getUniprotAccession()).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getUniprotAccession()).append("\",\"label\":\"Paralog\",\"faveColor\":\"blue\",\"faveStyle\":\"dotted\"}}*");
				}
				if(proteinDTO.getPathWays().size()>0){
					counter++;
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getUniprotAccession()).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getUniprotAccession()).append("\",\"label\":\"").append(proteinDTO.getPathWays().size())
					.append(" Pathways\",\"faveColor\":\"green\",\"faveStyle\":\"dotted\"}}*");
				}
				if(proteinDTO.getDiseaseCount()>0){
					counter++;
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getUniprotAccession()).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getUniprotAccession()).append("\",\"label\":\"").append(proteinDTO.getDiseaseCount())
					.append(" Pathways\",\"faveColor\":\"orange\",\"faveStyle\":\"dotted\"}}*");
				}
				counter++;
			}
			
		}
		
		if(edge.length()>0){
			edge.setLength(edge.length() - 1);
			edge.append("]");
		}
		
		jsonString = edge.toString();
	}
	
	
	private void geneGraph(){
		StringBuilder edge = new StringBuilder();
		List<String> tempNodes = new ArrayList<>();
		if(!proteinDTOS.isEmpty()){
			edge.append("[");	
			int counter = 0;
			for(ProteinDTO proteinDTO : proteinDTOS) {
				if(!tempNodes.contains(proteinDTO.getProtein1().getUniprotAccession())){
					tempNodes.add(proteinDTO.getProtein1().getUniprotAccession());
					nodes.add(proteinDTO.getProtein1().getGeneNames().get(0).toString());
				}
				if(!tempNodes.contains(proteinDTO.getProtein2().getUniprotAccession())){
					tempNodes.add(proteinDTO.getProtein2().getUniprotAccession());
					nodes.add(proteinDTO.getProtein2().getGeneNames().get(0).toString());
				}
				
				if(proteinDTO.getAssociate().get(0).getJaccSimScore() >= 0.4){
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getGeneNames().get(0)).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getGeneNames().get(0)).append("\",\"label\":\"").append(proteinDTO.getAssociate().get(0).getJaccSimScore().toString())
					.append("\",\"faveColor\":\"black\",\"faveStyle\":\"solid\"}}*");
				}else{
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getGeneNames().get(0)).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getGeneNames().get(0)).append("\",\"label\":\"").append(proteinDTO.getAssociate().get(0).getJaccSimScore().toString())
					.append("\",\"faveColor\":\"gray\",\"faveStyle\":\"solid\"}}*");
				}
				
				if(proteinDTO.getAssociate().get(0).getInteract().equals("yes")){
					counter++;
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getGeneNames().get(0)).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getGeneNames().get(0)).append("\",\"label\":\"Interact\",\"faveColor\":\"purple\",\"faveStyle\":\"dotted\"}}*");
				}
				if(proteinDTO.getAssociate().get(0).getParalog().equals("yes")){
					counter++;
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getGeneNames().get(0)).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getGeneNames().get(0)).append("\",\"label\":\"Paralog\",\"faveColor\":\"blue\",\"faveStyle\":\"dotted\"}}*");
				}
				if(proteinDTO.getPathWays().size()>0){
					counter++;
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getGeneNames().get(0)).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getGeneNames().get(0)).append("\",\"label\":\"").append(proteinDTO.getPathWays().size())
					.append(" Pathways\",\"faveColor\":\"green\",\"faveStyle\":\"dotted\"}}*");
				}
				if(proteinDTO.getDiseaseCount()>0){
					counter++;
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getGeneNames().get(0)).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getGeneNames().get(0)).append("\",\"label\":\"").append(proteinDTO.getDiseaseCount())
					.append(" Pathways\",\"faveColor\":\"orange\",\"faveStyle\":\"dotted\"}}*");
				}
				counter++;
			}
			
		}
		
		if(edge.length()>0){
			edge.setLength(edge.length() - 1);
			edge.append("]");
		}
		
		jsonString = edge.toString();
	}
	
	
	public void onNodeSelect(){
		selectedProtein = new Protein();

		for (ProteinDTO proteinDTO : graphDbManagedBean.getProteinDTOS()) {
			if (proteinDTO.getProtein1().getUniprotAccession().equals(selectedNode.trim())) {
				selectedProtein = proteinDTO.getProtein1();
			} else if (proteinDTO.getProtein2().getUniprotAccession().equals(selectedNode)) {
				selectedProtein = proteinDTO.getProtein2();
			}
		}

	}
	
	public void getNode(){
		selectedNode =  FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedNode").trim(); 
		if(selectedNode != null) {onNodeSelect();}
	}

	public void onEdgeSelect(){

		clearRelations();
		for(int i=0; i<graphDbManagedBean.getProteinDTOS().size(); i++){
			if(graphDbManagedBean.getProteinDTOS().get(i).getProtein2().getUniprotAccession().equals(selectedEdge) || (buttonVisible && graphDbManagedBean.getProteinDTOS().get(i).getProtein2().getGeneNames().get(0).equals(selectedEdge))){
				projects.addAll(graphDbManagedBean.getProteinDTOS().get(i).getProjects());
				association.addAll(graphDbManagedBean.getProteinDTOS().get(i).getAssociate());
				pathWays.addAll(graphDbManagedBean.getProteinDTOS().get(i).getPathWays());
				complexs.addAll(graphDbManagedBean.getProteinDTOS().get(i).getComplexes());
				mf.addAll(graphDbManagedBean.getProteinDTOS().get(i).getMf());
				bp.addAll(graphDbManagedBean.getProteinDTOS().get(i).getBp());
				cc.addAll(graphDbManagedBean.getProteinDTOS().get(i).getCc());
				diseases.addAll(graphDbManagedBean.getProteinDTOS().get(i).getDiseases());
				protein1 = graphDbManagedBean.getProteinDTOS().get(i).getProtein1();
				protein2 = graphDbManagedBean.getProteinDTOS().get(i).getProtein2();
				visible = true;
			}
		}
	}
	
	public void getEdge(){
		selectedEdge = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedEdge").trim();  
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
		diseases.clear();
		protein1 = new Protein();
		protein2 = new Protein();
	}
}
