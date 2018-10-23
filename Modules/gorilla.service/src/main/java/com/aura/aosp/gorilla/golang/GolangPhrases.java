/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.golang;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Perf;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Err;

import org.json.JSONObject;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

/**
 * Suggestion engine for words and phrases.
 *
 * @author Dennis Zierahn
 */
public class GolangPhrases
{
    private final static int READSIZE = 2048;
    private final static int CACHEPOINTS = 32;
    private final static int MAXPHRASELEN = 24;

    private final static Map<String, GolangPhrases> languages = new HashMap<>();

    private final String language;
    private PhrasesFile phrasesFile;

    private boolean inited;

    /**
     * Public method to conduct phrase searches.
     *
     * @param language two digit language tag.
     * @param phrase   the phrase to search.
     * @return JSOnObject with resulting hints or null.
     */
    @Nullable
    public static JSONObject phraseSuggest(String language, String phrase)
    {
        GolangPhrases gs = languages.get(language);

        if (gs == null)
        {
            //
            // Engine not yet initialized.
            //

            gs = new GolangPhrases(language);
            languages.put(language, gs);
        }

        if ((phrase == null) || phrase.isEmpty())
        {
            Err.err("null or empty phrase");
            return null;
        }

        //
        // Look up phrase in desired language.
        //

        return gs.phraseSuggest(phrase);
    }

    /**
     * Create a GolangSuggest object for a specified language.
     *
     * @param language two digit language tag.
     */
    private GolangPhrases(String language)
    {
        this.language = language;

        File suggestDir = GolangUtils.getStorageDir(language, "suggest", false);
        File phrasesFile = new File(suggestDir, "phrases.json");

        try
        {
            this.phrasesFile = new PhrasesFile(phrasesFile);
            Err err = this.phrasesFile.readFile();

            inited = (err == null);
        }
        catch (Exception ex)
        {
            Err.errp(ex);
        }
    }

    /**
     * Search a phrase. The phrase is split into tokens and
     * reassembled for multiple searches.
     *
     * @param phrase phrase to search.
     * @return JSOnObject with resulting hints or null.
     */
    @Nullable
    private JSONObject phraseSuggest(String phrase)
    {
        if (!inited)
        {
            Err.errp("uninitialized!");
            return null;
        }

        //
        // Remember if phrase is complete or not because
        // String.split ignores a trailing space.
        //

        boolean iscomplete = phrase.endsWith(" ");
        phrase = phrase.trim();

        //
        // Da fuck Java split method ignores a trailing space!
        //

        String[] parts = phrase.split(" ");

        //
        // Figure out how many tokens can be used from the end of text.
        //

        int startindex;
        int length = 0;

        for (startindex = parts.length - 1; startindex >= 0; startindex--)
        {
            if (length > 0) length += 1;
            length += parts[startindex].length();

            if (length > MAXPHRASELEN)
            {
                //
                // This tokens makes it overflow in size.
                //

                break;
            }
        }

        if (startindex < parts.length - 1)
        {
            startindex++;
        }

        //
        // Start trying phrases, starting with the longest phrase
        // possible, until we get a result.
        //

        List<JSONObject> results = new ArrayList<>();

        int hintCount = 0;

        for (int inx = startindex; inx < parts.length; inx++)
        {
            StringBuilder searchPhrase = new StringBuilder();

            for (int fnz = inx; fnz < parts.length; fnz++)
            {
                if (searchPhrase.length() > 0) searchPhrase.append(" ");
                searchPhrase.append(parts[fnz]);
            }

            if (iscomplete) searchPhrase.append(" ");

            JSONObject result = phraseSuggest(searchPhrase.toString(), false);

            if (result != null)
            {
                Integer count = Json.getInt(result, "count");
                if (count == null) continue;

                results.add(result);
                hintCount += count;

                if (hintCount > 10) break;
            }
        }

        if (results.size() == 0)
        {
            //
            // Nothing found.
            //

            return null;
        }

        if (results.size() == 1)
        {
            return results.get(0);
        }

        for (int inx = 0; inx < results.size(); inx++)
        {
            Log.d("############## inx=%d result=%s", inx, results.get(inx).toString());
        }

        return results.get(1);
    }

