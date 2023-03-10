package com.pd.chatapp.view.activities.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pd.chatapp.BuildConfig;
import com.pd.chatapp.R;
import com.pd.chatapp.databinding.ActivitySetUserInfoBinding;
import com.pd.chatapp.model.CallList;
import com.pd.chatapp.model.user.Users;
import com.pd.chatapp.view.MainActivity;
import com.pd.chatapp.view.activities.settings.SettingsActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class SetUserInfoActivity extends AppCompatActivity {

    private ActivitySetUserInfoBinding binding;
    private ProgressDialog progressDialog;

    private int IMAGE_GALLERY_REQUEST = 111;
    private Uri imageUri;

    private BottomSheetDialog bottomSheetDialog;

    String sdownload_url="";

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_set_user_info);

        // Check ,is the user new or not
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(firebaseUser.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    binding.edName.setText(task.getResult().getString("userName"));
                    Glide.with(SetUserInfoActivity.this).load(task.getResult().getString("imageProfile")).into(binding.imageProfile);

                } else {
                    Log.w("TAG", "Error getting documents.", task.getException());
                }
            }
        });

        progressDialog = new ProgressDialog(this);
        initButtonClick();
        initinfo();
    }

    private void initinfo() {
//        binding.edName.setText(firebaseUser.get);
//        if (firebaseUser.getPhotoUrl()!=null) {
//            Glide.with(SetUserInfoActivity.this).load(firebaseUser.getPhotoUrl()).into(binding.imageProfile);
//        }

//        HashMap<String, Object> mob = new HashMap<>();
//        mob.put("value",firebaseUser.getPhoneNumber());
        firestore.collection("Users")
                .document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //Here you can fetch data or convert it to object
//                            CallList call = new CallList(
//                                    documentSnapshot.getString("userID"),
                try {
                    if (!documentSnapshot.getString("userName").equals("")) {
                        binding.edName.setText(documentSnapshot.getString("userName"));
                    }
//                                    Objects.requireNonNull(dataSnapshot.child("date").getValue().toString()),
                    if (!documentSnapshot.getString("imageProfile").equals("") && documentSnapshot.getString("imageProfile").length() > 0) {
//                        Toast.makeText(SetUserInfoActivity.this, documentSnapshot.getString("imageProfile") + " 123", Toast.LENGTH_SHORT).show();
                        Glide.with(SetUserInfoActivity.this).load(documentSnapshot.getString("imageProfile")).into(binding.imageProfile);
                        sdownload_url = documentSnapshot.getString("imageProfile");
                    } else {
//                        Toast.makeText(SetUserInfoActivity.this, "456 " + " 123", Toast.LENGTH_SHORT).show();
                        binding.imageProfile.setImageDrawable(getDrawable(R.drawable.icon_male_ph));
                    }
//                                    Objects.requireNonNull(dataSnapshot.child("callType").getValue().toString())
//                  }
//                  );
                }catch (Exception e){

                }
            }
        });
    }

    private void initButtonClick() {
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(binding.edName.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please input username",Toast.LENGTH_SHORT).show();
                } else {
                    doUpdate();
//                    uploadToFirebase();
                }

            }
        });

        binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetPickPhoto();
            }
        });
    }


    private void showBottomSheetPickPhoto() {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_pick,null);

        ((View) view.findViewById(R.id.ln_gallery)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
                bottomSheetDialog.dismiss();
            }
        });
        ((View) view.findViewById(R.id.ln_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //ToDo Open Camera
                checkCameraPermission();


                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(bottomSheetDialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetDialog=null;
            }
        });

        bottomSheetDialog.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    221);

        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    222);
        }
        else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        try {
            File file = File.createTempFile("IMG_" + timeStamp, ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,  imageUri);
            intent.putExtra("listPhotoName", imageFileName);
            startActivityForResult(intent, 440);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openGallery(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), IMAGE_GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_GALLERY_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null){

            imageUri = data.getData();
//            Glide.with(SetUserInfoActivity.this).load(imageUri).into(binding.imageProfile);

            uploadToFirebase();

//             try {
//                 Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                 binding.imageProfile.setImageBitmap(bitmap);
//
//             }catch (Exception e){
//                 e.printStackTrace();
//             }
        }
        if (requestCode == 440
                && resultCode == RESULT_OK){
//            Glide.with(SetUserInfoActivity.this).load(imageUri).into(binding.imageProfile);
            uploadToFirebase();
        }
    }

    private void uploadToFirebase() {
        if (imageUri!=null){
            progressDialog.setMessage("Uploading...");
            progressDialog.show();

            StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("ImagesProfile/" + System.currentTimeMillis()+"."+getFileExtention(imageUri));
            riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();

                    sdownload_url = String.valueOf(downloadUrl);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("imageProfile", sdownload_url);

                    progressDialog.dismiss();
                    firestore.collection("Users").document(firebaseUser.getUid()).update(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
//                                    Toast.makeText(getApplicationContext(),"upload successfully",Toast.LENGTH_SHORT).show();

                                    firestore.collection("Users")
                                            .document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            //Here you can fetch data or convert it to object
//                            CallList call = new CallList(
//                                    documentSnapshot.getString("userID"),
//                                            binding.edName.setText(documentSnapshot.getString("userName"));
//                                    Objects.requireNonNull(dataSnapshot.child("date").getValue().toString()),
                                            if (!documentSnapshot.getString("imageProfile").equals("")) {
                                                Glide.with(SetUserInfoActivity.this).load(documentSnapshot.getString("imageProfile")).into(binding.imageProfile);
                                            }
//                                    Objects.requireNonNull(dataSnapshot.child("callType").getValue().toString())
//                            );

                                        }
                                    });
                                    progressDialog.dismiss();
