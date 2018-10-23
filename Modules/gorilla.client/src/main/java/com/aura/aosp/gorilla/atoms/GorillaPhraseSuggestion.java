/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.atoms;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The class {@code GorillaPhraseSuggestion} extends the basic
 * {@code GorillaAtom} by phrase suggestion values.
 *
 * @author Dennis Zierahn
 */
public class GorillaPhraseSuggestion extends GorillaAtom
{
    /**
     * Create empty phrase suggestion atom.
     */
    public GorillaPhraseSuggestion()
    {
        super();
    }

    /**
     * Create phrase suggestion atom from JSONObject.
     *
     * @param phraseSuggestion JSON phrase suggestion atom object.
     */
    public GorillaPhraseSuggestion(JSONObject phraseSuggestion)
    {
        super(phraseSuggestion);
    }

    /**
     * Get phrase.
     *
     * @return phrase or null.
     */
    @Nullable
    public String getPhrase()
    {
        return getJSONString(getLoad(), "phrase");
    }

    /**
     * Get language.
     *
     * @return language or null.
     */
    @Nullable
    public String getLanguage()
    {
        return getJSONString(getLoad(), "language");
    }

    /**
     * Get mode.
     *
     * @return mode or null.
     */
    @Nullable
    public String getMode()
    {
        return getJSONString(getLoad(), "mode");
    }

    /**
     * Get phrase frequency.
     *
     * @return phrase frequency or null.
     */
    @Nullable
    public Integer getFrequency()
    {
        return getJSONInt(getLoad(), "frequency");
    }

    /**
     * Get algorhythm milliseconds.
     *
     * @return mode or null.
     */
    @Nullable
    public Integer getAlgoMillis()
    {
        return getJSONInt(getLoad(), "algms");
    }

    /**
     * Get total milliseconds.
     *
     * @return mode or null.
     */
    @Nullable
    public Integer getTotalMillis()
    {
        return getJSONInt(getLoad(), "totms");
    }

    /**
     * Get hint array sorted by score descending.
     *
     * @return hint array or null.
     */
    @Nullable
    public List<GorillaPhraseSuggestionHint> getHints()
    {
        JSONObject hints = getJSONObject(getLoad(), "hints");
        if (hints == null) return null;

        List<GorillaPhraseSuggestionHint> hintList = new ArrayList<>();

        Iterator<String> keys = hints.keys();

        while (keys.hasNext())
        {
            String hintKey = keys.next();

            //
            // The hints are transported internally with
            // percent scores for performance reasons.
            //
            // Convert to double scores on the fly.
            //

            Integer hintPercent = getJSONInt(hints, hintKey);
            if (hintPercent == null) continue;

            Double hintScore = hintPercent / 100.0;

            hintList.add(new GorillaPhraseSuggestionHint(hintKey, hintScore));
        }

        Collections.sort(hintList, new Comparator<GorillaPhraseSuggestionHint>()
        {
            @Override
            public int compare(GorillaPhraseSuggestionHint psh1, GorillaPhraseSuggestionHint psh2)
            {
                Double d1 = psh1.getScore();
                Double d2 = psh2.getScore();

                return ((d1 == null) || (d2 == null)) ? 0 : Double.compare(d2, d1);
            }
        });

        return hintList;
    }
}
