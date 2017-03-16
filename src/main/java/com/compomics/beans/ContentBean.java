package com.compomics.beans;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@RequestScoped
public class ContentBean implements Serializable{

	private static final long serialVersionUID = -8187003693999445288L;
	
	private boolean content;
	private boolean version;
	private boolean licence;
	
	private boolean statistics;
	private boolean dataSource;
	private boolean reference;
	private boolean download;
	
	private boolean documentation;
	private boolean contact;
	//subpages
	private static final String SUB_PAGE_CONTENT = "content"; 
	private static final String SUB_PAGE_VERSION = "version";
	private static final String SUB_PAGE_LICENCE = "licence";
	
	private static final String SUB_PAGE_STATISTICS = "statistics";
	private static final String SUB_PAGE_DATA_SOURCE = "dataSource";
	private static final String SUB_PAGE_REFERENCE = "reference";
	private static final String SUB_PAGE_DOWNLOAD = "download";
	
	private static final String SUB_PAGE_DOCUMENTATION = "documentation";
	private static final String SUB_PAGE_CONTACT = "contact";	
	
	public boolean isContent() {
		return content;
	}

	public boolean isVersion() {
		return version;
	}

	public boolean isLicence() {
		return licence;
	}

	public boolean isStatistics() {
		return statistics;
	}

	public boolean isDataSource() {
		return dataSource;
	}

	public boolean isReference() {
		return reference;
	}

	public boolean isDownload() {
		return download;
	}

	public boolean isDocumentation() {
		return documentation;
	}

	public boolean isContact() {
		return contact;
	}

	@PostConstruct
	public void init(){
		String subpage = "";
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> requestParams = context.getExternalContext().getRequestParameterMap(); 
		if(requestParams.containsKey("active_subpage")){
			subpage = requestParams.get("active_subpage");
			
			switch(subpage){
				// about page
				case SUB_PAGE_CONTENT:
					updateAboutPage(SUB_PAGE_CONTENT);
					break;
				
				case SUB_PAGE_VERSION:
					updateAboutPage(SUB_PAGE_VERSION);
					break;
					
				case SUB_PAGE_LICENCE:
					updateAboutPage(SUB_PAGE_LICENCE);
					break;
				// data page
				case SUB_PAGE_STATISTICS:
					updateDataPage(SUB_PAGE_STATISTICS);
					break;
					
				case SUB_PAGE_DATA_SOURCE:
					updateDataPage(SUB_PAGE_DATA_SOURCE);
					break;
					
				case SUB_PAGE_REFERENCE:
					updateDataPage(SUB_PAGE_REFERENCE);
					break;
					
				case SUB_PAGE_DOWNLOAD:
					updateDataPage(SUB_PAGE_DOWNLOAD);
					break;
				// help page	
				case SUB_PAGE_DOCUMENTATION:
					updateHelpPage(SUB_PAGE_DOCUMENTATION);
					break;
				case SUB_PAGE_CONTACT:
					updateHelpPage(SUB_PAGE_CONTACT);
					break;
			}
		}
	}
	/**
	 * Update about page by given sub page name
	 * @param subpage sub page name.
	 */
	public void updateAboutPage(String subpage){
		content = false;
		version = false;
		licence = false;
		if(subpage.equals(SUB_PAGE_CONTENT)){
			content = true;
		}else if(subpage.equals(SUB_PAGE_VERSION)){
			version = true;
		}else if(subpage.equals(SUB_PAGE_LICENCE)){
			licence = true;
		}
	}
	/**
	 * Update data page by given sub page name
	 * @param subpage sub page name.
	 */
	public void updateDataPage(String subpage){
		statistics = false;
		dataSource = false;
		reference = false;
		download = false;
		if(subpage.equals(SUB_PAGE_STATISTICS)){
			statistics = true;
		}else if(subpage.equals(SUB_PAGE_DATA_SOURCE)){
			dataSource = true;
		}else if(subpage.equals(SUB_PAGE_REFERENCE)){
			reference = true;
		}else if(subpage.equals(SUB_PAGE_DOWNLOAD)){
			download = true;
		}
	}
	/**
	 * Update help page by given sub page name
	 * @param subpage sub page name.
	 */
	public void updateHelpPage(String subpage){
		documentation = false;
		contact = false;
		if(subpage.equals(SUB_PAGE_DOCUMENTATION)){
			documentation = true;
		}else if(subpage.equals(SUB_PAGE_CONTACT)){
			contact = true;
		}
	}

}
