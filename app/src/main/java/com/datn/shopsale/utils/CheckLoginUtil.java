package com.datn.shopsale.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import com.datn.shopsale.ui.login.LoginActivity;

public class CheckLoginUtil {
    public static void gotoLogin(Context context, String mess) {
        new AlertDialog.Builder(context)
                .setTitle("Notification")
                .setMessage(mess)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                    if (context instanceof Activity) {
                        PreferenceManager preferenceManager = new PreferenceManager(context);
                        preferenceManager.clear();
                        ((Activity) context).finish();
                    }
                })
                .show();
    }
}
