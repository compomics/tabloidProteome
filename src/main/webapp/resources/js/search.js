function searchSingle() {

    if ($('#singleAccession').val() != "" && isValidJaccardThreshold($('#jaccardSingle').val())) {
        if (isValidUniprotAcc($('#singleAccession').val())) {

            try {
                var surl = '/tabloidproteome/dataTable.xhtml?accession=' + $('#singleAccession').val()
                    + '&jaccard=' + $('#jaccardSingle').val() + '&species=' + $('#selSpeciesProteinSingle').val();
                if (validateURL(surl))
                    window.location = surl;
                else {
                    throw new InvalidURLException();
                }
            } catch (e) {
                if (e instanceof InvalidURLException)
                    alert(e.message);
            }

        } else {

            passSingleProteinNameToJSFManagedBean([{
                name: 'protein1',
                value: isSafe(document.getElementById("singleAccession").value)
            },
                {
                    name: 'jacc',
                    value: isSafe(document.getElementById("jaccardSingle").value)
                },
                {
                    name: 'protein2',
                    value: ''
                },
                {
                    name: "species",
                    value: isSafe(document.getElementById("selSpeciesProteinSingle").value)
                }
            ]);

        }

    } else {
        var dialogInstance = new BootstrapDialog();
        dialogInstance.setTitle('INPUT ERROR');
        if ($('#singleAccession').val() == "") {
            dialogInstance.setMessage('The protein accession cannot be empty!');
        } else if (!isValidJaccardThreshold($('#jaccardSingle').val())) {
            dialogInstance.setMessage('The Jaccard similarity threshold value has to be between 0 and 1');
        }
        dialogInstance.setType(BootstrapDialog.TYPE_DANGER);
        dialogInstance.open();
    }
    return false;
}

function searchDouble() {
    if ($('#doubleAccession1').val() != "" && $('#doubleAccession2').val() != "" && isValidJaccardThreshold($('#jaccardDouble').val())) {

        if (isValidUniprotAcc($('#doubleAccession1').val()) && isValidUniprotAcc($('#doubleAccession2').val())) {
            try {
                var surl = '/tabloidproteome/dataTable.xhtml?accession1=' + $('#doubleAccession1').val()
                    + '&accession2=' + $('#doubleAccession2').val() + '&jaccard=' + $('#jaccardDouble').val()
                    + '&species=' + $('#selSpeciesProteinDouble').val();
                if (validateURL(surl))
                    window.location = surl;
                else {
                    throw new InvalidURLException();
                }
            } catch (e) {
                if (e instanceof InvalidURLException)
                    alert(e.message);
            }

        } else {
            passDoubleProteinNameToJSFManagedBean([{
                name: 'protein1',
                value: isSafe(document.getElementById("doubleAccession1").value)
            },
                {
                    name: 'jacc',
                    value: isSafe(document.getElementById("jaccardDouble").value)
                },
                {
                    name: 'protein2',
                    value: isSafe(document.getElementById("doubleAccession2").value)
                },
                {
                    name: "species",
                    value: isSafe(document.getElementById("selSpeciesProteinDouble").value)
                }
            ]);

        }

    } else {
        var dialogInstance = new BootstrapDialog();
        dialogInstance.setTitle('INPUT ERROR');
        if ($('#doubleAccession1').val() == "" || $('#doubleAccession2').val() == "") {
            dialogInstance.setMessage('The protein accession(s) cannot be empty!');
        } else if (!isValidJaccardThreshold($('#jaccardDouble').val())) {
            dialogInstance.setMessage('The Jaccard similarity threshold value has to be between 0 and 1');
        }
        dialogInstance.setType(BootstrapDialog.TYPE_DANGER);
        dialogInstance.open();
    }
    return false;
}

function isValidUniprotAcc(accession) {
    if (accession.trim() == "") {
        return false;
    }
    if (accession.length != 6 && accession.length != 10) {
        return false;
    }
    var r = new RegExp("[OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2}", "i");
    if (accession.match(r)) {
        return true;
    }
    return false;
}

function isValidJaccardThreshold(threshold) {
    if (threshold == "") {
        return false;
    }
    if (isNaN(threshold)) {
        return false;
    }
    if (0.0 <= threshold && threshold <= 1.0) {
        return true;
    }
    return false;
}

