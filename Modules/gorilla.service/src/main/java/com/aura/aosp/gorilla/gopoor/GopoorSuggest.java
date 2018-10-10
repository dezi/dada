package com.aura.aosp.gorilla.gopoor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.gorilla.goatom.GoatomStorage;
import com.aura.aosp.gorilla.service.GorillaState;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GopoorSuggest
{
    /**
     * Number of seconds for recent events.
     */
    private final static int RECENT_SECONDS = 600;

    /**
     * GPS accuracy for making a different GPS location.
     */
    private final static int GPS_ACCURACY = 1000;

    /**
     * Indicates that all stored events have been cached.
     */
    private static boolean fetched;

    /**
     * Map from an enviroment tag
     * <p>
     * net.mobile
     * net.wifi
     * wifi.xxxx
     * wifi.yyyy
     * device.xxxx
     * device.yyyy
     * ...
     * <p>
     * to row index in matrix.
     */
    private final static Map<String, Integer> envtag2row = new HashMap<>();

    /**
     * Reverse map from row index to environment tag.
     */
    private final static SparseArray<String> row2envtag = new SparseArray<>();

    /**
     * Environment categories
     * <p>
     * net.*
     * wifi.*
     * pmon.*
     * wday.*
     * hour.*
     * gps.*
     * device.*
     * ...
     * <p>
     * to corresponding row indices in matrix.
     */
    private final static Map<String, ArrayList<Integer>> envcat2rowlist = new HashMap<>();

    /**
     * Event identifier to column index in matrix.
     */
    private final static Map<String, SparseIntArray> event2column = new HashMap<>();

    /**
     * Dedicated list with recent events.
     */
    private final static List<JSONObject> recentEvents = new ArrayList<>();

    /**
     * Current suggestion results.
     */
    private static JSONArray currentSuggestions;

    /**
     * Suggest actions of root level. Suggested are domain only action and
     * action with domain and sub action.
     *
     * @return JSON array with suggested action and scores or null.
     */
    @Nullable
    public static JSONArray suggestActions()
    {
        return null;
    }

    @Nullable
    public static JSONArray suggestActions(@NonNull String actionDomain)
    {
        return null;
    }

    @Nullable
    public static JSONArray suggestContextActions(@NonNull String actionDomain, String subContext)
    {
        return null;
    }

    @Nullable
    public static Err precomputeSuggestionsByEvent(@NonNull JSONObject event)
    {
        if (!fetched)
        {
            Err err = fetchEvents();
            if (err != null) return err;
        }

        addEvent(event);

        return precomputeSuggestions(GorillaState.getState().getLoad());
    }

    @Nullable
    public static Err precomputeSuggestionsByState(@NonNull JSONObject state)
    {
        if (!fetched)
        {
            Err err = fetchEvents();
            if (err != null) return err;
        }

        return precomputeSuggestions(state);
    }

    @Nullable
    private static Err precomputeSuggestions(@NonNull JSONObject state)
    {
        Long curTime = Json.getLong(state, "time");
        if ((curTime == null) || (curTime <= 0))
        {
            //
            // Final exit here, will probably never happen.
            //

            return Err.err("time missing in state");
        }

        //
        // Prepare index numbers from environment state.
        //

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(curTime);

        Integer curNet = getEnvtag2Index(getNetEnvironment(state));
        Integer curGps = getEnvtag2Index(getGpsEnvironment(state));
        Integer curDevice = getEnvtag2Index(Json.getString(state, "device"));
        Integer curDayOfWeek = getEnvtag2Index("wday." + Integer.toString(calendar.get(Calendar.DAY_OF_WEEK)));
        Integer curPartOfMonth = getEnvtag2Index("mpart." + Integer.toString((calendar.get(Calendar.DAY_OF_MONTH) / 10) + 1));
        Integer curHourOfDay = getEnvtag2Index("hour." + String.format(Locale.ROOT, "%02d", calendar.get(Calendar.HOUR_OF_DAY)));

        //
        // Compute score of each known event.
        //

        Map<String, JSONObject> domScores = new HashMap<>();

        double totalOverall = 0.0;
        double countOverall = 0.0;

        for (Map.Entry<String, SparseIntArray> entry : event2column.entrySet())
        {
            String domain = entry.getKey();
            SparseIntArray column = entry.getValue();

            JSONObject score = new JSONObject();
            Json.put(score, "domain", domain);

            domScores.put(domain, score);

            //
            // Score total event count.
            //

            double scoreCount = getScoreForEnvcat(column, envcat2rowlist.get("device"));
            countOverall += scoreCount;

            Json.put(score, "count", scoreCount);

            //
            // Score individual event categories.
            //

            double scoreNet = getScoreForEnvtagIndex(column, envcat2rowlist.get("net"), curNet);
            double scoreDevice = getScoreForEnvtagIndex(column, envcat2rowlist.get("device"), curDevice);
            double scoreDayOfWeek = getScoreForEnvtagIndex(column, envcat2rowlist.get("wday"), curDayOfWeek);
            double scorePartOfMonth = getScoreForEnvtagIndex(column, envcat2rowlist.get("mpart"), curPartOfMonth);
            double scoreHourOfDay = getScoreForEnvtagIndex(column, envcat2rowlist.get("hour"), curHourOfDay);
            double scoreGps = getScoreForEnvtagIndex(column, envcat2rowlist.get("gps"), curGps);

            double scoreTotal = scoreNet + scoreDevice + scoreDayOfWeek + scorePartOfMonth + scoreHourOfDay + scoreGps;
            totalOverall += scoreTotal;

            Json.put(score, "net", scoreNet);
            Json.put(score, "device", scoreDevice);
            Json.put(score, "wday", scoreDayOfWeek);
            Json.put(score, "mpart", scorePartOfMonth);
            Json.put(score, "hour", scoreHourOfDay);
            Json.put(score, "gps", scoreGps);
            Json.put(score, "total", scoreTotal);
        }

        //
        // Normalize all scores to be between 0.0 .. 1.0
        // and put into JSON array.
        //

        JSONArray resultScores = new JSONArray();

        for (String domain : event2column.keySet())
        {
            JSONObject scoreJson = domScores.get(domain);

            Double count = Json.getDouble(scoreJson, "count");
            if (count == null) continue;

            double countNormalized = (countOverall > 0) ? (count / countOverall) : 0.0;
            Json.put(scoreJson, "count", countNormalized);

            Double total = Json.getDouble(scoreJson, "total");
            if (total == null) continue;

            double scoreNormalized = (totalOverall > 0) ? (total / totalOverall) : 0.0;
            Json.put(scoreJson, "score", scoreNormalized);

            double finalScore = (countNormalized + scoreNormalized) / 2.0;
            Json.put(scoreJson, "final", finalScore);

            Json.put(resultScores, scoreJson);
        }

        resultScores = Json.sortDouble(resultScores, "final", true);

        //
        // Log all scores.
        //

        for (int inx = 0; inx < resultScores.length(); inx++)
        {
            JSONObject scoreJson = Json.getObject(resultScores, inx);

            Log.d("net=%.2f dev=%.2f dow=%.2f pom=%.2f hod=%.2f gps=%.2f count=%.2f total=%.2f score=%.2f final=%.2f => %s",
                    Json.getDouble(scoreJson, "net"),
                    Json.getDouble(scoreJson, "device"),
                    Json.getDouble(scoreJson, "wday"),
                    Json.getDouble(scoreJson, "mpart"),
                    Json.getDouble(scoreJson, "hour"),
                    Json.getDouble(scoreJson, "gps"),
                    Json.getDouble(scoreJson, "count"),
                    Json.getDouble(scoreJson, "total"),
                    Json.getDouble(scoreJson, "score"),
                    Json.getDouble(scoreJson, "final"),
                    Json.getString(scoreJson, "domain")
            );
        }

        return null;
    }

    @NonNull
    private static Double getScoreForEnvtagIndex(SparseIntArray column, ArrayList<Integer> envCatIndexes, Integer envTagIndex)
    {
        if ((envTagIndex == null) || (envCatIndexes.size() == 0))
        {
            //
            // Environment tag not present or no category data.
            //

            return 0.0;
        }

        //
        // Compute average and deviation of this category.
        //

        int total = 0;

        for (int inx = 0; inx < envCatIndexes.size(); inx++)
        {
            total += column.get(envCatIndexes.get(inx));
        }

        if (total == 0)
        {
            //
            // No events counted in category.
            //

            return 0.0;
        }

        float average = total / (float) envCatIndexes.size();
        float differs = 0f;

        for (int inx = 0; inx < envCatIndexes.size(); inx++)
        {
            differs += Math.abs(average - column.get(envCatIndexes.get(inx)));
        }

        float deviation = differs / (float) envCatIndexes.size();

        Log.d("envTagIndex=%s deviation=%f average=%f cats=%d", envTagIndex, deviation, average, envCatIndexes.size());

        if (deviation < (average / 10f))
        {
            //
            // The values in this category do not vary.
            // Therefore this category yields no score.
            //

            //Log.d("envTagIndex=%s deviation=%f average=%f unspecific...", envTagIndex, deviation, average);

            return 0.0;
        }

        return column.get(envTagIndex) / (double) total;
    }

    @NonNull
    private static Double getScoreForEnvcat(SparseIntArray column, ArrayList<Integer> envCatIndexes)
    {
        if (envCatIndexes.size() == 0)
        {
            //
            // Environment tag not present or no category data.
            //

            return 0.0;
        }

        //
        // Compute total in this category.
        //

        int total = 0;

        for (int inx = 0; inx < envCatIndexes.size(); inx++)
        {
            total += column.get(envCatIndexes.get(inx));
        }

        return (double) total;
    }

    @Nullable
    private static Integer getEnvtag2Index(String envtag)
    {
        if (envtag == null) return null;
        return envtag2row.get(envtag);
    }

    @Nullable
    private static Err fetchEvents()
    {
        fetched = true;

        envtag2row.clear();
        row2envtag.clear();
        event2column.clear();

        long startTime = System.currentTimeMillis();

        long timeTo = System.currentTimeMillis();
        long timeFrom = timeTo - (30L * 86400L * 1000L);

        JSONArray events = GoatomStorage.queryAtoms("aura.event.action", timeFrom, timeTo);
        if (events == null) return Err.getLastErr();

        long fetchTime = System.currentTimeMillis();

        for (int inx = 0; inx < events.length(); inx++)
        {
            JSONObject event = Json.getObject(events, inx);
            if (event == null) continue;

            addEvent(event);
        }

        long totalTime = System.currentTimeMillis();

        Log.d("fetchTime=%d computeTime=%d totalTime=%d", fetchTime - startTime, totalTime - fetchTime, totalTime - startTime);

        dumpMatrix();

        return null;
    }

    @Nullable
    private static Err addEvent(JSONObject event)
    {
        JSONObject load = Json.getObject(event, "load");
        if (load == null) return Err.errp("no load in event");

        String domain = Json.getString(load, "domain");
        if (domain == null) return Err.errp("no domain in event");

        String action = Json.getString(load, "action");
        if (action != null)
        {
            domain += "." + action;
        }

        JSONObject state = Json.getObject(load, "state");
        if (state == null) return Err.errp("no state in load");

        if (!event2column.containsKey(domain))
        {
            event2column.put(domain, new SparseIntArray());
        }

        SparseIntArray column = event2column.get(domain);

        String device = Json.getString(state, "device");
        if (device != null)
        {
            countEnvironmentTag(column, "device." + device);
        }

        String wifi = Json.getString(state, "wifi");
        if (wifi != null)
        {
            countEnvironmentTag(column, "wifi." + wifi);
        }

        Double lat = Json.getDouble(state, "gps.lat");
        Double lon = Json.getDouble(state, "gps.lon");

        if ((lat != null) && (lon != null))
        {
            lat = ((double) Math.round(lat * GPS_ACCURACY) / GPS_ACCURACY);
            lon = ((double) Math.round(lon * GPS_ACCURACY) / GPS_ACCURACY);

            String gpsloc = String.format(Locale.ROOT, "%.4f/%.4f", lat, lon);

            countEnvironmentTag(column, "gps." + gpsloc);
        }

        String netEnvTag = getNetEnvironment(state);
        countEnvironmentTag(column, netEnvTag);

        Long time = Json.getLong(state, "time");

        if ((time != null) && (time > 0))
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);

            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            countEnvironmentTag(column, "wday." + Integer.toString(dayOfWeek));

            int partOfMonth = (calendar.get(Calendar.DAY_OF_MONTH) / 10) + 1;
            countEnvironmentTag(column, "pmon." + Integer.toString(partOfMonth));

            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            countEnvironmentTag(column, "hour." + String.format(Locale.ROOT, "%02d", hourOfDay));

            if (time >= (System.currentTimeMillis() - RECENT_SECONDS * 1000))
            {
                recentEvents.add(load);
            }
        }

        return null;
    }

    @NonNull
    private static String getGpsEnvironment(JSONObject state)
    {
        Double lat = Json.getDouble(state, "lat");
        Double lon = Json.getDouble(state, "lon");

        if ((lat != null) && (lon != null))
        {
            lat = ((double) Math.round(lat * GPS_ACCURACY) / GPS_ACCURACY);
            lon = ((double) Math.round(lon * GPS_ACCURACY) / GPS_ACCURACY);

            return "gps." + String.format(Locale.ROOT, "%f/%f", lat, lon);
        }

        return null;
    }

    @NonNull
    private static String getNetEnvironment(JSONObject state)
    {
        Boolean netWifi = Json.getBoolean(state, "net.wifi");

        if ((netWifi != null) && netWifi)
        {
            return "net.wifi";
        }

        Boolean netMobile = Json.getBoolean(state, "net.mobile");

        if ((netMobile != null) && netMobile)
        {
            return "net.mobile";
        }

        return "net.offline";
    }

    private static void countEnvironmentTag(SparseIntArray column, String envtag)
    {
        int row = getRowForEnvtag(envtag);
        column.put(row, column.get(row) + 1);
    }

    private static int getRowForEnvtag(String envtag)
    {
        if (!envtag2row.containsKey(envtag))
        {
            //
            // Add a new index for this environment tag.
            //

            int index = envtag2row.size();
            envtag2row.put(envtag, index);
            row2envtag.put(index, envtag);

            //
            // Derive environment category.
            //

            String[] envparts = envtag.split("\\.");
            String envcat = envparts[0];

            if (!envcat2rowlist.containsKey(envcat))
            {
                //
                // Add a new integer list for this environment category.
                //

                envcat2rowlist.put(envcat, new ArrayList<Integer>());
            }

            //
            // Add new environment tag index to category list.
            //

            ArrayList<Integer> envcatlist = envcat2rowlist.get(envcat);
            envcatlist.add(index);
        }

        //
        // Finally return environment tag index.
        //

        return envtag2row.get(envtag);
    }

    private static void dumpMatrix()
    {
        for (Map.Entry<String, SparseIntArray> entry : event2column.entrySet())
        {
            String domain = entry.getKey();
            SparseIntArray column = entry.getValue();

            StringBuilder colstr = new StringBuilder();

            for (int inx = 0; inx < envtag2row.size(); inx++)
            {
                int count = column.get(inx);
                if (count == 0) continue;

                String item = " " + row2envtag.get(inx) + ":" + count;

                colstr.append(item);
            }

            Log.d("%s => %s", domain, colstr.toString());
        }
    }
}
