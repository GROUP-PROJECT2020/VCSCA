package com.example.vcsc_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class AdminNavigationActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    String mUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_navigation_activity);

        firebaseAuth = FirebaseAuth.getInstance();
  //      actionBar = getSupportActionBar();
//        actionBar.setTitle("Profile");

        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

       /* actionBar.setTitle("Home");
        startActivity(new Intent(NgoNavigationActivity.this, NgoHomeActivity.class));
        checkUserStatus();*/
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    public void updateToken(String token){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            mUID = user.getUid();
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        TokenActivity mToken = new TokenActivity(token);
        ref.child(mUID).setValue(mToken);

    }

    @SuppressLint("NonConstantResourceId")
    BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            menuItem -> {

                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                       //actionBar.setTitle("Home");
                        startActivity(new Intent(AdminNavigationActivity.this, AdminHomeActivity.class));
                        break;

                    case R.id.nav_volunteers:
                        //actionBar.setTitle("Profile");
                        startActivity(new Intent(AdminNavigationActivity.this, AdminVolunteerUsersActivity.class));
                        break;

                    case R.id.nav_ngos:
                        //actionBar.setTitle("Posts");
                        startActivity(new Intent(AdminNavigationActivity.this, AdminNgoUsersActivity.class));
                        break;

                    /*case R.id.item_notification:
                       // actionBar.setTitle("Notifications");
                        startActivity(new Intent(NgoNavigationActivity.this, NgoNotificationActivity.class));
                        break;*/

                }
                return false;
            };


    private void checkUserStatus(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mUID = user.getUid();

            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();

            updateToken(FirebaseInstanceId.getInstance().getToken());
        }else{
            startActivity(new Intent(AdminNavigationActivity.this, AdminLoginActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }

}


