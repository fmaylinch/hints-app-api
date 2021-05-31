package com.codethen.hintsapp.security;

import com.codethen.hintsapp.MongoUtil;
import com.codethen.hintsapp.cards.HintCard;
import com.codethen.hintsapp.cards.HintCardAdapter;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.smallrye.jwt.build.Jwt;
import org.bson.Document;
import org.eclipse.microprofile.jwt.Claims;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.util.List;
import java.util.Set;

import static com.codethen.hintsapp.security.UserAdapter.byEmail;

@Path("security")
@Produces(MediaType.APPLICATION_JSON)
public class SecurityApi {

    private final MongoCollection<Document> users;
    private final MongoCollection<Document> cards;

    @Inject
    PasswordEncoder passwordEncoder;

    public SecurityApi(MongoClient mongoClient) {
        final MongoDatabase database = mongoClient.getDatabase("hintsapp");
        users = database.getCollection("users");
        cards = database.getCollection("cards");
    }

    @POST
    @Path("login")
    public User loginOrRegister(Login login) {

        final Document doc = users.find(byEmail(login.getEmail())).first();
        User user = UserAdapter.from(doc);

        if (user == null) {
            if (hasInfoToRegister(login)) {
                user = register(login);
                createSampleCard(user);
            } else {
                throw new WebApplicationException("User not found", Response.Status.UNAUTHORIZED);
            }
        }

        if (!passwordEncoder.verify(login.getPassword(), user.getPassword())) {
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

        final String hashedPwd = passwordEncoder.encode(login.getPassword());

        final User user = User.builder()
                .email(login.getEmail())
                .password(hashedPwd)
                .build();

        final Document doc = UserAdapter.from(user);
        users.insertOne(doc);
        user.setId(doc.getObjectId(MongoUtil.CommonFields._id).toString());

        return user;
    }

    private void createSampleCard(User user) {

        final HintCard card = HintCard.builder()
                .userId(user.getId())
                .hints(List.of("example", "пример", "ejemplo"))
                .notes("This is a sample card.\n" +
                        "Create more cards with the ＋ button.\n" +
                        "Above, write hints, one hint in each line.\n" +
                        "Then, when you play (with the ▶︎ button), you will see a random hint of one of the cards. " +
                        "You can try to guess the rest of the card. For example, guess the translation of the word.\n" +
                        "Add optional notes to a card (like this one).\n" +
                        "Rate how well you know the card, using the slider below.\n" +
                        "Add optional tags, so you can then find cards by tag.\n" +
                        "Edit a card and just go back to the card list to save the card.\n" +
                        "Delete a card with the trash bin button.\n")
                .score(30)
                .tags(List.of("tag1", "tag2", "tag3"))
                .build();

        cards.insertOne(HintCardAdapter.from(card));
    }
}
