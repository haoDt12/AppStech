package com.datn.shopsale.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import com.datn.shopsale.ui.login.LoginActivity;

public class GotoLogin {
    public static void showConfirmationDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Notification")
                .setMessage("Đã hết phiên đăng nhập vui lòng đăng nhập lại")
                .setPositiveButton("Ok", (dialog, which) -> goToLogin(context))
                .show();
    }

    private static void goToLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }
}
