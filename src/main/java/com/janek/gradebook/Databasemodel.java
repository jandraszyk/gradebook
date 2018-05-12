package com.janek.gradebook;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class Databasemodel {

    private static Databasemodel Instance = new Databasemodel();
    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;
    private static Datastore datastore;
    private static MongoCollection<Document> collection;
    private static Morphia morphia;


    public static Databasemodel getInstance() {
        return Instance;
    }

    public static MongoClient getMongoClient() {
        return mongoClient;
    }

    public static MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public static Datastore getDatastore() {
        return datastore;
    }

    public static MongoCollection<Document> getCollection() {
        return collection;
    }

    public static Morphia getMorphia() {
        return morphia;
    }

    public Databasemodel() {
        mongoClient = new MongoClient("localhost", 8004);
        morphia = new Morphia();
        datastore = morphia.createDatastore(mongoClient, "gradebook_morphia");
        //morphia.mapPackage("com.janek.gradebook");
        //datastore.ensureIndexes();
    }
}
