/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.atoms;

import org.json.JSONObject;

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
}
