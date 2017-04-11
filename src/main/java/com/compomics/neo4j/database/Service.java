package com.compomics.neo4j.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import com.compomics.neo4j.model.dataTransferObjects.ProteinDTO;
import com.compomics.neo4j.model.nodes.Complex;
import com.compomics.neo4j.model.nodes.Go;
import com.compomics.neo4j.model.nodes.PathWay;
import com.compomics.neo4j.model.nodes.Project;
import com.compomics.neo4j.model.nodes.Protein;
import com.compomics.neo4j.model.relationshipTypes.Associate;

public class Service implements Serializable{

	private static final long serialVersionUID = -2151683664234775791L;
	private Properties prop = new Properties();
	private InputStream input = null;

	private Session session;
	private Connection connection;
	
	/**
	 * parameters to get data from neo4j database (key : parameter name, value : parameter)
	 */
	Map<String, Object> parameters;

	// queries
	private static final String PROJECT_QUERY = "projectSearch";
	private static final String ASSOCIATE_QUERY = "associationSearch";
	private static final String PATHWAY_QUERY = "pathwaySearch";
	private static final String COMPLEX_QUERY = "complexSearch";
	private static final String COMMONMF_QUERY = "commonMFSearch";
	private static final String COMMONBP_QUERY = "commonBPSearch";
	private static final String COMMONCC_QUERY = "commonCCSearch";
	private static final String CONTROL_QUERY = "associationControl";

	public Service() {
		connection = new Connection();
		session = connection.openConnection();
	}
	
