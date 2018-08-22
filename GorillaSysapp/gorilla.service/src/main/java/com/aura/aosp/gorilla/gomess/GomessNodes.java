package com.aura.aosp.gorilla.gomess;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.crypter.RND;
import com.aura.aosp.aura.simple.Err;
import com.aura.aosp.aura.simple.Log;
import com.aura.aosp.aura.simple.Json;
import com.aura.aosp.aura.simple.Simple;
import com.aura.aosp.aura.sockets.Connect;
import com.aura.aosp.gorilla.goproto.GoprotoSession;
import com.aura.aosp.gorilla.gorilla.PublicNode;
import com.aura.aosp.gorilla.gorilla.PublicNodes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GomessNodes
{
    private static final Map<String,List<GomessNode>> GomessNodes = new HashMap<>();

    @Nullable
    public static GomessNode getBestNode(String country, Double lat, Double lon)
    {
        int sleep = 1;

        while (true)
        {
            List<GomessNode> cNodes = getGomessNodes(country);

            if ((cNodes != null) && (cNodes.size() > 0))
            {
                //
                // Todo: select best node...
                //

                return cNodes.get(0);
            }

            Simple.sleep(sleep * 1000);
            if (sleep < 512) sleep = sleep * 2;
        }
    }

    @Nullable
    private static List<GomessNode> getGomessNodes(String country)
    {
        List<GomessNode> nodes = GomessNodes.get(country);

        if ((nodes == null) || (nodes.size() == 0))
        {
            nodes = readGomessNodes(country);
        }

        return nodes;
    }

    @Nullable
    public static List<GomessNode> unMarshall(JSONArray jGomessNodes)
    {
        if (jGomessNodes == null) return null;

        List<GomessNode> GomessNodesList = new ArrayList<>();

        for (int inx = 0; inx < jGomessNodes.length(); inx++)
        {
            JSONObject jGomessNode = Json.getObject(jGomessNodes, inx);

            GomessNode gomessNode = new GomessNode();
            Err err = gomessNode.unMarshall(jGomessNode);
            if (err != null) continue;

            GomessNodesList.add(gomessNode);
        }

        return GomessNodesList;
    }

    @Nullable
    private static List<GomessNode> readGomessNodes(String country)
    {
        List<PublicNode> pNodes = PublicNodes.getPublicNodes(country);
        if ((pNodes == null) || (pNodes.size() == 0)) return null;

        List<GomessNode> cNodes = null;

        while (pNodes.size() > 0)
        {
            int index = RND.randomInt(pNodes.size());

            PublicNode pNode = pNodes.get(index);

            if (pNode == null) continue;

            Log.d("pnode=%s", pNode.toString());

            cNodes = readGomessNodesFromGorilla(pNode);

            if (cNodes != null)
            {
                GomessNodes.put(country, cNodes);
                return cNodes;
            }

            //
            // Public node did not deliver any client nodes.
            // Remove from public nodes list.
            //

            pNodes.remove(index);
        }

        return null;
    }

    @Nullable
    private static List<GomessNode> readGomessNodesFromGorilla(PublicNode pnode)
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

        return client.getAvailableGomessNodes();
    }
}
