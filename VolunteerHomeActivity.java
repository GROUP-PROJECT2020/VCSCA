package com.example.vcsc_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VolunteerHomeActivity extends Activity {
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    ArrayList<NgoPostModel> postsList;
    NgoPostAdapter adapterPosts;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volunteer_home_activity);

        //setHasOptionsMenu(true);

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.postsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext ());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setAdapter(adapterPosts);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setLayoutManager(layoutManager);

        postsList = new ArrayList<>();

        loadPosts();

    }

    private void loadPosts() {
        progressDialog = new ProgressDialog(this);
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("NgoPosts");
        progressDialog.setMessage("Server Loading, Please Wait");
        progressDialog.show();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //userList.clear();
                progressDialog.dismiss();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    NgoPostModel modelUser = ds.getValue(NgoPostModel.class);

                    //if(modelUser.getUid()!=null && modelUser.getUid().equals(fUser.getUid())){
                    postsList.add(modelUser);
                }
                adapterPosts = new NgoPostAdapter(VolunteerHomeActivity.this, android.R.layout.simple_list_item_1, postsList);
                recyclerView.setAdapter(adapterPosts);
                adapterPosts.notifyDataSetChanged();
                //   }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), ""+databaseError.getDetails(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
