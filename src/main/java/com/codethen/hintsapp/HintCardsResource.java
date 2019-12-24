package com.codethen.hintsapp;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: This resource ignores HTTP methods and only uses {@link POST}.
 *  More details about this idea: https://softwareengineering.stackexchange.com/a/402901/353567
 */

@Path("/cards")
@Produces(MediaType.APPLICATION_JSON)
public class HintCardsResource {

    private final MongoCollection<Document> collection;

    // TODO: MongoClient is provided by the quarkus-mongodb-client dependency,
    //  but maybe we should add the official mongodb dependency and configure this manually.
    //  I could also use Spring Mongo support, which does automatic mapping.
    public HintCardsResource(MongoClient mongoClient) {
        collection = mongoClient.getDatabase("hintsapp").getCollection("cards");
    }

    @POST @Path("getAll")
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

    @POST @Path("/getOne")
    @Consumes(MediaType.TEXT_PLAIN)
    public HintCard getOne(String id) {

        final Document doc = collection.find(byId(id)).first();
        return HintsCardMongoAdapter.from(doc);
    }

    @POST @Path("/deleteOne")
    @Consumes(MediaType.TEXT_PLAIN)
    public HintCard deleteOne(String id) {

        final HintCard card = getOne(id);
        if (card != null) {
            collection.deleteOne(byId(id));
        }

        return card;
    }

    @POST @Path("/saveOrUpdate")
    @Consumes(MediaType.APPLICATION_JSON)
    public HintCard saveOrUpdate(HintCard card) {

        if (card.getHints() == null || card.getHints().isEmpty())
            throw new WebApplicationException("At least one hint is required", Response.Status.BAD_REQUEST);

        final Document doc = HintsCardMongoAdapter.from(card);
        assert doc != null;

        // TODO: I think we could do this for both cases (can we then retrieve the _id?)
        // collection.updateOne(byId(card.getId()), new Document("$set", doc), new UpdateOptions().upsert(true));

        if (card.getId() != null) {
            collection.updateOne(byId(card.getId()), new Document("$set", doc));
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