package com.example.vcsc_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class AllUsersActivity extends AppCompatActivity {

    TextView ngoView, volunteerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_users_activity);

        ngoView = findViewById(R.id.ngo_tv);
        volunteerView = findViewById(R.id.volunteer_tv);

        ngoView.setOnClickListener(view -> {
            startActivity(new Intent(AllUsersActivity.this, NgoUsersActivity.class));
            finish();
        });

        volunteerView.setOnClickListener(view -> {
            startActivity(new Intent(AllUsersActivity.this, VolunteerUsersActivity.class));
            finish();
        });

        ImageView ngoButton = findViewById(R.id.ngo_bt);
        ngoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ngoOnButtonClick();
            }
        });

        ImageView volunteerButton = findViewById(R.id.volunteer_bt);
        volunteerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volunteerOnButtonClick();
            }
        });
    }

    public void ngoOnButtonClick() {
        Intent i = new Intent(AllUsersActivity.this, NgoUsersActivity.class);
        startActivity(i);
    }

    public void volunteerOnButtonClick() {
        Intent i = new Intent(AllUsersActivity.this, VolunteerUsersActivity.class);
        startActivity(i);
    }
}