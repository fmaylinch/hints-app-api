package com.codethen.hintsapp;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import io.smallrye.jwt.build.Jwt;
import org.bson.Document;
import org.eclipse.microprofile.jwt.Claims;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;

import static com.codethen.hintsapp.UserMongoAdapter.byEmail;

@Path("security")
@Produces(MediaType.APPLICATION_JSON)
public class SecurityApi {

    private final MongoCollection<Document> users;

    public SecurityApi(MongoClient mongoClient) {
        // TODO: refactor, create MongoService and move this code to a method there
        users = mongoClient.getDatabase("hintsapp").getCollection("users");
    }

    @POST
    @Path("login")
    public User login(Login login) {

        final Document doc = users.find(byEmail(login.getEmail())).first();
        final User user = UserMongoAdapter.from(doc);

        if (user == null) {
            throw new WebApplicationException("User not found", Response.Status.UNAUTHORIZED);
        }

        // TODO: encrypt password
        if (!user.getPassword().equals(login.getPassword())) {
            throw new WebApplicationException("Wrong password", Response.Status.UNAUTHORIZED);
        }

        String token =
                Jwt.issuer("https://example.com/issuer")
                        .upn(user.getId())
                        .groups(new HashSet<>(Arrays.asList("User")))
                        .claim(Claims.email.name(), login.getEmail())
                        .expiresIn(Duration.ofDays(30))
                        .sign();

        // TODO: for now, reuse password field to send token
        user.setPassword(token);
        return user;
    }

}
