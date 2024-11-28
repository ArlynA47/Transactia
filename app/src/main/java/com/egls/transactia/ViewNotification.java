package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewNotification extends AppCompatActivity {

    TextView title, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        title = findViewById(R.id.title);
        message = findViewById(R.id.message);


        Intent intent = getIntent();

        title.setText(intent.getStringExtra("title"));
        message.setText(intent.getStringExtra("message"));

    }

    @Override
    public void onBackPressed() {
        // Access the existing instance of MainHome
        MainHome mainHomeActivity = (MainHome) MainHome.getInstance(); // Create a static instance accessor in MainHome
        if (mainHomeActivity != null) {
            mainHomeActivity.whatHomeScreen(5); // Set the value you want
        }
        super.onBackPressed(); // Navigate back
    }
}