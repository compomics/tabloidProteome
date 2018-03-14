function searchSingle(){

		if($('#singleAccession').val() != ""){
			if(isValidUniprotAcc($('#singleAccession').val())){
								
				try {    
					var surl = '/tabloidproteome/dataTable.xhtml?accession='+ $('#singleAccession').val()
		    		+ '&jaccard=' + $('#jaccardSingle').val();
	                if (validateURL(surl))  
	                    window.location = surl; 
	                else {  
	                    throw new InvalidURLException();  
	                }  
	            } catch (e) {  
	                if (e instanceof InvalidURLException)  
	                    alert(e.message);  
	            }  
		
			}else{
				
				passSingleProteinNameToJSFManagedBean ([ {
                    name : 'protein1',
                    value :  isSafe(document.getElementById("singleAccession").value)
                   },
                   {
                       name : 'jacc',
                       value :  isSafe(document.getElementById("jaccardSingle").value)
                      },
                   {
                      name : 'protein2',
                      value :  ''
                   }
                 ]);
				
			}
    		
    	}else{
            var dialogInstance = new BootstrapDialog();
            dialogInstance.setTitle('INPUT ERROR');
            dialogInstance.setMessage('Accession cannot be empty!');
            dialogInstance.setType(BootstrapDialog.TYPE_DANGER);
            dialogInstance.open();
    	}
		return false;
	}
    
	function searchDouble(){
		if($('#doubleAccession1').val() != ""  && $('#doubleAccession2').val() != "" ){
			
			if(isValidUniprotAcc($('#doubleAccession1').val()) && isValidUniprotAcc($('#doubleAccession2').val())){
				try {    
					var surl = '/tabloidproteome/dataTable.xhtml?accession1=' + $('#doubleAccession1').val()
		        	+ '&accession2=' + $('#doubleAccession2').val() + '&jaccard=' + $('#jaccardDouble').val();
	                if (validateURL(surl))  
	                    window.location = surl; 
	                else {  
	                    throw new InvalidURLException();  
	                }  
	            } catch (e) {  
	                if (e instanceof InvalidURLException)  
	                    alert(e.message);  
	            }
		
			}else{
				passDoubleProteinNameToJSFManagedBean ([ {
                    name : 'protein1',
                    value :  isSafe(document.getElementById("doubleAccession1").value)
                   },
                   {
                       name : 'jacc',
                       value :  isSafe(document.getElementById("jaccardDouble").value)
                      },
                   {
                      name : 'protein2',
                      value : isSafe(document.getElementById("doubleAccession2").value)
                   }
                 ]);
				
			}
			
			
    		
    	}else{
    		var dialogInstance = new BootstrapDialog();
            dialogInstance.setTitle('INPUT ERROR');
            dialogInstance.setMessage('Accessions cannot be empty!');
            dialogInstance.setType(BootstrapDialog.TYPE_DANGER);
            dialogInstance.open();
    	}
		return false;
	}
	
	function isValidUniprotAcc(accession){
		if (accession.trim() == "") {
            return false;
        }
        if (accession.length != 6 && accession.length != 10) {
        	return false;
        }
        var r = new RegExp("[OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2}", "i");
        if(accession.match(r)){
        	return true;
        }
        return false;
	}
	
	function searchMultipleProtein(){
		var array1 = [];
		var array2 = [];
		var array3 = [];
		//gets table
		var oTable = document.getElementById('oneToOneTable');

		//gets rows of table
		var rowLength = oTable.rows.length;

		//loops through rows    
		for (i = 0; i < rowLength; i++){
		   //gets cells of current row
		   var oCells = oTable.rows.item(i).cells;

		   //gets amount of cells of current row
		   var cellLength = oCells.length;

		   //loops through each cell in current row
		   for(var j = 0; j < cellLength; j += 3){
			   array1.push(oCells.item(j).innerHTML);
			   array2.push(oCells.item(j+1).innerHTML);
			   array3.push(oCells.item(j+2).innerHTML);
		   }
		   
		}

		if(array1.length != 0){
			passProteinsJSONToJSFManagedBean ([ {
                name : 'array1',
                value :  isSafe(array1)
            },
            {
                name : 'array2',
                value :  isSafe(array2)
             },
             {
                name : 'array3',
                value :  isSafe(array3)
             },
             {
                name : 'jacc',
                value :  isSafe(document.getElementById("jaccardOneToOne").value)
             }
             ]);
		}else{
			var dialogInstance = new BootstrapDialog();
            dialogInstance.setTitle('INPUT ERROR');
            dialogInstance.setMessage('Please upload CSV file that contains protein pairs!');
            dialogInstance.setType(BootstrapDialog.TYPE_DANGER);
            dialogInstance.open();
		}
	}
	
	function searchMultipleProtein2(){
		var array1 = [];
		//gets table
		var oTable = document.getElementById('manyToManyTable');

		//gets rows of table
		var rowLength = oTable.rows.length;

		//loops through rows    
		for (i = 0; i < rowLength; i++){
		   //gets cells of current row
		   var oCells = oTable.rows.item(i).cells;

		   //gets amount of cells of current row
		   var cellLength = oCells.length;

		   //loops through each cell in current row
		   for(var j = 0; j < cellLength; j ++){
			   array1.push(oCells.item(j).innerHTML);
		   }
		   
		}

		if(array1.length != 0){
			passProteinsJSONToJSFManagedBean ([ {
                name : 'array1',
                value :  isSafe(array1)
            },
            {
                name : 'array2',
                value :  ""
             },
             {
                name : 'array3',
                value :  ""
             },
             {
                name : 'jacc',
                value :  isSafe(document.getElementById("jaccardManyToMany").value)
             }
             ]);
		}else{
			var dialogInstance = new BootstrapDialog();
            dialogInstance.setTitle('INPUT ERROR');
            dialogInstance.setMessage('Please upload CSV file that contains protein accessions!');
            dialogInstance.setType(BootstrapDialog.TYPE_DANGER);
            dialogInstance.open();
		}
	}
	
    function searchGene(selection){
    	if(selection == "single"){
    		if($('#geneId').val() != ""){
    			passGeneToJSFManagedBean ([ {
                    name : 'gene',
                    value :  isSafe(document.getElementById("geneId").value)
                   }
                 ]);
    		}else{
    			var dialogInstance = new BootstrapDialog();
                dialogInstance.setTitle('INPUT ERROR');
                dialogInstance.setMessage('Gene name/id cannot be empty!');
                dialogInstance.setType(BootstrapDialog.TYPE_DANGER);
                dialogInstance.open();
    		}
    		
    	}else if(selection == "double"){
    		if($('#geneId1').val() != ""  && $('#geneId2').val() != "" ){
    			passGeneDoubleToJSFManagedBean ([ {
                    name : 'gene',
                    value :  isSafe(document.getElementById("geneId1").value) + "," + isSafe(document.getElementById("geneId2").value)
                   }
                 ]);
    		}else{
    			var dialogInstance = new BootstrapDialog();
                dialogInstance.setTitle('INPUT ERROR');
                dialogInstance.setMessage('Gene name/id cannot be empty!');
                dialogInstance.setType(BootstrapDialog.TYPE_DANGER);
                dialogInstance.open();
    		}
    		
    	}
    	 
    
		return false;
    }
    
    function searchPathway(){
    	if($('#pathwayId').val() != ""){
    		passPathwayNameToJSFManagedBean ([ {
                name : 'pathway',
                value :  isSafe(document.getElementById("pathwayId").value)
               }
             ]);
		}else{
			var dialogInstance = new BootstrapDialog();
            dialogInstance.setTitle('INPUT ERROR');
            dialogInstance.setMessage('Pathway name / reactome accession cannot be empty!');
            dialogInstance.setType(BootstrapDialog.TYPE_DANGER);
            dialogInstance.open();
		}
    }
    
    function searchDisease(){
    	if($('#diseaseId').val() != ""){
    		passDiseaseNameToJSFManagedBean ([ {
                name : 'disease',
                value :  isSafe(document.getElementById("diseaseId").value)
               }
             ]);
		}else{
			var dialogInstance = new BootstrapDialog();
            dialogInstance.setTitle('INPUT ERROR');
            dialogInstance.setMessage('Disease name / disGeNet id cannot be empty!');
            dialogInstance.setType(BootstrapDialog.TYPE_DANGER);
            dialogInstance.open();
		}
    }
    
    function searchTissue(){
    	var e = document.getElementById("selectTissue");   	
    	
    	if(e.selectedIndex != 0){
    		passTissueNameToJSFManagedBean ([ {
                name : 'tissue',
                value :  e.options[e.selectedIndex].value
               }
             ]);
		}else{
			var dialogInstance = new BootstrapDialog();
            dialogInstance.setTitle('INPUT ERROR');
            dialogInstance.setMessage('Please select a tissue to search!');
            dialogInstance.setType(BootstrapDialog.TYPE_DANGER);
            dialogInstance.open();
		}
    }
    
    function clickButton1() {
        document.getElementById('btnprotein1').click();
        var els=document.getElementsByName("jaccValue");
		for (var i=0; i < els.length; i++) {
			document.getElementById(els[i].id).value  = document.getElementById("jaccardSingle").value;
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
		document.getElementById('diseaseId').value = 'Invasive Breast Carcinoma';
		return false;
	}
	
	function setExampleDisease2() {
		document.getElementById('diseaseId').value = 'C0018802';
		return false;
	}
	
	function loadFile(event) {
		var table=document.getElementById("oneToOneTable");
		$("#oneToOneTable tr").remove(); 
		var maxColumn = 2;
		if($('#edgeAnnotationCheckBox').is(':checked')){
			maxColumn = 3;
		}
		
		alasql('SELECT * FROM FILE(?,{headers:false})',[event],function(data){
			for(var obj in data){
			  if(data.hasOwnProperty(obj)){
			      var headerIndex = 0;
			      var row=table.insertRow(table.rows.length);		
				  for(var prop in data[obj]){
			        if(data[obj].hasOwnProperty(prop)  && headerIndex < maxColumn ){
					   var cell=row.insertCell(headerIndex);
					   cell.innerHTML=data[obj][prop];
					   headerIndex++;
			        }
			      }
				  if(headerIndex != 3){
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
		var table=document.getElementById("manyToManyTable");
		$("#manyToManyTable tr").remove(); 
		var maxColumn = 1;

		alasql('SELECT * FROM FILE(?,{headers:false})',[event],function(data){
			for(var obj in data){
			  if(data.hasOwnProperty(obj)){
			      var headerIndex = 0;
			      var row=table.insertRow(table.rows.length);		
				  for(var prop in data[obj]){
			        if(data[obj].hasOwnProperty(prop)  && headerIndex < maxColumn ){
					   var cell=row.insertCell(headerIndex);
					   cell.innerHTML=data[obj][prop];
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
	
	function isSafe(text){
		if(text.includes("<script>")){
			safe = text.replace("<script>", "safe");
			return safe;
		}else{
			return text;
		}
	}
	