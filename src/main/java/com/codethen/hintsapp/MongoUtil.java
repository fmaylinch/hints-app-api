package com.codethen.hintsapp;

import org.bson.Document;
import org.bson.types.ObjectId;

public class MongoUtil {

    public static class CommonFields {
        public static String _id = "_id";
    }

    public static class Ops {
        public static String set = "$set";
    }

    public static Document doc() {
        return new Document();
    }

    public static Document doc(String key, Object value) {
        return new Document(key, value);
    }

    public static Document byId(String id) {
        return doc(CommonFields._id, new ObjectId(id));
    }
}
