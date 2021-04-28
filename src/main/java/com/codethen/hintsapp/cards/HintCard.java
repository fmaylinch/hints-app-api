package com.codethen.hintsapp.cards;

import java.util.List;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

public class HintCard {

    String id;
    /** From 0 to 100 */
    int score;
    /** Contains at least one element */
    List<String> hints;
    String notes;
    /** Might be empty but not null */
    List<String> tags;
}
