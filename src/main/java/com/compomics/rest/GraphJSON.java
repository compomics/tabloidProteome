package com.compomics.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.compomics.beans.VisualisationBean;
import com.compomics.neo4j.database.Service;
import com.compomics.neo4j.model.dataTransferObjects.GraphDTO;
import com.compomics.neo4j.model.dataTransferObjects.LinkDTO;
import com.compomics.neo4j.model.dataTransferObjects.ProteinDTO;
import com.compomics.neo4j.model.nodes.Protein;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;

@ManagedBean
@SessionScoped
@Path("/graph")
public class GraphJSON implements Serializable{

	private static final long serialVersionUID = -4243436284073815361L;
	
	// queries
    private static final String SINGLE_PROTEIN_QUERY = "singleProteinSearch";
    private static final String DOUBLE_PROTEIN_QUERY = "doubleProteinSearch";
	
	private GraphDTO graphElements;
	
	private List<String> accessions= new ArrayList<>();
	
	
	@ManagedProperty(value="#{visualisationBean}")
	private VisualisationBean visualisationBean;
	
	public GraphDTO getGraphElements() {
		return graphElements;
	}

	public void setGraphElements(GraphDTO graphElements) {
		this.graphElements = graphElements;
	}
	
	public static Object getBean(String beanName){
	    Object bean = null;
	    FacesContext fc = FacesContext.getCurrentInstance();
	    if(fc!=null){
	         ELContext elContext = fc.getELContext();
	         bean = elContext.getELResolver().getValue(elContext, null, beanName);
	    }

	    return bean;
	}

	@GET
	@Path("/create/{accession}/")
	@Produces(MediaType.APPLICATION_JSON)
	@JacksonFeatures(serializationEnable =  { SerializationFeature.INDENT_OUTPUT })
	public Response getNodesLinksInJSONSingle(@PathParam("accession") String accession){
		
		visualisationBean = (VisualisationBean) getBean("visualisationBean");
		
		if(graphElements == null){
			return Response.status(Response.Status.NOT_FOUND).entity("Resource not found for accession: " +  accession).build();
		}else{
			return Response.ok(graphElements).build();
		}
		
		/**
		Service dbService = new Service();
		dbService.startSession();
		try{
			List<ProteinDTO> proteinDTOs = dbService.getProteinDTOList(SINGLE_PROTEIN_QUERY, accession, null, 0.0);
			dbService.closeSession();
			if(proteinDTOs.isEmpty()){
				return Response.status(Response.Status.NOT_FOUND).entity("Resource not found for accession: " +  accession).build();
			}else{
				graphElements = new GraphDTO();
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
				return Response.ok(graphElements).build();
			}
			
		}catch(NumberFormatException e){
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).entity("Jaccard Similarity Score should be between 0 and 1.").build();
		}
		*/
	}
	
	@GET
	@Path("/create/{accession1}&{accession2}/")
	@Produces(MediaType.APPLICATION_JSON)
	@JacksonFeatures(serializationEnable =  { SerializationFeature.INDENT_OUTPUT })
	public Response getNodesLinksInJSONDouble(@PathParam("accession1") String accession1, @PathParam("accession2") String accession2){
		Service dbService = new Service();
		dbService.startSession();
		try{
			List<ProteinDTO> proteinDTOs = dbService.getProteinDTOList(DOUBLE_PROTEIN_QUERY, accession1, accession2, 0.0);
			dbService.closeSession();
			if(proteinDTOs.isEmpty()){
				return Response.status(Response.Status.NOT_FOUND).entity("Resource not found for accession: " +  accession1 + " and " + accession2).build();
			}else{
				graphElements = new GraphDTO();
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
				//	linkDTO.setSource(accessions.lastIndexOf(proteinDTO.getProtein1().getUniprotAccession()));
				//	linkDTO.setTarget(accessions.lastIndexOf(proteinDTO.getProtein2().getUniprotAccession()));
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
				return Response.ok(graphElements).build();
			}
			
		}catch(NumberFormatException e){
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).entity("Jaccard Similarity Score should be between 0 and 1.").build();
		}
		
	}
	
	@GET
	@Path("/add/{accession}/")
	@Produces(MediaType.APPLICATION_JSON)
	@JacksonFeatures(serializationEnable =  { SerializationFeature.INDENT_OUTPUT })
	public Response addNodesLinksInJSON(@PathParam("accession") String accession){
		Service dbService = new Service();
		dbService.startSession();
		try{
			List<ProteinDTO> proteinDTOs = dbService.getProteinDTOList(SINGLE_PROTEIN_QUERY, accession, null, 0.0);
			dbService.closeSession();
			if(proteinDTOs.isEmpty()){
				return Response.status(Response.Status.NOT_FOUND).entity("Resource not found for accession: " +  accession).build();
			}else{
				graphElements = new GraphDTO();
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
				//	linkDTO.setSource(accessions.lastIndexOf(proteinDTO.getProtein1().getUniprotAccession()));
				//	linkDTO.setTarget(accessions.lastIndexOf(proteinDTO.getProtein2().getUniprotAccession()));
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
				return Response.ok(graphElements).build();
			}
			
		}catch(NumberFormatException e){
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).entity("Jaccard Similarity Score should be between 0 and 1.").build();
		}
		
	}

}
