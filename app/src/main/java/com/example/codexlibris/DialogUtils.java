package com.example.codexlibris;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;

public class DialogUtils {

    public static void showMessage(Context context, String message) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Tancar", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
