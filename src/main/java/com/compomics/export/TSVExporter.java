package com.compomics.export;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.tomcat.util.http.fileupload.util.Streams;

import com.compomics.neo4j.model.dataTransferObjects.ProteinDTO;
import com.compomics.neo4j.model.nodes.Disease;
import com.compomics.neo4j.model.nodes.Go;

public class TSVExporter implements Serializable{

	private static final long serialVersionUID = -7839067679207942313L;

	private List<ProteinDTO> proteinDTOS;
	private StringBuilder tsvExport ;
	private StringBuilder csvExport ;
	
	private final String TAB_SEPARATOR = "\t";
	private final String COMMA_SEPARATOR = ",";
	private final String END_OF_LINE = "\n";
	private final String ACCESSION_SEPARATOR = ";";
	
	// headers
	private final String PROTEIN_1= "Protein 1";
	private final String PROTEIN_2= "Protein 2";
	private final String PROTEIN_1_NAME = "Protein 1 name";
	private final String PROTEIN_2_NAME = "Protein 2 name";
	private final String PROTEIN_1_GENE = "Protein 1 gene name";
	private final String PROTEIN_2_GENE = "Protein 2 gene name";
	private final String JACC_SIM_SCORE = "Jacc similarity score";
	private final String INTACT = "IntAct";
	private final String BIOGRID = "BioGRID";
	private final String PARALOG = "Ensemble paralog";
	private final String NUM_COMPLEX = "Number of common complexes";
	private final String NUM_PATHWAY = "Number of common pathways";
	private final String NUM_PROJECT = "Number of common projects";
	private final String NUM_MF = "Number of MFs";
	private final String NUM_BP = "Number of BPs";
	private final String NUM_CC = "Number of CCs";
	private final String NUM_DISEASE = "Number of common diseases";
	private final String PROJECTS = "Projects";
	private final String PATHWAYS = "Pathways";
	private final String COMPLEXES = "Complexes";
	private final String MF = "GO MF";
	private final String BP = "GO BP";
	private final String CC= "GO CC";
	private final String DISEASES = "Diseases";

	
	public void tsvExport(List<ProteinDTO> proteinDTOS) {
		this.proteinDTOS = proteinDTOS;
		tsvExport = new StringBuilder();
		createFileString(tsvExport, TAB_SEPARATOR);
		export(tsvExport);
	}
	
	public void csvExport(List<ProteinDTO> proteinDTOS) {
		this.proteinDTOS = proteinDTOS;
		csvExport = new StringBuilder();
		createFileString(csvExport, COMMA_SEPARATOR);
		export(csvExport);
	}
	
