/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.atoms;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * The class {@code GorillaPhraseSuggestion} extends to basic
 * {@code GorillaAtom} by phrase suggestion values.
 *
 * @author Dennis Zierahn
 */
public class GorillaPhraseSuggestion extends GorillaAtom
{
    /**
     * Create empty suggestion atom.
     */
    public GorillaPhraseSuggestion()
    {
        super();
    }

    /**
     * Create suggestion atom from JSONObject.
     *
     * @param owner JSON owner atom object.
     */
    public GorillaPhraseSuggestion(JSONObject owner)
    {
        super(owner);
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
            Integer hintScore = getJSONInt(hints, hintKey);
            if (hintScore == null) continue;

            hintList.add(new GorillaPhraseSuggestionHint(hintKey, hintScore));
        }

        Collections.sort(hintList, new Comparator<GorillaPhraseSuggestionHint>()
        {
            @Override
            public int compare(GorillaPhraseSuggestionHint psh1, GorillaPhraseSuggestionHint psh2)
            {
                return psh2.getScore() - psh1.getScore();
            }
        });

        return hintList;
    }
}
