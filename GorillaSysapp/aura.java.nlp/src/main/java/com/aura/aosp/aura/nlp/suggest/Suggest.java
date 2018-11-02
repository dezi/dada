package com.aura.aosp.aura.nlp.suggest;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Perf;

import org.json.JSONObject;

public class Suggest
{
    @NonNull
    public static JSONObject hintPhrase(String language, String phrase)
    {
        Perf perf = new Perf();

        JSONObject result;

        result = Phrases.phraseSuggest(language, phrase);

        if (result != null)
        {
            Json.put(result, "totms", perf.elapsedTimeMillis());

            return result;
        }

        result = Correct.phraseCorrect(language, phrase);

        if (result != null)
        {
            Json.put(result, "totms", perf.elapsedTimeMillis());

            return result;
        }

        if (! language.equals("en"))
        {
            //
            // Fallback to english.
            //

            result = Phrases.phraseSuggest("en", phrase);

            if (result != null)
            {
                Json.put(result, "totms", perf.elapsedTimeMillis());

                return result;
            }

            result = Correct.phraseCorrect("en", phrase);

            if (result != null)
            {
                Json.put(result, "totms", perf.elapsedTimeMillis());

                return result;
            }
        }

        //
        // Return empty hints.
        //

        result = new JSONObject();

        Json.put(result, "phrase", phrase);
        Json.put(result, "totms", perf.elapsedTimeMillis());

        return result;
    }

    /**
     * Dezi's test patterns.
     */
    public static void testDat()
    {
        Perf startTime;

        startTime = new Perf();
        Phrases.phraseSuggest("de", "");
        Phrases.phraseSuggest("en", "");
        Correct.phraseCorrect("de", "");
        Correct.phraseCorrect("en", "");
        Log.d("init=%d", startTime.elapsedTimeMillis());

        JSONObject result;

        result = hintPhrase("de", "Bit");
        Log.d("result=%s", result.toString());

        result = hintPhrase("de", "Bitte");
        Log.d("result=%s", result.toString());

        result = hintPhrase("de", "Bitte ");
        Log.d("result=%s", result.toString());

        result = hintPhrase("de", "Bitte d");
        Log.d("result=%s", result.toString());

        result = hintPhrase("de", "Bärt");
        Log.d("result=%s", result.toString());

        result = hintPhrase("en", "aspirations");
        Log.d("result=%s", result.toString());

        result = hintPhrase("de", "aspirations");
        Log.d("result=%s", result.toString());

        result = hintPhrase("de", "In folge der Demonstration würden wir gerne die Bitte");
        Log.d("result=%s", result.toString());

        result = hintPhrase("de", "In folge der Demonstration würden wir gerne die Bitte ");
        Log.d("result=%s", result.toString());

        result = hintPhrase("de", "In folge der Demonstration würden wir gerne die Bitte d");
        Log.d("result=%s", result.toString());

        result = hintPhrase("de", "In folge der Demonstration würden wir gerne die Bitte dd");
        Log.d("result=%s", result.toString());

        result = hintPhrase("de", "Bltte");
        Log.d("result=%s", result.toString());

        result = hintPhrase("de", "bltte");
        Log.d("result=%s", result.toString());

        result = hintPhrase("de", "Visibilifät");
        Log.d("result=%s", result.toString());

        result = hintPhrase("de", "Visibilitat");
        Log.d("result=%s", result.toString());

        result = hintPhrase("de", "Messwiener");
        Log.d("result=%s", result.toString());

        /*
        Integer dist;

        byte[] s1;
        byte[] s2;

        s1 = "Messdiener".getBytes();
        s2 = "Messdiener".getBytes();

        dist = GolangUtils.levenshtein(s1, s1.length, s2, s2.length, 2);
        Log.d("s1=%s s2=%s dist=%d", new String(s1), new String(s2), dist);

        s2 = "Messwiener".getBytes();

        dist = GolangUtils.levenshtein(s1, s1.length, s2, s2.length, 2);
        Log.d("s1=%s s2=%s dist=%d", new String(s1), new String(s2), dist);

        s2 = "Messweener".getBytes();

        dist = GolangUtils.levenshtein(s1, s1.length, s2, s2.length, 2);
        Log.d("s1=%s s2=%s dist=%d", new String(s1), new String(s2), dist);

        s2 = "Messwerte".getBytes();

        dist = GolangUtils.levenshtein(s1, s1.length, s2, s2.length, 2);
        Log.d("s1=%s s2=%s dist=%d", new String(s1), new String(s2), dist);
        */
    }
}