function searchMultipleProtein() {
    var array1 = [];
    var array2 = [];
    var array3 = [];
    //gets table
    var oTable = document.getElementById('oneToOneTable');

    //gets rows of table
    var rowLength = oTable.rows.length;

    //loops through rows
    for (i = 0; i < rowLength; i++) {
        //gets cells of current row
        var oCells = oTable.rows.item(i).cells;

        //gets amount of cells of current row
        var cellLength = oCells.length;

        //loops through each cell in current row
        for (var j = 0; j < cellLength; j += 3) {
            array1.push(oCells.item(j).innerHTML);
            array2.push(oCells.item(j + 1).innerHTML);
            array3.push(oCells.item(j + 2).innerHTML);
        }

    }

    if (array1.length != 0) {
        passProteinsJSONToJSFManagedBean([{
            name: 'array1',
            value: isSafe(array1)
        },
            {
                name: 'array2',
                value: isSafe(array2)
            },
            {
                name: 'array3',
                value: isSafe(array3)
            },
            {
                name: 'jacc',
                value: isSafe(document.getElementById("jaccardOneToOne").value)
            },
            {
                name: 'species',
                value: isSafe(document.getElementById("selSpeciesProteinOneToOne").value)
            }
        ]);
    } else {
        var dialogInstance = new BootstrapDialog();
        dialogInstance.setTitle('INPUT ERROR');
        dialogInstance.setMessage('Please upload CSV file that contains protein pairs!');
        dialogInstance.setType(BootstrapDialog.TYPE_DANGER);
        dialogInstance.open();
    }
}

function searchMultipleProtein2() {
    var array1 = [];
    //gets table
    var oTable = document.getElementById('manyToManyTable');

    //gets rows of table
    var rowLength = oTable.rows.length;

    //loops through rows
    for (i = 0; i < rowLength; i++) {
        //gets cells of current row
        var oCells = oTable.rows.item(i).cells;

        //gets amount of cells of current row
        var cellLength = oCells.length;

        //loops through each cell in current row
        for (var j = 0; j < cellLength; j++) {
            if (!array1.includes(oCells.item(j).innerHTML)) {
                array1.push(oCells.item(j).innerHTML);
            }
        }

    }

    if (array1.length != 0) {
        passProteinsJSONToJSFManagedBean([{
            name: 'array1',
            value: isSafe(array1)
        },
            {
                name: 'array2',
                value: ""
            },
            {
                name: 'array3',
                value: ""
            },
            {
                name: 'jacc',
                value: isSafe(document.getElementById("jaccardManyToMany").value)
            },
            {
                name: 'species',
                value: isSafe(document.getElementById("selSpeciesProteinManyToMany").value)
            }
        ]);
    } else {
        var dialogInstance = new BootstrapDialog();
        dialogInstance.setTitle('INPUT ERROR');
        dialogInstance.setMessage('Please upload CSV file that contains protein accessions!');
        dialogInstance.setType(BootstrapDialog.TYPE_DANGER);
        dialogInstance.open();
    }
}

function searchMultipleGene() {
    var array1 = [];
    //gets table
    var oTable = document.getElementById('manyToManyTableGene');

    //gets rows of table
    var rowLength = oTable.rows.length;

    //loops through rows
    for (i = 0; i < rowLength; i++) {
        //gets cells of current row
        var oCells = oTable.rows.item(i).cells;

        //gets amount of cells of current row
        var cellLength = oCells.length;

        //loops through each cell in current row
        for (var j = 0; j < cellLength; j++) {
            if (!array1.includes(oCells.item(j).innerHTML)) {
                array1.push(oCells.item(j).innerHTML);
            }
        }

    }
    if (array1.length != 0) {
        passGenesJSONToJSFManagedBean([{
            name: 'array1',
            value: isSafe(array1)
        },
            {
                name: 'species',
                value: isSafe(document.getElementById("selSpeciesGeneManyToMany").value)
            }
        ]);
    } else {
        var dialogInstance = new BootstrapDialog();
        dialogInstance.setTitle('INPUT ERROR');
        dialogInstance.setMessage('Please upload CSV file that contains gene names!');
        dialogInstance.setType(BootstrapDialog.TYPE_DANGER);
        dialogInstance.open();
    }
}

