package com.example.vcsc_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NgoNewPostActivity extends AppCompatActivity {

    ActionBar actionBar;
    FirebaseAuth firebaseAuth;
    DatabaseReference userDbRef, postDbRef;
    StorageReference storageReference;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;


    String[] cameraPermissions;
    String[] storagePermissions;

    EditText titleEt, descriptionEt, dateEt, locationEt;
    ImageView imageIv;
    WifiManager wifiManager;
    Button uploadBtn, placeBtn;
    NgoPostModel pm = new NgoPostModel();
    public int mYear;
    public int mMonth;
    public int mDay;
    static final int DATE_DIALOG_ID = 0;
    private final static int PLACE_PICKER_REQUEST = 999;
    int REQUEST_PLACE_PICKER=1;

    String name, email, uid, dp, date, location;
    String editTitle, editDescription, editImage, editLocation, editDate;

    Uri image_uri = null;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ngo_new_post_activity);

        /*actionBar = getSupportActionBar();
        actionBar.setTitle("Add new post");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);*/

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(true);

        firebaseAuth = FirebaseAuth.getInstance();

        titleEt = findViewById(R.id.pTitleEt);
        descriptionEt = findViewById(R.id.pDescriptionEt);
        imageIv = findViewById(R.id.pImageIv);
        uploadBtn = findViewById(R.id.pUploadBtn);
        placeBtn = findViewById(R.id.place_btn);
        dateEt = findViewById(R.id.e_event_date);
        locationEt = findViewById(R.id.place_et);

        wifiManager= (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

       placeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Disable Wifi
                wifiManager.setWifiEnabled(false);
                openPlacePicker();
            }
        });

       Intent intent = getIntent();

        /*String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type == null){
            if ("text/plain".equals(type)){
                handleSendText(intent);
            }else if (type.startsWith("image")){
                handleSendImage(intent);
            }
        }*/


        final String isUpdateKey = ""+ intent.getStringExtra("key");
        final String editPostId = ""+ intent.getStringExtra("editPostId");

        if(isUpdateKey.equals("editPost")){
            //actionBar.setTitle("Update Post");
            uploadBtn.setText("Update");
            loadPostData(editPostId);

        }else{
           // actionBar.setTitle("Add new Post");
            uploadBtn.setText("Upload");
        }

        //actionBar.setSubtitle(email);

        userDbRef = FirebaseDatabase.getInstance().getReference("NgoUsers");
        Query query = userDbRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    name = ""+ ds.child("name").getValue();
                    email = ""+ds.child("email").getValue();
                    dp = ""+ds.child("image").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePicDialog();
            }
        });


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*pm.setpTitle(titleEt.getText().toString().trim());
                pm.setpImage(imageIv.getDrawable().toString().trim());
                pm.setpDescr(descriptionEt.getText().toString().trim());
                pm.setLocation(locationEt.getText().toString().trim());
                pm.setDate(dateEt.getText().toString().trim());
                pm.setuDp(dp);
                pm.setuName(name);
                pm.setuEmail(email);
                pm.setUid(uid);

                postDbRef = FirebaseDatabase.getInstance().getReference("NgoPosts");
                postDbRef.push().setValue(pm);
                Toast.makeText(NgoNewPostActivity.this, "Successful Upload", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NgoNewPostActivity.this, NgoHomeActivity.class));*/

                String title = titleEt.getText().toString().trim();
                String description = descriptionEt.getText().toString().trim();
                String date = dateEt.getText().toString().trim();
                String location = locationEt.getText().toString().trim();

                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(NgoNewPostActivity.this, "Title empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(description)) {
                    Toast.makeText(NgoNewPostActivity.this, "description empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(date)) {
                    Toast.makeText(NgoNewPostActivity.this, "event date empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(location)) {
                    Toast.makeText(NgoNewPostActivity.this, "event location empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isUpdateKey.equals("editPost")) {
                    beginUpdate(title, description, date, location, editPostId);

                } else{
                    uploadData(title, description, date, location);
                }
            }

        });
    }

    private void beginUpdate(String title, String description, String editPostId, String date, String location) {

        pd.setMessage("Updating Post");
        pd.show();

        /*if(!editImage.equals("noImage")){
            updateWasWithImage(title, description, editPostId, location, date);
        }else*/ if (imageIv.getDrawable() != null){
            updateWithNowImage(title, description, date, location, editPostId);
        }else{
         //   updateWithoutImage(title, description, date, location, editPostId);
        }
    }

    /*private void updateWasWithImage(final String title, final String description, final String editPostId, String date, String location) {
        StorageReference mPictureRef = FirebaseStorage.getInstance().getReferenceFromUrl(editImage);
        mPictureRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        String filePathAndName = "NgoPosts/" + "post_"+ timeStamp;

                        Bitmap bitmap = ((BitmapDrawable)imageIv.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] data = baos.toByteArray();

                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                        ref.putBytes(data)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!uriTask.isSuccessful());

                                        String downloadUri = uriTask.getResult().toString();
                                        if (uriTask.isSuccessful()) {

                                            HashMap<String, Object> hashMap = new HashMap<>();

                                            hashMap.put("uId", uid);
                                            hashMap.put("uName", name);
                                            hashMap.put("uEmail", email);
                                            hashMap.put("uDp", dp);
                                            hashMap.put("pTitle", title);
                                            hashMap.put("pDescr", description);
                                            hashMap.put("date", date);
                                            hashMap.put("location", location);
                                            hashMap.put("pImage", downloadUri);

                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("NgoPosts");
                                            ref.child(editPostId)
                                                    .updateChildren(hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            pd.dismiss();
                                                            Toast.makeText(NgoNewPostActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    pd.dismiss();
                                                    Toast.makeText(NgoNewPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(NgoNewPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(NgoNewPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    private void updateWithNowImage(final String title, final String description, final String editPostId,String date, String location ) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "NgoPosts/" + "post_"+ timeStamp;

        Bitmap bitmap = ((BitmapDrawable)imageIv.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());

                        String downloadUri = uriTask.getResult().toString().trim();
                        if (uriTask.isSuccessful()) {

                            HashMap<String, String> hashMap = new HashMap<>();

                            hashMap.put("uId", uid);
                            hashMap.put("uName", name);
                            hashMap.put("uEmail", email);
                            hashMap.put("uDp", dp);
                            hashMap.put("pTitle", title);
                            hashMap.put("date", date);
                            hashMap.put("location", location);
                            hashMap.put("pDescr", description);
                            hashMap.put("pImage", downloadUri);

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("NgoPosts");
                            ref.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(NgoNewPostActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
                                }
                            });

                            /*ref.child(editPostId)
                                    .updateChildren(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(NgoNewPostActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(NgoNewPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });*/

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(NgoNewPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPostData(final String editPostId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("NgoPosts");
        Query fquery = reference.orderByChild("pId").equalTo(editPostId);
        fquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    editTitle = ""+ds.child("pTitle").getValue();
                    editDescription = ""+ds.child("pDescr").getValue();
                    editImage = ""+ds.child("pImage").getValue();
                    date = ""+ds.child("date").getValue();
                    location = ""+ds.child("location").getValue();

                    titleEt.setText(editTitle);
                    descriptionEt.setText(editDescription);
                    locationEt.setText(location);
                    dateEt.setText(date);
                    Picasso.get().load(editImage).into(imageIv);

                    /*if (!editImage.equals("noImage")){
                        try {
                            Picasso.get().load(editImage).into(imageIv);
                        } catch (Exception e) {


                        }
                    }*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void uploadData(final String title, final String description, final String date, final String location) {
        pd.setMessage("Publishing post...");
        pd.show();

        final String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "NgoPosts/" + "post_" + timeStamp;

        if(imageIv.getDrawable() != null){

            Bitmap bitmap = ((BitmapDrawable)imageIv.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            String downloadUri = uriTask.getResult().toString();


                            if (uriTask.isSuccessful()) {

                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("uid", uid);
                                hashMap.put("uName", name);
                                hashMap.put("uEmail", email);
                                hashMap.put("uDp", dp);
                                hashMap.put("pId", timeStamp);
                                hashMap.put("pTitle", title);
                                hashMap.put("pDescr", description);
                                hashMap.put("date", date);
                                hashMap.put("location", location);
                                hashMap.put("pImage", downloadUri);
                                hashMap.put("pTime", timeStamp);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("NgoPosts");
                                ref.child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                Toast.makeText(NgoNewPostActivity.this, "Post published", Toast.LENGTH_SHORT).show();

                                                startActivity(new Intent(NgoNewPostActivity.this, NgoHomeActivity.class));

                                                titleEt.setText("");
                                                descriptionEt.setText("");
                                                dateEt.setText("");
                                                locationEt.setText("");
                                                imageIv.setImageURI(null);
                                                image_uri = null;

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(NgoNewPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(NgoNewPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        else{
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("uid", uid);
            hashMap.put("uName", name);
            hashMap.put("uEmail", email);
            hashMap.put("uDp", dp);
            hashMap.put("date", date);
            hashMap.put("location", location);
            hashMap.put("pId", timeStamp);
            hashMap.put("pTitle", title);
            hashMap.put("pDescr", description);
            hashMap.put("pImage", "noImage");
            hashMap.put("pTime", timeStamp);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("NgoPosts");
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(NgoNewPostActivity.this, "Post published", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(NgoNewPostActivity.this, NgoHomeActivity.class));
                            titleEt.setText("");
                            descriptionEt.setText("");
                            dateEt.setText("");
                            locationEt.setText("");
                            imageIv.setImageURI(null);
                            image_uri = null;

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(NgoNewPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }

    }

    @SuppressLint("CutPasteId")
    public void datePickOnButtonClick(View v) {
        dateEt = findViewById(R.id.e_event_date);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        updateDisplay();

        dateEt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
    }

    private void updateDisplay() {
        dateEt.setText(new StringBuilder().append(mDay).append("-")
                .append(mMonth + 1).append("-").append(mYear).append(" "));
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDisplay();
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DATE_DIALOG_ID) { // set date picker as current time
            return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
                    mDay);
        }

        return null;
    }// date complete

    private void showImagePicDialog() {

    String options[] = {"Camera", "Gallery"};
    android.app.AlertDialog.Builder builder = new AlertDialog.Builder(NgoNewPostActivity.this);
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

    private void pickFromCamera() {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp Descr");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission(){

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }else{
                        Toast.makeText(this, "Permissions are necessary", Toast.LENGTH_SHORT).show();
                    }
                }else{

                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        pickFromGallery();
                    }else{
                        Toast.makeText(this, "Permissions are necessary", Toast.LENGTH_SHORT).show();
                    }
                }else{

                }
            }
            break;
        }
    }

    private void openPlacePicker() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);

            //Enable Wifi
            wifiManager.setWifiEnabled(true);

        } catch (GooglePlayServicesRepairableException e) {
            Log.d("Exception",e.getMessage());

            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d("Exception",e.getMessage());

            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(NgoNewPostActivity.this, data);

                    CharSequence name = place.getName();
                    //Locale name = place.getLocale();
                    String placeId = place.getId();
                    String PlaceValue = name.toString() + " , " + placeId;
                    locationEt.setText(placeId);
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();

                imageIv.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                imageIv.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

