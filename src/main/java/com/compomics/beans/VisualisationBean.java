package com.compomics.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.compomics.neo4j.database.Service;
import com.compomics.neo4j.model.dataTransferObjects.GraphDTO;
import com.compomics.neo4j.model.dataTransferObjects.LinkDTO;
import com.compomics.neo4j.model.dataTransferObjects.ProteinDTO;
import com.compomics.neo4j.model.nodes.Complex;
import com.compomics.neo4j.model.nodes.Disease;
import com.compomics.neo4j.model.nodes.Go;
import com.compomics.neo4j.model.nodes.PathWay;
import com.compomics.neo4j.model.nodes.Project;
import com.compomics.neo4j.model.nodes.Protein;
import com.compomics.neo4j.model.relationshipTypes.Associate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@ManagedBean
@SessionScoped
public class VisualisationBean implements Serializable{

	private static final long serialVersionUID = -1246774333059415835L;
	
	private static final String SINGLE_PROTEIN_QUERY = "singleProteinSearch";
	private static final String DOUBLE_PROTEIN_QUERY = "doubleProteinSearch";
		
	private GraphDTO graphElements;
		
	private List<String> accessions= new ArrayList<>();

	private List<String> nodes = new ArrayList<>();
	private String selectedNode;
	private String selectedEdge;
	private Protein selectedProtein;
	private boolean visible = false;
	private boolean buttonVisible = true;
	private String graphType = "proteinView";
	private List<ProteinDTO> proteinDTOS;
	private Protein protein1;
	private Protein protein2;

    
    
    
	private GraphDbManagedBean graphDbManagedBean;

	private String jsonString;

	public List<String> getNodes() {
		return nodes;
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

	public String getJsonString() {
		return jsonString;
	}

	public GraphDbManagedBean getGraphDbManagedBean() {
		return graphDbManagedBean;
	}
	

	public List<ProteinDTO> getProteinDTOS() {
		return proteinDTOS;
	}


	public void load2(){
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> requestParams = context.getExternalContext().getRequestParameterMap();
		if(requestParams.containsKey("graph")){
			graphType = requestParams.get("graph");
		//	switchGraph();
		}
		
		
	}
	
	public void load(GraphDbManagedBean bean) {
		proteinDTOS = new ArrayList<>();
		visible = false;
		graphDbManagedBean = bean;
		nodes = new ArrayList<>();
		graphType = "proteinView";
		proteinDTOS = graphDbManagedBean.getProteinDTOS();
		controlGeneNames();
	//	switchGraph();
		getNodesLinksInJSONSingle(proteinDTOS.get(0).getProtein1().getUniprotAccession());
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
		if(proteinDTOS != null && !proteinDTOS.isEmpty()){
			edge.append("[");	
			int counter = 0;
			int i = 0;
			for(ProteinDTO proteinDTO : proteinDTOS) {
				if(!nodes.contains(proteinDTO.getProtein1().getUniprotAccession())){
					nodes.add(proteinDTO.getProtein1().getUniprotAccession());
				}
				if(!nodes.contains(proteinDTO.getProtein2().getUniprotAccession())){
					nodes.add(proteinDTO.getProtein2().getUniprotAccession());
				}
				
				if(proteinDTO.getAssociate().get(0).getJaccSimScore() >= 0.4){
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getUniprotAccession()).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getUniprotAccession()).append("\",\"annotation\":\"").append((graphDbManagedBean.getEdgeAnnotations().size() >= proteinDTOS.size()) ? graphDbManagedBean.getEdgeAnnotations().get(i) : "")
					.append("\",\"width\":\"").append(proteinDTO.getAssociate().get(0).getJaccSimScore()*10).append("\",\"faveColor\":\"black\",\"faveStyle\":\"solid\"}}*");
				}else{
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getUniprotAccession()).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getUniprotAccession()).append("\",\"annotation\":\"").append((graphDbManagedBean.getEdgeAnnotations().size() >= proteinDTOS.size()) ? graphDbManagedBean.getEdgeAnnotations().get(i) : "")
					.append("\",\"width\":\"").append(proteinDTO.getAssociate().get(0).getJaccSimScore()*10).append("\",\"faveColor\":\"gray\",\"faveStyle\":\"solid\"}}*");
				}
				
