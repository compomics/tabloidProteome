package com.compomics.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.primefaces.context.RequestContext;

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
import com.compomics.rest.GraphJSON;
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
	private String graphJsonNewNodes;
	private String removedNodes;

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

	public String getGraphJsonNewNodes() {
		return graphJsonNewNodes;
	}

	public String getRemovedNodes() {
		return removedNodes;
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
		getNodesLinksInJSONSingle();
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
	
	
	/**
	 * Get all the nodes and links having default threshold (coming from data table search)
	 * Clean everything when the page is refreshed
	 * @param accession
	 */
	public void getNodesLinksInJSONSingle(){
		accessions.clear();
		graphElements = new GraphDTO();

		if (!proteinDTOS.isEmpty()) {
			List<Protein> proteins = new ArrayList<>();
			List<LinkDTO> links = new ArrayList<>();
			for (ProteinDTO proteinDTO : proteinDTOS) {
				// add nodes if they are not in the list
				if (!accessions.contains(proteinDTO.getProtein1().getUniprotAccession())) {
					proteinDTO.getProtein1().setGroup(0);
					proteins.add(proteinDTO.getProtein1());
					accessions.add(proteinDTO.getProtein1().getUniprotAccession());

				}
				if (!accessions.contains(proteinDTO.getProtein2().getUniprotAccession())) {
					proteinDTO.getProtein2().setGroup(0);
					proteins.add(proteinDTO.getProtein2());
					accessions.add(proteinDTO.getProtein2().getUniprotAccession());

				}
				// add links between nodes
				LinkDTO linkDTO = new LinkDTO();
				linkDTO.setSource(proteinDTO.getProtein1().getUniprotAccession());
				linkDTO.setTarget(proteinDTO.getProtein2().getUniprotAccession());
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
		Gson gson = new GsonBuilder().serializeNulls().create();
		jsonString = gson.toJson(graphElements).toString();
	}
	
	/**
	 * When threshold changes, all added nodes will be cleared only main search will be visible with new threshold.
	 */
	public void changeThreshold(){
		graphElements = new GraphDTO();
		accessions.clear();
		
		double jacc = Double.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jacc").trim()); 
		Service dbService = new Service();
		dbService.startSession();	

			try{
				List<ProteinDTO> proteinDTOs = dbService.getProteinDTOList(SINGLE_PROTEIN_QUERY, proteinDTOS.get(0).getProtein1().getUniprotAccession(), null, jacc);
				dbService.closeSession();
				if(!proteinDTOs.isEmpty()){
					
					List<Protein> proteins = new ArrayList<>();
					List<LinkDTO> links = new ArrayList<>();
					for(ProteinDTO proteinDTO : proteinDTOs){
						// add nodes if they are not in the list
						if (!accessions.contains(proteinDTO.getProtein1().getUniprotAccession())) {
							proteinDTO.getProtein1().setGroup(0);
							proteins.add(proteinDTO.getProtein1());
							accessions.add(proteinDTO.getProtein1().getUniprotAccession());

						}
						
						proteinDTO.getProtein2().setGroup(0);
						proteins.add(proteinDTO.getProtein2());
						accessions.add(proteinDTO.getProtein2().getUniprotAccession());

						// add links between nodes
						LinkDTO linkDTO = new LinkDTO();
						linkDTO.setSource(proteinDTO.getProtein1().getUniprotAccession());
						linkDTO.setTarget(proteinDTO.getProtein2().getUniprotAccession());
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
		jsonString = gson.toJson(graphElements).toString();
		
	}
	
	/**
	 * Get selected node and threshold from graph.xhtml
	 */
	public void getNodeExpand(){
		selectedNode =  FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedNode").trim(); 
		double jacc = Double.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jacc").trim()); 
		if(selectedNode != null) {	
			addNodesLinksInJSON(selectedNode, jacc);
		}
	}
	
	
	/**
	 * Add new nodes and relations of the selected protein for given threshold to expand
	 * @param accession
	 * @param jacc
	 */
	public void addNodesLinksInJSON(String accession, double jacc){
		GraphDTO newGraphDTO = new GraphDTO();
		Service dbService = new Service();
		dbService.startSession();
		try{
			List<ProteinDTO> proteinDTOs = dbService.getProteinDTOList(SINGLE_PROTEIN_QUERY, accession, null, jacc);
			dbService.closeSession();
			if(!proteinDTOs.isEmpty()){
				
				List<Protein> proteins = new ArrayList<>();
				List<LinkDTO> links = new ArrayList<>();
				for(ProteinDTO proteinDTO : proteinDTOs){
					// add nodes if they are not in the list
					
					if(!accessions.contains(proteinDTO.getProtein2().getUniprotAccession())){
						proteinDTO.getProtein2().setGroup(1);
						proteins.add(proteinDTO.getProtein2());
						accessions.add(proteinDTO.getProtein2().getUniprotAccession());
					}
					// add links between nodes
					LinkDTO linkDTO = new LinkDTO();
					linkDTO.setSource(proteinDTO.getProtein1().getUniprotAccession());
					linkDTO.setTarget(proteinDTO.getProtein2().getUniprotAccession());
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
				newGraphDTO.setProteins(proteins);
				newGraphDTO.setLinks(links);

			}
			
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		Gson gson = new GsonBuilder().serializeNulls().create();
		graphJsonNewNodes = gson.toJson(newGraphDTO);

	}
	
	/**
	 * Get selected node from graph.xhtml to collapse
	 */
	public void getNodeCollapse(){
		selectedNode =  FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedNode").trim(); 
		
		if(selectedNode != null) {
			removeNodesLinksInJSON(selectedNode);	
		}
	}
	
	
	public void removeNodesLinksInJSON(String accession){
		//proteins that will be removed
		List<Protein> removedProteins = new ArrayList<>();
		List<String> removedAccessions = new ArrayList<>();
		// all the links which have selected protein as a source.
		List<LinkDTO> removedLinks = graphElements.getLinks().stream().filter(link -> link.getSource().equals(accession)).collect(Collectors.toList());
		
		//remove the links from graphElements
		graphElements.getLinks().removeIf(x -> removedLinks.contains(x));
		for(LinkDTO link : removedLinks) {
			boolean keep = false;
			for(LinkDTO l : graphElements.getLinks()) {
				if(l.getSource().equals(link.getTarget()) || l.getTarget().equals(link.getTarget())){
					keep = true;
					break;
				}
			}
			if(keep == false){
				removedProteins.add(graphElements.getProteins().stream().filter(p -> p.getUniprotAccession().equals(link.getTarget())).findFirst().get());
				removedAccessions.add(link.getTarget());
			}
		}
		
		//remove the proteins from graphElements
		graphElements.getProteins().removeIf(p -> removedAccessions.contains(p.getUniprotAccession()));
		accessions.removeIf(p -> removedAccessions.contains(p));
		
		GraphDTO removedGraphDTO = new GraphDTO();
		removedGraphDTO.setLinks(removedLinks);
		removedGraphDTO.setProteins(removedProteins);
		Gson gson = new GsonBuilder().serializeNulls().create();
		removedNodes = gson.toJson(removedGraphDTO);
	}
}
