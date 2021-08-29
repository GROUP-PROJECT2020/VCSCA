package com.example.vcsc_app;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class VolunteerProfileDetailsActivity extends AppCompatActivity {

    public EditText name, email, bio, phone;
    public ImageView dp, cover;

    public FirebaseDatabase Database = FirebaseDatabase.getInstance();
    public DatabaseReference ref;
    DatabaseReference databaseReference;
    FirebaseUser user;
    StorageReference storageReference;
    String storagePath = "VolunteerUsers_Profile_Cover/";
    String profileOrCoverPhoto;
    NgoUserModel myDb;
    Uri image_uri;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    String cameraPermission[];
    String storagePermission[];

    ProgressDialog pd;

    NgoUserModel Db = new NgoUserModel();
    String uid = Db.getUid();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volunteer_profile_details_activity);

        pd = new ProgressDialog(this);

        name = findViewById(R.id.e_name);
        email = findViewById(R.id.e_email);
        bio = findViewById(R.id.e_bio);
        phone = findViewById(R.id.e_phone);
        dp = findViewById(R.id.dp_Iv);
        cover = findViewById(R.id.cover_Iv);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        myDb = new NgoUserModel();
        ref = Database.getReference().child("VolunteerUsers");
        databaseReference = Database.getReference("VolunteerUsers");
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = getInstance().getReference();

        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicDialog();
            }
        });

        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicDialog();
            }
        });

        Button createButton = findViewById(R.id.save_profile);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = 10;

                if (name.getText().toString().isEmpty()) {
                    name.setError("Please Enter Name");
                }
                else if(bio.getText().toString().isEmpty()) {
                    bio.setError("Please Enter Bio");
                }
                else if(email.getText().toString().isEmpty()) {
                    email.setError("Please Enter Email Address");
                }
                else if(phone.getText().toString().isEmpty()) {
                    phone.setError("Please Enter the Contact");
                }
                else if(phone.getText().toString().length()<10) {
                    phone.setError("Please Enter a valid number (10 digits)");
                }
                else {
                    myDb.setPhone(phone.getText().toString().trim());
                    myDb.setName(name.getText().toString().trim());
                    myDb.setEmail(email.getText().toString().trim());
                    myDb.setBio(bio.getText().toString().trim());
                    myDb.setImage("");
                    myDb.setCover("");
                    myDb.setUid(user.getUid());
                    //  myDb.setImage(mother.getText().toString().trim());
                    //  myDb.setCover(father.getText().toString().trim());

                    ref.push().setValue(myDb);
                    uploadProfileImages(image_uri);

                    Toast.makeText(VolunteerProfileDetailsActivity.this, "Profile Created Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkStoragePermission(){

        boolean result = ContextCompat.checkSelfPermission(VolunteerProfileDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission(){
        requestPermissions(storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){

        boolean result = ContextCompat.checkSelfPermission(VolunteerProfileDetailsActivity.this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(VolunteerProfileDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission(){
        requestPermissions(cameraPermission, CAMERA_REQUEST_CODE);
    }

    private void showImagePicDialog() {

        String options[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(VolunteerProfileDetailsActivity.this);
        builder.setTitle("Pick Image From");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (i == 0) {

                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    }else{
                        pickFromCamera();
                    }
                } else if (i == 1) {
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }else{
                        pickFromGallery();
                    }

                }
            }
        });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    }else{
                        Toast.makeText(VolunteerProfileDetailsActivity.this, "Camera permission required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{

                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (writeStorageAccepted) {
                        pickFromGallery();
                    }else{
                        Toast.makeText(VolunteerProfileDetailsActivity.this, "Storage permission required", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                uploadProfileImages(image_uri);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                uploadProfileImages(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileImages(Uri image_uri) {

        if(this.image_uri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            //progressDialog.show();

            String filePathAndName = storagePath + "" + profileOrCoverPhoto + "_" + user.getUid();

            StorageReference ref = storageReference.child(filePathAndName);
            ref.putFile(this.image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            final Uri downloadUri = uriTask.getResult();

                            if (uriTask.isSuccessful()) {
                                HashMap<String, Object> results = new HashMap<>();
                                results.put(profileOrCoverPhoto, downloadUri.toString());

                                databaseReference.child(user.getUid()).updateChildren(results)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                Toast.makeText(VolunteerProfileDetailsActivity.this, "Image upload Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(VolunteerProfileDetailsActivity.this, "Error Updating Image", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                if(profileOrCoverPhoto.equals("image")){
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("VolunteerUsers");
                                    Query query = ref.orderByChild("uid").equalTo(uid);
                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                String child = ds.getKey();
                                                dataSnapshot.getRef().child("uDp").setValue(downloadUri.toString());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                String child = ds.getKey();
                                                    Query child2 = FirebaseDatabase.getInstance().getReference("VolunteerUsers").orderByChild("uid").equalTo(uid);
                                                    child2.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                                String child = ds.getKey();
                                                                dataSnapshot.getRef().child(child).child("uDp").setValue(downloadUri.toString());

                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                            else{
                                pd.dismiss();
                                Toast.makeText(VolunteerProfileDetailsActivity.this, "Try again later", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(VolunteerProfileDetailsActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });

        }
    }


    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);

    }

    private void pickFromCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        image_uri = VolunteerProfileDetailsActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }


}


