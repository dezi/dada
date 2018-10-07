package com.aura.aosp.gorilla.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class ComposeMessageActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGAE = "de.matthiaslienau.c3po.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGAE, message);
        startActivity(intent);
    }

    /** Called when the user taps the Add button */
    public void openExtraActivity(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, LauncherActivity.class);
        startActivity(intent);
    }
}
