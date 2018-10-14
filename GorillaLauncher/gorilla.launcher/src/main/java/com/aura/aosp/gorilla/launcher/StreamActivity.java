package com.aura.aosp.gorilla.launcher;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.client.GorillaClient;
import com.aura.aosp.gorilla.launcher.store.StreamStore;
import com.aura.aosp.gorilla.launcher.ui.common.FuncBaseView;
import com.aura.aosp.gorilla.launcher.ui.common.SmartScrollableLayoutManager;
import com.aura.aosp.gorilla.launcher.ui.content.SimpleCalendarView;
import com.aura.aosp.gorilla.launcher.ui.content.StreamAdapter;
import com.aura.aosp.gorilla.launcher.ui.content.StreamView;

/**
 * Main activity, i.e. the "launcher" screen
 */
public class StreamActivity extends LauncherActivity {

    private final static String LOGTAG = StreamActivity.class.getSimpleName();

    //    public static List<ChatProfile> chatProfiles = new ArrayList<>();

    private StreamStore streamStore;

    private StreamView streamView;
    private RecyclerView streamRecyclerView;
    private StreamAdapter streamAdapter;
    private SmartScrollableLayoutManager streamLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.d(LOGTAG, "onCreate: ...");

        // Register generic "launcher opened" event with Gorilla
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GorillaClient.getInstance().registerActionEvent(getPackageName());
            }
        }, 2000);


        // Set main func view to "stream":
//        setMainFuncView(R.layout.func_stream);

        // Create "func" view by inflating xml, adding an item adapter
        // and attach it to launcher (main) view
        LayoutInflater inflater = LayoutInflater.from(this);
        streamView = (StreamView) inflater.inflate(R.layout.func_stream, mainContentContainer, false);
        streamView.setVisibility(View.INVISIBLE);

        // Add view to container
        mainContentContainer.addView(streamView);

        streamView.fadeIn(null);

        // Get references to main child view components
        streamRecyclerView = launcherView.findViewById(R.id.innerstream);

        // Create and display stream items
        createStream();
    }

    /**
     * Create main stream items
     */
    protected void createStream() {

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        streamView.setHasFixedSize(true);

        // use a linear layout manager
        streamLayoutManager = new SmartScrollableLayoutManager(this);
        streamRecyclerView.setLayoutManager(streamLayoutManager);

        // Register layout manger with "main content smart scrollable layout managers"
        mcSmartScrollableLayoutManagers.add(streamLayoutManager);

        // TODO: Replace with aggregated stream items from "Gorilla Content Stream Atoms"
        // Create initial content stream items and specify adapter
        streamStore = new StreamStore(getApplicationContext());

//        streamAdapter = new StreamAdapter(SampleData.getDummyStreamData(), this);
        streamAdapter = new StreamAdapter(streamStore.getItemsForAtomContext("aura.uxtream.launcher", getOwnerIdent()), this, this);
        streamRecyclerView.setAdapter(streamAdapter);
    }

    /**
     * ACTION: "Stream"
     */
    public void onOpenStream() {

        setMainFuncView(R.layout.func_stream);

        // Create and display stream items
        createStream();
    }

    /**
     * ACTION: "Open Content Composer"
     */
    public void onOpenContentComposer(@Nullable Identity identity) {

        setMainFuncView(R.layout.func_content_composer);
    }

    /**
     * ACTION: "Open Calendar"
     */
    public void onOpenSimpleCalendar() {

        // Create calendar view by inflating xml, adding an item adapter
        // and attach it to launcher (main) view
        LayoutInflater inflater = LayoutInflater.from(this);
        SimpleCalendarView simpleCalendarView = (SimpleCalendarView) inflater.inflate(R.layout.func_simple_calendar, launcherView, false);
        simpleCalendarView.setVisibility(View.INVISIBLE);
        simpleCalendarView.fadeIn(null);

        // Add view to launcher
        launcherView.addView(simpleCalendarView);
        funcViewManager.addFuncView(FuncBaseView.FuncType.OVERLAY, simpleCalendarView);
    }

    /**
     * ACTION: "Alarm Clock"
     */
    public void onOpenAlarmClock() {

        onOpenSimpleCalendar();
    }

    /**
     * ACTION: "Pick Date"
     */
    public void onPickDate() {

        onOpenSimpleCalendar();
    }

    /**
     * ACTION: "Mark Selected Text Bold"
     */
    public void onMarkSelectedTextBold() {

        onOpenSimpleCalendar();
    }

    /**
     * ACTION: "Mark Selected Text Italic"
     */
    public void onMarkSelectedTextItalic() {

        onOpenSimpleCalendar();
    }

    /**
     * ACTION: "Mark Selected Text Underlined"
     */
    public void onMarkSelectedTextUnderlined() {

        onOpenSimpleCalendar();
    }

    /**
     * ACTION: "Align Justify Selected Text"
     */
    public void onMarkSelectedTextAlignJustify() {

        onOpenSimpleCalendar();
    }
}
