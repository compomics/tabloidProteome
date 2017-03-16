package com.compomics.export;

import java.io.Serializable;
import java.util.List;

import com.compomics.neo4j.model.dataTransferObjects.ProteinDTO;
import com.compomics.neo4j.model.nodes.PathWay;
import com.compomics.neo4j.model.nodes.Project;

public class TSVExporter implements Serializable{

	private static final long serialVersionUID = -7839067679207942313L;

	private List<ProteinDTO> proteinDTOS;
	
	private String [][] matrix ;
	
	private final String SEPARATOR = "\t";
	private final String END_OF_LINE = "\n";
	private final int NUMBER_OF_COLUMNS = 34;
	private int NUMBER_OF_ROWS = 0;
	
	// headers
	private final String PROTEIN_1= "Protein A";
	private final String PROTEIN_2= "Protein B";
	private final String PROTEIN_1_NAME = "Protein A name";
	private final String PROTEIN_2_NAME = "Protein B name";
	private final String JACC_SIM_SCORE = "Jacc sim score";
	private final String INTERACT = "Interaction";
	private final String PARALOG = "Paralog";
	private final String DIST_COMPLEX = "Number of distinct complexes";
	private final String DIST_PATHWAY = "Number of distinct pathways";
	private final String COMM_PROJECT_SIZE = "Number of common project size";
	private final String INTACT_CONF = "Intact confidence";
	private final String INTACT_DETECTION = "Interaction detection";
	private final String INTACT_TYPE = "Interaction type";
	private final String PROJECT_ACC = "Project accession";
	private final String KEYWORDS = "Keywords";
	private final String TISSUE = "Tissue";
	private final String TAGS = "Tags";
	private final String REACTOME_ACC = "Reactome accession";
	private final String PATHWAY_NAME = "Pathway name";
	private final String EVIDENCE_CODE = "Evidence code";
	private final String CORUM_ID = "Corum id";
	private final String COMPLEX_NAME = "Complex name";
	private final String COMPLEX_COMMENT = "Complex comment";
	private final String CELL_LINE = "Cell line";
	private final String DISEASE_COMMENT = "Disease comment";
	private final String SUBUNIT_COMMENT = "Subunit comment";
	private final String PUBMED_ID = "Pubmed id";
	private final String PURIFICATION_METHOD = "Purification method";
	private final String MF_ID = "MF id";
	private final String MF_NAME = "MF name";
	private final String BP_ID = "BP id";
	private final String BP_NAME = "BP name";
	private final String CC_ID = "CC id";
	private final String CC_NAME = "CC name";
	
	
	public TSVExporter(List<ProteinDTO> proteinDTOS) {
		this.proteinDTOS = proteinDTOS;
	}
	
	private void createMatrix(){
		if(!proteinDTOS.isEmpty()){
			findMatrixSize();
			matrix = new String[NUMBER_OF_COLUMNS][NUMBER_OF_ROWS];
			addMatrixData();
		}
	}
	
	private void findMatrixSize(){
		NUMBER_OF_ROWS = proteinDTOS.size();
		proteinDTOS.forEach(proteinDTO ->{
			if(proteinDTO.getProjects().size() > NUMBER_OF_ROWS){
				NUMBER_OF_ROWS = proteinDTO.getProjects().size();
			}
			if(proteinDTO.getPathWays().size() > NUMBER_OF_ROWS){
				NUMBER_OF_ROWS = proteinDTO.getPathWays().size();
			}
			if(proteinDTO.getComplexes().size() > NUMBER_OF_ROWS){
				NUMBER_OF_ROWS = proteinDTO.getComplexes().size();
			}
			if(proteinDTO.getMf().size() > NUMBER_OF_ROWS){
				NUMBER_OF_ROWS = proteinDTO.getMf().size();
			}
			if(proteinDTO.getBp().size() > NUMBER_OF_ROWS){
				NUMBER_OF_ROWS = proteinDTO.getBp().size();
			}
			if(proteinDTO.getCc().size() > NUMBER_OF_ROWS){
				NUMBER_OF_ROWS = proteinDTO.getCc().size();
			}
		});
		// for header
		NUMBER_OF_ROWS++;
	}
	
