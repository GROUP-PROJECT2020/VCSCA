package com.example.vcsc_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        Button welcomeButton = findViewById(R.id.welcome_bt);
        welcomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginOnButtonClick();
            }
        });
    }

    public void loginOnButtonClick() {
        Intent i = new Intent(WelcomeActivity.this, UserTypeActivity.class);
        startActivity(i);
    }
}