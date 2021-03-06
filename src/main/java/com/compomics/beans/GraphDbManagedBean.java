package com.compomics.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.compomics.export.TSVExporter;
import com.compomics.neo4j.database.Service;
import com.compomics.neo4j.model.dataTransferObjects.ProteinDTO;

/**
 * Created by demet on 12/12/2016.
 */
@ManagedBean
@SessionScoped
public class GraphDbManagedBean implements Serializable {

    private static final long serialVersionUID = 9222685086844376613L;

    private List<ProteinDTO> proteinDTOS = new ArrayList<>();
    private Service dbService;
    private String accession1 = "";
    private String accession2 = "";
    private List<String> accessions = new ArrayList<>();
    private double jaccScore = -1;
    private String selectionType;
    private boolean multipleSearch = false;
    private String control;
    private List<String> edgeAnnotations = new ArrayList<>();
    private String multiSearchMessage = "";
    private final List<String> pairsNotFound = new ArrayList<>();
    private String species = "";

    private String array1 = "";
    private String array2 = "";
    private String array3 = "";

    // queries
    private static final String SINGLE_PROTEIN_QUERY = "singleProteinSearch";
    private static final String DOUBLE_PROTEIN_QUERY = "doubleProteinSearch";
    private static final String SINGLE_PROTEIN_QUERY_MOUSE = "singleProteinSearchMouse";
    private static final String DOUBLE_PROTEIN_QUERY_MOUSE = "doubleProteinSearchMouse";


    @ManagedProperty(value = "#{visualisationBean}")
    private VisualisationBean visualisationBean;

    public List<ProteinDTO> getProteinDTOS() {
        return proteinDTOS;
    }

    public String getAccession1() {
        return accession1;
    }

    public String getAccession2() {
        return accession2;
    }

    public void setAccession1(String accession1) {
        this.accession1 = accession1;
    }

    public void setAccession2(String accession2) {
        this.accession2 = accession2;
    }

    public double getJaccScore() {
        return jaccScore;
    }

    public void setJaccScore(double jaccScore) {
        this.jaccScore = jaccScore;
    }

    public String getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(String selectionType) {
        this.selectionType = selectionType;
    }

    public boolean isMultipleSearch() {
        return multipleSearch;
    }

    public void setMultipleSearch(boolean multipleSearch) {
        this.multipleSearch = multipleSearch;
    }

    public String getControl() {
        return control;
    }

    public List<String> getEdgeAnnotations() {
        return edgeAnnotations;
    }

    public String getMultiSearchMessage() {
        return multiSearchMessage;
    }

    public List<String> getPairsNotFound() {
        return pairsNotFound;
    }

    public VisualisationBean getVisualisationBean() {
        return visualisationBean;
    }

    public void setVisualisationBean(VisualisationBean visualisationBean) {
        this.visualisationBean = visualisationBean;
    }

    public String getArray1() {
        return array1;
    }

    public String getArray2() {
        return array2;
    }

