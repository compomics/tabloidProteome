package com.compomics.neo4j.database;

import com.compomics.neo4j.model.dataTransferObjects.DiseaseDTO;
import com.compomics.neo4j.model.dataTransferObjects.PathwayDTO;
import com.compomics.neo4j.model.dataTransferObjects.ProteinDTO;
import com.compomics.neo4j.model.nodes.*;
import com.compomics.neo4j.model.relationshipTypes.Associate;
import org.neo4j.driver.internal.value.NullValue;
import org.neo4j.driver.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

public class Service implements Serializable {

    private static final long serialVersionUID = -2151683664234775791L;
    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private final Properties queries = new Properties();

    /**
     * parameters to get data from neo4j database (key : parameter name, value : parameter)
     */
    Map<String, Object> parameters;

    // queries
    private static final String PROJECT_QUERY = "projectSearch";
    private static final String INTACT_QUERY = "IntActSearch";
    private static final String BIOGRID_QUERY = "BiogridSearch";
    private static final String PATHWAY_QUERY = "pathwaySearch";
    private static final String COMPLEX_QUERY = "complexSearch";
    private static final String COMMONGO_QUERY = "GOSearch";
    private static final String DISEASE_QUERY = "diseaseSearch";
    private static final String CONTROL_QUERY = "associationControl";
    private static final String GENE_QUERY_SINGLE = "searchByGeneSingle";
    private static final String GENE_QUERY_DOUBLE = "searchByGeneDouble";
    private static final String GENE_QUERY_MULTIPLE = "searchByGeneMultiple";
    private static final String PATHWAY_SEARCH_QUERY = "searchByPathway";
    private static final String HIERARCHY_SEARCH = "hierarchySearch";
    private static final String PATHWAY_FIND_PROTEIN = "findProteinsByPathway";
    private static final String DISEASE_SEARCH_QUERY = "searchByDisease";
    private static final String PROTEIN_FIND_BY_NAME_SINGLE = "searchByProteinNameSingle";
    private static final String PROTEIN_FIND_BY_NAME_DOUBLE = "searchByProteinNameDouble";
    private static final String PROTEIN_FIND_BY_TISSUE = "findProteinsByTissue";
    private static final String ONE_TO_ONE_SEARCH = "oneToOneSearch";
    private static final String ONE_TO_ONE_SEARCH_MOUSE = "oneToOneSearchMouse";

