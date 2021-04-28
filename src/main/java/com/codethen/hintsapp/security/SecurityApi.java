package com.codethen.hintsapp.security;

import com.codethen.hintsapp.MongoUtil;
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
import java.util.Set;

import static com.codethen.hintsapp.security.UserAdapter.byEmail;

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
    public User loginOrRegister(Login login) {

        final Document doc = users.find(byEmail(login.getEmail())).first();
        User user = UserAdapter.from(doc);

        if (user == null) {
            if (hasInfoToRegister(login)) {
                user = register(login);
            } else {
                throw new WebApplicationException("User not found", Response.Status.UNAUTHORIZED);
            }
        }

        // TODO: encrypt password
        if (!user.getPassword().equals(login.getPassword())) {
            throw new WebApplicationException("Wrong password", Response.Status.UNAUTHORIZED);
        }

        final String token =
                Jwt.issuer("https://example.com/issuer")
                        .upn(user.getId())
                        .groups(Set.of(Roles.USER))
                        .claim(Claims.email.name(), login.getEmail())
                        .expiresIn(Duration.ofDays(30))
                        .sign();

        user.setPassword(token); // We return the token instead of password
        return user;
    }

    private boolean hasInfoToRegister(Login login) {
        return login.getPassword() != null
                && login.getPassword().length() >= 5
                && login.getPassword().equals(login.getPassword2());
    }

    private User register(Login login) {
        final User user = User.builder()
                .email(login.getEmail())
                .password(login.getPassword())
                .build();
        final Document doc = UserAdapter.from(user);
        users.insertOne(doc);
        user.setId(doc.getObjectId(MongoUtil.CommonFields._id).toString());
        return user;
    }
}
