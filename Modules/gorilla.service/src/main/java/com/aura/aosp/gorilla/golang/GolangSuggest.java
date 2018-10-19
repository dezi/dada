/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.golang;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.os.Environment;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;

import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Perf;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.gorilla.service.GorillaBase;

import org.json.JSONObject;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

/**
 * Suggestion and corrections engine for words and phrases.
 *
 * @author Dennis Zierahn
 */
public class GolangSuggest
{
    private final static int READSIZE = 2048;
    private final static int CACHEPOINTS = 32;
    private final static int MAXPHRASELEN = 16;

    private final static Object mutex = new Object();
    private final static Map<String, GolangSuggest> languages = new HashMap<>();

    private RandomAccessFile raFile;
    private long raFileSize;
    private boolean inited;
    private String language;

    private final Phrase[] positionCache = new Phrase[CACHEPOINTS];

    /**
     * Public method to conduct phrase searches.
     *
     * @param language two digit language tag.
     * @param phrase the phrase to search.
     * @return JSOnObject with resulting hints or null.
     */
    @Nullable
    public static JSONObject searchPhrase(String language, String phrase)
    {
        GolangSuggest gs = languages.get(language);

        if (gs == null)
        {
            //
            // Engine not yet initialized.
            //

            gs = new GolangSuggest(language);
            languages.put(language, gs);
        }

        //
        // Look up phrase in desired language.
        //

        JSONObject result = gs.searchPhrase(phrase);

        if (result != null)
        {
            //
            // Have results in desired language.
            //

            return result;
        }

        //
        // Fallback into english.
        //

        if (language.equals("en"))
        {
            //
            // Was already english.
            //

            return null;
        }

        return searchPhrase("en", phrase);
    }

