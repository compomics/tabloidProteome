package com.compomics.neo4j.database;


import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by demet on 12/8/2016.
 */
public class Connection implements Serializable, AutoCloseable {

    private static final long serialVersionUID = 1029809887588268675L;
    private static final Logger LOGGER = LoggerFactory.getLogger(Connection.class);

    /**
     * Singleton instance.
     */
    private static final Connection instance = new Connection();

    private Driver driver;

    private Connection() {
        openConnection();
    }

    @Override
    public void close() {
        LOGGER.info("Closing neo4j driver connection");
        driver.close();
    }

    public static Connection getInstance() {
        return instance;
    }

    private void openConnection() {
        InputStream input = null;
        try {
            LOGGER.info("Opening neo4j driver connection");
            input = getClass().getClassLoader().getResourceAsStream("config.properties");
            Properties prop = new Properties();
            prop.load(input);
            driver = GraphDatabase.driver(prop.getProperty("url"), AuthTokens.basic(prop.getProperty("user"), prop.getProperty("password")));
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
    }

    public Driver getDriver() {
        return driver;
    }

}
