package com.aura.aosp.gorilla.atoms;

import android.support.annotation.NonNull;

public class GorillaPhraseSuggestionHint
{
    String hint;
    int score;

    public GorillaPhraseSuggestionHint(@NonNull String hint, int score)
    {
        this.hint = hint;
        this.score = score;
    }

    public String getHint()
    {
        return hint;
    }

    public int getScore()
    {
        return score;
    }
}
