package com.example.vcsc_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class NgoUsersAdapter extends RecyclerView.Adapter<NgoUsersAdapter.MyHolder> {

    Context context;
    List<NgoUserModel> userList;

    FirebaseAuth firebaseAuth;
    String myUid;

    public NgoUsersAdapter(Context context, int simple_list_item_1, List<NgoUserModel> userList) {
        this.context = context;
        this.userList = userList;

        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getUid();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.ngo_users_row, parent, false);
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ngo_users_row, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, final int position) {

        final String hisUID = userList.get(position).getUid();
        String userImage = userList.get(position).getImage();
        String userName = userList.get(position).getName();
        String userEmail = userList.get(position).getEmail();

        myHolder.mEmailTv.setText(userEmail);
        myHolder.mNameTv.setText(userName);
        try{
            Picasso.get().load(userImage).placeholder(R.drawable.person9).into(myHolder.mAvatarTv);
        }catch (Exception e){

        }

        myHolder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new String[]{"Profile", "Chat"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Intent intent = new Intent(context, NgoProfileLayoutActivity.class);
                            intent.putExtra("uid", hisUID);
                            context.startActivity(intent);
                        }
                        if (i == 1) {
                            Intent intent = new Intent(context, NgoChatActivity.class);
                            intent.putExtra("hisUID", hisUID);
                            context.startActivity(intent);
                            //imBlockedOrNot(hisUID);
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarTv, blockIV;
        TextView mNameTv, mEmailTv;
        ImageView more;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            more=itemView.findViewById(R.id.more_bt);
            mAvatarTv = itemView.findViewById(R.id.avatarIv);
            blockIV = itemView.findViewById(R.id.blockIv);
            mNameTv = itemView.findViewById(R.id.nameTv);
            mEmailTv = itemView.findViewById(R.id.emailTv);
        }
    }
}
