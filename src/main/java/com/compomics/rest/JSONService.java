package com.compomics.rest;

import java.io.Serializable;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
			List<ProteinDTO> proteinDTOs = dbService.getProteinDTOList(SINGLE_PROTEIN_QUERY, accession, null, Double.valueOf(jaccard));
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
			List<ProteinDTO> proteinDTOs = dbService.getProteinDTOList(DOUBLE_PROTEIN_QUERY, accession1, accession2, Double.valueOf(jaccard));
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

}