    /**
     * Search a phrase in file.
     *
     * @param phrase   phrase to search.
     * @param truncate true if phrase may be truncated.
     * @return JSOnObject with resulting hints or null.
     */
    @Nullable
    private JSONObject phraseSuggest(String phrase, boolean truncate)
    {
        Perf perf = new Perf();

        String prefix = phrase;
        String suffix = null;

        if (truncate)
        {
            //
            // Look up phrase with possible misspelled last token.
            //

            int lastSpace = prefix.lastIndexOf(" ");

            if (lastSpace > 0)
            {
                suffix = prefix.substring(lastSpace + 1);
                prefix = prefix.substring(0, lastSpace + 1);
            }
        }

        boolean iscomplete = prefix.endsWith(" ");
        prefix = prefix.trim();

        //
        // Initialize and narrow interval by
        // using cache positions.
        //

        long top = 0;
        long bot = phrasesFile.size - READSIZE;

        for (int inx = 0; inx < CACHEPOINTS; inx++)
        {
            Phrase cachePhrase = phrasesFile.positionCache[inx];

            if ((cachePhrase.phrase.compareTo(prefix) > 0) && (bot > cachePhrase.startSeek))
            {
                bot = cachePhrase.startSeek - READSIZE;
            }
            else
            {
                if ((cachePhrase.phrase.compareTo(prefix) < 0) && (top < cachePhrase.startSeek))
                {
                    top = cachePhrase.startSeek;
                }
            }
        }

        //
        // Go into binary search loop.
        //

        try
        {
            byte[] buffer = new byte[READSIZE];

            for (int loops = 0; top < bot && loops < 20; loops++)
            {
                //
                // Peek into middle of interval.
                //

                long middle = top + ((bot - top) >> 1);

                phrasesFile.file.seek(middle);

                //
                // Read unaligned buffer there.
                //

                int xfer = phrasesFile.file.read(buffer);
                if (xfer != READSIZE)
                {
                    Err.errp("fucked up.");
                    break;
                }

                //
                // Check first phrase contained in buffer.
                //

                Phrase first = getFirstPhrase(middle, buffer);

                if (first == null)
                {
                    Err.errp("fucked up.");
                    break;
                }

                if (first.phrase.compareTo(prefix) > 0)
                {
                    //
                    // First phrase is after target.
                    //

                    bot = middle + first.startBuff;
                    continue;
                }

                //
                // Check last phrase contained in buffer.
                //

                Phrase last = getLastPhrase(middle, buffer);

                if (last == null)
                {
                    Err.errp("fucked up.");
                    break;
                }

                if (last.phrase.compareTo(prefix) < 0)
                {
                    //
                    // Last phrase is before target.
                    //

                    top = middle + last.startBuff;
                    continue;
                }

                //
                // Target phrase must be inside this buffer or is missing.
                //

                if (iscomplete)
                {
                    //
                    // We search for a completed entry.
                    //

                    String bufstr = new String(buffer, first.startBuff, last.startBuff - first.startBuff);

                    String[] targetPhrases = bufstr.split("\n");
                    String matchPhrase = prefix + ":";

                    for (String targetPhrase : targetPhrases)
                    {
                        if (targetPhrase.startsWith(matchPhrase))
                        {
                            return convertResult(phrase, suffix, targetPhrase, perf);
                        }
                    }
                }

                //
                // No direct phrase match in buffer.
                //

                if (!truncate)
                {
                    //
                    // We could now chop of the last token or
                    // we could perform a scan from this position
                    // including the last token.
                    //

                    if (prefix.contains(" ") && !iscomplete)
                    {
                        //
                        // Retry with chopped off last token.
                        //

                        return phraseSuggest(prefix, true);
                    }

                    //
                    // Read sequential lines from this point.
                    //

                    phrasesFile.file.seek(middle + first.startBuff);

                    return convertResult(phrase, null, scanPhrase(prefix, iscomplete), perf);
                }

                break;
            }

            //
            // Nothing found at all.
            //

            return null;
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    /**
     * Convert the poorly JSON formatted search result into real JSONObject.
     *
     * @param phrase       the searched phrase.
     * @param rest         a chopped of token at end or null.
     * @param resultPhrase the poorly formatted result phrase or null if no result.
     * @param perf         Perf object or null.
     * @return JSONObject with result or null.
     */
    @Nullable
    private JSONObject convertResult(@NonNull String phrase, @Nullable String rest, @Nullable String resultPhrase, Perf perf)
    {
        if (resultPhrase == null)
        {
            //
            // This is not a error.
            //

            return null;
        }

        int firstBracket = resultPhrase.indexOf("{");

        if (firstBracket < 0)
        {
            Err.errp("wrong phrase format=%s", resultPhrase);
            return null;
        }

        int lastBracket = resultPhrase.lastIndexOf("}");

        if (lastBracket < firstBracket)
        {
            Err.errp("wrong phrase format=%s", resultPhrase);
            return null;
        }

        //
        // Start building the JSONObject.
        //

        JSONObject resultJson = new JSONObject();

        Json.put(resultJson, "phrase", phrase);
        Json.put(resultJson, "language", language);
        Json.put(resultJson, "mode", "phrase");

        String hintsStr = resultPhrase.substring(firstBracket + 1, lastBracket);
        String[] hints = hintsStr.split(",");
        JSONObject hintsJson = new JSONObject();
        int hintCount = 0;

        for (String hint : hints)
        {
            String[] parts = hint.split(":");
            if (parts.length != 2) continue;

            String hintPhrase = parts[0];
            int hintScore = Integer.parseInt(parts[1]);

            if (hintPhrase.equals("@"))
            {
                //
                // Hint "@" is the original phrase frequency.
                //

                Json.put(resultJson, "frequency", hintScore);
                continue;
            }

            if ((rest != null) && !hintPhrase.startsWith(rest))
            {
                //
                // We have a rest token which did not match.
                //

                continue;
            }

            //
            // Seems to be a valid hint.
            //

            Json.put(hintsJson, hintPhrase, hintScore);
            hintCount++;
        }

        if (hintCount > 0)
        {
            //
            // We have a least one valid hint.
            //

            Json.put(resultJson, "algms", perf.elapsedTimeMillis());
            Json.put(resultJson, "count", hintCount);
            Json.put(resultJson, "hints", hintsJson);

            return resultJson;
        }

        //
        // We had no valid hints.
        //

        return null;
    }

    /**
     * Helper class for maintaining a phrase.
     */
    private class Phrase
    {
        /**
         * The phrase.
         */
        private final String phrase;

        /**
         * Start of phrase in current byte data buffer.
         */
        private final int startBuff;

        /**
         * Absolute position of start of phrase in file.
         */
        private final long startSeek;

        /**
         * Create Phrase object.
         *
         * @param phrase  the phrase.
         * @param seekpos position in file at which buffer was read.
         * @param start   position of phrase within buffer.
         */
        private Phrase(String phrase, long seekpos, int start)
        {
            this.phrase = phrase;
            this.startBuff = start;
            this.startSeek = seekpos + start;
        }
    }

    /**
     * Get first phrase from unaligned memory buffer.
     *
     * @param seekpos seek position in file where buffer was read.
     * @param buffer  byte buffer with data.
     * @return the first phrase in buffer or null.
     */
    @Nullable
    private Phrase getFirstPhrase(long seekpos, byte[] buffer)
    {
        int start = 0;

        while (start < READSIZE)
        {
            if (buffer[start] == '\n') break;
            start++;
        }

        if (++start > READSIZE) return null;

        int end = start;

        while (end < READSIZE)
        {
            if (buffer[end] == ':') break;
            end++;
        }

        if (end > READSIZE) return null;

        return new Phrase(new String(buffer, start, end - start), seekpos, start);
    }

    /**
     * Get last phrase from unaligned memory buffer.
     *
     * @param seekpos seek position in file where buffer was read.
     * @param buffer  byte buffer with data.
     * @return the last phrase in buffer or null.
     */
    @Nullable
    private Phrase getLastPhrase(long seekpos, byte[] buffer)
    {
        int start = READSIZE - 1;

        while (start > 0)
        {
            if (buffer[start] == '\n') break;
            start--;
        }

        int realEnd = start;

        start--;

        if (start <= 0) return null;

        while (start > 0)
        {
            if (buffer[start] == '\n') break;
            start--;
        }

        if (start <= 0) return null;

        start++;

        int end = start;

        while (end < READSIZE)
        {
            if (buffer[end] == ':') break;
            end++;
        }

        if (end > READSIZE) return null;

        return new Phrase(new String(buffer, start, end - start), seekpos, realEnd);
    }

    /**
     * Sequentially read phrases from current file
     * position to match manually with search phrase.
     *
     * @param phrase the search phrase.
     * @return poorly formatted JSON string.
     */
    @Nullable
    private String scanPhrase(String phrase, boolean iscomplete)
    {
        List<GolangUtils.Score> targetScores = new ArrayList<>();
        String targetPhrase;
        long total = 0;

        //
        // Read at most 100 phrases from file.
        //

        for (int inx = 0; inx < 100; inx++)
        {
            //
            // DO NOT use raFile.readline() as
            // it fucks off UTF-8 characters!
            //

            targetPhrase = GolangUtils.readLineUTFSafe(phrasesFile.file);
            if (targetPhrase == null) break;

            if (targetPhrase.startsWith(phrase))
            {
                //
                // This phrase is a match for what we want.
                //

                int colon = targetPhrase.indexOf(":");
                if (colon < 0) continue;

                String targetWord = targetPhrase.substring(0, colon);

                if (iscomplete)
                {
                    targetWord = targetWord.substring(phrase.length()).trim();
                }

                int atchar = targetPhrase.indexOf("@:");
                if (atchar < 0) continue;

                int comma = targetPhrase.indexOf(",", atchar);
                if (comma < 0) continue;

                int targetScore = Integer.parseInt(targetPhrase.substring(atchar + 2, comma));

                targetScores.add(new GolangUtils.Score(targetWord, targetScore));
                total += targetScore;
            }
            else
            {
                //
                // This phrase is no match.
                //

                if (targetScores.size() > 0)
                {
                    //
                    // We are after the target phrase in list,
                    // so we exit now.
                    //

                    break;
                }
            }
        }

        if (total == 0)
        {
            return null;
        }

        Collections.sort(targetScores, new Comparator<GolangUtils.Score>()
        {
            @Override
            public int compare(GolangUtils.Score score1, GolangUtils.Score score2)
            {
                return score2.score - score1.score;
            }
        });

        //
        // We have some results.
        //

        StringBuilder prefixPhrases = new StringBuilder();

        prefixPhrases.append("@:");
        prefixPhrases.append(Long.toString(total));

        for (GolangUtils.Score targetScore : targetScores)
        {
            //
            // Normalize phrase count in percentage.
            //

            targetScore.score = Math.round((targetScore.score / (float) total) * 100);

            if (targetScore.score == 0)
            {
                continue;
            }

            if (prefixPhrases.length() > 0)
            {
                prefixPhrases.append(",");
            }

            prefixPhrases.append(targetScore.phrase);
            prefixPhrases.append(":");
            prefixPhrases.append(targetScore.score);
        }

        //
        // Poorly format the result into almost JSON string.
        //

        return phrase + ":{" + prefixPhrases.toString() + "}";
    }

    private class PhrasesFile
    {
        final private File path;
        final private RandomAccessFile file;
        final private long size;

        final private Phrase[] positionCache = new Phrase[CACHEPOINTS];

        private PhrasesFile(File path) throws IOException
        {
            this.path = path;
            this.file = new RandomAccessFile(path, "r");
            this.size = file.length();
        }

        @Nullable
        private Err readFile()
        {
            try
            {
                //
                // Build a number of cached phrases and
                // their positions.
                //
                // Using a 32 item cache reduces each search
                // by 5 disk accesses.
                //
                // A 64 item cache reduces by 6 accesses.
                //

                byte[] buffer = new byte[READSIZE];

                long intervallSize = size / (CACHEPOINTS + 2);
                long intervallStart = intervallSize;

                Perf startTime = new Perf();

                for (int inx = 0; inx < CACHEPOINTS; inx++)
                {
                    file.seek(intervallStart);
                    int xfer = file.read(buffer);
                    if (xfer != READSIZE) break;

                    Phrase phrase = getFirstPhrase(intervallStart, buffer);
                    if (phrase == null) break;

                    positionCache[inx] = phrase;
                    intervallStart += intervallSize;
                }

                Log.d("path=%s elapsed=%d", path.toString(), startTime.elapsedTimeMillis());

                return null;
            }
            catch (Exception ex)
            {
                return Err.errp(ex);
            }
        }
    }
}