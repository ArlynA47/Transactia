package com.egls.transactia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {

    // Method to display a custom toast
    public static void show(Context context, String message) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast, null);

        // Set the message text
        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);

        // Create and show the Toast
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}