    public Service() {
        // load the queries from the properties file
        InputStream input = null;
        try {
            input = getClass().getClassLoader().getResourceAsStream("query.properties");
            queries.load(input);
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
    }

    public Driver getDriver() {
        return Connection.getInstance().getDriver();
    }

    /**
     * check if there is any relation between two proteins
     *
     * @param accession1
     * @param accession2
     * @return
     */
    public String controlRelation(String accession1, String accession2, double jaccScore) {
        String result;
        result = checkAssociation(accession1, accession2, jaccScore);
        return result;
    }

    public List<ProteinDTO> findProteinsByName(String proteinName1, String proteinName2, double jaccScore, String species) {
        List<ProteinDTO> proteins;

        if (proteinName2.equals("")) {
            proteins = searchProteinByName(PROTEIN_FIND_BY_NAME_SINGLE, proteinName1, proteinName2, jaccScore, species);
        } else {
            proteins = searchProteinByName(PROTEIN_FIND_BY_NAME_DOUBLE, proteinName1, proteinName2, jaccScore, species);
            for (int i = 0; i < proteins.size(); i++) {
                for (int j = i; j < proteins.size(); j++) {
                    if (proteins.get(i).getProtein1().getUniprotAccession().equals(proteins.get(j).getProtein2().getUniprotAccession())
                            && proteins.get(i).getProtein2().getUniprotAccession().equals(proteins.get(j).getProtein1().getUniprotAccession())) {
                        proteins.remove(j);
                    }
                }
            }
        }

        return proteins;
    }

    private List<ProteinDTO> searchProteinByName(String queryName, String proteinName1, String proteinName2, double jaccScore, String species) {

        List<ProteinDTO> proteins = new ArrayList<>();
        parameters = new HashMap<>();
        parameters.put("proteinName1", proteinName1);
        parameters.put("proteinName2", proteinName2);
        parameters.put("jacc", jaccScore);

        try (Session session = Connection.getInstance().getDriver().session()) {
            StatementResult result = session.run(queries.getProperty(queryName), parameters);
            while (result.hasNext()) {
                Record record = result.next();
                if (!record.get("species1").asString().equals(species)) {
                    continue;
                }
                ProteinDTO proteinDTO = new ProteinDTO();
                Protein protein1 = new Protein();
                protein1.setUniprotAccession(record.get("uniprot_accession1").asString());
                protein1.setProteinName(record.get("protein_name1").asString());
                protein1.setGeneNames(new ArrayList<>());
                if (!NullValue.NULL.equals(record.get("gene_name1"))) {
                    record.get("gene_name1").asList().forEach(gene -> {
                        protein1.getGeneNames().add(gene.toString().replace("[", "").replace("]", ""));
                    });
                }
                protein1.setSpecies(record.get("species1").asString());
                proteinDTO.setProtein1(protein1);
                if (queryName.equals(PROTEIN_FIND_BY_NAME_DOUBLE)) {
                    if (!record.get("species2").asString().equals(species)) {
                        continue;
                    }
                    Protein protein2 = new Protein();
                    protein2.setUniprotAccession(record.get("uniprot_accession2").asString());
                    protein2.setProteinName(record.get("protein_name2").asString());
                    protein2.setGeneNames(new ArrayList<>());
                    if (!NullValue.NULL.equals(record.get("gene_name2"))) {
                        record.get("gene_name2").asList().forEach(gene -> {
                            protein2.getGeneNames().add(gene.toString().replace("[", "").replace("]", ""));
                        });
                    }
                    protein2.setSpecies(record.get("species2").asString());
                    proteinDTO.setProtein2(protein2);
                    Associate associate = new Associate();
                    associate.setJaccSimScore(record.get("jacc_sim_score").asDouble());
                    proteinDTO.setAssociate(associate);
                }

                proteins.add(proteinDTO);
            }
        }

        return proteins;
    }

    /**
     * find protein accession by gene id or name
     *
     * @param gene1
     * @param gene2
     * @param species
     * @return
     */
    public List<ProteinDTO> findProteinsByGene(String gene1, String gene2, String species) {
        List<ProteinDTO> proteins;

        if (gene2.equals("")) {
            proteins = findProteinsByGeneIdOrName(GENE_QUERY_SINGLE, gene1, gene2, species);
        } else {
            proteins = findProteinsByGeneIdOrName(GENE_QUERY_DOUBLE, gene1, gene2, species);
        }

        return proteins;
    }

    /**
     * find protein accession by gene name list
     *
     * @param genes
     * @param species
     * @return
     */
    public List<ProteinDTO> findProteinsByGenes(List<String> genes, String species) {
        List<ProteinDTO> proteins = new ArrayList<>();

        parameters = new HashMap<>();
        parameters.put("genes", genes);
        parameters.put("species", species);

        try (Session session = Connection.getInstance().getDriver().session()) {
            StatementResult result = session.run(queries.getProperty(GENE_QUERY_MULTIPLE), parameters);
            while (result.hasNext()) {
                Record record = result.next();
                ProteinDTO proteinDTO = new ProteinDTO();
                Protein protein1 = new Protein();
                protein1.setUniprotAccession(record.get("uniprot_accession1").asString());
                protein1.setProteinName(record.get("protein_name1").asString());

                protein1.setGeneNames(new ArrayList<>());
                if (!NullValue.NULL.equals(record.get("gene_name1"))) {
                    record.get("gene_name1").asList().forEach(gene -> {
                        protein1.getGeneNames().add(gene.toString().replace("[", "").replace("]", ""));
                    });
                }

                protein1.setGeneIds(new ArrayList<>());
                if (!NullValue.NULL.equals(record.get("gene_id1"))) {
                    record.get("gene_id1").asList().forEach(gene -> {
                        protein1.getGeneIds().add(gene.toString().replace("[", "").replace("]", ""));
                    });
                }

                protein1.setSpecies(record.get("species1").asString());
                proteinDTO.setProtein1(protein1);

                proteins.add(proteinDTO);
            }
        }

        return proteins;
    }

    public List<PathwayDTO> findPathwayDTOs(String pathway) {
        LOGGER.info("Start finding pathway " + pathway);

        List<PathwayDTO> pathwayDTOs = new ArrayList<>();

        parameters = new HashMap<>();
        parameters.put("pathway", pathway);

        try (Session session = Connection.getInstance().getDriver().session()) {
            LOGGER.info("Start running query pathway " + pathway);
            StatementResult result = session.run(queries.getProperty(PATHWAY_SEARCH_QUERY), parameters);
            LOGGER.info("Finished running query pathway " + pathway);
            while (result.hasNext()) {
                Record record = result.next();
                PathWay pthwy = new PathWay();
                pthwy.setPathwayName(record.get("pathway_name").asString());
                pthwy.setReactomeAccession(record.get("reactome_accession").asString());
                pthwy.setLabels(record.get("label").asList(Value::asString));
                PathwayDTO pathwayDTO = new PathwayDTO();
                pathwayDTO.setPathWay(pthwy);
                pathwayDTO.setPathwayDTOs(findLeafPathwaysandProteins(session, pthwy));
                pathwayDTOs.add(pathwayDTO);
            }
        }

        LOGGER.info("Finished finding pathway " + pathway + " found " + pathwayDTOs.size() + " results");

        return pathwayDTOs;
    }

    private List<PathwayDTO> findLeafPathwaysandProteins(Session session, PathWay pathway) {
        List<PathwayDTO> pathwayDTOs = new ArrayList<>();

        parameters = new HashMap<>();
        parameters.put("reactomeAccession", pathway.getReactomeAccession());
        if (pathway.getLabels().contains("Leaf_node")) {
            PathWay pthwy = new PathWay();
            pthwy.setReactomeAccession(pathway.getReactomeAccession());
            pthwy.setPathwayName(pathway.getPathwayName());
            PathwayDTO pathwayDTO = new PathwayDTO();
            pathwayDTO.setPathWay(pthwy);
            pathwayDTO.setProteinDTOs(findProteinDTOsByPathway(pathway.getReactomeAccession()));
            pathwayDTOs.add(pathwayDTO);
        } else {
            StatementResult result = session.run(queries.getProperty(HIERARCHY_SEARCH), parameters);
            while (result.hasNext()) {
                Record record = result.next();
                String leafPathwayReactomeAcc = (String) record.get("leaf_pathway_reactome_acc").asList().get(record.get("leaf_pathway_reactome_acc").asList().size() - 1);
                String leafPathwayName = (String) record.get("leaf_pathway_name").asList().get(record.get("leaf_pathway_name").asList().size() - 1);
                PathWay pthwy = new PathWay();
                pthwy.setReactomeAccession(leafPathwayReactomeAcc);
                pthwy.setPathwayName(leafPathwayName);
                PathwayDTO pathwayDTO = new PathwayDTO();
                pathwayDTO.setPathWay(pthwy);
                pathwayDTO.setProteinDTOs(findProteinDTOsByPathway(leafPathwayReactomeAcc));
                if (pathwayDTO.getProteinDTOs().size() > 0) {
                    pathwayDTOs.add(pathwayDTO);
                }
            }
        }

        return pathwayDTOs;
    }

    private List<ProteinDTO> findProteinDTOsByPathway(String reactomeAccession) {
        List<ProteinDTO> proteinDTOS = new ArrayList<>();
        Set<String> uniprotAccessions1 = new HashSet<>();
        Set<String> uniprotAccessions2 = new HashSet<>();
        parameters = new HashMap<>();
        parameters.put("reactomeAccession", reactomeAccession);

        try (Session session = Connection.getInstance().getDriver().session()) {
            StatementResult result = session.run(queries.getProperty(PATHWAY_FIND_PROTEIN), parameters);
            while (result.hasNext()) {
                Record record = result.next();

                if (uniprotAccessions1.contains(record.get("uniprot_accession2").asString()) && uniprotAccessions2.contains(record.get("uniprot_accession1").asString())) {
                    continue;
                }

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
                proteinDTO.setAssociate(associate);
                uniprotAccessions1.add(record.get("uniprot_accession1").asString());
                uniprotAccessions2.add(record.get("uniprot_accession2").asString());
                proteinDTOS.add(proteinDTO);
            }
        }


        proteinDTOS.sort((o1, o2) -> o2.getAssociate().getJaccSimScore().compareTo(o1.getAssociate().getJaccSimScore()));

        return proteinDTOS;
    }

    public List<DiseaseDTO> findDiseaseDTOs(String disease, Double jacc) {
        LOGGER.info("Start finding disease " + disease + " with jaccard coef " + jacc);

        List<DiseaseDTO> diseaseDTOs = new ArrayList<>();
        Map<String, List<String>> uniprotAccessions1 = new HashMap<>();
        Map<String, List<String>> uniprotAccessions2 = new HashMap<>();

        parameters = new HashMap<>();
        parameters.put("disease", disease);
        parameters.put("jacc", jacc);

        try (Session session = getDriver().session()) {
            LOGGER.info("Start running query disease " + disease + " with jaccard coef " + jacc);
            StatementResult result = session.run(queries.getProperty(DISEASE_SEARCH_QUERY), parameters);
            LOGGER.info("Finished running query disease " + disease + " with jaccard coef " + jacc);
            while (result.hasNext()) {
                Record record = result.next();

                if (uniprotAccessions1.get(record.get("disgenet_id").asString()) != null && uniprotAccessions2.get(record.get("disgenet_id").asString()) != null &&
                        uniprotAccessions1.get(record.get("disgenet_id").asString()).contains(record.get("uniprot_accession2").asString()) &&
                        uniprotAccessions2.get(record.get("disgenet_id").asString()).contains(record.get("uniprot_accession1").asString())) {
                    continue;
                }

                Disease dss = new Disease();
                dss.setDiseaseName(record.get("disease_name").asString());
                dss.setDisgenetId(record.get("disgenet_id").asString());
                DiseaseDTO diseaseDTO = new DiseaseDTO();
                diseaseDTO.setDisease(dss);
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
                proteinDTO.setAssociate(associate);

                boolean includedDisease = false;
                for (DiseaseDTO dDTO : diseaseDTOs) {
                    if (dDTO.getDisease().getDisgenetId().equals(record.get("disgenet_id").asString())) {
                        dDTO.getProteinDTOs().add(proteinDTO);
                        uniprotAccessions1.get(record.get("disgenet_id").asString()).add(record.get("uniprot_accession1").asString());
                        uniprotAccessions2.get(record.get("disgenet_id").asString()).add(record.get("uniprot_accession2").asString());
                        includedDisease = true;
                    }
                }

                if (!includedDisease) {
                    diseaseDTO.setProteinDTOs(new ArrayList<>());
                    diseaseDTO.getProteinDTOs().add(proteinDTO);
                    uniprotAccessions1.put(record.get("disgenet_id").asString(), new ArrayList<>(Arrays.asList(record.get("uniprot_accession1").asString())));
                    uniprotAccessions2.put(record.get("disgenet_id").asString(), new ArrayList<>(Arrays.asList(record.get("uniprot_accession2").asString())));
                    diseaseDTOs.add(diseaseDTO);
                }
            }
        }

        diseaseDTOs.forEach(d -> {
            d.getProteinDTOs().sort((o1, o2) -> o2.getAssociate().getJaccSimScore().compareTo(o1.getAssociate().getJaccSimScore()));
        });

        LOGGER.info("Finished finding disease " + disease + " with jaccard coef " + jacc + " found " + diseaseDTOs.size() + " results");

        return diseaseDTOs;
    }

    /**
     * Get Protein DTO list by tissue name
     *
     * @param tissue : tissue name
     * @return List of proteinDTO
     */
    public List<ProteinDTO> getProteinDTOsByTissue(String tissue, Double jacc) {
        LOGGER.info("Start finding tissue " + tissue + " with jaccard coef " + jacc);
        List<ProteinDTO> proteinDTOS = new ArrayList<>();

        parameters = new HashMap<>();
        parameters.put("tissueName", tissue);
        parameters.put("jacc", jacc);

        try (Session session = Connection.getInstance().getDriver().session()) {
            LOGGER.info("Start query tissue " + tissue + " with jaccard coef " + jacc);
            StatementResult result = session.run(queries.getProperty(PROTEIN_FIND_BY_TISSUE), parameters);
            LOGGER.info("Finished query tissue " + tissue + " with jaccard coef " + jacc);
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
                proteinDTO.setAssociate(associate);
                proteinDTO.setTissueName(record.get("tissue_name").asString());
                proteinDTOS.add(proteinDTO);
            }
        }

        proteinDTOS.sort((o1, o2) -> o2.getAssociate().getJaccSimScore().compareTo(o1.getAssociate().getJaccSimScore()));

        LOGGER.info("Finished finding tissue " + tissue + " with jaccard coef " + jacc + " found " + proteinDTOS.size() + " results");

        return proteinDTOS;
    }

