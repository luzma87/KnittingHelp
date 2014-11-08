package com.lzm.KnittingHelp.preferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.util.AttributeSet;
import com.lzm.KnittingHelp.R;
import com.lzm.KnittingHelp.db.DbInserter;
import com.lzm.KnittingHelp.db.Pattern;
import com.lzm.KnittingHelp.utils.Utils;

/**
 * Created by DELL on 08/11/2014.
 */
public class ClickPreference extends Preference {

    Context context;

    public ClickPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public ClickPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public ClickPreference(Context context) {
        super(context);
        this.context = context;
    }


    @Override
    protected void onClick() {
        super.onClick();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(context.getString(R.string.pattern_reset_dlg_message))
                .setTitle(context.getString(R.string.pattern_reset_dlg_title));

        // Add the buttons
        builder.setPositiveButton(R.string.global_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                DbInserter.resetPatterns((Activity) context);
                Utils.toast(context.getString(R.string.pattern_reset_done), context);
            }
        });
        builder.setNegativeButton(R.string.global_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Set other dialog properties

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
