package com.aura.aosp.facerecognizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        FrameLayout topFrame = new FrameLayout(this);

        topFrame.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        topFrame.setBackgroundColor(0x88888800);

        setContentView(topFrame);

        FaceView faceView = new FaceView(this);
        topFrame.addView(faceView);
    }
}
