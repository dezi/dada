package com.aura.aosp.gorilla.sysapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.view.View;
import android.os.Bundle;
import android.util.Log;

import com.aura.aosp.aura.common.rights.Perms;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.univid.Contacts;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.aura.common.univid.Owner;

import com.aura.aosp.aura.gui.views.GUIButtonView;
import com.aura.aosp.aura.gui.views.GUIFrameLayout;
import com.aura.aosp.aura.gui.views.GUILinearLayout;
import com.aura.aosp.aura.gui.views.GUIListEntry;
import com.aura.aosp.aura.gui.views.GUIListView;
import com.aura.aosp.aura.gui.views.GUIScrollView;
import com.aura.aosp.aura.gui.views.GUITextView;
import com.aura.aosp.aura.gui.base.GUIDefs;

import com.aura.aosp.gorilla.gomess.GomessHandler;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static final String LOGTAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_LOCATION = 4711;
    private static final int REQUEST_EXTERNAL = 4712;

    private GUITextView identview;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(LOGTAG, "onCreate: activity started.");

        super.onCreate(savedInstanceState);

        createLayout();

        if (!Perms.checkLocationPermission(this))
        {
            Perms.requestPermissionForNeed(this, "loc", REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_LOCATION)
        {
            if (!Perms.checkExternalPermission(this))
            {
                Perms.requestPermissionForNeed(this, "ext", REQUEST_LOCATION);
            }
        }
    }

    private void createLayout()
    {
        List<Identity> contacts = Contacts.getAllContacts();
        String ownerUUID = Owner.getOwnerUUIDBase64();

        GUIFrameLayout topFrame = new GUIFrameLayout(this);

        setContentView(topFrame);

        GUILinearLayout centerFrame = new GUILinearLayout(this);
        centerFrame.setOrientation(LinearLayout.VERTICAL);
        centerFrame.setGravity(Gravity.CENTER_HORIZONTAL);

        topFrame.addView(centerFrame);

        identview = new GUITextView(this);
        identview.setText(R.string.select_identity);
        identview.setSizeDip(Simple.WC, Simple.WC);
        identview.setGravity(Gravity.CENTER_HORIZONTAL);
        identview.setPaddingDip(GUIDefs.PADDING_MEDIUM);
        identview.setTextSizeDip(24);

        centerFrame.addView(identview);

        if (ownerUUID != null)
        {
            for (Identity identity : contacts)
            {
                if (identity.getUserUUIDBase64().equals(ownerUUID))
                {
                    identview.setText(identity.getNick());
                }
            }
        }

        GUIScrollView identitiesScroll = new GUIScrollView(this);
        identitiesScroll.setSizeDip(Simple.WC, Simple.MP, 1.0f);

        centerFrame.addView(identitiesScroll);

        GUIListView identitiesView = new GUIListView(this);
        identitiesView.setSizeDip(Simple.WC, Simple.MP, 1.0f);
        identitiesView.setBackgroundColor(0x88888888);

        identitiesScroll.addView(identitiesView);

        for (Identity identity : contacts)
        {
            String nick = identity.getNick();
            String info = identity.getUserUUIDBase64();

            GUIListEntry entry = identitiesView.findGUIListEntryOrCreate(identity.getUserUUIDBase64());

            entry.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Identity identity = (Identity) view.getTag();
                    identview.setText(identity.getNick());

                    Owner.setOwnerUUIDBase64(identity.getUserUUIDBase64());
                    GomessHandler.getInstance().killSession();
                    GomessHandler.getInstance().changeOwner();
                }
            });

            entry.setTag(identity);

            entry.iconView.setImageResource(R.drawable.human_260);
            entry.headerViev.setText(nick);
            entry.infoView.setText(info);
        }

        GUILinearLayout buttonFrame = new GUILinearLayout(this);
        buttonFrame.setOrientation(LinearLayout.HORIZONTAL);
        buttonFrame.setSizeDip(Simple.MP, Simple.WC);
        buttonFrame.setGravity(Gravity.CENTER_HORIZONTAL);

        centerFrame.addView(buttonFrame);

        GUIButtonView doneButton = new GUIButtonView(this);

        doneButton.setRoundedCorners(GUIDefs.ROUNDED_NORMAL, GUIDefs.COLOR_LIGHT_GRAY);
        doneButton.setText(R.string.done_button);
        doneButton.setSizeDip(Simple.WC, Simple.WC);
        doneButton.setMarginLeftDip(GUIDefs.PADDING_NORMAL);
        doneButton.setMarginTopDip(GUIDefs.PADDING_NORMAL);
        doneButton.setMarginRightDip(GUIDefs.PADDING_NORMAL);
        doneButton.setMarginBottomDip(GUIDefs.PADDING_NORMAL);

        doneButton.setPaddingDip(
                GUIDefs.PADDING_XLARGE, GUIDefs.PADDING_SMALL,
                GUIDefs.PADDING_XLARGE, GUIDefs.PADDING_SMALL);

        doneButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                MainActivity.this.finish();
            }
        });

        buttonFrame.addView(doneButton);

        GUIButtonView clearButton = new GUIButtonView(this);

        clearButton.setRoundedCorners(GUIDefs.ROUNDED_NORMAL, GUIDefs.COLOR_LIGHT_GRAY);
        clearButton.setText(R.string.clear_button);
        clearButton.setSizeDip(Simple.WC, Simple.WC);
        clearButton.setMarginLeftDip(GUIDefs.PADDING_NORMAL);
        clearButton.setMarginTopDip(GUIDefs.PADDING_NORMAL);
        clearButton.setMarginRightDip(GUIDefs.PADDING_NORMAL);
        clearButton.setMarginBottomDip(GUIDefs.PADDING_NORMAL);

        clearButton.setPaddingDip(
                GUIDefs.PADDING_XLARGE, GUIDefs.PADDING_SMALL,
                GUIDefs.PADDING_XLARGE, GUIDefs.PADDING_SMALL);

        clearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                identview.setText(R.string.select_identity);

                Owner.clearOwner();

                GomessHandler.getInstance().killSession();
                GomessHandler.getInstance().changeOwner();
            }
        });

        buttonFrame.addView(clearButton);
    }
}
