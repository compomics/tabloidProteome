/**
 * 
 */
function format ( idx ) {
	  var associationAll = document.getElementById('association').value.slice(0, document.getElementById('association').value.length).split(',');
	  var association =	associationAll[idx].slice(0, associationAll[idx].length).split('*');

	  var commonProjectsAll = document.getElementById('commonProjects').value.slice(0, document.getElementById('commonProjects').value.length).split(',');
	  var commonProjects = commonProjectsAll[idx].slice(0, commonProjectsAll[idx].length).split('*');
	  
	  var pathwaysAll = document.getElementById('pathways').value.slice(0, document.getElementById('pathways').value.length).split(',');
	  var pathways = pathwaysAll[idx].slice(0, pathwaysAll[idx].length).split('*');
	  
	  var complexesAll = document.getElementById('complexes').value.slice(0, document.getElementById('complexes').value.length).split(',');
	  var complexes = complexesAll[idx].slice(0, complexesAll[idx].length).split('*');
	  
	  var mfsAll = document.getElementById('mfs').value.slice(0, document.getElementById('mfs').value.length).split(',');
	  var mfs = mfsAll[idx].slice(0, mfsAll[idx].length).split('*');
	  var bpsAll = document.getElementById('bps').value.slice(0, document.getElementById('bps').value.length).split(',');
	  var bps = bpsAll[idx].slice(0, bpsAll[idx].length).split('*');
	  var ccsAll = document.getElementById('ccs').value.slice(0, document.getElementById('ccs').value.length).split(',');
	  var ccs = ccsAll[idx].slice(0, ccsAll[idx].length).split('*');
	  
	  var tables = '<h3 style="padding-left:10px;">Association</h3>' +
	    '<table cellpadding="0" cellspacing="0" >'+
        '<tr>'+
        '<td>intact confidence:</td>'+
        '<td>'+association[0].replace("[", "").replace("]", "").replace(/;/g, ",")+'</td>'+
    '</tr>'+
    '<tr>'+
        '<td>interaction detection:</td>'+
        '<td>'+association[1].replace("[", "").replace("]", "").replace(/;/g, ",")+'</td>'+
    '</tr>'+
    '<tr>'+
        '<td>interactionType:</td>'+
        '<td>'+association[2].replace("[", "").replace("]", "").replace(/;/g, ",")+'</td>'+
    '</tr>'+
'</table>'+
	'<h3 style="padding-left:10px;">Common projects</h3>' +
	'<div style = "height:200px; overflow:auto">' +
	'<table cellpadding="5" cellspacing="0" border="1px solid rgba(0, 0, 0, 0)">'+
		'<thead> <tr> <th>No</th><th>project accession</th><th>keywords</th><th>tissue</th><th>tags</th></tr></thead>'+
		'<tbody>';
		for(i=0; i < commonProjects.length/4; i++){
			tables += '<tr><td>'+(i+1)+'</td><td><a href="https://www.ebi.ac.uk/pride/archive/projects/'
			+commonProjects[i*4].replace("[", "").replace("]", "").replace(/;/g, ",")
			+'" style="color: #404040;">'
			+commonProjects[i*4].replace("[", "").replace("]", "").replace(/;/g, ",")+'</a>'
			+'</td><td>'+commonProjects[i*4+1].replace("[", "").replace("]", "").replace(/;/g, ",")
			+'</td><td>'+commonProjects[i*4+2].replace("[", "").replace("]", "").replace(/;/g, ",")
			+'</td><td>'+commonProjects[i*4+3].replace("[", "").replace("]", "").replace(/;/g, ",")+'</td></tr>';
		}
	tables += '</tbody></table></div>' + 

	'<h3 style="padding-left:10px;">Pathways</h3>' +
	'<div style = "height:200px; overflow:auto">' +
	'<table cellpadding="5" cellspacing="0" border="1px solid rgba(0, 0, 0, 0)" >'+
		'<thead> <tr> <th>No</th><th>reactome accession</th><th>pathway name</th><th>evidence code</th></tr></thead>'+
		'<tbody>';
		for(i=0; i < pathways.length/3; i++){
			tables += '<tr><td>'+(i+1)+'</td><td><a href="http://www.reactome.org/PathwayBrowser/#/'
			+pathways[i*3].replace("[", "").replace("]", "").replace(/;/g, ",")+'" style="color: #404040;">'
			+pathways[i*3].replace("[", "").replace("]", "").replace(/;/g, ",")+'</a>'
			+'</td><td>'+pathways[i*3+1].replace("[", "").replace("]", "").replace(/;/g, ",")
			+'</td><td>'+pathways[i*3+2].replace("[", "").replace("]", "").replace(/;/g, ",")+'</td></tr>';
		}
	tables += '</tbody></table></div>'+
	'<h3 style="padding-left:10px;">Complexes</h3>' +
	'<div style = "height:200px; overflow:auto">' +
	'<table cellpadding="5" cellspacing="0" border="1px solid rgba(0, 0, 0, 0)" >'+
		'<thead> <tr> <th>No</th><th>corum id</th><th>complex name</th><th>complex comment</th><th>cell line</th>'+
		'<th>disease comment</th><th>subunit comment</th><th>pubmed id</th><th>purification method</th></tr></thead>'+
		'<tbody>';
		for(i=0; i < complexes.length/8; i++){
			tables += '<tr><td>'+(i+1)+'</td><td>'+complexes[i*8].replace("[", "").replace("]", "").replace(/;/g, ",")
			+'</td><td>'+complexes[i*8+1].replace("[", "").replace("]", "").replace(/;/g, ",")
			+'</td><td>'+complexes[i*8+2].replace("[", "").replace("]", "").replace(/;/g, ",")
			+'</td><td>'+complexes[i*8+3].replace("[", "").replace("]", "").replace(/;/g, ",")
			+'</td><td>'+complexes[i*8+4].replace("[", "").replace("]", "").replace(/;/g, ",")
			+'</td><td>'+complexes[i*8+5].replace("[", "").replace("]", "").replace(/;/g, ",")
			+'</td><td><a href="https://www.ncbi.nlm.nih.gov/pubmed?cmd=search&amp;term='
			+complexes[i*8+6].replace("[", "").replace("]", "").replace(/;/g, ",")+'" style="color: #404040;">'
			+complexes[i*8+6].replace("[", "").replace("]", "").replace(/;/g, ",")+'</a>'
			+'</td><td>'+complexes[i*8+7].replace("[", "").replace("]", "").replace(/;/g, ",")+'</td></tr>';
		}
	tables += '</tbody></table></div>' +
	
		'<h3 style="padding-left:10px;">GO</h3>' +
		'<div style = "height:200px; overflow:auto; display: inline-block;">' +
		'<table border="1px solid rgba(0, 0, 0, 0)">'+
			'<thead> <tr> <th>No</th><th>MF id</th><th>MF name</th></tr></thead>'+
			'<tbody>';
			if(mfs.length > 2 ){
			for(i=0; i < mfs.length/2; i++){
				tables += '<tr><td>'+(i+1)+'</td><td><a href="http://www.ebi.ac.uk/QuickGO/GTerm?id='
				+mfs[i*2].replace("[", "").replace("]", "").replace(/;/g, ",")+'" style="color: #404040;">'
				+mfs[i*2].replace("[", "").replace("]", "").replace(/;/g, ",")+'</a>'
				+'</td><td>'+mfs[i*2+1].replace("[", "").replace("]", "").replace(/;/g, ",")+'</td></tr>';
			}
			}
		tables += '</tbody></table></div>' +
		'<div style = "height:200px; overflow:auto; display: inline-block;">' +
		'<table cellpadding="5" cellspacing="0" border="1px solid rgba(0, 0, 0, 0)">'+
			'<thead> <tr> <th>No</th><th>BP id</th><th>BP name</th></tr></thead>'+
			'<tbody>';
			for(i=0; i < bps.length/2; i++){
				tables += '<tr><td>'+(i+1)+'</td><td><a href="http://www.ebi.ac.uk/QuickGO/GTerm?id='
				+bps[i*2].replace("[", "").replace("]", "").replace(/;/g, ",")+'" style="color: #404040;">'
				+bps[i*2].replace("[", "").replace("]", "").replace(/;/g, ",")+'</a>'
				+'</td><td>'+bps[i*2+1].replace("[", "").replace("]", "").replace(/;/g, ",")+'</td></tr>';
			}
		tables += '</tbody></table></div>' +
		'<div style = "height:200px; overflow:auto; display: inline-block;">' +
		'<table cellpadding="5" cellspacing="0" border="1px solid rgba(0, 0, 0, 0))">'+
			'<thead> <tr> <th>No</th><th>CC id</th><th>CC name</th></tr></thead>'+
			'<tbody>';
			for(i=0; i < ccs.length/2; i++){
				tables += '<tr><td>'+(i+1)+'</td><td><a href="http://www.ebi.ac.uk/QuickGO/GTerm?id='
				+ccs[i*2].replace("[", "").replace("]", "").replace(/;/g, ",")+'" style="color: #404040;">'
				+ccs[i*2].replace("[", "").replace("]", "").replace(/;/g, ",")+'</a>'
				+'</td><td>'+ccs[i*2+1].replace("[", "").replace("]", "").replace(/;/g, ",")+'</td></tr>';
			}
		tables += '</tbody></table></div>';
	return tables;
  }
  $(document).ready(function() {
	  $('#dataTable').DataTable( {
		  "paging":   false,
	      "filter": false,
	      "info":     false
	    } );
	  	var table = $('#dataTable').DataTable();

	    // Add event listener for opening and closing details
	    $('#dataTable tbody').on('click', '#btn', function () {
	    	
	        var tr = $(this).closest('tr');
	        
	        var row = table.row(tr);
	        
	        if ( row.child.isShown() ) {
	        	
	            // This row is already open - close it
	            row.child.hide();
	            tr.removeClass('shown');
	        }
	        else {
	            // Open this row
	            row.child( format(row.index()) ).show();
	            
	            tr.addClass('shown');
	        }
	    } );
	} );