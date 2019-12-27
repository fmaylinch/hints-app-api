package com.codethen.hintsapp;

import com.codethen.hintsapp.MongoUtil.CommonFields;
import com.mongodb.lang.Nullable;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.codethen.hintsapp.MongoUtil.doc;

public class HintsCardMongoAdapter {

    private static class Fields {
        /** Used to sort. See {@link #sortByFirstHint}. */
        public static String hint1 = "hint1";
        public static String score = "score";
        public static String hints = "hints";
        public static String notes = "notes";
    }

    @Nullable
    public static HintCard from(@Nullable Document doc) {

        if (doc == null) return null;

        final HintCard card = new HintCard();
        card.setId( doc.getObjectId(CommonFields._id).toString() );
        card.setScore( doc.getInteger(Fields.score) );
        card.setHints( doc.getList(Fields.hints, String.class) );
        card.setNotes( doc.getString(Fields.notes) );
        return card;
    }

    @Nullable
    public static Document from(@Nullable HintCard card) {

        if (card == null) return null;

        return doc()
                .append(Fields.score, card.getScore())
                .append(Fields.hint1, card.getHints().get(0))
                .append(Fields.hints, card.getHints())
                .append(Fields.notes, card.getNotes());
    }

    public static Bson sortByFirstHint() {
        // TODO: Maybe we can get rid of hint1 and do this: https://stackoverflow.com/a/52320364/1121497
        //       In that case, make sure index can be created too.
        return doc(Fields.hint1, 1);
    }
}
