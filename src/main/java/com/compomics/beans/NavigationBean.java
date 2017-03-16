package com.compomics.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class NavigationBean implements Serializable{

	private static final long serialVersionUID = -134801779505298415L;

    /**
     * Go to search page.
     * @return search page name.
     */
    public String toSearch() {
        return "/search.xhtml";
    }
	
    /**
     * Redirect to search page.
     * @return search page name.
     */
    public String redirectToSearch() {
        return "/search.xhtml?faces-redirect=true";
    }
    
    /**
     * Redirect to data page.
     * @return data page name.
     */
    public String redirectToData() {
        return "/dataTable.xhtml?faces-redirect=true";
    }
    
    /**
     * Redirect to data page.
     * @return data page name.
     */
    public String redirectToGraph() {
        return "/graph.xhtml?faces-redirect=true";
    }
    
    /**
     * Redirect to about page.
     * @return data page name.
     */
    public String redirectToAbout() {
        return "/about.xhtml?faces-redirect=true";
    }
}
