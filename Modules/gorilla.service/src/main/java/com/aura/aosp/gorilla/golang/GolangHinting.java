package com.aura.aosp.gorilla.golang;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Perf;

import org.json.JSONObject;

public class GolangHinting
{
    @NonNull
    public static JSONObject hintPhrase(String language, String phrase)
    {
        JSONObject result;

        result = GolangSuggest.suggestPhrase(language, phrase);

        if (result != null)
        {
            return result;
        }

        result = GolangCorrect.correctPhrase(language, phrase);

        if (result != null)
        {
            return result;
        }

        if (! language.equals("en"))
        {
            //
            // Fallback to english.
            //

            result = GolangSuggest.suggestPhrase("en", phrase);

            if (result != null)
            {
                return result;
            }

            result = GolangCorrect.correctPhrase("en", phrase);

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
    }
}
