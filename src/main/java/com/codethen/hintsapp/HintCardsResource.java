package com.codethen.hintsapp;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/cards")
public class HintCardsResource {

    // TODO: This is done by the quarkus-mongodb-client dependency,
    //  but maybe we should add the official mongodb dependency and configure this manually.
    //  I could also use Spring Mongo support, which does automatic mapping.
    @Inject MongoClient mongoClient;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<HintCard> getAll() {

        final List<HintCard> result = new ArrayList<>();

        try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                final Document doc = cursor.next();
                final HintCard card = HintsCardMongoAdapter.from(doc);
                result.add(card);
            }
            return result;
        }
    }

    private MongoCollection<Document> getCollection(){
        return mongoClient.getDatabase("hintsapp").getCollection("cards");
    }
}