	public String controlRelation(String accession1, String accession2){
		String result = "";
		try {
			input = getClass().getClassLoader().getResourceAsStream("query.properties");
			prop.load(input);
			result = checkAssociation(CONTROL_QUERY, accession1, accession2);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	/**
	 * Get ProteinDTOs by given query name and two proteins.
	 * @param queryName query name (it can be single or double).
	 * @param acc1 the first accession
	 * @param acc2
	 * @return
	 */
	public List<ProteinDTO> getProteinDTOList(String queryName, String accession1, String accession2) {
		List<ProteinDTO> proteinDTOS = new ArrayList<>();
		
		parameters = new HashMap<String, Object>();
		parameters.put("acc1", accession1);
		parameters.put("acc2", accession2);

		try {
			input = getClass().getClassLoader().getResourceAsStream("query.properties");
			prop.load(input);
			StatementResult result = session.run(prop.getProperty(queryName), parameters);

			while (result.hasNext()) {
				Record record = result.next();
				ProteinDTO proteinDTO = new ProteinDTO();
				// set protein 1
				Protein protein1 = new Protein();
				protein1.setUniprotAccession(record.get("p1_acc").asString());
				protein1.setProteinName(record.get("p1_name").asString());
				protein1.setGeneName(record.get("p1_gene_name").asString());
				protein1.setSpecies(record.get("p1_species").asString());
				protein1.setUniprotEntryName(record.get("p1_uniprot_entry_name").asString());
				protein1.setLength(record.get("p1_length").asString());
				protein1.setUniprotStatus(record.get("p1_uniport_status").asString());
				proteinDTO.setProtein1(protein1);
				// set protein 2
				Protein protein2 = new Protein();
				protein2.setUniprotAccession(record.get("p2_acc").asString());
				protein2.setProteinName(record.get("p2_name").asString());
				protein2.setGeneName(record.get("p2_gene_name").asString());
				protein2.setSpecies(record.get("p2_species").asString());
				protein2.setUniprotEntryName(record.get("p2_uniprot_entry_name").asString());
				protein2.setLength(record.get("p2_length").asString());
				protein2.setUniprotStatus(record.get("p2_uniport_status").asString());
				proteinDTO.setProtein2(protein2);
				accession1 = proteinDTO.getProtein1().getUniprotAccession();
				accession2 = proteinDTO.getProtein2().getUniprotAccession();
				// set associate relationship
				proteinDTO.setAssociate(getAssociate(ASSOCIATE_QUERY, accession1, accession2));
				
				proteinDTO.setBioGrid(proteinDTO.getAssociate().get(0).getBioGrid());
				proteinDTO.setParalog(proteinDTO.getAssociate().get(0).getParalog());
				proteinDTO.setJaccSimScore(proteinDTO.getAssociate().get(0).getJaccSimScore());
				proteinDTO.setIntact(proteinDTO.getAssociate().get(0).getIntact());
				proteinDTO.setInteract(proteinDTO.getAssociate().get(0).getInteract());
				
				// set projects
				proteinDTO.setProjects(getCommonProjectList(PROJECT_QUERY, accession1, accession2));
				// set pathways
				proteinDTO.setPathWays(getPathwayList(PATHWAY_QUERY, accession1, accession2));
				// set complexes
				proteinDTO.setComplexes(getComplexList(COMPLEX_QUERY, accession1, accession2));
				// set common MF
				proteinDTO.setMf(getCommonGOList(COMMONMF_QUERY, accession1, accession2));
				// set common BP
				proteinDTO.setBp(getCommonGOList(COMMONBP_QUERY, accession1, accession2));
				// set common CC
				proteinDTO.setCc(getCommonGOList(COMMONCC_QUERY, accession1, accession2));
				// set number of distinct complex
				proteinDTO.setDistinctComplexCount(record.get("distinct_comp").asInt());
				// set number of distinct path
				proteinDTO.setDistinctPathCount(record.get("distinct_path2").asInt());
				// set size of common project
				proteinDTO.setCommonProjectSize(record.get("common_project_size").asInt());
				
				proteinDTOS.add(proteinDTO);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return proteinDTOS;

	}

	/**
	 * Get common projects between two given proteins.
	 * @param queryName query for common projects
	 * @param accession1 the first protein accession.
	 * @param accession2 the second protein accession.
	 * @return list of common projects.
	 */
	private List<Project> getCommonProjectList(String queryName, String accession1, String accession2) {
		List<Project> projects = new ArrayList<>();

		parameters = new HashMap<String, Object>();
		parameters.put("acc1", accession1);
		parameters.put("acc2", accession2);
		
		StatementResult result = session.run(prop.getProperty(queryName), parameters);
		while (result.hasNext()) {
			Record record = result.next();
			// set Project
			if(!record.get("project_accession").asString().equals("null")){
				Project project = new Project();
				project.setProjectAccession(record.get("project_accession").asString());
				project.setKeywords(record.get("keywords").asList().toString().replace("[", "").replace("]", ""));
				project.setTissue(record.get("tissue").asList().toString().replace("[", "").replace("]", ""));
				project.setTags(record.get("tags").asList().toString().replace("[", "").replace("]", ""));

				projects.add(project);
			}			
		}

		return projects;

	}

	/**
	 * Get associate relation for the given proteins.
	 * @param queryName query for associate.
	 * @param accession1 the first protein accession.
	 * @param accession2 the second protein accession.
	 * @return associate object.
	 */
	private List<Associate> getAssociate(String queryName, String accession1, String accession2) {
		List<Associate> associates = new ArrayList<>();
		

		parameters = new HashMap<String, Object>();
		parameters.put("acc1", accession1);
		parameters.put("acc2", accession2);
		
		StatementResult result = session.run(prop.getProperty(queryName), parameters);
		if (result.hasNext()) {
			Record record = result.next();
			if(record.get("intact").asString().equals("yes")){
				for(int i=0; i<record.get("intact_confidence").asList().size(); i++){
					Associate associate = new Associate();
					associate.setJaccSimScore(record.get("jacc_sim_score").asDouble());
					associate.setInteract(record.get("interact").asString());
					associate.setParalog(record.get("paralog").asString());
					associate.setCommonAssayCount(record.get("common_assay_count").asString());
					associate.setIntactConfidence(record.get("intact_confidence").asList().get(i).toString());
					associate.setIntactDetection(record.get("intact_detection").asList().get(i).toString());
					associate.setIntactInteractionType(record.get("intact_interaction_type").asList().get(i).toString());
					if(record.get("intact").asString().equals("yes")){
						associate.setIntact(record.get("intact").asString());
					}else{
						associate.setIntact("no");
					}
					if(record.get("biogrid").asString().equals("yes")){
						associate.setBioGrid(record.get("biogrid").asString());
					}else{
						associate.setBioGrid("no");
					}
					
					associates.add(associate);
				}
			}else{
				Associate associate = new Associate();
				associate.setJaccSimScore(record.get("jacc_sim_score").asDouble());
				associate.setInteract(record.get("interact").asString());
				associate.setParalog(record.get("paralog").asString());
				associate.setCommonAssayCount(record.get("common_assay_count").asString());
				associate.setIntactConfidence(record.get("intact_confidence").asList().toString().replace("[", "").replace("]", ""));
				associate.setIntactDetection(record.get("intact_detection").asList().toString().replace("[", "").replace("]", ""));
				associate.setIntactInteractionType(record.get("intact_interaction_type").asList().toString().replace("[", "").replace("]", ""));
				if(record.get("intact").asString().equals("yes")){
					associate.setIntact(record.get("intact").asString());
				}else{
					associate.setIntact("no");
				}
				if(record.get("biogrid").asString().equals("yes")){
					associate.setBioGrid(record.get("biogrid").asString());
				}else{
					associate.setBioGrid("no");
				}
				
				associates.add(associate);
			}			
		}
		return associates;
	}
	
	/**
	 * Get pathways between two given proteins.
	 * @param queryName queryName query for pathway.
	 * @param accession1 the first protein accession.
	 * @param accession2 the second protein accession.
	 * @return list of pathways
	 */
	private List<PathWay> getPathwayList(String queryName, String accession1, String accession2){
		List<PathWay> pathWays = new ArrayList<>();
		
		parameters = new HashMap<String, Object>();
		parameters.put("acc1", accession1);
		parameters.put("acc2", accession2);
		
		StatementResult result = session.run(prop.getProperty(queryName), parameters);
		while (result.hasNext()) {
			Record record = result.next();
			// set Project
			PathWay pathWay = new PathWay();
			pathWay.setReactomeAccession(record.get("reactome_accession").asString());
			pathWay.setPathwayName(record.get("pathway_name").asString());
			pathWay.setEvidenceCode(record.get("evidence_code").asString());
			
			pathWays.add(pathWay);
		}
		
		return pathWays;
	}
	
	/**
	 * Get complexes between two given proteins.
	 * @param queryName queryName query for complex.
	 * @param accession1 the first protein accession.
	 * @param accession2 the second protein accession.
	 * @return list of complexes
	 */
	private List<Complex> getComplexList(String queryName, String accession1, String accession2){
		List<Complex> complexs = new ArrayList<>();
		
		parameters = new HashMap<String, Object>();
		parameters.put("acc1", accession1);
		parameters.put("acc2", accession2);
		
		StatementResult result = session.run(prop.getProperty(queryName), parameters);
		while (result.hasNext()) {
			Record record = result.next();
			// set Project
			if(record.get("corum_id") != null && !record.get("corum_id").asString().trim().equals("") && !record.get("corum_id").asString().trim().equals("null") ){
				Complex complex = new Complex();
				complex.setCorumId(record.get("corum_id").asString());
				complex.setComplexName(record.get("complex_name").asString());
				complex.setComplexComment(record.get("complex_comment").asString());
				complex.setCellLine(record.get("cell_line").asString());
				complex.setDiseaseComment(record.get("disease_comment").asString());
				complex.setSubUnitComment(record.get("subunit_comment").asString());
				complex.setPubmedId(record.get("pubmed_id").asString());
				complex.setPurificationMethod(record.get("purification_method").asList().toString().replace("[", "").replace("]", ""));
				
				complexs.add(complex);
				
			}
			
		}
		
		return complexs;
	}
	
	/**
	 * Get common GOs between two given proteins.
	 * @param queryName queryName query for common GO.
	 * @param accession1 the first protein accession.
	 * @param accession2 the second protein accession.
	 * @return list of common GOs
	 */
	private List<Go> getCommonGOList(String queryName, String accession1, String accession2){
		List<Go> commonGos = new ArrayList<>();

		parameters = new HashMap<String, Object>();
		parameters.put("acc1", accession1);
		parameters.put("acc2", accession2);
		
		StatementResult result = session.run(prop.getProperty(queryName), parameters);
		while (result.hasNext()) {
			Record record = result.next();
			// set Project
			if(!record.get("go_id").asString().equals("null")){
				Go go = new Go();
				go.setId(record.get("go_id").asString());
				go.setName(record.get("go_name").asString());
				
				commonGos.add(go);
			}
		}
		
		return commonGos;
	}

	/**
	 * Check if association exists between two proteins.
	 * @param queryName
	 * @param accession1
	 * @param accession2
	 * @return list of path if exists, else return empty string.
	 */
	private String checkAssociation(String queryName, String accession1, String accession2) {

		parameters = new HashMap<String, Object>();
		parameters.put("acc1", accession1);
		parameters.put("acc2", accession2);

		StatementResult result = session.run(prop.getProperty(queryName), parameters);
		if (result.hasNext()) {
			Record record = result.next();
			return record.get("path").asList().toString();
		}else{
			return "";
		}
	}
}
