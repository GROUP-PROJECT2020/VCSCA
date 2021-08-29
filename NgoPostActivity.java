package com.example.vcsc_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class NgoPostActivity extends AppCompatActivity {

    ProgressDialog pd;

    String myUid, hisUid, myEmail, myName, myDp, postId, hisDp, hisName, pImage, location;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    DatabaseReference usersDbRef;

    ImageView uPictureIv, pImageIv;
    TextView uNameTv, pTitleTv, pTimeTv, pDescriptionTv, locationTv, dateTv;

    LinearLayout profileLayout;
    RecyclerView recyclerView;

    ImageView cAvatarIv;

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ngo_post_activity);

        /*actionBar = getSupportActionBar();
        actionBar.setTitle("Post Detail");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);*/

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

        uPictureIv = findViewById(R.id.uPictureIv);
        pImageIv = findViewById(R.id.pImageIv);
        uNameTv = findViewById(R.id.uNameIV);
        pTitleTv = findViewById(R.id.pTitleTv);
        pTimeTv = findViewById(R.id.pTimeIV);
        locationTv = findViewById(R.id.location_tv);
        dateTv = findViewById(R.id.date_tv);
        pDescriptionTv = findViewById(R.id.pDescriptionTv);
        profileLayout = findViewById(R.id.profileLayout);

        loadPostInfo();

        loadUsersInfo();

        //actionBar.setSubtitle("SignedIn as: " + myEmail);
    }

    private void loadUsersInfo() {
        //Query query = FirebaseDatabase.getInstance().getReference("NgoUsers");
        //query.orderByChild("uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
        usersDbRef = firebaseDatabase.getReference("NgoUsers");
        Query userQuery = usersDbRef.orderByChild("uid").equalTo(hisUid);
        userQuery.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    myName = ""+ ds.child("name").getValue();
                    myDp = ""+ds.child("image").getValue();

                    try {
                        Picasso.get().load(myDp).placeholder(R.drawable.person9).into(cAvatarIv);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.person9).into(cAvatarIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadPostInfo() {
        usersDbRef = firebaseDatabase.getReference("NgoPosts");
        Query userQuery = usersDbRef.orderByChild("pId").equalTo(postId);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    String pTitle = ""+ds.child("pTitle").getValue();
                    String pDescription = ""+ds.child("pDescr").getValue();
                    String pTimeStamp = ""+ds.child("pTime").getValue();
                    String location =""+ds.child("location").getValue();
                    String date =""+ds.child("date").getValue();
                    pImage = ""+ds.child("pImage").getValue();
                    hisDp = ""+ds.child("uDp").getValue();

                    String hisUid = ""+ds.child("uid").getValue();
                    String uEmail = ""+ds.child("uEmail").getValue();
                    hisName = ""+ds.child("uName").getValue();

                    Calendar calender = Calendar.getInstance(Locale.getDefault());
                    calender.setTimeInMillis(Long.parseLong(pTimeStamp));
                    String pTime =  DateFormat.format("dd/MM/yyyy hh:mm aa", calender).toString();

                    pTitleTv.setText(pTitle);
                    pDescriptionTv.setText(pDescription);
                    pTimeTv.setText(pTime);
                    locationTv.setText(location);
                    dateTv.setText(date);

                    uNameTv.setText(hisName);
                    Picasso.get().load(pImage).into(pImageIv);

                    try {
                        Picasso.get().load(hisDp).placeholder(R.drawable.person9).into(uPictureIv);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.person9).into(uPictureIv);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
