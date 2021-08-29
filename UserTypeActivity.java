package com.example.vcsc_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class UserTypeActivity extends AppCompatActivity {

    TextView adminView, ngoView, volunteerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_type_activity);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPage);
        NextImageAdapter adapterView = new NextImageAdapter(this);
        mViewPager.setAdapter(adapterView);

        adminView = findViewById(R.id.admin_tv);
        ngoView = findViewById(R.id.ngo_tv);
        volunteerView = findViewById(R.id.volunteer_tv);

        adminView.setOnClickListener(view -> {
            startActivity(new Intent(UserTypeActivity.this, AdminLoginActivity.class));
            finish();
        });

        ngoView.setOnClickListener(view -> {
            startActivity(new Intent(UserTypeActivity.this, NgoLoginActivity.class));
            finish();
        });

        volunteerView.setOnClickListener(view -> {
            startActivity(new Intent(UserTypeActivity.this, VolunteerLoginActivity.class));
            finish();
        });

        ImageView adminButton = findViewById(R.id.admin_bt);
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminOnButtonClick();
            }
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

    public void adminOnButtonClick() {
        Intent i = new Intent(UserTypeActivity.this, AdminLoginActivity.class);
        startActivity(i);
    }

    public void ngoOnButtonClick() {
        Intent i = new Intent(UserTypeActivity.this, NgoLoginActivity.class);
        startActivity(i);
    }

    public void volunteerOnButtonClick() {
        Intent i = new Intent(UserTypeActivity.this, VolunteerLoginActivity.class);
        startActivity(i);
    }
}