package com.codethen.hintsapp.cards;

import com.codethen.hintsapp.MongoUtil;
import com.codethen.hintsapp.MongoUtil.CommonFields;
import com.codethen.hintsapp.MongoUtil.Ops;
import com.codethen.hintsapp.security.Roles;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.List;

import static com.codethen.hintsapp.cards.HintCardAdapter.*;

/**
 * Manages HintCards
 *
 * This API ignores HTTP methods and only uses {@link POST}.
 * More details about this idea: https://softwareengineering.stackexchange.com/a/402901/353567
 */
@Path("cards")
@Produces(MediaType.APPLICATION_JSON)
public class HintCardsApi {

    private final MongoCollection<Document> collection;

    // TODO: MongoClient is provided by the quarkus-mongodb-client dependency,
    //  but maybe we should add the official mongodb dependency and configure this manually.
    //  I could also use Spring Mongo support, which does automatic mapping.
    public HintCardsApi(MongoClient mongoClient) {
        collection = mongoClient.getDatabase("hintsapp").getCollection("cards");
    }

    @POST @Path("getAll")
    @RolesAllowed({ Roles.USER })
    public List<HintCard> getAll(@Context SecurityContext ctx) {

        final String userId = getUserId(ctx);

        return MongoUtil.iterableToList(
                collection.find(byUserId(userId)).sort(sortByFirstHint()),
                HintCardAdapter::from);
    }

    @POST @Path("getOne")
    @RolesAllowed({ Roles.USER })
    @Consumes(MediaType.TEXT_PLAIN)
    public HintCard getOne(@Context SecurityContext ctx, String id) {

        final Document doc = collection.find(byIdAndUserId(id, getUserId(ctx))).first();
        return HintCardAdapter.from(doc);
    }

    @POST @Path("deleteOne")
    @RolesAllowed({ Roles.USER })
    @Consumes(MediaType.TEXT_PLAIN)
    public HintCard deleteOne(@Context SecurityContext ctx, String id) {

        final HintCard card = getOne(ctx, id);
        if (card != null) {
            collection.deleteOne(byIdAndUserId(id, getUserId(ctx)));
        }

        return card;
    }

    @POST @Path("saveOrUpdate")
    @RolesAllowed({ Roles.USER })
    @Consumes(MediaType.APPLICATION_JSON)
    public HintCard saveOrUpdate(@Context SecurityContext ctx, HintCard card) {

        if (card.getHints() == null || card.getHints().isEmpty())
            throw new WebApplicationException("At least one hint is required", Response.Status.BAD_REQUEST);

        final String userId = getUserId(ctx);
        card.setUserId(userId);
        final Document doc = HintCardAdapter.from(card);
        assert doc != null;

        // Maybe we could do this for both update and insert (can we then retrieve the _id?)
        // collection.updateOne(byId(card.getId()), new Document(Ops.set, doc), new UpdateOptions().upsert(true));

        if (card.getId() != null) {
            collection.updateOne(byIdAndUserId(card.getId(), userId), new Document(Ops.set, doc));
        } else {
            collection.insertOne(doc);
            card.setId(doc.getObjectId(CommonFields._id).toString());
        }

        return card;
    }

    private JsonWebToken getJwt(SecurityContext ctx) {
        final Principal userPrincipal = ctx.getUserPrincipal();
        return (JsonWebToken) userPrincipal;
    }

    private String getUserId(SecurityContext ctx) {
        final JsonWebToken jwt = getJwt(ctx);
        return jwt.getClaim(Claims.upn.name());
    }
}