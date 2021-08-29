 package com.example.vcsc_app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class NgoProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    String storagePath = "NgoUsers_Profile_Cover/";

    ImageView avatarTv, coverIv;
    TextView nameTv, emailTv, phoneTv, bioTv;
    FloatingActionButton fab;

    RecyclerView postsRecyclerView;

    Uri image_uri;

    ProgressDialog pd;
    String hisUid;
    String profileOrCoverPhoto;

    DatabaseReference usersDbRef;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    String cameraPermission[];
    String storagePermission[];

    ArrayList<NgoPostModel> postList;
    NgoPostAdapter adapterPosts;
    NgoUserModel myDb = new NgoUserModel();
    String uid = myDb.getUid();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ngo_profile_activity);
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUID");

        //setHasOptionsMenu(true);
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDbRef = firebaseDatabase.getReference("NgoUsers");
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("NgoUsers");
        storageReference = getInstance().getReference();

        avatarTv = findViewById(R.id.avatarIv);
        bioTv = findViewById(R.id.bio_tv);
        coverIv = findViewById(R.id.coverIv);
        nameTv = findViewById(R.id.nameTv);
        phoneTv = findViewById(R.id.phoneTv);
        emailTv = findViewById(R.id.emailTv);
        fab = findViewById(R.id.fab);
        postsRecyclerView = findViewById(R.id.recyclerViewPost);

        pd = new ProgressDialog(getApplicationContext());

        Query userQuery = usersDbRef.orderByChild("email").equalTo(user.getEmail());
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    myDb = ds.getValue(NgoUserModel.class);

                    /*String name  = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String phone = "" + ds.child("phone").getValue();*/
                    String image = "" + ds.child("image").getValue();
                    String cover = "" + ds.child("cover").getValue();

                    assert myDb != null;
                    String name  = myDb.getName();
                    String email = myDb.getEmail();
                    String phone = myDb.getPhone();
                    //String image = myDb.getImage();
                    //String cover = myDb.getCover();
                    String bio = myDb.getBio();

                    nameTv.setText(name);
                    emailTv.setText(email);
                    phoneTv.setText(phone);
                    bioTv.setText(bio);

                    /*try {
                        Picasso.get().load(image).into(avatarTv);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.person11).into(avatarTv);
                    }

                    try {
                        Picasso.get().load(cover).into(coverIv);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.person11).into(coverIv);
                    }*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NgoProfileActivity.this, NgoProfileDetailsActivity.class));
                finish();
            }
        });

        postList = new ArrayList<>();

       // loadMyPosts();
    }

    private void loadMyPosts() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(NgoProfileActivity.this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        postsRecyclerView.setLayoutManager(layoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("NgoPosts");

        Query query = ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    NgoPostModel myPosts = ds.getValue(NgoPostModel.class);

                    postList.add(myPosts);

                    adapterPosts = new NgoPostAdapter(NgoProfileActivity.this, android.R.layout.simple_list_item_1, postList);

                    postsRecyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NgoProfileActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
