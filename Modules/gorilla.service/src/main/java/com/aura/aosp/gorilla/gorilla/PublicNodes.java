package com.aura.aosp.gorilla.gorilla;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.utility.Regions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublicNodes
{
    private static final Map<String,List<PublicNode>> publicNodes = new HashMap<>();
    private static final String gorillPublicBucketName = "aura-gorilla-public";

    @Nullable
    public static List<PublicNode> getPublicNodes(String country)
    {
        List<PublicNode> nodes = publicNodes.get(country);

        if ((nodes == null) || (nodes.size() == 0))
        {
            nodes = readPublicNodes(country);
        }

        return nodes;
    }

    @Nullable
    private static List<PublicNode> readPublicNodes(String country)
    {
        String auraregion = Regions.CountryToRegion(country);
        if (auraregion == null) return null;

        String awsregion = Regions.MapToAWS(auraregion);
        if (awsregion == null) return null;

        String bucketFile = "gorilla-public-nodes-" + country + ".json";
        String bucketUrl = String.format("https://s3.%s.amazonaws.com/%s/%s",
                awsregion,
                gorillPublicBucketName,
                bucketFile
        );

        Log.d("bucketUrl=%s", bucketUrl);

        JSONArray jNodes = Simple.getHTTPJSONArray(bucketUrl);
        if (jNodes == null) return null;

        Log.d("jNodes=%s", jNodes.toString());

        List<PublicNode> publNodes = new ArrayList<>();

        for (int inx = 0; inx < jNodes.length(); inx++)
        {
            JSONObject jsonNode = Json.getObject(jNodes, inx);

            PublicNode publNode = new PublicNode();
            Err err = publNode.unMarshall(jsonNode);
            if (err != null) continue;

            publNodes.add(publNode);
        }

        publicNodes.put(country, publNodes);

        return publNodes;
    }
}