    /**
     * Get Protein DTO list for multiple proteins, for one to one search
     *
     * @param accessions1
     * @param accessions2
     * @param jaccScore
     * @param species
     * @return
     */
    public List<ProteinDTO> getProteinDTOListForMultipleProteins(List<String> accessions1, List<String> accessions2, double jaccScore, String species) {
        List<ProteinDTO> proteinDTOS = new ArrayList<>();

        parameters = new HashMap<>();
        parameters.put("array1", accessions1);
        parameters.put("array2", accessions2);
        parameters.put("jacc", jaccScore);

        try (Session session = Connection.getInstance().getDriver().session()) {
            String query = "";
            if (species.equals("9606")) {
                query = ONE_TO_ONE_SEARCH;
            } else if (species.equals("10090")) {
                query = ONE_TO_ONE_SEARCH_MOUSE;
            }

            StatementResult result = session.run(queries.getProperty(query), parameters);
            createProteinDTOList(session, result, proteinDTOS);
        }

        return proteinDTOS;
    }

    /**
     * Get ProteinDTOs by given query name and two proteins.
     *
     * @param queryName  query name (it can be single or double).
     * @param accession1 the first accession
     * @param accession2 the second accession
     * @return
     */
    public List<ProteinDTO> getProteinDTOList(String queryName, String accession1, String accession2, double jaccScore) {
        List<ProteinDTO> proteinDTOS = new ArrayList<>();

        try (Session session = getDriver().session()) {
            parameters = new HashMap<>();
            parameters.put("acc1", accession1);
            parameters.put("acc2", accession2);
            parameters.put("jacc", jaccScore);

            StatementResult result = session.run(queries.getProperty(queryName), parameters);
            createProteinDTOList(session, result, proteinDTOS);
        }

        proteinDTOS.sort((o1, o2) -> o2.getAssociate().getJaccSimScore().compareTo(o1.getAssociate().getJaccSimScore()));

        return proteinDTOS;
    }

