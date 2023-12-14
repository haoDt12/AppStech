package com.datn.shopsale.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class LoadingDialog {
    private static ProgressDialog progressDialog;

    public static void showProgressDialog(Context context, String message) {
        dismissProgressDialog();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
