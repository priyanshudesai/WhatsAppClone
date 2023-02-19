package com.pd.chatapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pd.chatapp.R;
import com.pd.chatapp.model.CallList;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.pd.chatapp.view.activities.chats.CallsActivity;
import com.pd.chatapp.view.activities.chats.ChatsActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CallListAdapter extends RecyclerView.Adapter<CallListAdapter.Holder> {
    private List<CallList> list;
    private Context context;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public CallListAdapter(List<CallList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_call_list, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {

        final CallList callList = list.get(position);

        holder.tvName.setText(callList.getUserName());
        holder.tvDate.setText(callList.getDateTime());

        if (callList.getCallType().equals("missed") && callList.getReceiver().equals(firebaseUser.getUid())) {
            holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_downward_black_24dp));
            holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_red_dark));

            firestore.collection("Users").document(callList.getSender()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String username = documentSnapshot.getString("userName");
//                String userPhone = documentSnapshot.getString("userPhone");
                    String imageProfile = documentSnapshot.getString("imageProfile");
//                String userAbout = documentSnapshot.getString("bio");

                holder.tvName.setText(username);
//                binding.tvAbout.setText(userAbout);
//                binding.tvPhone.setText(userPhone);
                    if (!imageProfile.equals("")) {
                        Glide.with(context).load(imageProfile).into(holder.profile);
                    }
                }
            });

        } else if (callList.getCallType().equals("income") && callList.getReceiver().equals(firebaseUser.getUid())) {
            holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_downward_black_24dp));
            holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_green_dark));

            firestore.collection("Users").document(callList.getSender()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String username = documentSnapshot.getString("userName");
//                String userPhone = documentSnapshot.getString("userPhone");
                    String imageProfile = documentSnapshot.getString("imageProfile");
//                String userAbout = documentSnapshot.getString("bio");

                    holder.tvName.setText(username);
//                binding.tvAbout.setText(userAbout);
//                binding.tvPhone.setText(userPhone);
                    if (!imageProfile.equals("")) {
                        Glide.with(context).load(imageProfile).into(holder.profile);
                    }
                }
            });
        } else {
            holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_upward_black_24dp));
            holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_green_dark));
            // for image we need library ...
            if (!callList.getUrlProfile().equals("")) {
                Glide.with(context).load(callList.getUrlProfile()).into(holder.profile);
            }
        }

        holder.call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseUser.getUid().equals(callList.getSender())) {
                    context.startActivity(new Intent(context, CallsActivity.class)
                            .putExtra("userID", callList.getReceiver())
                            .putExtra("userName", callList.getUserName())
                            .putExtra("userProfile", callList.getUrlProfile()));
                } else {
                    context.startActivity(new Intent(context, CallsActivity.class)
                            .putExtra("userID", callList.getSender())
                            .putExtra("userName", callList.getUserName())
                            .putExtra("userProfile", callList.getUrlProfile()));
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private TextView tvName, tvDate;
        private CircularImageView profile;
        private ImageView arrow, call_btn;

        public Holder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_date);
            tvName = itemView.findViewById(R.id.tv_name);
            profile = itemView.findViewById(R.id.image_profile);
            arrow = itemView.findViewById(R.id.img_arrow);
            call_btn = itemView.findViewById(R.id.call_btn);
        }
    }
}
