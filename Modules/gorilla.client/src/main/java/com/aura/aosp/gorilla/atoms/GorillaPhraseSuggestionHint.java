package com.aura.aosp.gorilla.atoms;

import android.support.annotation.NonNull;

public class GorillaPhraseSuggestionHint
{
    private final String hint;
    private final int score;

    public GorillaPhraseSuggestionHint(@NonNull String hint, int score)
    {
        this.hint = hint;
        this.score = score;
    }

    @NonNull
    public String getHint()
    {
        return hint;
    }

    public int getScore()
    {
        return score;
    }
}