	private void createFileString(StringBuilder export, String separator){
		export.append(PROTEIN_1).append(separator).append(PROTEIN_2).append(separator).append(PROTEIN_1_NAME).append(separator).append(PROTEIN_2_NAME)
			.append(separator).append(PROTEIN_1_GENE).append(separator).append(PROTEIN_2_GENE).append(separator).append(NUM_PROJECT).append(separator)
			.append(PROJECTS).append(separator).append(JACC_SIM_SCORE).append(separator).append(INTACT).append(separator).append(BIOGRID).append(separator)
			.append(NUM_PATHWAY).append(separator).append(PATHWAYS).append(separator).append(NUM_COMPLEX).append(separator).append(COMPLEXES).append(separator)
			.append(PARALOG).append(separator).append(NUM_BP).append(separator).append(BP).append(separator).append(NUM_MF).append(separator).append(MF)
			.append(separator).append(NUM_CC).append(separator).append(CC)
			.append(separator).append(NUM_DISEASE).append(separator).append(DISEASES).append(END_OF_LINE);
		
		proteinDTOS.forEach(proteinDTO -> {
			
			// find genes of the first protein
			StringBuilder genes1 = new StringBuilder();
			proteinDTO.getProtein1().getGeneNames().forEach(gene ->{
				genes1.append(gene).append(ACCESSION_SEPARATOR);
			});
			if(genes1.length() > 0){
				genes1.setLength(genes1.length() - 1);
			}
			
			// find genes of the second protein
			StringBuilder genes2 = new StringBuilder();
			proteinDTO.getProtein2().getGeneNames().forEach(gene -> {
				genes2.append(gene).append(ACCESSION_SEPARATOR);
			});
			if (genes2.length() > 0) {
				genes2.setLength(genes2.length() - 1);
			}
			
			// find projects accessions
			StringBuilder projects = new StringBuilder();
			proteinDTO.getProjects().forEach(project ->{
				projects.append(project.getProjectAccession()).append(ACCESSION_SEPARATOR);
			});
			if(projects.length() > 0){
				projects.setLength(projects.length() - 1);
			}
			
			// find pathways reactome accessions
			StringBuilder pathways = new StringBuilder();
			proteinDTO.getPathWays().forEach(pathway ->{
				pathways.append(pathway.getReactomeAccession()).append(ACCESSION_SEPARATOR);
			});
			if(pathways.length() > 0){
				pathways.setLength(pathways.length() - 1);
			}
			
			// find complexes corum id
			StringBuilder complexes = new StringBuilder();
			proteinDTO.getComplexes().forEach(complex -> {
				complexes.append(complex.getCorumId()).append(ACCESSION_SEPARATOR);
			});
			if (complexes.length() > 0) {
				complexes.setLength(complexes.length() - 1);
			}
			
			// find GO BP id and number of BPs
			int bpSize = 0;
			StringBuilder bps = new StringBuilder();
			for(Go bp : proteinDTO.getBp()){
				bps.append(bp.getId()).append(ACCESSION_SEPARATOR);
				bpSize++;
			}
			if (bps.length() > 0) {
				bps.setLength(bps.length() - 1);
			}

			// find GO MF id and number of MFs
			int mfSize = 0;
			StringBuilder mfs = new StringBuilder();
			for (Go mf : proteinDTO.getMf()) {
				mfs.append(mf.getId()).append(ACCESSION_SEPARATOR);
				mfSize++;
			}
			if (mfs.length() > 0) {
				mfs.setLength(mfs.length() - 1);
			}

			// find GO CC id and number of CCs
			int ccSize = 0;
			StringBuilder ccs = new StringBuilder();
			for (Go cc : proteinDTO.getCc()) {
				ccs.append(cc.getId()).append(ACCESSION_SEPARATOR);
				ccSize++;
			}
			if (ccs.length() > 0) {
				ccs.setLength(ccs.length() - 1);
			}

			// find diseases disGeNet Id
			StringBuilder diseases = new StringBuilder();
			for (Disease disease : proteinDTO.getDiseases()) {
				diseases.append(disease.getDisgenetId()).append(ACCESSION_SEPARATOR);
			}
			if (diseases.length() > 0) {
				diseases.setLength(diseases.length() - 1);
			}
			
			export.append(proteinDTO.getProtein1().getUniprotAccession()).append(separator).append(proteinDTO.getProtein2().getUniprotAccession()).append(separator)
			.append(proteinDTO.getProtein1().getProteinName()).append(separator).append(proteinDTO.getProtein2().getProteinName()).append(separator)
			.append(genes1.toString()).append(separator).append(genes2.toString()).append(separator)
			.append(proteinDTO.getCommonProjectSize()).append(separator).append(projects.toString()).append(separator)
			.append(proteinDTO.getAssociate().getJaccSimScore()).append(separator).append(proteinDTO.getAssociate().getIntact().size() > 0 ? "yes" : "no")
			.append(separator).append(proteinDTO.getAssociate().getBioGrid().size() > 0 ? "yes" : "no").append(separator)
			.append(proteinDTO.getDistinctPathCount()).append(separator).append(pathways.toString()).append(separator)
			.append(proteinDTO.getDistinctComplexCount()).append(separator).append(complexes.toString()).append(separator)
			.append(proteinDTO.getAssociate().getParalog()).append(separator)
			.append(bpSize).append(separator).append(bps.toString()).append(separator)
			.append(mfSize).append(separator).append(mfs.toString()).append(separator)
			.append(ccSize).append(separator).append(ccs.toString()).append(separator)
			.append(proteinDTO.getDiseaseCount()).append(separator).append(diseases.toString()).append(END_OF_LINE);
		});
	}
	
	private void export(StringBuilder export) {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();

        byte[] exportContent = export.toString().getBytes();
        // here something bad happens that the user should know about
        // but this message does not go out to the user
        fc.addMessage(null, new FacesMessage("record 2 was flawed"));

        ec.responseReset();
        ec.setResponseContentType("text/plain");
        ec.setResponseContentLength(exportContent.length);
        String attachmentName = "";
        if(export.equals(tsvExport)){
        	attachmentName = "attachment; filename=\"proteinAssociation.txt\"";
        }else if(export.equals(csvExport)){
        	attachmentName = "attachment; filename=\"proteinAssociation.csv\"";
        }
         
        ec.setResponseHeader("Content-Disposition", attachmentName);
        try {
            OutputStream output = ec.getResponseOutputStream();
            Streams.copy(new ByteArrayInputStream(exportContent), output, false);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        fc.responseComplete();
    }

}
