package com.psy.places.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.psy.places.R;

/**
 * Created by sromanov on 16/2/2016.
 */
public class DialogUtil {

    public static void showMissingInternetAlert(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.internet_connection);
        builder.setMessage(R.string.internet_connection_problem);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.create().show();
    }

    public static void showAlertMessage(final Context context, int msgResId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msgResId);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.create().show();
    }
}