package com.codethen.hintsapp;

import com.codethen.hintsapp.MongoUtil.CommonFields;
import com.mongodb.lang.Nullable;
import org.bson.Document;

import static com.codethen.hintsapp.MongoUtil.doc;

public class UserMongoAdapter {

    private static class Fields {
        public static String email = "email";
        public static String password = "pwd";
    }

    public static Document byEmail(String email) {
        return doc(Fields.email, email);
    }

    @Nullable
    public static User from(@Nullable Document doc) {

        if (doc == null) return null;

        final User user = new User();
        user.setId( doc.getObjectId(CommonFields._id).toString() );
        user.setEmail( doc.getString(Fields.email) );
        user.setPassword( doc.getString(Fields.password) );
        return user;
    }

    @Nullable
    public static Document from(@Nullable User user) {

        if (user == null) return null;

        return doc()
                .append(Fields.email, user.getEmail())
                .append(Fields.password, user.getPassword());
    }
}
