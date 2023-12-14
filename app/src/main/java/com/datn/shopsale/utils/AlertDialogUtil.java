package com.datn.shopsale.utils;

import android.app.AlertDialog;
import android.content.Context;

public class AlertDialogUtil {
    public static void showSimpleAlertDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Notification")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    public static void showAlertDialogWithOk(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Notification")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
