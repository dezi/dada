package com.aura.aosp.gorilla.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.aura.aosp.gorilla.launcher.store.StreamStore;

/**
 * TODO: Integrate and/or remove, currently just kept for reference.
 */
public class ComposeMessageActivity extends BaseActivity {

    private final static String LOGTAG = ComposeMessageActivity.class.getSimpleName();

    public final static String EXTRA_MESSAGAE = "com.aura.aosp.launcher.message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Set main content view
        setContentView(R.layout.func_content_composer);

        // Get references to main child view components
        launcherView = findViewById(R.id.launcher);
        statusBar = findViewById(R.id.statusBar);

        // Hide status and action bars
        hideStatusAndActionBar();
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(StreamStore.EXTRA_CHAT_PARTNER_UUID, message);
        startActivity(intent);
    }

    /** Called when the user taps the Add button */
    public void openExtraActivity(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, LauncherActivity.class);
        startActivity(intent);
    }
}
