package com.aura.aosp.gorilla.messenger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.aura.aosp.gorilla.client.GorillaClient;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
{
    private static final String LOGTAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d(LOGTAG, "onCreate: started.");

        GorillaClient gc = GorillaClient.getInstance(this);

        gc.setOnResultListener(new GorillaClient.OnResultListener()
        {
            @Override
            public void onResult(JSONObject result)
            {
                Log.d(LOGTAG, "onResult: sendPayload: result=" + result.toString());
            }
        });

        gc.sendPayload("huhublabla", "hallo");
    }
}
