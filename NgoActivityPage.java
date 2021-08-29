package com.example.vcsc_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class NgoActivityPage extends AppCompatActivity {

    TextView feedView, profileView, postView, usersView, chatView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ngo_activity_page);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPage);
        NgoActivityImageAdapter adapterView = new NgoActivityImageAdapter(this);
        mViewPager.setAdapter(adapterView);

        feedView = findViewById(R.id.feed_tv);
        profileView = findViewById(R.id.profile_tv);
        postView = findViewById(R.id.post_tv);
        usersView = findViewById(R.id.users_tv);
        chatView = findViewById(R.id.chat_tv);

        feedView.setOnClickListener(view -> {
            startActivity(new Intent(NgoActivityPage.this, NgoHomeActivity.class));
            finish();
        });

        profileView.setOnClickListener(view -> {
            startActivity(new Intent(NgoActivityPage.this, NgoProfileActivity.class));
            finish();
        });

        postView.setOnClickListener(view -> {
            startActivity(new Intent(NgoActivityPage.this, NgoNewPostActivity.class));
            finish();
        });

        usersView.setOnClickListener(view -> {
            startActivity(new Intent(NgoActivityPage.this, AllUsersActivity.class));
            finish();
        });

        chatView.setOnClickListener(view -> {
            startActivity(new Intent(NgoActivityPage.this, NgoChatListActivity.class));
            finish();
        });

        ImageView feedButton = findViewById(R.id.feed_bt);
        feedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedOnButtonClick();
            }
        });

        ImageView profileButton = findViewById(R.id.profile_bt);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileOnButtonClick();
            }
        });

        ImageView postButton = findViewById(R.id.post_bt);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postOnButtonClick();
            }
        });

        ImageView usersButton = findViewById(R.id.users_bt);
        usersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersOnButtonClick();
            }
        });

        ImageView chatButton = findViewById(R.id.chat_bt);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatOnButtonClick();
            }
        });
    }

    public void feedOnButtonClick() {
        Intent i = new Intent(NgoActivityPage.this, NgoHomeActivity.class);
        startActivity(i);
    }

    public void profileOnButtonClick() {
        Intent i = new Intent(NgoActivityPage.this, NgoProfileActivity.class);
        startActivity(i);
    }

    public void postOnButtonClick() {
        Intent i = new Intent(NgoActivityPage.this, NgoNewPostActivity.class);
        startActivity(i);
    }

    public void usersOnButtonClick() {
        Intent i = new Intent(NgoActivityPage.this, AllUsersActivity.class);
        startActivity(i);
    }

    public void chatOnButtonClick() {
        Intent i = new Intent(NgoActivityPage.this, NgoChatListActivity.class);
        startActivity(i);
    }
}