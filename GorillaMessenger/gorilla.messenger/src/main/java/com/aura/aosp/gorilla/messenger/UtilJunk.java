package com.aura.aosp.gorilla.messenger;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aura.aosp.gorilla.atoms.GorillaMessage;
import com.aura.aosp.gorilla.atoms.GorillaPayload;
import com.aura.aosp.gorilla.client.GorillaClient;

public class UtilJunk
{
    private static final String LOGTAG = UtilJunk.class.getSimpleName();

    public static void startMainActivity(Context context)
    {
        if (MainActivity.currentMainActivity == null)
        {
            Log.d(LOGTAG, "startMainActivity: ...");

            Intent startIntent = new Intent(context, MainActivity.class);

            try
            {
                context.startActivity(startIntent);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    @Nullable
    static GorillaMessage convertPayloadToMessageAndPersist(GorillaPayload payload)
    {
        Long time = payload.getTime();
        String uuid = payload.getUUIDBase64();
        String text = payload.getPayload();
        String remoteUserUUID = payload.getSenderUUIDBase64();
        String ownerDeviceUUID = EventManager.getOwnerDeviceBase64();

        if ((time == null) || (uuid == null) || (text == null) || (remoteUserUUID == null))
        {
            Log.e(LOGTAG, "invalid payload=" + payload.toString());
            return null;
        }

        if (ownerDeviceUUID == null)
        {
            Log.e(LOGTAG, "unknown owner device");
            return null;
        }

        GorillaMessage message = new GorillaMessage();

        message.setType("aura.chat.message");
        message.setTime(time);
        message.setUUID(uuid);
        message.setMessageText(text);
        message.setStatusTime("received", ownerDeviceUUID, System.currentTimeMillis());

        if (! GorillaClient.getInstance().putAtomSharedBy(remoteUserUUID, message.getAtom()))
        {
            return null;
        }

        return message;
    }
}
