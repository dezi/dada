package com.aura.aosp.gorilla.sysapp;

import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aura.android.gorilla.R;
import com.aura.aosp.aura.crypter.RND;
import com.aura.aosp.aura.univid.Owner;
import com.aura.aosp.gorilla.gomess.GomessHandler;
import com.aura.aosp.gui.views.GUIFrameLayout;
import com.aura.aosp.gui.views.GUILinearLayout;
import com.aura.aosp.gui.views.GUITextView;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
{
    private static final String LOGTAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(LOGTAG, "onCreate: activity started.");

        super.onCreate(savedInstanceState);

        createLayout();

        sendMessage();
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

    private void createLayout()
    {
        GUIFrameLayout topFrame = new GUIFrameLayout(this);

        setContentView(topFrame);

        GUILinearLayout centerFrame = new GUILinearLayout(this);
        centerFrame.setOrientation(LinearLayout.VERTICAL);
        centerFrame.setGravity(Gravity.CENTER_HORIZONTAL);
        centerFrame.setBackgroundColor(0x88880000);

        topFrame.addView(centerFrame);

        GUITextView titleView = new GUITextView(this);
        titleView.setText(R.string.select_identity);
        titleView.setTextSizeDip(60);

        centerFrame.setBackgroundColor(0x88008800);

        centerFrame.addView(titleView);
    }
}
