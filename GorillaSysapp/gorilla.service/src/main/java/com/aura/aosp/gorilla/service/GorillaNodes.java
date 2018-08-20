package com.aura.aosp.gorilla.service;

import android.support.annotation.Nullable;
import android.util.Log;

import com.aura.aosp.aura.simple.Json;
import com.aura.aosp.aura.simple.Simple;
import com.aura.aosp.aura.univid.Identity;
import com.aura.aosp.gorilla.utility.Regions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GorillaNodes
{
    private static final String LOGTAG = GorillaNodes.class.getSimpleName();

    //region Public implementation.

    public static class ClientNode
    {
        public String Addr;
        public int Port;
        public int Cons;
        public String City;
        public Double Lat;
        public Double Lon;

        @Override
        public  String toString()
        {
            return "Addr=" + Addr + " Port=" + Port + " Cons=" + Cons
                    + " City=" + City + " Lat=" + Lat + " Lon=" + Lon;
        }
    }

    @Nullable
    public static ClientNode getBestNode(String country, Double lat, Double lon)
    {
        boolean sleep = false;

        ClientNode cNode = null;

        while (true)
        {
            if (sleep) Simple.sleep(1000);
            sleep = true;

            List<ClientNode> cNodes = getClientNodes(country);
            if ((cNodes == null) || (cNodes.size() == 0)) break; //##############;

            //
            // Todo: select best node...
            //

            cNode = cNodes.get(0);

            break;
        }

        return cNode;
    }

    //endregion Public implementation.

    //region Private implementation.

    private static class PublicNode
    {
        private String Addr;
        private int Port;

        @Override
        public  String toString()
        {
            return "Addr=" + Addr + " Port=" + Port;
        }
    }

    private static final Map<String,List<PublicNode>> publicNodes = new HashMap<>();
    private static final Map<String,List<ClientNode>> clientNodes = new HashMap<>();

    @Nullable
    private static List<ClientNode> getClientNodes(String country)
    {
        List<ClientNode> nodes = clientNodes.get(country);

        if ((nodes == null) || (nodes.size() == 0))
        {
            nodes = readClientNodes(country);
        }

        return nodes;
    }

    @Nullable
    private static List<ClientNode> readClientNodes(String country)
    {
        List<PublicNode> pNodes = getPublicNodes(country);
        if ((pNodes == null) || (pNodes.size() == 0)) return null;

        List<ClientNode> cNodes = null;

        for (int inx = 0; inx < pNodes.size(); inx++)
        {
            PublicNode pNode = pNodes.get(inx);
            if (pNode == null) continue;

            Log.d(LOGTAG, "readClientNodes: " + pNode);

            cNodes = readClientNodesFromGorilla(pNode);
            if (cNodes != null) break;
        }

        return cNodes;
    }

    @Nullable
    private static List<ClientNode> readClientNodesFromGorilla(PublicNode pnode)
    {
        GorillaConnect conn = new GorillaConnect(pnode.Addr, pnode.Port);
        if (! conn.connect()) return null;

        GorillaSession session = new GorillaSession(conn);

        session.UserUUID = Identity.getUserUUID();
        session.DeviceUUID = Identity.getDeviceUUID();
        session.ClientPrivKey = Identity.getRSAPrivateKey();

        GorillaClient client = new GorillaClient(session, true);

        client.clientHandler();

        return null;
    }

    @Nullable
    private static List<PublicNode> getPublicNodes(String country)
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
        String bucketFile = "gorilla-public-nodes-" + country + ".json";
        String bucketUrl = "https://s3." + awsregion + ".amazonaws.com/aura-public/" + bucketFile;

        Log.d(LOGTAG, "readPublicNodes: url=" + bucketUrl);

        JSONArray jNodes = Simple.getHTTPJSONArray(bucketUrl);
        if (jNodes == null) return null;

        Log.d(LOGTAG, "readPublicNodes: jNodes=" + jNodes.toString());

        List<PublicNode> publNodes = new ArrayList<>();

        for (int inx = 0; inx < jNodes.length(); inx++)
        {
            JSONObject jsonNode = Json.getObject(jNodes, inx);

            String addr = Json.getString(jsonNode, "addr");
            int port = Json.getInt(jsonNode, "port");

            if ((addr == null) || (port == 0)) continue;

            PublicNode publNode = new PublicNode();
            publNode.Addr = addr;
            publNode.Port = port;

            publNodes.add(publNode);
        }

        publicNodes.put(country, publNodes);

        return publNodes;
    }

    //endregion Private implementation.
}
