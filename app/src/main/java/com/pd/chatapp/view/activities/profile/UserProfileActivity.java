package com.pd.chatapp.view.activities.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pd.chatapp.R;
import com.pd.chatapp.common.Common;
import com.pd.chatapp.databinding.ActivityUserProfileBinding;
import com.pd.chatapp.view.activities.chats.CallsActivity;
import com.pd.chatapp.view.activities.display.ViewImageActivity;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {

    String userName, receiverID, userProfile;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    private ActivityUserProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_user_profile);

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        receiverID = intent.getStringExtra("userID");
        userProfile = intent.getStringExtra("userProfile");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        if (firebaseUser!=null){
            getInfo();
        }

        if (receiverID!=null){

            binding.toolbar.setTitle(userName);
            if (userProfile != null) {
                if (userProfile.equals("")){
                    binding.imageProfile.setImageResource(R.drawable.icon_male_ph);  // set  default image when profile user is null
                } else {
                    Glide.with(this).load(userProfile).into( binding.imageProfile);
                }
                binding.imageProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!userProfile.equals("")) {
                            binding.imageProfile.invalidate();
                            Drawable dr = binding.imageProfile.getDrawable();
                            Common.IMAGE_BITMAP = ((GlideBitmapDrawable) dr.getCurrent()).getBitmap();
                            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(UserProfileActivity.this, binding.imageProfile, "image");
                            Intent intent = new Intent(UserProfileActivity.this, ViewImageActivity.class);
                            startActivity(intent, activityOptionsCompat.toBundle());
                        }else {
                            startActivity(new Intent(UserProfileActivity.this,ViewImageActivity.class));
                        }

                    }
                });
            }
        }
        initToolbar();

        binding.vidCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserProfileActivity.this, CallsActivity.class)
                        .putExtra("userID",receiverID)
                        .putExtra("userName",userName)
                        .putExtra("userProfile",userProfile));
            }
        });
    }
    private void initToolbar() {

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getInfo() {
        firestore.collection("Users").document(receiverID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
              //  String userName = documentSnapshot.getString("userName");
               // String userPhone = documentSnapshot.getString("userPhone");
                //String imageProfile = documentSnapshot.getString("imageProfile");
                String userAbout = documentSnapshot.getString("bio");

               // binding.tv.setText(userName);
                binding.tvDesc.setText(userAbout);
               // binding.tvPhone.setText(userPhone);
               // Glide.with(UserProfileActivity.this).load(imageProfile).into(binding.imageProfile);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

}