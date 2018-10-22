package com.aura.aosp.gorilla.golang;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Perf;

import org.json.JSONObject;

public class GolangSuggest
{
    public static void initializeLanguage(String language)
    {
        GolangPhrases.phraseSuggest(language, "");
        GolangPhrases.phraseSuggest("en", "");
        GolangCorrect.phrasecorrect(language, "");
        GolangCorrect.phrasecorrect("en", "");
    }

    @NonNull
    public static JSONObject hintPhrase(String language, String phrase)
    {
        JSONObject result;

        result = GolangPhrases.phraseSuggest(language, phrase);

        if (result != null)
        {
            return result;
        }

        result = GolangCorrect.phrasecorrect(language, phrase);

        if (result != null)
        {
            return result;
        }

        if (! language.equals("en"))
        {
            //
            // Fallback to english.
            //

            result = GolangPhrases.phraseSuggest("en", phrase);

            if (result != null)
            {
                return result;
            }

            result = GolangCorrect.phrasecorrect("en", phrase);

            if (result != null)
            {
                return result;
            }
        }

        //
        // Return empty hints.
        //

        result = new JSONObject();
        Json.put(result, "phrase", phrase);

        return result;
    }

    /**
     * Dezi's test patterns.
     */
    public static void testDat()
    {
        Perf startTime;

        startTime = new Perf();
        initializeLanguage("de");
        Log.d("init=%d", startTime.elapsedTimeMillis());

        JSONObject result;

        startTime = new Perf();
        result = hintPhrase("de", "Bit");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), result.toString());

        startTime = new Perf();
        result = hintPhrase("de", "Bitte");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), result.toString());

        startTime = new Perf();
        result = hintPhrase("de", "Bitte ");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), result.toString());

        startTime = new Perf();
        result = hintPhrase("de", "Bitte d");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), result.toString());

        startTime = new Perf();
        result = hintPhrase("de", "Bärt");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), result.toString());

        startTime = new Perf();
        result = hintPhrase("en", "aspirations");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), result.toString());

        startTime = new Perf();
        result = hintPhrase("de", "aspirations");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), result.toString());

        startTime = new Perf();
        result = hintPhrase("de", "In folge der Demonstration würden wir gerne die Bitte");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), result.toString());

        startTime = new Perf();
        result = hintPhrase("de", "In folge der Demonstration würden wir gerne die Bitte ");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), result.toString());

        startTime = new Perf();
        result = hintPhrase("de", "In folge der Demonstration würden wir gerne die Bitte d");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), result.toString());

        startTime = new Perf();
        result = hintPhrase("de", "In folge der Demonstration würden wir gerne die Bitte dd");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), result.toString());

        startTime = new Perf();
        result = hintPhrase("de", "Bltte");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), result.toString());

        startTime = new Perf();
        result = hintPhrase("de", "bltte");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), result.toString());

        startTime = new Perf();
        result = hintPhrase("de", "Visibilifät");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), result.toString());

        startTime = new Perf();
        result = hintPhrase("de", "Visibilitat");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), result.toString());

        startTime = new Perf();
        result = hintPhrase("de", "Messwiener");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), result.toString());

        Integer dist;

        byte[] s1 = "Messdiener".getBytes();
        byte[] s2 = "Messdiene".getBytes();

        dist = GolangUtils.levenshtein(s1, s1.length, s2, s2.length);
        Log.d("s1=%s s2=%s dist=%d", new String(s1), new String(s2), dist);
    }
}
