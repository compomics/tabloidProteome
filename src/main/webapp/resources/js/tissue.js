$(function(){
	var select = document.getElementById("selectTissue");
	var options = ["adrenal gland", "appendix", "bone marrow", "breast", "bronchus", "caudate", "cerebellum", "cerebral cortex", "cervix, uterine",
		"colon","duodenum","endometrium 1","endometrium 2","epididymis", "esophagus","fallopian tube","gallbladder","hair","heart muscle","hippocampus",
		"hypothalamus","kidney","lactating breast","liver","lung","lymph node","nasopharynx","oral mucosa","ovary","pancreas","parathyroid gland","placenta","prostate",
		"rectum","retina","salivary gland","seminal vesicle","skeletal muscle","skin","skin 1","skin 2","small intestine","smooth muscle","soft tissue 1","soft tissue 2",
		"spleen","stomach 1","stomach 2","testis","thyroid gland","tonsil","urinary bladder","vagina"];
	for(var i = 0; i < options.length; i++) {
	    var opt = options[i];
	    var el = document.createElement("option");
	    el.textContent = opt;
	    el.value = opt;
	    select.appendChild(el);
	}	
});


