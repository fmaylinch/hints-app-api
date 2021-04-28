package com.codethen.hintsapp.security;

import com.codethen.hintsapp.MongoUtil.CommonFields;
import com.mongodb.lang.Nullable;
import org.bson.Document;

import static com.codethen.hintsapp.MongoUtil.doc;

public class UserAdapter {

    private static class Fields {
        public static String email = "email";
        public static String password = "pwd";
    }

    public static Document byEmail(String email) {
        return doc(Fields.email, email);
    }

    @Nullable
    public static User from(@Nullable Document doc) {

        return doc == null ? null : User.builder()
                .id(doc.getObjectId(CommonFields._id).toString())
                .email(doc.getString(Fields.email))
                .password(doc.getString(Fields.password))
                .build();
    }

    @Nullable
    public static Document from(@Nullable User user) {

        return user == null ? null : doc()
                .append(Fields.email, user.getEmail())
                .append(Fields.password, user.getPassword());
    }
}
