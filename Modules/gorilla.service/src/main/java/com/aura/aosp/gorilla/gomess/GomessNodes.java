package com.aura.aosp.gorilla.gomess;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.crypter.RND;
import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.sockets.Connect;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.aura.common.univid.Owner;
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

    public static void removeDeadNode(String country, GomessNode cNode)
    {
        List<GomessNode> cNodes = getGomessNodes(country);
        if (cNodes == null) return;

        cNodes.remove(cNode);
    }

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
            if (sleep < 64) sleep = sleep * 2;
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
        if (jGomessNodes == null)
        {
            Err.errp();
            return null;
        }

        List<GomessNode> gomessNodes = new ArrayList<>();

        for (int inx = 0; inx < jGomessNodes.length(); inx++)
        {
            JSONObject jGomessNode = Json.getObject(jGomessNodes, inx);

            GomessNode gomessNode = new GomessNode();
            Err err = gomessNode.unMarshall(jGomessNode);
            if (err != null) continue;

            gomessNodes.add(gomessNode);
        }

        return gomessNodes;
    }

    @Nullable
    private static List<GomessNode> readGomessNodes(String country)
    {
        List<PublicNode> pNodes = PublicNodes.getPublicNodes(country);

        if ((pNodes == null) || (pNodes.size() == 0)) return null;

        List<GomessNode> cNodes = null;

        while (pNodes.size() > 0)
        {
            int index = RND.randomIntn(pNodes.size());

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

        Identity owner = Owner.getOwnerIdentity();
        if (owner == null) return null;

        Connect conn = new Connect(pnode.Addr, pnode.Port);
        if (conn.connect() != null) return null;

        GoprotoSession session = new GoprotoSession(conn);
        Err err = session.aquireIdentity();
        if (err != null) return null;

        session.setIsBoot(true);
        GomessClient client = new GomessClient(session);
        err = client.nodesHandler();
        if (err != null) return null;

        return client.getAvailableGomessNodes();
    }
}
