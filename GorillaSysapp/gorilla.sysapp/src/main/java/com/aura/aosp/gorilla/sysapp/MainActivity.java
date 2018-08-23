package com.aura.aosp.gorilla.sysapp;

import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.aura.aosp.aura.crypter.RND;
import com.aura.aosp.aura.univid.Owner;
import com.aura.aosp.gorilla.gomess.GomessHandler;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
{
    private static final String LOGTAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(LOGTAG, "onCreate: activity started.");

        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                sendMessage();
            }
        }, 4000);
    }

    private void sendMessage()
    {
        long time = System.currentTimeMillis();
        String uuid = RND.randomUUIDBase64();

        String apkname = getPackageName();
        String userUUID = Owner.getUserUUIDBase64();
        String deviceUUID = Owner.getDeviceUUIDBase64();
        String payload = "Huhu";

        JSONObject result = GomessHandler.getInstance().sendPayload(uuid, time, apkname, userUUID, deviceUUID, payload);
        Log.d(LOGTAG, "sendPayloadTest: result=" + result.toString());
    }
}
