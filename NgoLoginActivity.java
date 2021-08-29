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

public class NgoLoginActivity extends AppCompatActivity {

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
        setContentView(R.layout.ngo_login_activity);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPage);
        NgoImageAdapter adapterView = new NgoImageAdapter(this);
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

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailTxt.setError("Invalid Email");
                emailTxt.setFocusable(true);
            } else if (password.length() < 5) {
                passTxt.setError("Password length at least 6 characters");
                passTxt.setFocusable(true);
            }else{
                loginNgoUser(email, password);
            }
        });

        newUserView.setOnClickListener(view -> {
            startActivity(new Intent(NgoLoginActivity.this, NgoRegisterActivity.class));
            finish();
        });

        newPassView.setOnClickListener(view -> showRecoverPasswordDialog());
    }

    private void showRecoverPasswordDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        LinearLayout linearLayout = new LinearLayout(this);

        final EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);
        emailEt.setMinEms(16);

        builder.setPositiveButton("Recover", (dialogInterface, i) -> {
            String email = emailEt.getText().toString().trim();
            beginRecovery(email);
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        builder.create().show();

    }

    private void beginRecovery(String email) {
        progressDialog.show();
        progressDialog.setMessage("Sending email ");
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(NgoLoginActivity.this, "Please check your Email", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(NgoLoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(NgoLoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginNgoUser(String email, String password) {
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
                            startActivity(new Intent(NgoLoginActivity.this, NgoActivityPage.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(NgoLoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(NgoLoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