//                                    Glide.with(SetUserInfoActivity.this).load(documentSnapshot.getString("imageProfile")).into(binding.imageProfile);
                                }
                            });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"upload Failed",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

//    private void uploadToFirebase() {
//        if (imageUri!=null){
//            progressDialog.setMessage("Uploading...");
//            progressDialog.show();
//
//            StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("ImagesProfile/" + System.currentTimeMillis()+"."+getFileExtention(imageUri));
//            riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
//                    while (!urlTask.isSuccessful());
//                    Uri downloadUrl = urlTask.getResult();
//
//                    final String sdownload_url = String.valueOf(downloadUrl);
//
//                    HashMap<String, Object> hashMap = new HashMap<>();
//                    hashMap.put("imageProfile", sdownload_url);
//                    hashMap.put("userName", binding.edName.getText().toString());
//
//                    progressDialog.dismiss();
//
//                    db.collection("Users").document(firebaseUser.getUid()).update(hashMap)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Toast.makeText(getApplicationContext(),"upload successfully",Toast.LENGTH_SHORT).show();
//                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                                    finish();
//
//                                }
//                            });
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(getApplicationContext(),"upload Failed",Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                }
//            });
//        }
//    }

    private String getFileExtention(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void doUpdate() {
        ///
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null){
            String userID = firebaseUser.getUid();
            Users users = new Users(userID,
                    binding.edName.getText().toString(),
                    firebaseUser.getPhoneNumber(),
                    sdownload_url,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "");
           firebaseFirestore.collection("Users").document(firebaseUser.getUid()).set(users)
                  // .update("userName",binding.edName.getText().toString())
                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           progressDialog.dismiss();
                           Toast.makeText(getApplicationContext(),"Update Successful",Toast.LENGTH_SHORT).show();
                           startActivity(new Intent(getApplicationContext(), MainActivity.class));
                       }
                   }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   progressDialog.dismiss();
                   Log.d("Update", "onFailure: "+e.getMessage());
                   Toast.makeText(getApplicationContext(),"Update Failed :"+e.getMessage(),Toast.LENGTH_SHORT).show();

               }
           });
        } else {
            Toast.makeText(getApplicationContext(),"you need to login first",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }
}
