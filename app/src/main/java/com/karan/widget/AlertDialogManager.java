package com.karan.widget;

/**
 * Created by stpl on 12/30/2016.
 */

import android.app.AlertDialog;
import android.content.Context;

import com.karan.widget.activity.WebViewActivity;

public class AlertDialogManager {
    /**
     * Function to display simple Alert Dialog
     *
     * @param context - application context
     * @param title   - alert dialog title
     * @param message - alert message
     * @param status  - success/failure (used to set icon)
     *                - pass null if you don't want icon
     */
    public void showAlertDialog(final Context context, String title, String message,
                                Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        if (status != null)
            // Setting alert dialog icon
//            alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

            // Setting OK Button
            alertDialog.setButton("OK", (dialog, which) -> {
                ((WebViewActivity) context).finish();
            });

        // Showing Alert Message
        alertDialog.show();
    }
}