function searchGene(selection) {
    if (selection == "single") {
        if ($('#geneId').val() != "") {
            passGeneToJSFManagedBean([{
                name: 'gene',
                value: isSafe(document.getElementById("geneId").value)
            },
                {
                    name: "species",
                    value: isSafe(document.getElementById("selSpeciesGeneSingle").value)
                }
            ]);
        } else {
            var dialogInstance = new BootstrapDialog();
            dialogInstance.setTitle('INPUT ERROR');
            dialogInstance.setMessage('The gene name/id cannot be empty!');
            dialogInstance.setType(BootstrapDialog.TYPE_DANGER);
            dialogInstance.open();
        }

    } else if (selection == "double") {
        if ($('#geneId1').val() != "" && $('#geneId2').val() != "") {
            passGeneDoubleToJSFManagedBean([{
                name: 'gene',
                value: isSafe(document.getElementById("geneId1").value) + "," + isSafe(document.getElementById("geneId2").value)
            },
                {
                    name: "species",
                    value: isSafe(document.getElementById("selSpeciesGeneDouble").value)
                }
            ]);
        } else {
            var dialogInstance = new BootstrapDialog();
            dialogInstance.setTitle('INPUT ERROR');
            dialogInstance.setMessage('The gene name/id cannot be empty!');
            dialogInstance.setType(BootstrapDialog.TYPE_DANGER);
            dialogInstance.open();
        }

    }


    return false;
}

function searchPathway() {
    if ($('#pathwayId').val() != "") {
        $('#progressModal').modal('show');
        passPathwayNameToJSFManagedBean([{
            name: 'pathway',
            value: isSafe(document.getElementById("pathwayId").value)
        }
        ]);
    } else {
        var dialogInstance = new BootstrapDialog();
        dialogInstance.setTitle('INPUT ERROR');
        dialogInstance.setMessage('The pathway name/reactome accession cannot be empty!');
        dialogInstance.setType(BootstrapDialog.TYPE_DANGER);
        dialogInstance.open();
    }
}

function searchDisease() {
    if ($('#diseaseId').val() != "") {
        $('#progressModal').modal('show');
        passDiseaseNameToJSFManagedBean([{
            name: 'disease',
            value: isSafe(document.getElementById("diseaseId").value)
        }
        ]);
    } else {
        var dialogInstance = new BootstrapDialog();
        dialogInstance.setTitle('INPUT ERROR');
        dialogInstance.setMessage('The disease name/disGeNet id cannot be empty!');
        dialogInstance.setType(BootstrapDialog.TYPE_DANGER);
        dialogInstance.open();
    }
}

function searchTissue() {
    var e = document.getElementById("selectTissue");

    if (e.selectedIndex != 0 && isValidJaccardThreshold($('#jaccardTissue').val())) {
        $('#progressModal').modal('show');
        passTissueNameToJSFManagedBean([{
            name: 'tissue',
            value: e.options[e.selectedIndex].value
        },
            {
                name: "jacc",
                value: isSafe(document.getElementById("jaccardTissue").value)
            }
        ]);
    } else {
        var dialogInstance = new BootstrapDialog();
        dialogInstance.setTitle('INPUT ERROR');
        if (e.selectedIndex == 0) {
            dialogInstance.setMessage('Please select a tissue to search!');
        } else if (!isValidJaccardThreshold($('#jaccardTissue').val())) {
            dialogInstance.setMessage('The Jaccard similarity threshold value has to be between 0 and 1');
        }
        dialogInstance.setType(BootstrapDialog.TYPE_DANGER);
        dialogInstance.open();
    }
}

function clickButton1() {
    document.getElementById('btnprotein1').click();
    var els = document.getElementsByName("jaccValue");
    for (var i = 0; i < els.length; i++) {
        document.getElementById(els[i].id).value = document.getElementById("jaccardSingle").value;
    }
    return false;
}

function clickButton2() {
    document.getElementById('btnprotein2').click();
    return false;
}

function clickButton3() {
    $('#progressModal').modal('hide');
    try {
        var surl = '/tabloidproteome/detailedSearch.xhtml?pathway';
        if (validateURL(surl))
            window.location = surl;
        else {
            throw new InvalidURLException();
        }
    } catch (e) {
        if (e instanceof InvalidURLException)
            alert(e.message);
    }
    return false;
}

function clickButton4() {
    $('#progressModal').modal('hide');
    try {
        var surl = '/tabloidproteome/detailedSearch.xhtml?disease';
        if (validateURL(surl))
            window.location = surl;
        else {
            throw new InvalidURLException();
        }
    } catch (e) {
        if (e instanceof InvalidURLException)
            alert(e.message);
    }
    return false;
}

function clickButton5() {
    try {
        var surl = '/tabloidproteome/dataTable.xhtml?multipleSearch';
        if (validateURL(surl))
            window.location = surl;
        else {
            throw new InvalidURLException();
        }
    } catch (e) {
        if (e instanceof InvalidURLException)
            alert(e.message);
    }

    return false;
}

function clickButton6() {
    $('#progressModal').modal('hide');
    try {
        var surl = '/tabloidproteome/detailedSearch.xhtml?tissue';
        if (validateURL(surl))
            window.location = surl;
        else {
            throw new InvalidURLException();
        }
    } catch (e) {
        if (e instanceof InvalidURLException)
            alert(e.message);
    }
    return false;
}

