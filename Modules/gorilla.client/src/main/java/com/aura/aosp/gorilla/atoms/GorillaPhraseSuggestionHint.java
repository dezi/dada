/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.atoms;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * The class {@code GorillaPhraseSuggestionHint} extends the basic
 * {@code GorillaAtom} by phrase suggestion hint values.
 *
 * @author Dennis Zierahn
 */
public class GorillaPhraseSuggestionHint extends GorillaAtom
{
    public GorillaPhraseSuggestionHint(@NonNull String hint, @NonNull Double score)
    {
        putJSON(getLoad(), "hint", hint);
        putJSON(getLoad(), "score", score);
    }

    /**
     * Get hint phrase or word.
     *
     * @return hint phrase or word or null.
     */
    @Nullable
    public String getHint()
    {
        return getJSONString(getLoad(), "hint");
    }

    /**
     * Get hint score as double number between 0 and 1.
     *
     * @return hint score as double or null.
     */
    @Nullable
    public Double getScore()
    {
        return getJSONDouble(getLoad(), "score");
    }
}
