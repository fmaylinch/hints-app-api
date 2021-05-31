package com.codethen.hintsapp.cards;

import com.codethen.hintsapp.MongoUtil.CommonFields;
import com.mongodb.lang.Nullable;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.codethen.hintsapp.MongoUtil.doc;

public class HintCardAdapter {

    private static class Fields {
        public static String userId = "userId";
        public static String score = "score";
        public static String hints = "hints";
        public static String notes = "notes";
        public static String tags = "tags";
    }

    @Nullable
    public static HintCard from(@Nullable Document doc) {

        return doc == null ? null : HintCard.builder()
                .id( doc.getObjectId(CommonFields._id).toString() )
                .userId( doc.getObjectId(Fields.userId).toString() )
                .score( doc.getInteger(Fields.score) )
                .hints( doc.getList(Fields.hints, String.class) )
                .notes( doc.getString(Fields.notes) )
                .tags( doc.getList(Fields.tags, String.class) )
                .build();
    }

    @Nullable
    public static Document from(@Nullable HintCard card) {

        return card == null ? null : doc()
                .append(Fields.userId, new ObjectId(card.getUserId()))
                .append(Fields.score, card.getScore())
                .append(Fields.hints, card.getHints())
                .append(Fields.notes, card.getNotes())
                .append(Fields.tags, card.getTags());
    }

    public static Document byUserId(String userId) {
        return doc(Fields.userId, new ObjectId(userId));
    }

    public static Document byIdAndUserId(String id, String userId) {
        return byUserId(userId).append(CommonFields._id, new ObjectId(id));
    }

    public static Document sortByFirstHint() {
        // Sort by hints[0] - https://stackoverflow.com/a/52320364/1121497
        return doc(Fields.hints + ".0", 1);
    }
}
