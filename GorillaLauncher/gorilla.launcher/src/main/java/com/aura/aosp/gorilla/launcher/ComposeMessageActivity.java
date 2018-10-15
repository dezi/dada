package com.aura.aosp.gorilla.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.aura.aosp.gorilla.launcher.store.StreamStore;

/**
 * Alternative way to open a "func" based on new activity view instead of directly
 * adding view to funcContainer from StreamActiviy.
 *
 * TODO: Integrate and/or remove, currently kept for reference.
 */
public class ComposeMessageActivity extends LauncherActivity {

    private final static String LOGTAG = ComposeMessageActivity.class.getSimpleName();

    public final static String EXTRA_MESSAGAE = "com.aura.aosp.launcher.message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Set main content view
        setMainFuncView(R.layout.func_content_composer, true);
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(StreamStore.EXTRA_CHAT_PARTNER_UUID, message);
        startActivity(intent);

        // Just to remember how to create an Intent with setAction:
//        Intent intent = new Intent();
//        intent.setPackage(view.getContext().getPackageName());
//        intent.setAction("de.matthiaslienau.c3po.action.MESSAGE_COMPOSE");
//        intent.putExtra("foo", "bar");
//        view.getContext().sendBroadcast(intent);
    }

    /** Called when the user taps the Add button */
    public void openExtraActivity(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, StreamActivity.class);
        startActivity(intent);
    }
}
