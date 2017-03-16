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



@Path("/protein")
public class JSONService implements Serializable{
	
	private static final long serialVersionUID = -4536221884626642852L;

  
    // queries
    private static final String SINGLE_PROTEIN_QUERY = "singleProteinSearch";
    private static final String DOUBLE_PROTEIN_QUERY = "doubleProteinSearch";
	
    

	@GET
	@Path("/{accession}")
	@Produces(MediaType.APPLICATION_JSON)
	@JacksonFeatures(serializationEnable =  { SerializationFeature.INDENT_OUTPUT })
	public Response getProteinDTOsInJSON(@PathParam("accession") String accession){
		Service dbService = new Service();
		List<ProteinDTO> proteinDTOs = dbService.getProteinDTOList(SINGLE_PROTEIN_QUERY, accession, "");
		if(proteinDTOs.isEmpty()){
			return Response.status(Response.Status.NOT_FOUND).entity("Resource not found for accession: " +  accession).build();
		}else{
			return Response.ok(proteinDTOs).build();
		}
		
	}
	
	
	@GET
	@Path("/{accession1}&{accession2}/")
	@Produces(MediaType.APPLICATION_JSON)
	@JacksonFeatures(serializationEnable =  { SerializationFeature.INDENT_OUTPUT })
	public Response getProteinDTOsInJSON(@PathParam("accession1") String accession1, @PathParam("accession2") String accession2){
		Service dbService = new Service();
		List<ProteinDTO> proteinDTOs = dbService.getProteinDTOList(DOUBLE_PROTEIN_QUERY, accession1, accession2);
		if(proteinDTOs.isEmpty()){
			return Response.status(Response.Status.NOT_FOUND).entity("Resource not found for accessions: " +  accession1 + " and " + accession2).build();
		}else{
			return Response.ok(proteinDTOs).build();
		}
	}

}