    public void setArray1(String array1) {
        this.array1 = array1;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    @PostConstruct
    private void init() {
        dbService = new Service();
    }

    public void load() {
        control = "";
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> requestParams = context.getExternalContext().getRequestParameterMap();
        if (requestParams.containsKey("species")) {
            species = requestParams.get("species");
        }

        if (requestParams.containsKey("jaccard")) {
            jaccScore = Double.valueOf(requestParams.get("jaccard"));
        }
        if (requestParams.containsKey("accession")) {
            if (requestParams.get("accession") != null && !requestParams.get("accession").equals("")) {
                accession1 = requestParams.get("accession").toUpperCase();
                accession2 = null;
                control = "single";
                setSelectionType("single");
                getProteinDTOs(species);
            }
        } else if (requestParams.containsKey("accession1") && requestParams.containsKey("accession2")) {
            if (requestParams.get("accession1") != null && !requestParams.get("accession1").equals("") && requestParams.get("accession2") != null && !requestParams.get("accession2").equals("")) {
                accession1 = requestParams.get("accession1").toUpperCase();
                accession2 = requestParams.get("accession2").toUpperCase();
                controlRelation();
                setSelectionType("double");
                getProteinDTOs(species);
            }
        } else if (requestParams.containsKey("path")) {
            if (requestParams.get("path") != null && !requestParams.get("path").equals("")) {
                proteinDTOS.clear();
                String[] path = requestParams.get("path").split("\\*");
                for (int i = 0; i < path.length - 1; i++) {
                    proteinDTOS.addAll(dbService.getProteinDTOList(DOUBLE_PROTEIN_QUERY, path[i], path[i + 1], jaccScore));
                }
                visualisationBean.load(this);
            }
        }
    }

    /**
     * Find combinations of given array
     *
     * @param arr
     * @param len
     * @param startPosition
     * @param result
     * @return array
     */
    public void combinations(String[] arr, int len, int startPosition, String[] result, List<String> firstProtein, List<String> secondProtein) {
        if (len == 0) {
            firstProtein.add(result[0]);
            secondProtein.add(result[1]);
            return;
        }
        for (int i = startPosition; i <= arr.length - len; i++) {
            result[result.length - len] = arr[i];
            combinations(arr, len - 1, i + 1, result, firstProtein, secondProtein);
        }

    }

    public void searchListOfProteins() {
        clearFields();
        dbService.getDriver();
        array1 = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("array1");
        array2 = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("array2");
        array3 = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("array3");
        species = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("species");
        jaccScore = Double.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jacc"));
        setMultipleSearch(true);
        if (array2 == null || array2.equals("")) {
            List<String> arr1 = new ArrayList<>();
            List<String> arr2 = new ArrayList<>();
            combinations(array1.split("\\s*,\\s*"), 2, 0, new String[2], arr1, arr2);
            array1 = arr1.toString().replace("[", "").replace("]", "");
            array2 = arr2.toString().replace("[", "").replace("]", "");
            proteinDTOS = dbService.getProteinDTOListForMultipleProteins(arr1, arr2, jaccScore, species);
        } else {
            proteinDTOS = dbService.getProteinDTOListForMultipleProteins(Arrays.asList(array1.split("\\s*,\\s*")), Arrays.asList(array2.split("\\s*,\\s*")), jaccScore, species);
        }
        if (Arrays.asList(array1.split("\\s*,\\s*")).size() - proteinDTOS.size() > 0) {
            List<String> accessions1 = new ArrayList<>();
            List<String> accessions2 = new ArrayList<>();
            proteinDTOS.forEach(p -> {
                accessions1.add(p.getProtein1().getUniprotAccession());
                accessions2.add(p.getProtein2().getUniprotAccession());
            });
            multiSearchMessage = Arrays.asList(array1.split("\\s*,\\s*")).size() - proteinDTOS.size() + " protein pair(s) cannot be found. You can lower the threshold and search again.";
            for (int i = 0; i < array1.split("\\s*,\\s*").length; i++) {
                if (!accessions1.contains(array1.split("\\s*,\\s*")[i]) || !accessions2.contains(array2.split("\\s*,\\s*")[i])) {
                    pairsNotFound.add(array1.split("\\s*,\\s*")[i] + ", " + array2.split("\\s*,\\s*")[i]);
                }
            }
        }

        visualisationBean.load(this);
        edgeAnnotations = Arrays.asList(array3.split(","));
    }

    public void updateListofProteinSearch() {
        clearFields();
        jaccScore = Double.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jacc"));
        dbService.getDriver();

        proteinDTOS = dbService.getProteinDTOListForMultipleProteins(Arrays.asList(array1.split("\\s*,\\s*")), Arrays.asList(array2.split("\\s*,\\s*")), jaccScore, species);

        if (Arrays.asList(array1.split("\\s*,\\s*")).size() - proteinDTOS.size() > 0) {
            List<String> accessions1 = new ArrayList<>();
            List<String> accessions2 = new ArrayList<>();
            proteinDTOS.forEach(p -> {
                accessions1.add(p.getProtein1().getUniprotAccession());
                accessions2.add(p.getProtein2().getUniprotAccession());
            });
            multiSearchMessage = Arrays.asList(array1.split("\\s*,\\s*")).size() - proteinDTOS.size() + " protein pair(s) cannot be found. You can lower the threshold and search again.";
            for (int i = 0; i < array1.split("\\s*,\\s*").length; i++) {
                if (!accessions1.contains(array1.split("\\s*,\\s*")[i]) || !accessions2.contains(array2.split("\\s*,\\s*")[i])) {
                    pairsNotFound.add(array1.split("\\s*,\\s*")[i] + ", " + array2.split("\\s*,\\s*")[i]);
                }
            }
        }

        visualisationBean.load(this);
        edgeAnnotations = Arrays.asList(array3.split(","));
    }

    private void controlRelation() {
        control = dbService.controlRelation(accession1, accession2, jaccScore);
    }

    private void getProteinDTOs(String species) {
        multiSearchMessage = "";
        pairsNotFound.clear();
        clearArrays();
        proteinDTOS.clear();
        if (selectionType.equals("single")) {
            getSingleProteinDTOs(species);
            visualisationBean.load(this);
        } else if (selectionType.equals("double")) {
            getDoubleProteinDTOs(species);
            visualisationBean.load(this);
        }
    }

    private void clearFields() {
        accession1 = "";
        accession2 = "";
        selectionType = "";
        jaccScore = -1;
        multiSearchMessage = "";
        multipleSearch = false;
        pairsNotFound.clear();
    }

    private void clearArrays() {
        array1 = "";
        array2 = "";
        array3 = "";
    }

    public void getSingleProteinDTOs(String species) {
        if (species.equals("9606")) {
            proteinDTOS = dbService.getProteinDTOList(SINGLE_PROTEIN_QUERY, accession1, accession2, jaccScore);
        } else if (species.equals("10090")) {
            proteinDTOS = dbService.getProteinDTOList(SINGLE_PROTEIN_QUERY_MOUSE, accession1, accession2, jaccScore);
        }

    }

    public void getDoubleProteinDTOs(String species) {
        if (species.equals("9606")) {
            proteinDTOS = dbService.getProteinDTOList(DOUBLE_PROTEIN_QUERY, accession1, accession2, jaccScore);
        } else if (species.equals("10090")) {
            proteinDTOS = dbService.getProteinDTOList(DOUBLE_PROTEIN_QUERY_MOUSE, accession1, accession2, jaccScore);
        }

    }

    public void tsvExport() {
        TSVExporter tsvExporter = new TSVExporter();
        tsvExporter.tsvExport(proteinDTOS);
    }

    public void csvExport() {
        TSVExporter csvExporter = new TSVExporter();
        csvExporter.csvExport(proteinDTOS);
    }
}
