package com.codethen.hintsapp;

import com.mongodb.lang.Nullable;
import org.bson.Document;

public class HintsCardMongoAdapter {

    @Nullable
    public static HintCard from(@Nullable Document doc) {

        if (doc == null) return null;

        final HintCard card = new HintCard();
        card.setId( doc.getObjectId("_id").toString() );
        card.setHints( doc.getList("hints", String.class) );
        card.setNotes( doc.getString("notes") );
        return card;
    }

    @Nullable
    public static Document from(@Nullable HintCard card) {

        if (card == null) return null;

        return new Document()
                .append("hints", card.getHints())
                .append("notes", card.getNotes());
    }
}
