package com.compomics.neo4j.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import com.compomics.neo4j.model.dataTransferObjects.DiseaseDTO;
import com.compomics.neo4j.model.dataTransferObjects.PathwayDTO;
import com.compomics.neo4j.model.dataTransferObjects.ProteinDTO;
import com.compomics.neo4j.model.nodes.Complex;
import com.compomics.neo4j.model.nodes.Disease;
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
	private static final String DISEASE_QUERY = "diseaseSearch";
	private static final String CONTROL_QUERY = "associationControl";
	private static final String GENE_QUERY_SINGLE = "searchByGeneSingle";
	private static final String GENE_QUERY_DOUBLE = "searchByGeneDouble";
	private static final String PATHWAY_SEARCH_QUERY = "searchByPathway";
	private static final String PATHWAY_FIND_PROTEIN = "findProteinsByPathway";
	private static final String DISEASE_SEARCH_QUERY = "searchByDisease";
	private static final String DISEASE_FIND_PROTEIN = "findProteinsByDisease";
	private static final String PROTEIN_FIND_BY_NAME_SINGLE = "searchByProteinNameSingle";
	private static final String PROTEIN_FIND_BY_NAME_DOUBLE = "searchByProteinNameDouble";
	private static final String ONE_TO_ONE_SEARCH = "oneToOneSearch";

	public Service() {
		connection = new Connection();
		connection.openConnection();
	}
	
	public void startSession(){
		session = connection.startSession();
	}
	
	public void closeSession(){
		session.close();
	}
	/**
	 * check if there is any relation between two proteins
	 * @param accession1
	 * @param accession2
	 * @return
	 */
	public String controlRelation(String accession1, String accession2, double jaccScore){
		String result = "";
		try {
			input = getClass().getClassLoader().getResourceAsStream("query.properties");
			prop.load(input);
			result = checkAssociation(CONTROL_QUERY, accession1, accession2, jaccScore);
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
	
	public List<ProteinDTO> findProteinsByName(String proteinName1, String proteinName2, double jaccScore){
		List<ProteinDTO> proteins = new ArrayList<>();
		try {
			input = getClass().getClassLoader().getResourceAsStream("query.properties");
			prop.load(input);
			if(proteinName2.equals("")){
				proteins = searchProteinByName(PROTEIN_FIND_BY_NAME_SINGLE, proteinName1, proteinName2, jaccScore);
			}else{
				proteins = searchProteinByName(PROTEIN_FIND_BY_NAME_DOUBLE, proteinName1, proteinName2, jaccScore);
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
		return proteins;
	}
	
	private List<ProteinDTO> searchProteinByName( String queryName, String proteinName1, String proteinName2, double jaccScore){
		
		List<ProteinDTO> proteins = new ArrayList<>();
		parameters = new HashMap<String, Object>();
		parameters.put("proteinName1", proteinName1);
		parameters.put("proteinName2", proteinName2);
		parameters.put("jacc", jaccScore);

		StatementResult result = session.run(prop.getProperty(queryName), parameters);
		while (result.hasNext()) {
			Record record = result.next();
			ProteinDTO proteinDTO = new ProteinDTO();
			Protein protein1 = new Protein();
			protein1.setUniprotAccession(record.get("uniprot_accession1").asString());
			protein1.setProteinName(record.get("protein_name1").asString());
			protein1.setGeneNames(record.get("gene_name1").asList());
			proteinDTO.setProtein1(protein1);
			if(queryName.equals(PROTEIN_FIND_BY_NAME_DOUBLE)){
				Protein protein2 = new Protein();
				protein2.setUniprotAccession(record.get("uniprot_accession2").asString());
				protein2.setProteinName(record.get("protein_name2").asString());
				protein2.setGeneNames(record.get("gene_name2").asList());
				proteinDTO.setProtein2(protein2);
				Associate associate = new Associate();
				associate.setJaccSimScore(record.get("jacc_sim_score").asDouble());
				List<Associate> associates = new ArrayList<>();
				associates.add(associate);
				proteinDTO.setAssociate(associates);
			}
			proteins.add(proteinDTO);	
		}
		
		return proteins;
	}
	
	/**
	 * find protein accession by gene id or name
	 * @param gene
	 * @return
	 */
	public List<ProteinDTO> findProteinsByGene(String gene1, String gene2){
		List<ProteinDTO> proteins = new ArrayList<>();
		try {
			input = getClass().getClassLoader().getResourceAsStream("query.properties");
			prop.load(input);
			if(gene2.equals("")){
				proteins = findProteinsByGeneIdOrName(GENE_QUERY_SINGLE, gene1, gene2);
			}else{
				proteins = findProteinsByGeneIdOrName(GENE_QUERY_DOUBLE, gene1, gene2);
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
		return proteins;
	}
	
	public List<PathwayDTO> findPathwayDTOs(String pathway){
		List<PathwayDTO> pathwayDTOs = new ArrayList<>();
		try {
			input = getClass().getClassLoader().getResourceAsStream("query.properties");
			prop.load(input);
			parameters = new HashMap<String, Object>();
			parameters.put("pathway", pathway);
			StatementResult result = session.run(prop.getProperty(PATHWAY_SEARCH_QUERY), parameters);
			while (result.hasNext()) {
				Record record = result.next();
				PathWay pthwy = new PathWay();
				pthwy.setPathwayName(record.get("pathway_name").asString());
				pthwy.setReactomeAccession(record.get("reactome_accession").asString());
				PathwayDTO pathwayDTO = new PathwayDTO();
				pathwayDTO.setPathWay(pthwy);
				pathwayDTO.setProteinDTOs(findProteinDTOsByPathway(pthwy.getReactomeAccession()));
				pathwayDTOs.add(pathwayDTO);
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
		return pathwayDTOs;
	}
	
	private List<ProteinDTO> findProteinDTOsByPathway(String reactomeAccession){
		List<ProteinDTO> proteinDTOS = new ArrayList<>();
		parameters = new HashMap<String, Object>();
		parameters.put("reactomeAccession", reactomeAccession);
		StatementResult result = session.run(prop.getProperty(PATHWAY_FIND_PROTEIN), parameters);
		while (result.hasNext()) {
			Record record = result.next();
			ProteinDTO proteinDTO = new ProteinDTO();
			Protein protein1 = new Protein();
			protein1.setUniprotAccession(record.get("uniprot_accession1").asString());
			protein1.setProteinName(record.get("protein_name1").asString());
			proteinDTO.setProtein1(protein1);
			Protein protein2 = new Protein();
			protein2.setUniprotAccession(record.get("uniprot_accession2").asString());
			protein2.setProteinName(record.get("protein_name2").asString());
			proteinDTO.setProtein2(protein2);
			Associate associate = new Associate();
			associate.setJaccSimScore(record.get("jacc_sim_score").asDouble());
			List<Associate> associates = new ArrayList<>();
			associates.add(associate);
			proteinDTO.setAssociate(associates);
			
			proteinDTOS.add(proteinDTO);		
		}
		
		Collections.sort(proteinDTOS, new Comparator<ProteinDTO>() {
			@Override
			public int compare(ProteinDTO o1, ProteinDTO o2) {
				return o2.getAssociate().get(0).getJaccSimScore().compareTo(o1.getAssociate().get(0).getJaccSimScore());
			}
		});
		
		return proteinDTOS;
	}
	
	public List<DiseaseDTO> findDiseaseDTOs(String disease){
		List<DiseaseDTO> diseaseDTOs = new ArrayList<>();
		try {
			input = getClass().getClassLoader().getResourceAsStream("query.properties");
			prop.load(input);
			parameters = new HashMap<String, Object>();
			parameters.put("disease", disease);
			StatementResult result = session.run(prop.getProperty(DISEASE_SEARCH_QUERY), parameters);
			while (result.hasNext()) {
				Record record = result.next();
				Disease dss = new Disease();
				dss.setDiseaseName(record.get("disease_name").asString());
				dss.setDisgenetId(record.get("disgenet_id").asString());
				DiseaseDTO diseaseDTO = new DiseaseDTO();
				diseaseDTO.setDisease(dss);
				diseaseDTO.setProteinDTOs(findProteinDTOsByDisease(dss.getDisgenetId()));
				diseaseDTOs.add(diseaseDTO);
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
		return diseaseDTOs;
	}
	
	private List<ProteinDTO> findProteinDTOsByDisease(String disgenetId){
		List<ProteinDTO> proteinDTOS = new ArrayList<>();
		parameters = new HashMap<String, Object>();
		parameters.put("disgenetId", disgenetId);
		StatementResult result = session.run(prop.getProperty(DISEASE_FIND_PROTEIN), parameters);
		while (result.hasNext()) {
			Record record = result.next();
			ProteinDTO proteinDTO = new ProteinDTO();
			Protein protein1 = new Protein();
			protein1.setUniprotAccession(record.get("uniprot_accession1").asString());
			protein1.setProteinName(record.get("protein_name1").asString());
			proteinDTO.setProtein1(protein1);
			Protein protein2 = new Protein();
			protein2.setUniprotAccession(record.get("uniprot_accession2").asString());
			protein2.setProteinName(record.get("protein_name2").asString());
			proteinDTO.setProtein2(protein2);
			Associate associate = new Associate();
			associate.setJaccSimScore(record.get("jacc_sim_score").asDouble());
			List<Associate> associates = new ArrayList<>();
			associates.add(associate);
			proteinDTO.setAssociate(associates);
		
			proteinDTOS.add(proteinDTO);		
		}
		
		Collections.sort(proteinDTOS, new Comparator<ProteinDTO>() {
			@Override
			public int compare(ProteinDTO o1, ProteinDTO o2) {
				return o2.getAssociate().get(0).getJaccSimScore().compareTo(o1.getAssociate().get(0).getJaccSimScore());
			}
		});
		
		return proteinDTOS;
	}
	
	/**
	 * Get Protein DTO list for multiple proteins, for one to one search
	 * @param queryName
	 * @param accessions
	 * @param jaccScore
	 * @return
	 */
	public List<ProteinDTO> getProteinDTOListForMultipleProteins(List<String> accessions1, List<String> accessions2, double jaccScore){
		List<ProteinDTO> proteinDTOS = new ArrayList<>();

			parameters = new HashMap<String, Object>();
			parameters.put("array1", accessions1);
			parameters.put("array2", accessions2);
			parameters.put("jacc", jaccScore);
			try {
				input = getClass().getClassLoader().getResourceAsStream("query.properties");
				prop.load(input);
				StatementResult result = session.run(prop.getProperty(ONE_TO_ONE_SEARCH), parameters);
				proteinDTOS = createProteinDTOList(result, proteinDTOS);
				
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
	 * Get ProteinDTOs by given query name and two proteins.
	 * @param queryName query name (it can be single or double).
	 * @param acc1 the first accession
	 * @param acc2 the second accession
	 * @return
	 */
	public List<ProteinDTO> getProteinDTOList(String queryName, String accession1, String accession2, double jaccScore) {
		List<ProteinDTO> proteinDTOS = new ArrayList<>();
		
		parameters = new HashMap<String, Object>();
		parameters.put("acc1", accession1);
		parameters.put("acc2", accession2);
		parameters.put("jacc", jaccScore);

		try {
			input = getClass().getClassLoader().getResourceAsStream("query.properties");
			prop.load(input);
			StatementResult result = session.run(prop.getProperty(queryName), parameters);
			proteinDTOS = createProteinDTOList(result, proteinDTOS);
			
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
		
		Collections.sort(proteinDTOS, new Comparator<ProteinDTO>() {
			@Override
			public int compare(ProteinDTO o1, ProteinDTO o2) {
				return o2.getAssociate().get(0).getJaccSimScore().compareTo(o1.getAssociate().get(0).getJaccSimScore());
			}
		});

		return proteinDTOS;

	}
	/**
	 * Create proteinDTO list. Check if there is duplicated couple. 
	 * Do not add the same path twice.
	 * @param result
	 * @param proteinDTOs
	 * @return
	 */
	private List<ProteinDTO> createProteinDTOList(StatementResult result, List<ProteinDTO> proteinDTOs){
		
		while (result.hasNext()) {
			Record record = result.next();
			ProteinDTO proteinDTO = new ProteinDTO();
			// set protein 1
			Protein protein1 = new Protein();
			protein1.setUniprotAccession(record.get("p1_acc").asString());
			protein1.setProteinName(record.get("p1_name").asString());
			protein1.setGeneNames(record.get("p1_gene_name").asList());
			protein1.setGeneIds(record.get("p1_gene_id").asList());
			protein1.setSpecies(record.get("p1_species").asString());
			protein1.setUniprotEntryName(record.get("p1_uniprot_entry_name").asString());
			protein1.setLength(record.get("p1_length").asString());
			protein1.setUniprotStatus(record.get("p1_uniport_status").asString());
			proteinDTO.setProtein1(protein1);
			// set protein 2
			Protein protein2 = new Protein();
			protein2.setUniprotAccession(record.get("p2_acc").asString());
			protein2.setProteinName(record.get("p2_name").asString());
			protein2.setGeneNames(record.get("p2_gene_name").asList());
			protein2.setGeneIds(record.get("p2_gene_id").asList());
			protein2.setSpecies(record.get("p2_species").asString());
			protein2.setUniprotEntryName(record.get("p2_uniprot_entry_name").asString());
			protein2.setLength(record.get("p2_length").asString());
			protein2.setUniprotStatus(record.get("p2_uniport_status").asString());
			proteinDTO.setProtein2(protein2);
			String accession1 = proteinDTO.getProtein1().getUniprotAccession();
			String accession2 = proteinDTO.getProtein2().getUniprotAccession();
			// set associate relationship
			proteinDTO.setAssociate(getAssociate(ASSOCIATE_QUERY, accession1, accession2));
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
			// set diseases
			List<Disease> diseases = getDiseases(DISEASE_QUERY, accession1, accession2);
			proteinDTO.setDiseases(diseases);
			// set number of distinct complex
			proteinDTO.setDistinctComplexCount(record.get("distinct_comp").asInt());
			// set number of distinct path
			proteinDTO.setDistinctPathCount(record.get("distinct_path2").asInt());
			// set size of common project
			proteinDTO.setCommonProjectSize(record.get("common_project_size").asInt());
			// set disease size
			proteinDTO.setDiseaseCount(diseases.size());
			for(ProteinDTO p : proteinDTOs){
				if(p.getProtein1().getUniprotAccession().equals(proteinDTO.getProtein2().getUniprotAccession())){
					continue;
				}
			}
			proteinDTOs.add(proteinDTO);
		}
		return proteinDTOs;
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
				project.setInstruments(record.get("instruments").asList().toString().replace("[", "").replace("]", ""));

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
			if(record.get("reactome_accession") != null && !record.get("reactome_accession").asString().trim().equals("") && !record.get("reactome_accession").asString().trim().equals("null")){
				PathWay pathWay = new PathWay();
				pathWay.setReactomeAccession(record.get("reactome_accession").asString());
				pathWay.setPathwayName(record.get("pathway_name").asString());
				pathWay.setEvidenceCode(record.get("evidence_code").asString());
				
				pathWays.add(pathWay);
			}
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
	 * @param queryName query for common GO.
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
	 * Get common diseases between two proteins
	 * @param queryName query for common diseases.
	 * @param accession1 the first protein accession.
	 * @param accession2 the second protein accession.
	 * @return list of common diseases
	 */
	private List<Disease> getDiseases(String queryName, String accession1, String accession2){
		List<Disease> diseases = new ArrayList<>();
		
		parameters = new HashMap<String, Object>();
		parameters.put("acc1", accession1);
		parameters.put("acc2", accession2);
		
		StatementResult result = session.run(prop.getProperty(queryName), parameters);
		while (result.hasNext()) {
			Record record = result.next();
			// set Project
			if(!record.get("disgenet_id").asString().equals("null")){
				Disease disease = new Disease();
				disease.setDisgenetId(record.get("disgenet_id").asString());
				disease.setDiseaseName(record.get("disease_name").asString());
				
				diseases.add(disease);
			}
		}
		
		return diseases;
	}

	/**
	 * Check if association exists between two proteins.
	 * @param queryName
	 * @param accession1
	 * @param accession2
	 * @return list of path if exists, else return empty string.
	 */
	private String checkAssociation(String queryName, String accession1, String accession2, double jaccScore) {

		parameters = new HashMap<String, Object>();
		parameters.put("acc1", accession1);
		parameters.put("acc2", accession2);
		parameters.put("jacc", jaccScore);

		StatementResult result = session.run(prop.getProperty(queryName), parameters);
		if (result.hasNext()) {
			Record record = result.next();
			return record.get("path").asList().toString();
		}else{
			return "*";
		}
	}
	/**
	 * find protein by gene id or gene name
	 * @param gene gene id or name
	 * @param queryName
	 * @return
	 */
	private List<ProteinDTO> findProteinsByGeneIdOrName( String queryName, String gene1, String gene2){
		List<ProteinDTO> proteins = new ArrayList<>();
		parameters = new HashMap<String, Object>();
		parameters.put("gene1", gene1);
		parameters.put("gene2", gene2);

		StatementResult result = session.run(prop.getProperty(queryName), parameters);
		while (result.hasNext()) {
			Record record = result.next();
			ProteinDTO proteinDTO = new ProteinDTO();
			Protein protein1 = new Protein();
			protein1.setUniprotAccession(record.get("uniprot_accession1").asString());
			protein1.setProteinName(record.get("protein_name1").asString());
			protein1.setGeneNames(record.get("gene_name1").asList());
			proteinDTO.setProtein1(protein1);
			if(queryName.equals(GENE_QUERY_DOUBLE)){
				Protein protein2 = new Protein();
				protein2.setUniprotAccession(record.get("uniprot_accession2").asString());
				protein2.setProteinName(record.get("protein_name2").asString());
				protein2.setGeneNames(record.get("gene_name2").asList());
				proteinDTO.setProtein2(protein2);
				Associate associate = new Associate();
				associate.setJaccSimScore(record.get("jacc_sim_score").asDouble());
				List<Associate> associates = new ArrayList<>();
				associates.add(associate);
				proteinDTO.setAssociate(associates);
			}
			proteins.add(proteinDTO);	
		}
		return proteins;
	}
	
}
