package com.example.vcsc_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NgoHomeActivity extends Activity {
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    ArrayList<NgoPostModel> postList = new ArrayList<>();
    ArrayList<String> postList1 = new ArrayList<>();
    RecyclerView recyclerView;

    NgoPostAdapter adapterPosts;
    String uid;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ngo_home_activity);

        //setHasOptionsMenu(true);

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.postsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext ());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setAdapter(adapterPosts);
        recyclerView.setLayoutManager(layoutManager);

        //postsList = new ArrayList<>();

        loadPosts();

    }

    private void loadPosts() {
        progressDialog = new ProgressDialog(this);
       // final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("NgoPosts");
        progressDialog.setMessage("Server Loading, Please Wait");
        progressDialog.show();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                progressDialog.dismiss();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    NgoPostModel modelUser = ds.getValue(NgoPostModel.class);
                    postList.add(modelUser);

                    adapterPosts = new NgoPostAdapter(NgoHomeActivity.this, android.R.layout.simple_list_item_1, postList);
                    recyclerView.setAdapter(adapterPosts);

                }
               // adapterPosts.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), ""+databaseError.getDetails(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
