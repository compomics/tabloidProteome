package com.compomics.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.compomics.neo4j.database.Service;
import com.compomics.neo4j.model.dataTransferObjects.GraphDTO;
import com.compomics.neo4j.model.dataTransferObjects.LinkDTO;
import com.compomics.neo4j.model.dataTransferObjects.ProteinDTO;
import com.compomics.neo4j.model.nodes.Protein;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@ManagedBean
@SessionScoped
public class VisualisationBean implements Serializable{

	private static final long serialVersionUID = -1246774333059415835L;
	
	private static final String SINGLE_PROTEIN_QUERY = "singleProteinSearch";
	private static final String DOUBLE_PROTEIN_QUERY = "doubleProteinSearch";
	private static final String SINGLE_PROTEIN_QUERY_MOUSE = "singleProteinSearchMouse";
    private static final String DOUBLE_PROTEIN_QUERY_MOUSE = "doubleProteinSearchMouse";
		
	private GraphDTO graphElements;
	private GraphDTO allAddedElements;

	private List<String> accessions= new ArrayList<>();

	private List<String> nodes = new ArrayList<>();
	private String selectedNode;
	private String selectedEdge;
	private Protein selectedProtein;
	private boolean visible = false;
	private boolean buttonVisible = true;
	private List<ProteinDTO> proteinDTOS;
	private Protein protein1;
	private Protein protein2;

	private GraphDbManagedBean graphDbManagedBean;

	private String jsonString;
	private String graphJsonNewNodes;
	private String removedNodes;
	private double jacc;

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
	
	public double getJacc() {
		return jacc;
	}

	public void load2(){
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> requestParams = context.getExternalContext().getRequestParameterMap();
		if(requestParams.containsKey("view") && requestParams.get("view").equals("graph")){
			allAddedElements = new GraphDTO();
			allAddedElements.setProteins(new ArrayList<Protein>());
			allAddedElements.setLinks(new ArrayList<LinkDTO>());
		}
	}
	
	public void load(GraphDbManagedBean bean) {
		proteinDTOS = new ArrayList<>();
		visible = false;
		graphDbManagedBean = bean;
		nodes = new ArrayList<>();
		proteinDTOS = graphDbManagedBean.getProteinDTOS();
		allAddedElements = new GraphDTO();
		allAddedElements.setProteins(new ArrayList<Protein>());
		allAddedElements.setLinks(new ArrayList<LinkDTO>());
		jacc = graphDbManagedBean.getJaccScore();
		controlGeneNames();
		getNodesLinksInJSONSingle();
	}

