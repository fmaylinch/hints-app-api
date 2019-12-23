package com.codethen.hintsapp;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/cards")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HintCardsResource {

    private final MongoCollection<Document> collection;

    // TODO: MongoClient is provided by the quarkus-mongodb-client dependency,
    //  but maybe we should add the official mongodb dependency and configure this manually.
    //  I could also use Spring Mongo support, which does automatic mapping.
    public HintCardsResource(MongoClient mongoClient) {
        collection = mongoClient.getDatabase("hintsapp").getCollection("cards");
    }

    @GET
    public List<HintCard> getAll() {

        final List<HintCard> result = new ArrayList<>();

        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                final Document doc = cursor.next();
                final HintCard card = HintsCardMongoAdapter.from(doc);
                result.add(card);
            }
            return result;
        }
    }

    @GET @Path("/{id}")
    public HintCard getOne(@PathParam String id) {

        final Document doc = collection.find(byId(id)).first();
        return HintsCardMongoAdapter.from(doc);
    }

    @DELETE @Path("/{id}")
    public HintCard deleteOne(@PathParam String id) {

        final HintCard card = getOne(id);
        if (card != null) {
            collection.deleteOne(byId(id));
        }

        return card;
    }

    @POST
    public HintCard saveOrUpdate(HintCard card) {

        if (card.getHints() == null || card.getHints().isEmpty())
            throw new WebApplicationException("At least one hint is required", Response.Status.BAD_REQUEST);

        final Document doc = HintsCardMongoAdapter.from(card);
        assert doc != null;

        if (card.getId() != null) {
            collection.updateOne(byId(card.getId()), doc);
        } else {
            collection.insertOne(doc);
            card.setId(doc.getObjectId("_id").toString());
        }

        return card;
    }

    private Bson byId(String id) {
        return new Document("_id", new ObjectId(id));
    }
}