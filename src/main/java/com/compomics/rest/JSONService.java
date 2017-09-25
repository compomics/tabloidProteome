package com.compomics.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.compomics.neo4j.database.Service;
import com.compomics.neo4j.model.dataTransferObjects.ProteinDTO;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.ResponseHeader;



@Path("/protein")
@Api(value = "protein", authorizations = {
	      @Authorization(value="sampleoauth", scopes = {})
	    })
public class JSONService implements Serializable{
	
	private static final long serialVersionUID = -4536221884626642852L;

  
    // queries
    private static final String SINGLE_PROTEIN_QUERY = "singleProteinSearch";
    private static final String DOUBLE_PROTEIN_QUERY = "doubleProteinSearch";
	
    private List<ProteinDTO> proteinDTOs;
    

	@GET
	@Path("/{accession}&{jaccard}/")
	@ApiOperation(value = "Finds protein and related paths by Uniprot accessions and Jaccard Similarity Score",
	    response = ProteinDTO.class,
	    responseContainer = "List")
	 @ApiResponses(value = {
		      @ApiResponse(code = 400, message = "Invalid ID supplied", 
		                   responseHeaders = @ResponseHeader(name = "X-Rack-Cache", description = "Explains whether or not a cache was used", response = Boolean.class)),
		      @ApiResponse(code = 404, message = "Protein not found") })
	@Produces(MediaType.APPLICATION_JSON)
	@JacksonFeatures(serializationEnable =  { SerializationFeature.INDENT_OUTPUT })
	public Response getProteinDTOsInJSON(@PathParam("accession") String accession, @PathParam("jaccard") String jaccard){
		Service dbService = new Service();
		dbService.startSession();
		try{
			proteinDTOs = dbService.getProteinDTOList(SINGLE_PROTEIN_QUERY, accession, null, Double.valueOf(jaccard));
			dbService.closeSession();
			if(proteinDTOs.isEmpty()){
				return Response.status(Response.Status.NOT_FOUND).entity("Resource not found for accession: " +  accession).build();
			}else{
				return Response.ok(proteinDTOs).build();
			}
			
		}catch(NumberFormatException e){
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).entity("Jaccard Similarity Score should be between 0 and 1.").build();
		}
		
		
		
	}
	
	
	@GET
	@Path("/{accession1}&{accession2}&{jaccard}/")
	@ApiOperation(value = "Finds provided proteins and relations between them filtering by Jaccard Similarity Score",
    response = ProteinDTO.class,
    responseContainer = "List")
	@ApiResponses(value = {
		      @ApiResponse(code = 400, message = "Invalid ID supplied", 
		                   responseHeaders = @ResponseHeader(name = "X-Rack-Cache", description = "Explains whether or not a cache was used", response = Boolean.class)),
		      @ApiResponse(code = 404, message = "Protein not found") })
	@Produces(MediaType.APPLICATION_JSON)
	@JacksonFeatures(serializationEnable =  { SerializationFeature.INDENT_OUTPUT })
	public Response getProteinDTOsInJSON(@PathParam("accession1") String accession1, @PathParam("accession2") String accession2, @PathParam("jaccard") String jaccard){
		Service dbService = new Service();
		dbService.startSession();
		try{
			proteinDTOs = dbService.getProteinDTOList(DOUBLE_PROTEIN_QUERY, accession1, accession2, Double.valueOf(jaccard));
			dbService.closeSession();
			if(proteinDTOs.isEmpty()){
				return Response.status(Response.Status.NOT_FOUND).entity("Resource not found for accessions: " +  accession1 + " and " + accession2).build();
			}else{
				return Response.ok(proteinDTOs).build();
			}
		}catch(NumberFormatException e){
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).entity("Jaccard Similarity Score should be between 0 and 1.").build();
		}
		
	}
	
	@GET
	@Path("/gene/{gene}&{jaccard}/")
	@ApiOperation(value = "Finds protein and related paths by given entrez gene id or gene name and Jaccard Similarity Score",
	    response = ProteinDTO.class,
	    responseContainer = "List")
	 @ApiResponses(value = {
		      @ApiResponse(code = 400, message = "Invalid ID supplied", 
		                   responseHeaders = @ResponseHeader(name = "X-Rack-Cache", description = "Explains whether or not a cache was used", response = Boolean.class)),
		      @ApiResponse(code = 404, message = "Protein not found") })
	@Produces(MediaType.APPLICATION_JSON)
	@JacksonFeatures(serializationEnable =  { SerializationFeature.INDENT_OUTPUT })
	public Response getProteinDTOsInJSONbyGene(@PathParam("gene") String gene, @PathParam("jaccard") String jaccard){
		Service dbService = new Service();
		dbService.startSession();
		try{
			gene = gene.toUpperCase();
			proteinDTOs = new ArrayList<>();
			dbService.findProteinsByGene(gene, "").forEach(proteinDTO -> {
				proteinDTOs.addAll(dbService.getProteinDTOList(SINGLE_PROTEIN_QUERY, proteinDTO.getProtein1().getUniprotAccession(), null, Double.valueOf(jaccard)));
			});
			
			dbService.closeSession();
			if(proteinDTOs.isEmpty()){
				return Response.status(Response.Status.NOT_FOUND).entity("Resource not found for gene: " +  gene).build();
			}else{
				removeDuplicateTuples();
				return Response.ok(proteinDTOs).build();
			}
			
		}catch(NumberFormatException e){
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).entity("Jaccard Similarity Score should be between 0 and 1.").build();
		}
	}
	
	@GET
	@Path("/gene/{gene1}&{gene2}&{jaccard}/")
	@ApiOperation(value = "Finds proteins and relations between provided genes (entrez gene id or name) and filtering by Jaccard Similarity Score",
    response = ProteinDTO.class,
    responseContainer = "List")
	@ApiResponses(value = {
		      @ApiResponse(code = 400, message = "Invalid ID supplied", 
		                   responseHeaders = @ResponseHeader(name = "X-Rack-Cache", description = "Explains whether or not a cache was used", response = Boolean.class)),
		      @ApiResponse(code = 404, message = "Protein not found") })
	@Produces(MediaType.APPLICATION_JSON)
	@JacksonFeatures(serializationEnable =  { SerializationFeature.INDENT_OUTPUT })
	public Response getProteinDTOsInJSONbyGene(@PathParam("gene1") String gene1, @PathParam("gene2") String gene2, @PathParam("jaccard") String jaccard){
		Service dbService = new Service();
		dbService.startSession();
		try{
			gene1 = gene1.toUpperCase();
			gene2 = gene2.toUpperCase();
			proteinDTOs = new ArrayList<>();
			dbService.findProteinsByGene(gene1, gene2).forEach(proteinDTO -> {
				proteinDTOs.addAll(dbService.getProteinDTOList(DOUBLE_PROTEIN_QUERY, proteinDTO.getProtein1().getUniprotAccession(), proteinDTO.getProtein2().getUniprotAccession(), Double.valueOf(jaccard)));
			});
			
			dbService.closeSession();
			if(proteinDTOs.isEmpty()){
				return Response.status(Response.Status.NOT_FOUND).entity("Resource not found for genes: " +  gene1 + " and " + gene2).build();
			}else{
				removeDuplicateTuples();
				return Response.ok(proteinDTOs).build();
			}
		}catch(NumberFormatException e){
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).entity("Jaccard Similarity Score should be between 0 and 1.").build();
		}
		
	}
	
	@GET
	@Path("/pathway/{pathway}&{jaccard}/")
	@ApiOperation(value = "Finds protein and related paths by given pathway name or reactome accession and Jaccard Similarity Score",
	    response = ProteinDTO.class,
	    responseContainer = "List")
	 @ApiResponses(value = {
		      @ApiResponse(code = 400, message = "Invalid ID supplied", 
		                   responseHeaders = @ResponseHeader(name = "X-Rack-Cache", description = "Explains whether or not a cache was used", response = Boolean.class)),
		      @ApiResponse(code = 404, message = "Protein not found") })
	@Produces(MediaType.APPLICATION_JSON)
	@JacksonFeatures(serializationEnable =  { SerializationFeature.INDENT_OUTPUT })
	public Response getProteinDTOsInJSONbyPathway(@PathParam("pathway") String pathway, @PathParam("jaccard") String jaccard){
		Service dbService = new Service();
		dbService.startSession();
		try{
			pathway = pathway.toUpperCase();
			proteinDTOs = new ArrayList<>();
			
			if(!pathway.startsWith("R-HSA")){
				if(pathway.split(" ")[pathway.split(" ").length-1].equals("PATHWAY")){
					pathway = StringUtils.join(Arrays.copyOf(pathway.split(" "), pathway.split(" ").length-1), " ");
				}
				pathway = ".*"+pathway+".*";
			}
			
			dbService.findPathwayDTOs(pathway).forEach(pathwayDTO -> {
				pathwayDTO.getProteinDTOs().forEach(proteinDTO -> {
					proteinDTOs.addAll(dbService.getProteinDTOList(DOUBLE_PROTEIN_QUERY, proteinDTO.getProtein1().getUniprotAccession(), proteinDTO.getProtein2().getUniprotAccession(), Double.valueOf(jaccard)));
				});
				
			});
			
			dbService.closeSession();
			if(proteinDTOs.isEmpty()){
				return Response.status(Response.Status.NOT_FOUND).entity("Resource not found for pathway: " +  pathway).build();
			}else{
				removeDuplicateTuples();
				return Response.ok(proteinDTOs).build();
			}
			
		}catch(NumberFormatException e){
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).entity("Jaccard Similarity Score should be between 0 and 1.").build();
		}
	}
	
	
	@GET
	@Path("/disease/{disease}&{jaccard}/")
	@ApiOperation(value = "Finds protein and related paths by given disease name or disGENet id and Jaccard Similarity Score",
	    response = ProteinDTO.class,
	    responseContainer = "List")
	 @ApiResponses(value = {
		      @ApiResponse(code = 400, message = "Invalid ID supplied", 
		                   responseHeaders = @ResponseHeader(name = "X-Rack-Cache", description = "Explains whether or not a cache was used", response = Boolean.class)),
		      @ApiResponse(code = 404, message = "Protein not found") })
	@Produces(MediaType.APPLICATION_JSON)
	@JacksonFeatures(serializationEnable =  { SerializationFeature.INDENT_OUTPUT })
	public Response getProteinDTOsInJSONbyDisease(@PathParam("disease") String disease, @PathParam("jaccard") String jaccard){
		Service dbService = new Service();
		dbService.startSession();
		try{
			disease = disease.toUpperCase();
			if(!disease.startsWith("C") || disease.length() != 8){
				if(disease.split(" ")[disease.split(" ").length-1].equals("DISEASE")){
					disease = StringUtils.join(Arrays.copyOf(disease.split(" "), disease.split(" ").length-1), " ");
				}
				disease = ".*"+disease+".*";
			}
			
			proteinDTOs = new ArrayList<>();
			dbService.findDiseaseDTOs(disease).forEach(diseaseDTO -> {
				diseaseDTO.getProteinDTOs().forEach(proteinDTO -> {
					proteinDTOs.addAll(dbService.getProteinDTOList(DOUBLE_PROTEIN_QUERY, proteinDTO.getProtein1().getUniprotAccession(), proteinDTO.getProtein2().getUniprotAccession(), Double.valueOf(jaccard)));
				});
				
			});
			
			dbService.closeSession();
			if(proteinDTOs.isEmpty()){
				return Response.status(Response.Status.NOT_FOUND).entity("Resource not found for disease: " +  disease).build();
			}else{
				removeDuplicateTuples();
				return Response.ok(proteinDTOs).build();
			}
			
		}catch(NumberFormatException e){
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).entity("Jaccard Similarity Score should be between 0 and 1.").build();
		}
	}
	
	/**
	 * Remove duplicate protein dtos like (protein1, protein2) and (protein2, protein1)
	 * return proteinDTO list
	 */
	public void removeDuplicateTuples(){
		for(int i=0; i<proteinDTOs.size(); i++){
		      for(int j=i; j<proteinDTOs.size(); j++){
		      	if(proteinDTOs.get(i).getProtein1().getUniprotAccession().equals(proteinDTOs.get(j).getProtein2().getUniprotAccession()) 
		      			&& proteinDTOs.get(i).getProtein2().getUniprotAccession().equals(proteinDTOs.get(j).getProtein1().getUniprotAccession())){
		      		proteinDTOs.remove(j);
		        }
		      }
		 }
	}

}
