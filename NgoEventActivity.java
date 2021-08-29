package com.example.vcsc_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class NgoEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ngo_event_activity);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPage);
        NextImageAdapter adapterView = new NextImageAdapter(this);
        mViewPager.setAdapter(adapterView);


        Button feedButton = findViewById(R.id.feed_bt);
        feedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedOnButtonClick();
            }
        });

        Button chatButton = findViewById(R.id.chat_bt);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatOnButtonClick();
            }
        });
    }

    public void feedOnButtonClick() {
        Intent i = new Intent(this, NgoHomeActivity.class);
        startActivity(i);
    }

    public void chatOnButtonClick() {
        Intent i = new Intent(this, NgoChatActivity.class);
        startActivity(i);
    }

}