    /**
     * Create proteinDTO list. Check if there is duplicated couple.
     * Do not add the same path twice.
     *
     * @param session     the db session
     * @param result
     * @param proteinDTOs
     * @return
     */
    private void createProteinDTOList(Session session, StatementResult result, List<ProteinDTO> proteinDTOs) {

        while (result.hasNext()) {
            Record record = result.next();
            ProteinDTO proteinDTO = new ProteinDTO();
            // set protein 1
            Protein protein1 = new Protein();
            protein1.setUniprotAccession(record.get("p1_acc").asString());
            protein1.setProteinName(record.get("p1_name").asString());
            protein1.setSpecies(record.get("p1_species").asString());
            protein1.setUniprotEntryName(record.get("p1_uniprot_entry_name").asString());
            protein1.setLength(record.get("p1_length").asString());
            protein1.setUniprotStatus(record.get("p1_uniport_status").asString());
            protein1.setAssociationId(record.get("associationId").asString());

            protein1.setGeneNames(new ArrayList<>());
            if (!NullValue.NULL.equals(record.get("p1_gene_name"))) {
                record.get("p1_gene_name").asList().forEach(gene -> {
                    protein1.getGeneNames().add(gene.toString().replace("[", "").replace("]", ""));
                });
            }

            protein1.setGeneIds(new ArrayList<>());
            if (!NullValue.NULL.equals(record.get("p1_gene_id"))) {
                record.get("p1_gene_id").asList().forEach(gene -> {
                    protein1.getGeneIds().add(gene.toString().replace("[", "").replace("]", ""));
                });
            }

            proteinDTO.setProtein1(protein1);
            // set protein 2
            Protein protein2 = new Protein();
            protein2.setUniprotAccession(record.get("p2_acc").asString());
            protein2.setProteinName(record.get("p2_name").asString());
            protein2.setSpecies(record.get("p2_species").asString());
            protein2.setUniprotEntryName(record.get("p2_uniprot_entry_name").asString());
            protein2.setLength(record.get("p2_length").asString());
            protein2.setUniprotStatus(record.get("p2_uniport_status").asString());

            protein2.setGeneNames(new ArrayList<>());
            if (!NullValue.NULL.equals(record.get("p2_gene_name"))) {
                record.get("p2_gene_name").asList().forEach(gene -> {
                    protein2.getGeneNames().add(gene.toString().replace("[", "").replace("]", ""));
                });
            }

            protein2.setGeneIds(new ArrayList<>());
            if (!NullValue.NULL.equals(record.get("p2_gene_id"))) {
                record.get("p2_gene_id").asList().forEach(gene -> {
                    protein2.getGeneIds().add(gene.toString().replace("[", "").replace("]", ""));
                });
            }
            proteinDTO.setProtein2(protein2);
            String accession1 = proteinDTO.getProtein1().getUniprotAccession();
            String accession2 = proteinDTO.getProtein2().getUniprotAccession();

            // set associate relationship
            Associate associate = new Associate();
            associate.setJaccSimScore(record.get("jacc_sim_score").asDouble());
            associate.setInteract(!NullValue.NULL.equals(record.get("interact")) ? "yes" : "no");
            associate.setParalog(!NullValue.NULL.equals(record.get("paralog")) ? "yes" : "no");
            associate.setCommonAssayCount(record.get("common_assay_count").asString());
            associate.setIntact(getIntactList(session, proteinDTO.getProtein1().getAssociationId()));
            associate.setBioGrid(getBioGridList(session, proteinDTO.getProtein1().getAssociationId()));
            proteinDTO.setAssociate(associate);

            // set projects
            proteinDTO.setProjects(getCommonProjectList(session, accession1, accession2, associate.getJaccSimScore()));

            // set pathways
            proteinDTO.setPathWays(getPathwayList(session, accession1, accession2, associate.getJaccSimScore()));

            // set complexes
            proteinDTO.setComplexes(getComplexList(session, accession1, accession2, associate.getJaccSimScore()));

            // set common GO
            List<Go> commonGoList = getCommonGOList(session, accession1, accession2);
            proteinDTO.setMf(new ArrayList<>());
            proteinDTO.setCc(new ArrayList<>());
            proteinDTO.setBp(new ArrayList<>());
            commonGoList.forEach(go -> {
                switch (go.getLabel()) {
                    case "MF":
                        proteinDTO.getMf().add(go);
                        break;
                    case "CC":
                        proteinDTO.getCc().add(go);
                        break;
                    case "BP":
                        proteinDTO.getBp().add(go);
                        break;
                }
            });

            // set diseases
            List<Disease> diseases = getDiseases(session, accession1, accession2, associate.getJaccSimScore());
            proteinDTO.setDiseases(diseases);

            // set number of distinct complex
            proteinDTO.setDistinctComplexCount(proteinDTO.getComplexes().size());

            // set number of distinct path
            proteinDTO.setDistinctPathCount(proteinDTO.getPathWays().size());

            // set size of common project
            proteinDTO.setCommonProjectSize(proteinDTO.getProjects().size());

            // set disease size
            proteinDTO.setDiseaseCount(diseases.size());
            //for (ProteinDTO p : proteinDTOs) {
            // @TODO what is going on here???
            //if (p.getProtein1().getUniprotAccession().equals(proteinDTO.getProtein2().getUniprotAccession())) {
            //    continue;
            //}
            //}
            proteinDTOs.add(proteinDTO);
        }
    }