	private void addMatrixHeaders(){
		// headers
		// main protein association table
		matrix[0][0] = PROTEIN_1;
		matrix[0][1] = PROTEIN_2;
		matrix[0][2] = PROTEIN_1_NAME;
		matrix[0][3] = PROTEIN_2_NAME;
		matrix[0][4] = JACC_SIM_SCORE;
		matrix[0][5] = INTERACT;
		matrix[0][6] = PARALOG;
		matrix[0][7] = DIST_COMPLEX;
		matrix[0][8] = DIST_PATHWAY;
		matrix[0][9] = COMM_PROJECT_SIZE;
		// association
		matrix[0][10] = INTACT_CONF;
		matrix[0][11] = INTACT_DETECTION;
		matrix[0][12] = INTACT_TYPE;
		// common projects
		matrix[0][13] = PROJECT_ACC;
		matrix[0][14] = KEYWORDS;
		matrix[0][15] = TISSUE;
		matrix[0][16] = TAGS;
		// pathways
		matrix[0][17] = REACTOME_ACC;
		matrix[0][18] = PATHWAY_NAME;
		matrix[0][19] = EVIDENCE_CODE;
		// complexes
		matrix[0][20] = CORUM_ID;
		matrix[0][21] = COMPLEX_NAME;
		matrix[0][22] = COMPLEX_COMMENT;
		matrix[0][23] = CELL_LINE;
		matrix[0][24] = DISEASE_COMMENT;
		matrix[0][25] = SUBUNIT_COMMENT;
		matrix[0][26] = PUBMED_ID;
		matrix[0][27] = PURIFICATION_METHOD;
		// mf
		matrix[0][28] = MF_ID;
		matrix[0][29] = MF_NAME;
		// bp
		matrix[0][30] = BP_ID;
		matrix[0][31] = BP_NAME;
		// cc
		matrix[0][32] = CC_ID;
		matrix[0][33] = CC_NAME;
	}
	
	public void addMatrixData(){
		addMatrixHeaders();
		
	    proteinDTOS.forEach(proteinDTO ->{
	    	for(int i = 1; i<NUMBER_OF_ROWS; i++){
		    	matrix[i][0] = proteinDTO.getProtein1().getUniprotAccession();
		    	matrix[i][1] = proteinDTO.getProtein2().getUniprotAccession();
		    	matrix[i][2] = proteinDTO.getProtein1().getProteinName();
		    	matrix[i][3] = proteinDTO.getProtein2().getProteinName();
		    	matrix[i][4] = proteinDTO.getAssociate().getJaccSimScore().toString();
		    	matrix[i][5] = proteinDTO.getAssociate().getInteract();
		    	matrix[i][6] = proteinDTO.getAssociate().getParalog();
		    	matrix[i][7] = String.valueOf(proteinDTO.getDistinctComplexCount());
		    	matrix[i][8] = String.valueOf(proteinDTO.getDistinctPathCount());
		    	matrix[i][9] = String.valueOf(proteinDTO.getCommonProjectSize());
		    	matrix[i][10] = proteinDTO.getAssociate().getIntactConfidence();
		    	matrix[i][11] = proteinDTO.getAssociate().getInteractionDetection();
		    	matrix[i][12] = proteinDTO.getAssociate().getInteractionType();
		    	int cp = i;
		    	for(Project project : proteinDTO.getProjects()){
		    		matrix[cp][13] = project.getProjectAccession();
		    		matrix[cp][14] = project.getKeywords();
		    		matrix[cp][15] = project.getTissue();
		    		matrix[cp][16] = project.getTags();
		    		cp++;
		    	}
		    	int pw = i;
		    	for(PathWay pathWay : proteinDTO.getPathWays()){
		    		matrix[pw][17] = pathWay.getReactomeAccession();
		    		matrix[pw][18] = pathWay.getPathwayName();
		    		matrix[pw][19] = pathWay.getEvidenceCode();
		    		pw++;
		    	}
		    	int cm = i;
		    	
		    }
		});
		
	}
}