    /**
     * Dezi's test patterns.
     */
    public static void testDat()
    {
        Perf startTime;
        JSONObject result;

        startTime = new Perf();
        result = searchPhrase("de", "Bit");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), (result == null) ? "null" : result.toString());

        startTime = new Perf();
        result = searchPhrase("de", "Bitte");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), (result == null) ? "null" : result.toString());

        startTime = new Perf();
        result = searchPhrase("de", "Bitte ");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), (result == null) ? "null" : result.toString());

        startTime = new Perf();
        result = searchPhrase("de", "Bitte d");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), (result == null) ? "null" : result.toString());

        startTime = new Perf();
        result = searchPhrase("de", "Bärt");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), (result == null) ? "null" : result.toString());

        startTime = new Perf();
        result = searchPhrase("en", "aspirations");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), (result == null) ? "null" : result.toString());

        startTime = new Perf();
        result = searchPhrase("de", "aspirations");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), (result == null) ? "null" : result.toString());

        startTime = new Perf();
        result = searchPhrase("de", "In folge der Demonstration würden wir gerne die Bitte");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), (result == null) ? "null" : result.toString());

        startTime = new Perf();
        result = searchPhrase("de", "In folge der Demonstration würden wir gerne die Bitte ");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), (result == null) ? "null" : result.toString());

        startTime = new Perf();
        result = searchPhrase("de", "In folge der Demonstration würden wir gerne die Bitte d");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), (result == null) ? "null" : result.toString());

        startTime = new Perf();
        result = searchPhrase("de", "In folge der Demonstration würden wir gerne die Bitte dd");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), (result == null) ? "null" : result.toString());

        startTime = new Perf();
        result = searchPhrase("de", "Bltte");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), (result == null) ? "null" : result.toString());
    }

    public static void testSpell()
    {
        final TextServicesManager tsm = (TextServicesManager) GorillaBase.getAppContext().getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE);
        if (tsm == null)
        {
            Log.d("nix hier....");
            return;
        }

        SpellCheckerSession.SpellCheckerSessionListener listener = new SpellCheckerSession.SpellCheckerSessionListener()
        {
            @Override
            public void onGetSuggestions(SuggestionsInfo[] results)
            {

            }

            @Override
            public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results)
            {
                final StringBuilder sb = new StringBuilder();

                for (int i = 0; i < results.length; ++i)
                {
                    final SentenceSuggestionsInfo ssi = results[i];
                    for (int j = 0; j < ssi.getSuggestionsCount(); ++j)
                    {
                        dumpSuggestionsInfoInternal(sb, ssi.getSuggestionsInfoAt(j), ssi.getOffsetAt(j), ssi.getLengthAt(j));
                    }

                    Log.d("results=%s", sb.toString());
                }
            }
        };

        SpellCheckerSession mScs = tsm.newSpellCheckerSession(null, null, listener, true);

        if (mScs == null)
        {
            Log.d("nix da....");
            return;
        }

        Log.d("start....");
        mScs.getSentenceSuggestions(new TextInfo[] {new TextInfo("tgisis")}, 3);
    }

    private static void dumpSuggestionsInfoInternal(final StringBuilder sb, final SuggestionsInfo si, final int length, final int offset)
    {
        // Returned suggestions are contained in SuggestionsInfo
        final int len = si.getSuggestionsCount();
        sb.append('\n');
        for (int j = 0; j < len; ++j)
        {
            if (j != 0)
            {
                sb.append(", ");
            }
            sb.append(si.getSuggestionAt(j));
        }
        sb.append(" (" + len + ")");
        if (length != -1)
        {
            sb.append(" length = " + length + ", offset = " + offset);
        }
    }

    /**
     * Create a GolangSuggest object for a specified language.
     *
     * @param language two digit language tag.
     */
    private GolangSuggest(String language)
    {
        this.language = language;

        File suggestDir = getStorageDir(language, "suggest", false);
        File suggestFile = new File(suggestDir, "suggest.json");

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

            Perf startTime = new Perf();

            raFile = new RandomAccessFile(suggestFile, "r");
            raFileSize = raFile.length();

            byte[] buffer = new byte[READSIZE];

            long intervallSize = raFileSize / (CACHEPOINTS + 2);
            long intervallStart = intervallSize;

            for (int inx = 0; inx < CACHEPOINTS; inx++)
            {
                raFile.seek(intervallStart);
                int xfer = raFile.read(buffer);
                if (xfer != READSIZE) break;

                Phrase phrase = getFirstPhrase(intervallStart, buffer);
                if (phrase == null) break;

                positionCache[inx] = phrase;
                intervallStart += intervallSize;
            }

            Log.d("elapsed=%d", startTime.elapsedTimeMillis());

            inited = true;
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
    private JSONObject searchPhrase(String phrase)
    {
        if (!inited) return null;

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

        for (int inx = startindex; inx < parts.length; inx++)
        {
            StringBuilder searchPhrase = new StringBuilder();

            for (int fnz = inx; fnz < parts.length; fnz++)
            {
                if (searchPhrase.length() > 0) searchPhrase.append(" ");
                searchPhrase.append(parts[fnz]);
            }

            if (iscomplete) searchPhrase.append(" ");

            JSONObject result = searchPhrase(searchPhrase.toString(), false);

            if (result != null)
            {
                //
                // We have found somthing.
                //

                return result;
            }
        }

        return null;
    }

    /**
     * Search a phrase in file.
     *
     * @param phrase phrase to search.
     * @param truncate true if phrase may be truncated.
     * @return JSOnObject with resulting hints or null.
     */
    @Nullable
    private JSONObject searchPhrase(String phrase, boolean truncate)
    {
        String prefix = phrase;
        String suffix = null;

        if (truncate)
        {
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
        long bot = raFileSize - READSIZE;

        for (int inx = 0; inx < CACHEPOINTS; inx++)
        {
            Phrase cachePhrase = positionCache[inx];

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

            while (top < bot)
            {
                //
                // Peek into middle of interval.
                //

                long middle = top + ((bot - top) >> 1);

                raFile.seek(middle);

                //
                // Read unaligned buffer there.
                //

                int xfer = raFile.read(buffer);
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

                    String bufstr = newString(buffer, first.startBuff, last.startBuff - first.startBuff);

                    String[] targetPhrases = bufstr.split("\n");
                    String matchPhrase = prefix + ":";

                    for (String targetPhrase : targetPhrases)
                    {
                        if (targetPhrase.startsWith(matchPhrase))
                        {
                            return convertResult(phrase, suffix, targetPhrase);
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

                        return searchPhrase(prefix, true);
                    }

                    //
                    // Read sequential lines from this point.
                    //

                    raFile.seek(middle + first.startBuff);

                    return convertResult(phrase, null, scanPhrase(prefix));
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
     * @return JSONObject with result or null.
     */
    @Nullable
    private JSONObject convertResult(@NonNull String phrase, @Nullable String rest, @Nullable String resultPhrase)
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

        String hintsStr = resultPhrase.substring(firstBracket + 1, lastBracket);
        String[] hints = hintsStr.split(",");
        JSONObject hintsJson = new JSONObject();
        boolean valid = false;

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

                Json.put(resultJson, "total", hintScore);
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
            valid = true;
        }

        if (valid)
        {
            //
            // We have a least one valid hint.
            //

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
        private String phrase;

        /**
         * Start of phrase in current byte data buffer.
         */
        private int startBuff;

        /**
         * Absolute position of start of phrase in file.
         */
        private long startSeek;

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
     * Helper class for maintaining a score.
     */
    private class Score
    {
        /**
         * The phrase.
         */
        private String phrase;

        /**
         * The score.
         */
        private int score;

        /**
         * Create Score object.
         *
         * @param phrase the phrase.
         * @param score  the score.
         */
        private Score(String phrase, int score)
        {
            this.phrase = phrase;
            this.score = score;
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

        if (++end > READSIZE) return null;

        return new Phrase(newString(buffer, start, end - start), seekpos, start);
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

        int lastEnd = start;

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

        if (++end > READSIZE) return null;

        return new Phrase(newString(buffer, start, end - start), seekpos, start);
    }

    @Nullable
    private File getStorageDir(@NonNull String language, @NonNull String area, boolean create)
    {
        //noinspection ConstantConditions
        if ((language == null) || (area == null))
        {
            Err.errp();
            return null;
        }

        File appfilesdir = Environment.getExternalStorageDirectory();
        File golangdir = new File(appfilesdir, "golang");
        File langdir = new File(golangdir, language);
        File areadir = new File(langdir, area);

        if (create)
        {
            synchronized (mutex)
            {
                Err err = Simple.mkdirs(appfilesdir, golangdir, langdir, areadir);
                if (err != null) return null;
            }
        }

        return areadir;
    }

    /**
     * Sequentially read phrases from current file
     * position to match manually with search phrase.
     *
     * @param phrase the search phrase.
     * @return poorly formatted JSON string.
     */
    @Nullable
    private String scanPhrase(String phrase)
    {
        List<Score> targetScores = new ArrayList<>();
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

            targetPhrase = readLineUTFSafe();
            if (targetPhrase == null) break;

            if (targetPhrase.startsWith(phrase))
            {
                //
                // This phrase is a match for what we want.
                //

                int colon = targetPhrase.indexOf(":");
                if (colon < 0) continue;

                String targetWord = targetPhrase.substring(0, colon);

                int atchar = targetPhrase.indexOf("@:");
                if (atchar < 0) continue;

                int comma = targetPhrase.indexOf(",", atchar);
                if (comma < 0) continue;

                int targetScore = Integer.parseInt(targetPhrase.substring(atchar + 2, comma));

                targetScores.add(new Score(targetWord, targetScore));
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

        if (total > 0)
        {
            //
            // We have some results.
            //

            StringBuilder prefixPhrases = new StringBuilder();

            prefixPhrases.append("@:");
            prefixPhrases.append(Long.toString(total));

            for (Score targetScore : targetScores)
            {
                //
                // Normalize phrase count in percentage.
                //

                targetScore.score = Math.round((targetScore.score / (float) total) * 100);

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

        return null;
    }

    /**
     * New string from bytes method w/o throwing an exception.
     *
     * @param bytes  byte array.
     * @param offset offset into array.
     * @param length length in array.
     * @return UTF-8 string from bytes.
     */
    @NonNull
    private String newString(byte[] bytes, int offset, int length)
    {
        try
        {
            return new String(bytes, offset, length, "UTF-8");
        }
        catch (Exception ignore)
        {
            return new String(bytes, offset, length);
        }
    }

    /**
     * UTF-8 safe read line from random access file.
     * <p>
     * Max line size is READSIZE.
     *
     * @return UTF-8 string or null on error.
     */
    @Nullable
    private String readLineUTFSafe()
    {
        byte[] bytes = new byte[READSIZE];
        int xfer = 0;

        try
        {
            while (true)
            {
                byte byt = raFile.readByte();
                if (byt == '\n') break;

                bytes[xfer++] = byt;
                if (xfer >= bytes.length) break;
            }

            return newString(bytes, 0, xfer);
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }
}