    /**
     * Get common projects between two given proteins.
     *
     * @param session    the db session
     * @param accession1 the first protein accession.
     * @param accession2 the second protein accession.
     * @return list of common projects.
     */
    private List<Project> getCommonProjectList(Session session, String accession1, String accession2, double jacc) {
        List<Project> projects = new ArrayList<>();

        parameters = new HashMap<>();
        parameters.put("acc1", accession1);
        parameters.put("acc2", accession2);
        parameters.put("jacc", jacc);

        StatementResult result = session.run(queries.getProperty(Service.PROJECT_QUERY), parameters);
        while (result.hasNext()) {
            Record record = result.next();
            // set Project
            if (!record.get("project_accession").asString().equals("null")) {
                Project project = new Project();
                project.setProjectAccession(record.get("project_accession").asString());
                project.setKeywords(record.get("keywords").asList().toString().replace("[", "").replace("]", ""));
                project.setTissue(record.get("tissue").asList().toString().replace("[", "").replace("]", ""));
                project.setTags(record.get("tags").asList().toString().replace("[", "").replace("]", ""));
                project.setInstruments(record.get("instruments").asList().toString().replace("[", "").replace("]", ""));
                project.setExperimentType(record.get("experiment_type").asList().toString());
                projects.add(project);
            }
        }

        return projects;
    }