				if(proteinDTO.getAssociate().get(0).getInteract().equals("yes")){
					counter++;
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getUniprotAccession()).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getUniprotAccession()).append("\",\"width\":\"2\",\"label\":\"Interact\",\"faveColor\":\"purple\",\"faveStyle\":\"dotted\"}}*");
				}
				if(proteinDTO.getAssociate().get(0).getParalog().equals("yes")){
					counter++;
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getUniprotAccession()).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getUniprotAccession()).append("\",\"width\":\"2\",\"label\":\"Paralog\",\"faveColor\":\"blue\",\"faveStyle\":\"dotted\"}}*");
				}
				if(proteinDTO.getPathWays().size()>0){
					counter++;
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getUniprotAccession()).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getUniprotAccession()).append("\",\"label\":\"").append(proteinDTO.getPathWays().size())
					.append(" Pathways\",\"width\":\"2\",\"faveColor\":\"green\",\"faveStyle\":\"dotted\"}}*");
				}
				if(proteinDTO.getDiseaseCount()>0){
					counter++;
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getUniprotAccession()).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getUniprotAccession()).append("\",\"label\":\"").append(proteinDTO.getDiseaseCount())
					.append(" Pathways\",\"width\":\"2\",\"faveColor\":\"orange\",\"faveStyle\":\"dotted\"}}*");
				}
				counter++;
				i++;
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
			int i=0;
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
					.append(proteinDTO.getProtein2().getGeneNames().get(0)).append("\",\"annotation\":\"").append((graphDbManagedBean.getEdgeAnnotations().size() >= proteinDTOS.size()) ? graphDbManagedBean.getEdgeAnnotations().get(i) : "")
					.append("\",\"width\":\"").append(proteinDTO.getAssociate().get(0).getJaccSimScore()*10).append("\",\"faveColor\":\"black\",\"faveStyle\":\"solid\"}}*");
				}else{
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getGeneNames().get(0)).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getGeneNames().get(0)).append("\",\"annotation\":\"").append((graphDbManagedBean.getEdgeAnnotations().size() >= proteinDTOS.size()) ? graphDbManagedBean.getEdgeAnnotations().get(i) : "")
					.append("\",\"width\":\"").append(proteinDTO.getAssociate().get(0).getJaccSimScore()*10).append("\",\"faveColor\":\"gray\",\"faveStyle\":\"solid\"}}*");
				}
				
				if(proteinDTO.getAssociate().get(0).getInteract().equals("yes")){
					counter++;
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getGeneNames().get(0)).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getGeneNames().get(0)).append("\",\"width\":\"2\",\"label\":\"Interact\",\"faveColor\":\"purple\",\"faveStyle\":\"dotted\"}}*");
				}
				if(proteinDTO.getAssociate().get(0).getParalog().equals("yes")){
					counter++;
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getGeneNames().get(0)).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getGeneNames().get(0)).append("\",\"width\":\"2\",\"label\":\"Paralog\",\"faveColor\":\"blue\",\"faveStyle\":\"dotted\"}}*");
				}
				if(proteinDTO.getPathWays().size()>0){
					counter++;
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getGeneNames().get(0)).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getGeneNames().get(0)).append("\",\"label\":\"").append(proteinDTO.getPathWays().size())
					.append(" Pathways\",\"width\":\"2\",\"faveColor\":\"green\",\"faveStyle\":\"dotted\"}}*");
				}
				if(proteinDTO.getDiseaseCount()>0){
					counter++;
					edge.append("{\"group\":\"edges\",\"data\":{\"id\":\"").append(counter).append("\",\"source\":\"").append(proteinDTO.getProtein1().getGeneNames().get(0)).append("\",\"target\":\"")
					.append(proteinDTO.getProtein2().getGeneNames().get(0)).append("\",\"label\":\"").append(proteinDTO.getDiseaseCount())
					.append(" Pathways\",\"width\":\"2\",\"faveColor\":\"orange\",\"faveStyle\":\"dotted\"}}*");
				}
				counter++;
				i++;
			}
			
		}
		
		if(edge.length()>0){
			edge.setLength(edge.length() - 1);
			edge.append("]");
		}
		
		jsonString = edge.toString();
	}
	
	
	
	public void getNodesLinksInJSONSingle(String accession){
		Service dbService = new Service();
		dbService.startSession();
		accessions.clear();
		graphElements = new GraphDTO();
		try{
			List<ProteinDTO> proteinDTOs = dbService.getProteinDTOList(SINGLE_PROTEIN_QUERY, accession, null, 0.0);
			dbService.closeSession();
			if(!proteinDTOs.isEmpty()){	
				List<Protein> proteins = new ArrayList<>();
				List<LinkDTO> links = new ArrayList<>();
				for(ProteinDTO proteinDTO : proteinDTOs){
					// add nodes if they are not in the list
					if(!accessions.contains(proteinDTO.getProtein1().getUniprotAccession())){
						proteinDTO.getProtein1().setGroup(0);
						proteins.add(proteinDTO.getProtein1());
						accessions.add(proteinDTO.getProtein1().getUniprotAccession());
					}
					if(!accessions.contains(proteinDTO.getProtein2().getUniprotAccession())){
						proteinDTO.getProtein2().setGroup(0);
						proteins.add(proteinDTO.getProtein2());
						accessions.add(proteinDTO.getProtein2().getUniprotAccession());
					}
					// add links between nodes
					LinkDTO linkDTO = new LinkDTO();
					linkDTO.setSource(accessions.lastIndexOf(proteinDTO.getProtein1().getUniprotAccession()));
					linkDTO.setTarget(accessions.lastIndexOf(proteinDTO.getProtein2().getUniprotAccession()));
					linkDTO.setAssociate(proteinDTO.getAssociate());
					linkDTO.setBp(proteinDTO.getBp());
					linkDTO.setCc(proteinDTO.getCc());
					linkDTO.setMf(proteinDTO.getMf());
					linkDTO.setComplexes(proteinDTO.getComplexes());
					linkDTO.setDiseases(proteinDTO.getDiseases());
					linkDTO.setPathWays(proteinDTO.getPathWays());
					linkDTO.setProjects(proteinDTO.getProjects());
					links.add(linkDTO);
				}
				graphElements.setProteins(proteins);
				graphElements.setLinks(links);
			}
			
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		
		Gson gson = new GsonBuilder().serializeNulls().create();
		jsonString = gson.toJson(graphElements);
		System.out.println(graphElements.getProteins().size());
	}
	
	
	public void addNodesLinksInJSON(String accession){
		Service dbService = new Service();
		dbService.startSession();
		try{
			List<ProteinDTO> proteinDTOs = dbService.getProteinDTOList(SINGLE_PROTEIN_QUERY, accession, null, 0.0);
			dbService.closeSession();
			if(!proteinDTOs.isEmpty()){
				
				List<Protein> proteins = new ArrayList<>();
				List<LinkDTO> links = new ArrayList<>();
				for(ProteinDTO proteinDTO : proteinDTOs){
					// add nodes if they are not in the list
					
					if(!accessions.contains(proteinDTO.getProtein2().getUniprotAccession())){
						proteinDTO.getProtein2().setGroup(0);
						proteins.add(proteinDTO.getProtein2());
						accessions.add(proteinDTO.getProtein2().getUniprotAccession());
					}
					// add links between nodes
					LinkDTO linkDTO = new LinkDTO();
					linkDTO.setSource(accessions.lastIndexOf(proteinDTO.getProtein1().getUniprotAccession()));
					linkDTO.setTarget(accessions.lastIndexOf(proteinDTO.getProtein2().getUniprotAccession()));
					linkDTO.setAssociate(proteinDTO.getAssociate());
					linkDTO.setBp(proteinDTO.getBp());
					linkDTO.setCc(proteinDTO.getCc());
					linkDTO.setMf(proteinDTO.getMf());
					linkDTO.setComplexes(proteinDTO.getComplexes());
					linkDTO.setDiseases(proteinDTO.getDiseases());
					linkDTO.setPathWays(proteinDTO.getPathWays());
					linkDTO.setProjects(proteinDTO.getProjects());
					links.add(linkDTO);
				}
				graphElements.getProteins().addAll(proteins);
				graphElements.getLinks().addAll(links);
			}
			
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		Gson gson = new GsonBuilder().serializeNulls().create();
		jsonString = gson.toJson(graphElements);
		System.out.println(graphElements.getProteins().size());
	}
	
	public void getNode(){
		selectedNode =  FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedNode").trim(); 
		if(selectedNode != null) {addNodesLinksInJSON(selectedNode.trim());}
	}
		
}
