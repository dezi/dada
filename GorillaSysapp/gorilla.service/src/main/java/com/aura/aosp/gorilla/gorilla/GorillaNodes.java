package com.aura.aosp.gorilla.gorilla;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.simple.Err;
import com.aura.aosp.aura.simple.Log;
import com.aura.aosp.aura.simple.Json;
import com.aura.aosp.aura.simple.Simple;
import com.aura.aosp.aura.sockets.Connect;
import com.aura.aosp.aura.utility.Regions;
import com.aura.aosp.gorilla.gomess.GomessClient;
import com.aura.aosp.gorilla.goproto.GoprotoSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GorillaNodes
{
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

        public ClientNode unMarshall(JSONObject jClientNode)
        {
            Addr = Json.getString(jClientNode, "addr");
            Port = Json.getInt(jClientNode, "port");
            Cons = Json.getInt(jClientNode, "cons");
            City = Json.getString(jClientNode, "city");
            Lat = Json.getDouble(jClientNode, "lat");
            Lon = Json.getDouble(jClientNode, "lon");

            return this;
        }

        public JSONObject marshall()
        {
            JSONObject jClientNode = new JSONObject();

            Json.put(jClientNode, "addr", Addr);
            Json.put(jClientNode, "port", Port);
            Json.put(jClientNode, "cons", Cons);
            Json.put(jClientNode, "city", City);
            Json.put(jClientNode, "lat", Lat);
            Json.put(jClientNode, "lon", Lon);

            return jClientNode;
        }

        @Nullable
        public static List<ClientNode> unmarshall(JSONArray jClientNodes)
        {
            if (jClientNodes == null) return null;

            List<ClientNode> clientNodesList = new ArrayList<>();

            for (int inx = 0; inx < jClientNodes.length(); inx++)
            {
                clientNodesList.add(new ClientNode().unMarshall(Json.getObject(jClientNodes, inx)));
            }

            return clientNodesList;
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
            if ((cNodes == null) || (cNodes.size() == 0)) continue;

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

            Log.d("pnode=%s", pNode.toString());

            cNodes = readClientNodesFromGorilla(pNode);

            if (cNodes != null)
            {
                clientNodes.put(country, cNodes);
                return cNodes;
            }

            //
            // Public node did not deliver any client nodes.
            // Remove from public nodes list.
            //

            pNodes.remove(inx--);
        }

        return null;
    }

    @Nullable
    private static List<ClientNode> readClientNodesFromGorilla(PublicNode pnode)
    {
        Log.d("...");

        Connect conn = new Connect(pnode.Addr, pnode.Port);
        if (conn.connect() != null) return null;

        GoprotoSession session = new GoprotoSession(conn);
        Err err = session.aquireIdentity();
        if (err != null) return null;

        GomessClient client = new GomessClient(session, true);

        err = client.clientHandler();
        if (err != null) return null;

        return client.getAvailableClientNodes();
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

        Log.d("bucketUrl=%s", bucketUrl);

        JSONArray jNodes = Simple.getHTTPJSONArray(bucketUrl);
        if (jNodes == null) return null;

        Log.d("jNodes=%s", jNodes.toString());

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