    private List<Intact> getIntactList(Session session, String associationId) {
        List<Intact> intacts = new ArrayList<>();
        parameters = new HashMap<>();
        parameters.put("associationId", associationId);

        StatementResult result = session.run(queries.getProperty(INTACT_QUERY), parameters);
        if (result.hasNext()) {
            Record record = result.next();

            if (!NullValue.NULL.equals(record.get("intact_id"))) {
                Intact intact = new Intact();
                intact.setIntactId(record.get("intact_id").asString());
                intact.setConfidenceValue(record.get("confidenceValue").asString());
                intact.setDetection(record.get("intact_detection").asString());
                intact.setInteractionType(record.get("intact_interaction_type").asString());
                intacts.add(intact);
            }

        }

        return intacts;
    }

    private List<BioGrid> getBioGridList(Session session, String associationId) {
        List<BioGrid> bioGrids = new ArrayList<>();
        parameters = new HashMap<>();
        parameters.put("associationId", associationId);

        StatementResult result = session.run(queries.getProperty(BIOGRID_QUERY), parameters);
        if (result.hasNext()) {
            Record record = result.next();
            if (!NullValue.NULL.equals(record.get("biogridId"))) {
                BioGrid bioGrid = new BioGrid();
                bioGrid.setBiogridId(record.get("biogridId").asString());
                bioGrid.setExperimentName(record.get("experiment_name").asString());
                bioGrid.setExperimentType(record.get("experiment_type").asString());
                bioGrid.setModification(record.get("modification").asString());
                if (!NullValue.NULL.equals(record.get("score"))) {
                    bioGrid.setScore(record.get("score").asDouble());
                }

                bioGrid.setThroughput(record.get("throughput").asString());
                bioGrids.add(bioGrid);
            }

        }
        return bioGrids;
    }


