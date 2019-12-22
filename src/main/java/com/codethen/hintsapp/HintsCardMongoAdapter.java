package com.codethen.hintsapp;

import org.bson.Document;

public class HintsCardMongoAdapter {

    public static HintCard from(Document doc) {
        final HintCard card = new HintCard();
        card.setId( doc.getObjectId("_id").toString() );
        card.setHints( doc.getList("hints", String.class) );
        card.setNotes( doc.getString("notes") );
        return card;
    }
}
