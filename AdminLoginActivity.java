package com.example.vcsc_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminLoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;

    private EditText emailTxt;
    private EditText passTxt;
    TextView newUserView, newPassView;
    Button loginButton;

    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login_activity);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPage);
        AdminImageAdapter adapterView = new AdminImageAdapter(this);
        mViewPager.setAdapter(adapterView);

//        Intent intent = getIntent();
//        email1 = intent.getStringExtra("email1");
//        password1 = intent.getStringExtra("password1");

        //Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
          .build();
        //mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        emailTxt = findViewById(R.id.username_txt);
        passTxt = findViewById(R.id.password_txt);
        newUserView = findViewById(R.id.user_view);
        newPassView = findViewById(R.id.password_view);
        loginButton = findViewById(R.id.login_bt);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In ");
        progressDialog.setCanceledOnTouchOutside(true);

//        mEmailEt.setText(email1);
//        mPasswordEt.setText(password1);

        loginButton.setOnClickListener(view -> {
            String email = emailTxt.getText().toString();
            String password = passTxt.getText().toString();

            if(email.equals("angelakairu22@gmail.com")){
                loginAdminUser(email, password);
            } else {
                emailTxt.setError("Invalid Email");
                emailTxt.setFocusable(true);
            }

            if(password.equals("20112011")){
                loginAdminUser(email, password);
            } else {
                passTxt.setError("Invalid Password");
                passTxt.setFocusable(true);
            }
        });
    }

    private void loginAdminUser(String email, String password) {
        progressDialog.show();
        progressDialog.setMessage("Logging In ");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(AdminLoginActivity.this, AdminNavigationActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(AdminLoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AdminLoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}

