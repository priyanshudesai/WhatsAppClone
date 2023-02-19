package com.pd.chatapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pd.chatapp.R;
import com.pd.chatapp.model.user.Users;
import com.pd.chatapp.view.activities.chats.CallsActivity;
import com.pd.chatapp.view.activities.chats.ChatsActivity;
import com.pd.chatapp.view.activities.profile.UserProfileActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private List<Users> list;
    private Context context;

    public ContactsAdapter(List<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_contact_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Users user = list.get(position);

        holder.username.setText(user.getUserName());
        holder.desc.setText(user.getBio());

        if (!user.getImageProfile().equals("")) {
            Glide.with(context).load(user.getImageProfile()).into(holder.imageProfile);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatsActivity.class)
                        .putExtra("userID",user.getUserID())
                        .putExtra("userName",user.getUserName())
                        .putExtra("userProfile",user.getImageProfile()));
            }
        });
        holder.vidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, CallsActivity.class)
                        .putExtra("userID",user.getUserID())
                        .putExtra("userName",user.getUserName())
                        .putExtra("userProfile",user.getImageProfile()));
            }
        });

    }

    public void getAllContacts(List<Users> contactList){
        this.list = contactList;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageProfile,vidBtn;
        private TextView username,desc;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            vidBtn = itemView.findViewById(R.id.vid_call_btn);
            username = itemView.findViewById(R.id.tv_username);
            desc = itemView.findViewById(R.id.tv_desc);
        }
    }
}
