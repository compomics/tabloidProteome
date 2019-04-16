package com.compomics.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.core.Application;

public class RestTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(JSONService.class);
    }

    @Test
    public void ordersPathParamTest() {
        String response = target("/protein/pathway/R-HSA-69620&0.4").request().get(String.class);
        System.out.println(response);
    }

}
