package com.example.vcsc_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminNgoPostAdapter extends RecyclerView.Adapter<AdminNgoPostAdapter.MyHolder> {

    Context context;
    List<NgoPostModel> postList;
    String myUid;

    private DatabaseReference postsRef;

    public AdminNgoPostAdapter(Context context, int simple_list_item_1, ArrayList<NgoPostModel> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        postsRef = FirebaseDatabase.getInstance().getReference("NgoPosts");
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.admin_ngo_post_row, viewGroup, false);
        return new MyHolder(view);
    }


    public void onBindViewHolder(@NonNull final MyHolder holder, final int i) {

        NgoPostModel postModel = postList.get(i);

        holder.uNameTv.setText(postModel.getuName());
//        holder.pTimeTv.setText(postModel.getpTime());
        holder.pTitleTv.setText(postModel.getpTitle());
        holder.pDescriptionTv.setText(postModel.getpDescr());
        holder.dateTv.setText(postModel.getDate());
        holder.locationTv.setText(postModel.getLocation());
        Picasso.get().load(postModel.getpImage()).into(holder.pImageIv);
        try {
            Picasso.get().load(postModel.getuDp()).placeholder(R.drawable.person9).into(holder.uPictureIv);
        }

        catch (Exception e){

        }


        String uid = postList.get(i).getUid();
        String pId = postList.get(i).getpId();
        String pImage = postList.get(i).getpImage();
        /*
        String uEmail = postList.get(i).getuEmail();
        String uName = postList.get(i).getuName();
        String uDp = postList.get(i).getuDp();
        String pId = postList.get(i).getpId();
        String pTitle = postList.get(i).getpTitle();
        String pDescription = postList.get(i).getpDescr();
        String pImage = postList.get(i).getpImage();
        String pTimeStamp = postList.get(i).getpTime();
        String date = postList.get(i).getDate();
        String location = postList.get(i).getLocation();

        //Calendar calender = Calendar.getInstance(Locale.getDefault());
        //calender.setTimeInMillis(Long.parseLong(pTimeStamp));
        //String pTime =  DateFormat.format("dd/MM/yyyy hh:mm aa", calender).toString();

        holder.uNameTv.setText(uName);
        //holder.pTimeTv.setText(pTime);
        holder.pTitleTv.setText(pTitle);
        holder.pDescriptionTv.setText(pDescription);
        holder.dateTv.setText(date);
        holder.locationTv.setText(location);
        Picasso.get().load(pImage).into(holder.pImageIv);
        try {
            Picasso.get().load(uDp).placeholder(R.drawable.person9).into(holder.uPictureIv);
        }
        catch (Exception e){

        }

        /*if(pImage.equals("noImage")){
            holder.pImageIv.setVisibility(View.GONE);
        }else{

            holder.pImageIv.setVisibility(View.VISIBLE);

            try {

            }
            catch (Exception e){

            }
        }*/

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                showMoreOptions(holder.delete, uid, myUid, pId, pImage);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showMoreOptions(ImageButton delete, final String uid, String myUid, final String pId, String pImage) {

        PopupMenu popupMenu = new PopupMenu(context, delete, Gravity.END);

//        if(uid.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
  //      }
    //    popupMenu.getMenu().add(Menu.NONE, 2, 0, "View Detail");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == 0){

                    deletePost(pId, pImage);

                }else {
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void deletePost(String pImage, final String pId) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");

        StorageReference picRed = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRed.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Query fquery = FirebaseDatabase.getInstance().getReference("NgoPosts").orderByChild("pId").equalTo(pId);
                        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                    ds.getRef().removeValue();
                                }
                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{

        ImageView uPictureIv, pImageIv;
        TextView uNameTv, pTitleTv, pTimeTv, pDescriptionTv, dateTv, locationTv;
        ImageButton delete;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            delete = itemView.findViewById(R.id.delete_bt);
            uPictureIv = itemView.findViewById(R.id.uPictureIv);
            pImageIv = itemView.findViewById(R.id.pImageIv);
            uNameTv = itemView.findViewById(R.id.uNameIV);
            pTitleTv = itemView.findViewById(R.id.pTitleTv);
            dateTv = itemView.findViewById(R.id.date_tv);
            locationTv = itemView.findViewById(R.id.location_tv);
            pTimeTv = itemView.findViewById(R.id.pTimeIV);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
        }
    }
}
