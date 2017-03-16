package com.compomics.neo4j.model.nodes;

import java.io.Serializable;

public class Go implements Serializable{

	private static final long serialVersionUID = -7200784279034091136L;
	
	private String id;
	private String name;
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	

}
