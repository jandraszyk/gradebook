package com.janek.gradebook;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.IOException;
import java.net.URI;

public class Main {

    public static void main(String [] args) throws IOException {

        MongoClient mongoClient = new MongoClient("localhost", 8004);
        MongoDatabase database = mongoClient.getDatabase("gradebook_example");
        Morphia morphia = new Morphia();
        morphia.mapPackage("com.janek.gradebook");

        final Datastore datastore = morphia.createDatastore(mongoClient, "gradebook_morphia");
        datastore.ensureIndexes();

        ResourceConfig resourceConfig = new ResourceConfig().packages("com.janek.gradebook").packages("org.glassfish.jersey.examples.linking").register(DeclarativeLinkingFeature.class);
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://localhost:8000/"), resourceConfig);

        System.in.read();
        server.shutdown();
    }
}
