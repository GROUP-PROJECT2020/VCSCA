package com.example.vcsc_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class VolunteerRegisterActivity extends AppCompatActivity {

    private EditText emailTxt;
    private EditText passTxt;
    TextView oldUserView;
    Button registerButton;

        ProgressDialog progressDialog;

        private FirebaseAuth mAuth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.volunteer_register_activity);

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Registering ");

            emailTxt = findViewById(R.id.username_txt);
            passTxt = findViewById(R.id.password_txt);
            registerButton = findViewById(R.id.register_bt);
            oldUserView = findViewById(R.id.user_view);

            mAuth = FirebaseAuth.getInstance();

            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String email = emailTxt.getText().toString();
                    String password = passTxt.getText().toString();

                    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        emailTxt.setError("Email is not valid");
                        emailTxt.setFocusable(true);
                    } else if (password.length() < 5) {
                        passTxt.setError("Password should be at least 6 characters long");
                        passTxt.setFocusable(true);
                    }else{

                        registerUSer(email, password);
                    }

                }
            });

            oldUserView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(VolunteerRegisterActivity.this, VolunteerLoginActivity.class));
                    finish();
                }
            });

        }

        private void registerUSer(String email, final String password) {

            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                progressDialog.dismiss();
                                FirebaseUser user = mAuth.getCurrentUser();

                                String email = user.getEmail();
                                String uid = user.getUid();

                                /*HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("email", email);
                                hashMap.put("uid", uid);
                                hashMap.put("name", "");
                                //hashMap.put("onlineStatus", "online");
                                //hashMap.put("typingTo", "noOne");
                                //hashMap.put("phone", "");
                                //hashMap.put("image", "");
                                //hashMap.put("cover", "");*/


                                Toast.makeText(VolunteerRegisterActivity.this, "You are now Registered " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(VolunteerRegisterActivity.this, VolunteerLoginActivity.class));
                                finish();


                            } else {
                                // If sign in fails, display a message to the user.
                                progressDialog.dismiss();
                                Toast.makeText(VolunteerRegisterActivity.this, "Could not Authenticate.", Toast.LENGTH_SHORT).show();


                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(VolunteerRegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public boolean onSupportNavigateUp() {
            onBackPressed();
            return super.onSupportNavigateUp();
        }
    }


