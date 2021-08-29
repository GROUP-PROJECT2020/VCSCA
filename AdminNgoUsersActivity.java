package com.example.vcsc_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminNgoUsersActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    AdminNgoUsersAdapter adapterUsers;
    List<AdminNgoUserModel> userList;
    FirebaseAuth firebaseAuth;

    public AdminNgoUsersActivity() {
        // Required empty public constructor
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_ngo_users_activity);

       // adapterUsers = new NgoUsersAdapter(NgoUsersActivity.this, userList);
        recyclerView = findViewById(R.id.users_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminNgoUsersActivity.this));
        //recyclerView.setAdapter(adapterUsers);

        userList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();

        getAllNgoUsers();

    }

    private void getAllNgoUsers() {
        progressDialog = new ProgressDialog(this);
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("NgoUsers");
        progressDialog.setMessage("Server Loading, Please Wait");
        progressDialog.show();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //userList.clear();
                progressDialog.dismiss();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    AdminNgoUserModel modelUser = ds.getValue(AdminNgoUserModel.class);

                    //if(modelUser.getUid()!=null && modelUser.getUid().equals(fUser.getUid())){
                        userList.add(modelUser);
                    //}
                    adapterUsers = new AdminNgoUsersAdapter(AdminNgoUsersActivity.this, android.R.layout.simple_list_item_1, userList);
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
