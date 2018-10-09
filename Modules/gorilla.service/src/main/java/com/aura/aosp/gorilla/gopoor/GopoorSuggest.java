package com.aura.aosp.gorilla.gopoor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Log;
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
    public final static int RECENT_SECONDS = 600;
    public final static int GPS_ACCURACY = 1000;

    private static boolean fetched;

    private final static Map<String, Integer> envtag2index = new HashMap<>();
    private final static SparseArray<String> index2envtag = new SparseArray<>();
    private final static Map<String, SparseIntArray> event2column = new HashMap<>();
    private final static List<JSONObject> recentEvents = new ArrayList<>();
    private final static Map<String, ArrayList<Integer>> envcat2indexlist = new HashMap<>();

    private static JSONArray currentSuggestions;

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
        if (! fetched)
        {
            Err err = fetchEvents();
            if (err != null) return err;
        }

        addEvent(event);

        return precomputeSuggestions(GorillaState.getState());
    }

    @Nullable
    public static Err precomputeSuggestionsByState(@NonNull JSONObject state)
    {
        if (! fetched)
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
        double scoreOverall = 0.0;

        for (Map.Entry<String, SparseIntArray> entry : event2column.entrySet())
        {
            String domain = entry.getKey();
            SparseIntArray column = entry.getValue();

            JSONObject score = new JSONObject();
            domScores.put(domain, score);

            //
            // Score network environment category.
            //

            double scoreNet = getScoreForEnvtagIndex(column, envcat2indexlist.get("net"), curNet);
            double scoreDevice = getScoreForEnvtagIndex(column, envcat2indexlist.get("device"), curDevice);
            double scoreDayOfWeek = getScoreForEnvtagIndex(column, envcat2indexlist.get("wday"), curDayOfWeek);
            double scorePartOfMonth = getScoreForEnvtagIndex(column, envcat2indexlist.get("mpart"), curPartOfMonth);
            double scoreHourOfDay = getScoreForEnvtagIndex(column, envcat2indexlist.get("hour"), curHourOfDay);
            double scoreGps = getScoreForEnvtagIndex(column, envcat2indexlist.get("gps"), curGps);

            double scoreTotal = scoreNet + scoreDevice + scoreDayOfWeek + scorePartOfMonth + scoreHourOfDay + scoreGps;

            Json.put(score, "net", scoreNet);
            Json.put(score, "device", scoreDevice);
            Json.put(score, "wday", scoreDayOfWeek);
            Json.put(score, "mpart", scorePartOfMonth);
            Json.put(score, "hour", scoreHourOfDay);
            Json.put(score, "gps", scoreGps);
            Json.put(score, "total", scoreTotal);

            scoreOverall += scoreTotal;
        }

        for (String domain : event2column.keySet())
        {
            JSONObject scoreJson = domScores.get(domain);

            Double total = Json.getDouble(scoreJson, "total");
            if (total == null) continue;

            double score = (scoreOverall > 0) ? (total / scoreOverall) : 0.0;
            Json.put(scoreJson, "score", score);

            Log.d("%s => net=%f dev=%f dow=%f pom=%f hod=%f gps=%s total=%f score=%f", domain,
                    Json.getDouble(scoreJson, "net"),
                    Json.getDouble(scoreJson, "device"),
                    Json.getDouble(scoreJson, "wday"),
                    Json.getDouble(scoreJson, "mpart"),
                    Json.getDouble(scoreJson, "hour"),
                    Json.getDouble(scoreJson, "gps"),
                    Json.getDouble(scoreJson, "total"),
                    Json.getDouble(scoreJson, "score")
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

        if (deviation < (average / 10f))
        {
            //
            // The values in this category do not vary.
            // Therefore this category yields no score.
            //

            return 0.0;
        }

        return column.get(envTagIndex) / (double) total;
    }

    @Nullable
    private static Integer getEnvtag2Index(String envtag)
    {
        if (envtag == null) return null;
        return envtag2index.get(envtag);
    }

    @Nullable
    private static Err fetchEvents()
    {
        fetched = true;

        envtag2index.clear();
        index2envtag.clear();
        event2column.clear();

        long timeTo = System.currentTimeMillis();
        long timeFrom = timeTo - (30L * 86400L * 1000L);

        JSONArray events = GoatomStorage.queryAtoms("aura.event.action", timeFrom, timeTo);
        if (events == null) return Err.getLastErr();

        Log.d("events=%s", Json.toPretty(events));

        for (int inx = 0; inx < events.length(); inx++)
        {
            JSONObject event = Json.getObject(events, inx);
            if (event == null) continue;

            addEvent(event);
        }

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

        if (! event2column.containsKey(domain))
        {
            event2column.put(domain, new SparseIntArray());
        }

        SparseIntArray column = event2column.get(domain);

        String device = Json.getString(state, "device");
        if (device != null)
        {
            countEnvironmentTag(column,"device." + device);
        }

        String wifi = Json.getString(state, "wifi");
        if (wifi != null)
        {
            countEnvironmentTag(column,"wifi." + wifi);
        }

        Double lat = Json.getDouble(state, "lat");
        Double lon = Json.getDouble(state, "lon");

        if ((lat != null) && (lon != null))
        {
            lat = ((double) Math.round(lat * GPS_ACCURACY) / GPS_ACCURACY);
            lon = ((double) Math.round(lon * GPS_ACCURACY) / GPS_ACCURACY);

            String gpsloc = String.format(Locale.ROOT, "%f/%f", lat, lon);

            countEnvironmentTag(column,"gps." + gpsloc);
        }

        String netEnvTag = getNetEnvironment(state);
        countEnvironmentTag(column,netEnvTag);

        Long time = Json.getLong(state, "time");

        if ((time != null) && (time > 0))
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);

            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            countEnvironmentTag(column,"wday." + Integer.toString(dayOfWeek));

            int partOfMonth = (calendar.get(Calendar.DAY_OF_MONTH) / 10) + 1;
            countEnvironmentTag(column,"pmon." + Integer.toString(partOfMonth));

            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            countEnvironmentTag(column,"hour." + String.format(Locale.ROOT, "%02d", hourOfDay));

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
        if (! envtag2index.containsKey(envtag))
        {
            //
            // Add a new index for this environment tag.
            //

            int index = envtag2index.size();
            envtag2index.put(envtag, index);
            index2envtag.put(index, envtag);

            //
            // Derive environment category.
            //

            String[] envparts = envtag.split("\\.");
            String envcat = envparts[ 0 ];

            if (! envcat2indexlist.containsKey(envcat))
            {
                //
                // Add a new integer list for this environment category.
                //

                envcat2indexlist.put(envcat, new ArrayList<Integer>());
            }

            //
            // Add new environment tag index to category list.
            //

            ArrayList<Integer> envcatlist = envcat2indexlist.get(envcat);
            envcatlist.add(index);
        }

        //
        // Finally return environment tag index.
        //

        return envtag2index.get(envtag);
    }

    private static void dumpMatrix()
    {
        for (Map.Entry<String, SparseIntArray> entry : event2column.entrySet())
        {
            String domain = entry.getKey();
            SparseIntArray column = entry.getValue();

            StringBuilder colstr = new StringBuilder();

            for (int inx = 0; inx < envtag2index.size(); inx++)
            {
                int count = column.get(inx);
                if (count == 0) continue;

                String item = " " + index2envtag.get(inx) + ":" + count;

                colstr.append(item);
            }

            Log.d("%s => %s", domain, colstr.toString());
        }
    }
}