function setExampleSingle1() {
    document.getElementById('singleAccession').value = 'Q13330';
    return false;
}

function setExampleSingle2() {
    document.getElementById('singleAccession').value = 'P08134';
    return false;
}

function setExampleDouble1() {
    document.getElementById('doubleAccession1').value = 'Q8WXI9';
    document.getElementById('doubleAccession2').value = 'Q12873';
    return false;
}

function setExampleDouble2() {
    document.getElementById('doubleAccession1').value = 'Q12873';
    document.getElementById('doubleAccession2').value = 'Q13330';
    return false;
}

function setExampleGeneSingle1() {
    document.getElementById('geneId').value = 'MTA1';
    return false;
}

function setExampleGeneSingle2() {
    document.getElementById('geneId').value = '1107';
    return false;
}

function setExampleGeneDouble1() {
    document.getElementById('geneId1').value = 'MTA1';
    document.getElementById('geneId2').value = 'CHD3';
    return false;
}

function setExampleGeneDouble2() {
    document.getElementById('geneId1').value = 'MTA1';
    document.getElementById('geneId2').value = 'SIN3A';
    return false;
}

function setExamplePathway1() {
    document.getElementById('pathwayId').value = 'NOD1/2 Signaling Pathway';
    return false;
}

function setExamplePathway2() {
    document.getElementById('pathwayId').value = 'R-HSA-114604';
    return false;
}

function setExampleDisease1() {
    document.getElementById('diseaseId').value = 'Liver carcinoma';
    return false;
}

function setExampleDisease2() {
    document.getElementById('diseaseId').value = 'C2239176';
    return false;
}

function loadFile(event) {
    var table = document.getElementById("oneToOneTable");
    $("#oneToOneTable tr").remove();
    var maxColumn = 2;
    if ($('#edgeAnnotationCheckBox').is(':checked')) {
        maxColumn = 3;
    }

    alasql('SELECT * FROM FILE(?,{headers:false})', [event], function (data) {
        for (var obj in data) {
            if (data.hasOwnProperty(obj)) {
                var headerIndex = 0;
                var row = table.insertRow(table.rows.length);
                for (var prop in data[obj]) {
                    if (data[obj].hasOwnProperty(prop) && headerIndex < maxColumn) {
                        var cell = row.insertCell(headerIndex);
                        cell.innerHTML = data[obj][prop];
                        headerIndex++;
                    }
                }
                if (headerIndex != 3) {
                    row.insertCell(headerIndex);
                }
            }
        }
        $("#oneToOneRegion").show();
        $("#informationOneToOne").hide();
    });

    $("#readfile").val("");
}


function loadFile2(event) {
    var table = document.getElementById("manyToManyTable");
    $("#manyToManyTable tr").remove();
    var maxColumn = 1;

    alasql('SELECT * FROM FILE(?,{headers:false})', [event], function (data) {
        for (var obj in data) {
            if (data.hasOwnProperty(obj)) {
                var headerIndex = 0;
                var row = table.insertRow(table.rows.length);
                for (var prop in data[obj]) {
                    if (data[obj].hasOwnProperty(prop) && headerIndex < maxColumn) {
                        var cell = row.insertCell(headerIndex);
                        cell.innerHTML = data[obj][prop];
                        headerIndex++;
                    }
                }
            }
        }
        $("#manyToManyRegion").show();
        $("#informationManyToMany").hide();
    });

    $("#readfile2").val("");
}


function loadFileGene(event) {
    var table = document.getElementById("manyToManyTableGene");
    $("#manyToManyTableGene tr").remove();
    var maxColumn = 1;

    alasql('SELECT * FROM FILE(?,{headers:false})', [event], function (data) {
        for (var obj in data) {
            if (data.hasOwnProperty(obj)) {
                var headerIndex = 0;
                var row = table.insertRow(table.rows.length);
                for (var prop in data[obj]) {
                    if (data[obj].hasOwnProperty(prop) && headerIndex < maxColumn) {
                        var cell = row.insertCell(headerIndex);
                        cell.innerHTML = data[obj][prop];
                        headerIndex++;
                    }
                }
                if (headerIndex != 3) {
                    row.insertCell(headerIndex);
                }
            }
        }
        $("#manyToManyRegionGene").show();
        $("#informationGeneManyToMany").hide();
    });

    $("#readfile").val("");
}

function isSafe(text) {
    if (text.includes("<script>")) {
        safe = text.replace("<script>", "safe");
        return safe;
    } else {
        return text;
    }
}
	