package com.codethen.hintsapp.cards;

import com.codethen.hintsapp.MongoUtil.CommonFields;
import com.mongodb.lang.Nullable;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.codethen.hintsapp.MongoUtil.doc;

public class HintsCardMongoAdapter {

    private static class Fields {
        public static String score = "score";
        public static String hints = "hints";
        public static String notes = "notes";
        public static String tags = "tags";
    }

    @Nullable
    public static HintCard from(@Nullable Document doc) {

        if (doc == null) return null;

        final HintCard card = new HintCard();
        card.setId( doc.getObjectId(CommonFields._id).toString() );
        card.setScore( doc.getInteger(Fields.score) );
        card.setHints( doc.getList(Fields.hints, String.class) );
        card.setNotes( doc.getString(Fields.notes) );
        card.setTags( doc.getList(Fields.tags, String.class) );
        return card;
    }

    @Nullable
    public static Document from(@Nullable HintCard card) {

        if (card == null) return null;

        return doc()
                .append(Fields.score, card.getScore())
                .append(Fields.hints, card.getHints())
                .append(Fields.notes, card.getNotes())
                .append(Fields.tags, card.getTags());
    }

    public static Bson sortByFirstHint() {
        // Sort by hints[0] - https://stackoverflow.com/a/52320364/1121497
        return doc(Fields.hints + ".0", 1);
    }
}
