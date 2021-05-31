package com.codethen.hintsapp;

import com.mongodb.Function;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

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

    /** Maps a {@link FindIterable} to a {@link List}, using the given mapping {@link Function} */
    public static <T> List<T> iterableToList(FindIterable<Document> iterable, Function<Document, T> mapping) {
        try (MongoCursor<Document> cursor = iterable.iterator()) {
            return MongoUtil.cursorToList(cursor, mapping);
        }
    }

    /** Maps a {@link MongoCursor} to a {@link List}, using the given mapping {@link Function} */
    public static <T> List<T> cursorToList(MongoCursor<Document> cursor, Function<Document, T> mapping) {
        final List<T> result = new ArrayList<>();
        while (cursor.hasNext()) {
            final Document doc = cursor.next();
            final T element = mapping.apply(doc);
            result.add(element);
        }
        return result;
    }

    public static Document byId(String id) {
        return doc(CommonFields._id, new ObjectId(id));
    }
}