    /**
     * Get pathways between two given proteins.
     *
     * @param session    the db session
     * @param accession1 the first protein accession.
     * @param accession2 the second protein accession.
     * @return list of pathways
     */
    private List<PathWay> getPathwayList(Session session, String accession1, String accession2, Double jacc) {
        List<PathWay> pathWays = new ArrayList<>();

        parameters = new HashMap<>();
        parameters.put("acc1", accession1);
        parameters.put("acc2", accession2);
        parameters.put("jacc", jacc);

        StatementResult result = session.run(queries.getProperty(Service.PATHWAY_QUERY), parameters);
        while (result.hasNext()) {
            Record record = result.next();
            // set Project
            if (record.get("reactome_accession") != null && !record.get("reactome_accession").asString().trim().equals("") && !record.get("reactome_accession").asString().trim().equals("null")) {
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
     *
     * @param session    the db session
     * @param accession1 the first protein accession.
     * @param accession2 the second protein accession.
     * @return list of complexes
     */
    private List<Complex> getComplexList(Session session, String accession1, String accession2, Double jacc) {
        List<Complex> complexs = new ArrayList<>();

        parameters = new HashMap<>();
        parameters.put("acc1", accession1);
        parameters.put("acc2", accession2);
        parameters.put("jacc", jacc);

        StatementResult result = session.run(queries.getProperty(Service.COMPLEX_QUERY), parameters);
        while (result.hasNext()) {
            Record record = result.next();
            // set Project
            if (record.get("corum_id") != null && !record.get("corum_id").asString().trim().equals("") && !record.get("corum_id").asString().trim().equals("null")) {
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
     *
     * @param session    the db session
     * @param accession1 the first protein accession.
     * @param accession2 the second protein accession.
     * @return list of common GOs
     */
    private List<Go> getCommonGOList(Session session, String accession1, String accession2) {
        List<Go> commonGos = new ArrayList<>();

        parameters = new HashMap<>();
        parameters.put("acc1", accession1);
        parameters.put("acc2", accession2);

        StatementResult result = session.run(queries.getProperty(Service.COMMONGO_QUERY), parameters);
        while (result.hasNext()) {
            Record record = result.next();
            // set Project
            if (!record.get("go_id").asString().equals("null")) {
                Go go = new Go();
                go.setId(record.get("go_id").asString());
                go.setName(record.get("go_name").asString());
                go.setLabel(String.valueOf(record.get("label").asList().get(1)));
                commonGos.add(go);
            }
        }

        return commonGos;
    }

    /**
     * Get common diseases between two proteins
     *
     * @param session    the db session
     * @param accession1 the first protein accession.
     * @param accession2 the second protein accession.
     * @return list of common diseases
     */
    private List<Disease> getDiseases(Session session, String accession1, String accession2, Double jacc) {
        List<Disease> diseases = new ArrayList<>();

        parameters = new HashMap<>();
        parameters.put("acc1", accession1);
        parameters.put("acc2", accession2);
        parameters.put("jacc", jacc);

        StatementResult result = session.run(queries.getProperty(Service.DISEASE_QUERY), parameters);
        while (result.hasNext()) {
            Record record = result.next();
            // set Project
            if (!record.get("disgenet_id").asString().equals("null")) {
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
     *
     * @param accession1
     * @param accession2
     * @return list of path if exists, else return empty string.
     */
    private String checkAssociation(String accession1, String accession2, double jaccScore) {
        parameters = new HashMap<>();
        parameters.put("acc1", accession1);
        parameters.put("acc2", accession2);
        parameters.put("jacc", jaccScore);

        String resultString = "*";
        try (Session session = Connection.getInstance().getDriver().session()) {
            StatementResult result = session.run(queries.getProperty(Service.CONTROL_QUERY), parameters);
            if (result.hasNext()) {
                Record record = result.next();
                List<Object> paths = new ArrayList<>(record.get("path").asList());
                paths.removeIf(Objects::isNull);
                resultString = paths.toString();
            }
        }

        return resultString;
    }

    /**
     * find protein by gene id or gene name
     *
     * @param queryName
     * @return
     */
    private List<ProteinDTO> findProteinsByGeneIdOrName(String queryName, String gene1, String gene2, String species) {
        List<ProteinDTO> proteins = new ArrayList<>();
        parameters = new HashMap<>();
        parameters.put("gene1", gene1);
        parameters.put("gene2", gene2);
        parameters.put("species", species);

        try (Session session = Connection.getInstance().getDriver().session()) {
            StatementResult result = session.run(queries.getProperty(queryName), parameters);
            while (result.hasNext()) {
                Record record = result.next();
                ProteinDTO proteinDTO = new ProteinDTO();
                Protein protein1 = new Protein();
                protein1.setUniprotAccession(record.get("uniprot_accession1").asString());
                protein1.setProteinName(record.get("protein_name1").asString());

                protein1.setGeneNames(new ArrayList<>());
                if (!NullValue.NULL.equals(record.get("gene_name1"))) {
                    record.get("gene_name1").asList().forEach(gene -> {
                        protein1.getGeneNames().add(gene.toString().replace("[", "").replace("]", ""));
                    });
                }
                protein1.setGeneIds(new ArrayList<>());
                if (!NullValue.NULL.equals(record.get("gene_id1"))) {
                    record.get("gene_id1").asList().forEach(gene -> {
                        protein1.getGeneIds().add(gene.toString().replace("[", "").replace("]", ""));
                    });
                }
                protein1.setSpecies(record.get("species1").asString());
                proteinDTO.setProtein1(protein1);
                Protein protein2 = new Protein();
                if (queryName.equals(GENE_QUERY_DOUBLE)) {
                    protein2.setUniprotAccession(record.get("uniprot_accession2").asString());
                    protein2.setProteinName(record.get("protein_name2").asString());
                    protein2.setGeneNames(new ArrayList<>());
                    if (!NullValue.NULL.equals(record.get("gene_name2"))) {
                        record.get("gene_name2").asList().forEach(gene -> {
                            protein2.getGeneNames().add(gene.toString().replace("[", "").replace("]", ""));
                        });
                    }

                    protein2.setGeneIds(new ArrayList<>());
                    if (!NullValue.NULL.equals(record.get("gene_id2"))) {
                        record.get("gene_id2").asList().forEach(gene -> {
                            protein2.getGeneIds().add(gene.toString().replace("[", "").replace("]", ""));
                        });
                    }

                    protein2.setSpecies(record.get("species2").asString());
                    proteinDTO.setProtein2(protein2);
                    Associate associate = new Associate();
                    associate.setJaccSimScore(record.get("jacc_sim_score").asDouble());
                    proteinDTO.setAssociate(associate);
                }
                proteins.add(proteinDTO);
            }
        }

        return proteins;
    }

}
