package com.janek.gradebook;


import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class Main {

    public static void main(String [] args) throws IOException {

        try {
            ResourceConfig resourceConfig = new ResourceConfig().packages("com.janek.gradebook")
                    .packages("org.glassfish.jersey.examples.linking")
                    .register(DeclarativeLinkingFeature.class)
                    .register(DateParamConverterProvider.class)
                    .register(RestError.class);
            HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://localhost:8000/"), resourceConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
