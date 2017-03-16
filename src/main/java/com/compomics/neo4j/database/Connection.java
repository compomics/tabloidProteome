package com.compomics.neo4j.database;


import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

/**
 * Created by demet on 12/8/2016.
 */
public class Connection implements Serializable{

	private static final long serialVersionUID = 1029809887588268675L;
	private Session session;
    private Properties prop = new Properties();
    private InputStream input = null;


    
    public Session openConnection() {
        try {
            input = getClass().getClassLoader().getResourceAsStream("config.properties");
            prop.load(input);
            Driver driver = GraphDatabase.driver(prop.getProperty("url"), AuthTokens.basic(prop.getProperty("user"), prop.getProperty("password")));
            session = driver.session();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return session;
    }
    
    public void closeConnection(){
    	session.close();
    }

}