	private void controlGeneNames(){
		proteinDTOS.forEach(proteinDTO ->{
			if(proteinDTO.getProtein1().getGeneNames().size() ==0 || proteinDTO.getProtein2().getGeneNames().size() ==0){
				buttonVisible = false;
			}
		});
	}
	
	
	/**
	 * Get all the nodes and links having default threshold (coming from data table search)
	 * Clean everything when the page is refreshed
	 */
	public void getNodesLinksInJSONSingle(){

		accessions.clear();
		graphElements = new GraphDTO();

		if (!proteinDTOS.isEmpty()) {
			List<Protein> proteins = new ArrayList<>();
			List<LinkDTO> links = new ArrayList<>();
			int counter =0;
			for (ProteinDTO proteinDTO : proteinDTOS) {
				// add nodes if they are not in the list
				if (!accessions.contains(proteinDTO.getProtein1().getUniprotAccession())) {
					if(graphDbManagedBean.getSelectionType().equals("single")){
						proteinDTO.getProtein1().setGroup(0);
					}else{
						proteinDTO.getProtein1().setGroup(1);
					}
					proteins.add(proteinDTO.getProtein1());
					accessions.add(proteinDTO.getProtein1().getUniprotAccession());

				}
				if (!accessions.contains(proteinDTO.getProtein2().getUniprotAccession())) {
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
				linkDTO.setEdgeAnnotation((graphDbManagedBean.getEdgeAnnotations().size() >= proteinDTOS.size()) ? graphDbManagedBean.getEdgeAnnotations().get(counter) : "");
				links.add(linkDTO);
				counter++;
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
		allAddedElements = new GraphDTO();
		allAddedElements.setProteins(new ArrayList<Protein>());
		allAddedElements.setLinks(new ArrayList<LinkDTO>());
		
		jacc = Double.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jaccThreshold").trim()); 
		Service dbService = new Service();
		dbService.startSession();	

			try{
				List<ProteinDTO> proteinDTOs = new ArrayList<>();
				if(graphDbManagedBean.getSelectionType() != null && !graphDbManagedBean.getSelectionType().equals("")){
					if(graphDbManagedBean.getSelectionType().equals("single")){
						if(graphDbManagedBean.getSpecies().equals("9606")){
							proteinDTOs = dbService.getProteinDTOList(SINGLE_PROTEIN_QUERY, proteinDTOS.get(0).getProtein1().getUniprotAccession(), null, jacc);
				    	}else if(graphDbManagedBean.getSpecies().equals("10090")){
				    		proteinDTOs = dbService.getProteinDTOList(SINGLE_PROTEIN_QUERY_MOUSE, proteinDTOS.get(0).getProtein1().getUniprotAccession(), null, jacc);
				    	}
						
					}else if(graphDbManagedBean.getSelectionType().equals("double")){
						if(graphDbManagedBean.getSpecies().equals("9606")){
							proteinDTOs = dbService.getProteinDTOList(DOUBLE_PROTEIN_QUERY, proteinDTOS.get(0).getProtein1().getUniprotAccession(), proteinDTOS.get(0).getProtein2().getUniprotAccession(), jacc);
				    	}else if(graphDbManagedBean.getSpecies().equals("10090")){
				    		proteinDTOs = dbService.getProteinDTOList(DOUBLE_PROTEIN_QUERY_MOUSE, proteinDTOS.get(0).getProtein1().getUniprotAccession(), proteinDTOS.get(0).getProtein2().getUniprotAccession(), jacc);
				    	}
						
					}else if(graphDbManagedBean.getSelectionType().equals("path")){
						proteinDTOs = graphDbManagedBean.getProteinDTOS();
					}
				}else if(graphDbManagedBean.isMultipleSearch()){
					if(graphDbManagedBean.getArray2() == null || graphDbManagedBean.getArray2().equals("")){
						List<String> arr1 = new ArrayList<>();
						List<String> arr2 = new ArrayList<>();
						graphDbManagedBean.combinations(graphDbManagedBean.getArray1().split("\\s*,\\s*"), 2, 0, new String[2], arr1,arr2);
						proteinDTOs = dbService.getProteinDTOListForMultipleProteins(arr1, arr2, jacc, graphDbManagedBean.getSpecies());
					}else{
						proteinDTOs = dbService.getProteinDTOListForMultipleProteins(Arrays.asList(graphDbManagedBean.getArray1().split("\\s*,\\s*")), Arrays.asList(graphDbManagedBean.getArray2().split("\\s*,\\s*")), jacc,graphDbManagedBean.getSpecies());
					}
					
				}
				
				dbService.closeSession();
				if(!proteinDTOs.isEmpty()){
					
					List<Protein> proteins = new ArrayList<>();
					List<LinkDTO> links = new ArrayList<>();
					for(ProteinDTO proteinDTO : proteinDTOs){
						// add nodes if they are not in the list
						if (!accessions.contains(proteinDTO.getProtein1().getUniprotAccession())) {
							if(graphDbManagedBean.getSelectionType().equals("single")){
								proteinDTO.getProtein1().setGroup(0);
							}else{
								proteinDTO.getProtein1().setGroup(1);
							}
							proteins.add(proteinDTO.getProtein1());
							accessions.add(proteinDTO.getProtein1().getUniprotAccession());
						}
						if (!accessions.contains(proteinDTO.getProtein2().getUniprotAccession())) {
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
						linkDTO.setEdgeAnnotation("");
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
		int groupNo = Integer.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("groupNo")) ;
		double jacc = Double.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jacc").trim()); 
		if(selectedNode != null) {	
			addNodesLinksInJSON(selectedNode, groupNo, jacc);
		}
	}
	
	
	/**
	 * Add new nodes and relations of the selected protein for given threshold to expand
	 * @param accession
	 * @param jacc
	 */
	public void addNodesLinksInJSON(String accession, int groupNo, double jacc){
		GraphDTO newGraphDTO = new GraphDTO();
		Service dbService = new Service();
		dbService.startSession();
		try{
			List<ProteinDTO> proteinDTOs = new ArrayList<>();
			if(graphDbManagedBean.getSpecies().equals("9606")){
				proteinDTOs = dbService.getProteinDTOList(SINGLE_PROTEIN_QUERY, accession, null, jacc);
	    	}else if(graphDbManagedBean.getSpecies().equals("10090")){
	    		proteinDTOs = dbService.getProteinDTOList(SINGLE_PROTEIN_QUERY_MOUSE, accession, null, jacc);
	    	}
			dbService.closeSession();
			if(!proteinDTOs.isEmpty()){
				
				List<Protein> proteins = new ArrayList<>();
				List<LinkDTO> links = new ArrayList<>();
				for(ProteinDTO proteinDTO : proteinDTOs){
					// add nodes if they are not in the list
					
					boolean nodeExist = false;
					for(Protein pr: allAddedElements.getProteins()){
						if(pr.getUniprotAccession().equals(proteinDTO.getProtein2().getUniprotAccession())){
							nodeExist=true;
							break;
						}
					}
					for(Protein pr: graphElements.getProteins()){
						if(pr.getUniprotAccession().equals(proteinDTO.getProtein2().getUniprotAccession())){
							nodeExist=true;
							break;
						}
					}
					
					if(!nodeExist){
						proteinDTO.getProtein2().setGroup(groupNo+1);
						proteins.add(proteinDTO.getProtein2());
						accessions.add(proteinDTO.getProtein2().getUniprotAccession());
					}
					
					boolean linkExist = false;
					for(LinkDTO link: allAddedElements.getLinks()){
						if(link.getSource().equals(proteinDTO.getProtein2().getUniprotAccession()) && 
								link.getTarget().equals(proteinDTO.getProtein1().getUniprotAccession())){
							linkExist=true;
						}
					}
					for(LinkDTO link: graphElements.getLinks()){
						if(link.getSource().equals(proteinDTO.getProtein2().getUniprotAccession()) && 
								link.getTarget().equals(proteinDTO.getProtein1().getUniprotAccession())){
							linkExist=true;
						}
					}
					
					// add links between nodes
					if(!linkExist){
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
						linkDTO.setEdgeAnnotation("");
						links.add(linkDTO);
					}
					
				}
				allAddedElements.getProteins().addAll(proteins);
				allAddedElements.getLinks().addAll(links);
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
		List<LinkDTO> removedLinksFirstLevel = allAddedElements.getLinks().stream().filter(link -> link.getSource().equals(accession)).collect(Collectors.toList());		
		//remove the links from allAddedElements
		
		List<LinkDTO> removedLinks = addNewLinksToRemove(removedLinksFirstLevel);
		
		allAddedElements.getLinks().removeIf(x -> removedLinks.contains(x));
		
		
		for(LinkDTO link : removedLinks) {
			boolean keep = false;
			for(LinkDTO l : allAddedElements.getLinks()) {
				if(l.getSource().equals(link.getTarget()) || l.getTarget().equals(link.getTarget())){
					keep = true;
					break;
				}
			}
			if(!keep){
				if(allAddedElements.getProteins().stream().filter(p -> p.getUniprotAccession().equals(link.getTarget())).findFirst().isPresent()){
					removedProteins.add(allAddedElements.getProteins().stream().filter(p -> p.getUniprotAccession().equals(link.getTarget())).findFirst().get());
				}
				removedAccessions.add(link.getTarget());
			}
		}
		
		//remove the proteins from graphElements
		allAddedElements.getProteins().removeIf(p -> removedAccessions.contains(p.getUniprotAccession()));
		accessions.removeIf(p -> removedAccessions.contains(p));
		
		GraphDTO removedGraphDTO = new GraphDTO();
		removedGraphDTO.setLinks(removedLinks);
		removedGraphDTO.setProteins(removedProteins);
		Gson gson = new GsonBuilder().serializeNulls().create();
		removedNodes = gson.toJson(removedGraphDTO);
	}
	
	private List<LinkDTO> addNewLinksToRemove(List<LinkDTO> removedLinks){
		List<LinkDTO> newLinksToRemove = new ArrayList<>();
		for(LinkDTO removedLink: removedLinks){
			newLinksToRemove.addAll(allAddedElements.getLinks().stream().filter(link -> link.getSource().equals(removedLink.getTarget())).collect(Collectors.toList()));
		}
		if(!newLinksToRemove.isEmpty()){
			removedLinks.addAll(newLinksToRemove);
			allAddedElements.getLinks().removeIf(x -> newLinksToRemove.contains(x));
			addNewLinksToRemove(removedLinks);
		}
		return removedLinks;
	}
}