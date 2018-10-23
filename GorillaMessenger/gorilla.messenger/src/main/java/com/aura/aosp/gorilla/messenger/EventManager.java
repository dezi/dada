package com.aura.aosp.gorilla.messenger;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aura.aosp.aura.common.crypter.UID;
import com.aura.aosp.aura.common.univid.Contacts;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.atoms.GorillaMessage;
import com.aura.aosp.gorilla.atoms.GorillaOwner;
import com.aura.aosp.gorilla.atoms.GorillaPayload;
import com.aura.aosp.gorilla.atoms.GorillaPayloadResult;
import com.aura.aosp.gorilla.atoms.GorillaPhraseSuggestion;
import com.aura.aosp.gorilla.atoms.GorillaPhraseSuggestionHint;
import com.aura.aosp.gorilla.client.GorillaClient;
import com.aura.aosp.gorilla.client.GorillaListener;

import java.util.List;

public class EventManager extends GorillaListener
{
    private static final String LOGTAG = EventManager.class.getSimpleName();

    private static Identity ownerIdent;

    @Nullable
    public static Identity getOwnerIdent()
    {
        return ownerIdent;
    }

    @Nullable
    public static String getOwnerNick()
    {
        if (ownerIdent == null) return null;
        return ownerIdent.getNick();
    }


    @Nullable
    public static String getOwnerDeviceBase64()
    {
        if (ownerIdent == null) return null;
        return ownerIdent.getDeviceUUIDBase64();
    }

    private final Context context;

    public EventManager(Context context)
    {
        this.context = context;
    }

    @Override
    public void onServiceChange(boolean connected)
    {
        Log.d(LOGTAG, "onServiceChange: connected=" + connected);
    }

    @Override
    public void onUplinkChange(boolean connected)
    {
        Log.d(LOGTAG, "onUplinkChange: connected=" + connected);

        if (connected)
        {
            //
            // Test dummy.
            //

            GorillaClient.getInstance().getAtom(UID.randomUUIDBase64());
        }
    }

    @Override
    public void onOwnerReceived(GorillaOwner owner)
    {
        Log.d(LOGTAG, "onOwnerReceived: owner=" + owner.toString());

        String ownerUUID = owner.getOwnerUUIDBase64();
        ownerIdent = Contacts.getContact(ownerUUID);
    }

    @Override
    public void onPayloadReceived(GorillaPayload payload)
    {
        Log.d(LOGTAG, "onPayloadReceived: message=" + payload.toString());

        if (UtilJunk.convertPayloadToMessageAndPersist(payload) != null)
        {
            UtilJunk.startMainActivity(context);
        }
    }

    @Override
    public void onPayloadResultReceived(GorillaPayloadResult result)
    {
        Log.d(LOGTAG, "onPayloadResultReceived: result=" + result.toString());
    }

    @Override
    public void onPhraseSuggestionsReceived(GorillaPhraseSuggestion result)
    {
        Log.d(LOGTAG, "onPhraseSuggestionsReceived: result=" + result.toString());

        List<GorillaPhraseSuggestionHint> hints = result.getHints();
        if (hints == null) return;

        for (GorillaPhraseSuggestionHint hint : hints)
        {
            Log.d(LOGTAG, "onPhraseSuggestionsReceived:"
                    + " hint=" + hint.getHint()
                    + " score=" + hint.getScore()
            );
        }

        ChatActivity activeChat = ChatActivity.activeChat;

        if (activeChat != null)
        {
            activeChat.dispatchPhraseSuggestion(result);
        }
    }
}
