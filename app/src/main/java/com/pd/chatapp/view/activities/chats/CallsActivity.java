package com.pd.chatapp.view.activities.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pd.chatapp.R;
import com.pd.chatapp.databinding.ActivityCallsBinding;
import com.pd.chatapp.databinding.ActivityChatsBinding;
import com.pd.chatapp.model.Chatlist;
import com.pd.chatapp.view.MainActivity;
import com.pd.chatapp.view.activities.video.VideoChatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CallsActivity extends AppCompatActivity {

    private static final String TAG = "CallsActivity";
    private ActivityCallsBinding binding;
    private String userProfile, userName;
    private String receiverID;
    private String checker = "";
    private String messagePushID = "";
    private String senduserProfile, senduserName, senderid;


    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_calls);

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        receiverID = intent.getStringExtra("userID");
        userProfile = intent.getStringExtra("userProfile");

        senderid = firebaseUser.getUid();

        mediaPlayer = MediaPlayer.create(this, R.raw.ringing);


        if (receiverID != null) {
            Log.d(TAG, "onCreate: receiverID " + receiverID);


            firestore.collection("Users").document(receiverID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.d(TAG, "onSuccess: ddd" + documentSnapshot.getString("userName"));
                    try {
                        //senduserid=documentSnapshot.getString("userID");
                        userName = documentSnapshot.getString("userName");
                        userProfile = documentSnapshot.getString("imageProfile");

                        binding.nameCall.setText(userName);
                        if (userProfile != null) {
                            if (userProfile.equals("")) {
                                binding.profImgCall.setImageResource(R.drawable.icon_male_ph);  // set  default image when profile user is null
                            } else {
                                Glide.with(CallsActivity.this).load(userProfile).into(binding.profImgCall);
                            }
                        }

                    } catch (Exception e) {
                        Log.d(TAG, "onSuccess: " + e.getMessage());
                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Error L" + e.getMessage());
                }
            });


        }

        if (senderid != null) {
            firestore.collection("Users").document(senderid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.d(TAG, "onSuccess: ddd" + documentSnapshot.getString("userName"));
                    try {
                        //senduserid=documentSnapshot.getString("userID");
                        senduserName = documentSnapshot.getString("userName");
                        senduserProfile = documentSnapshot.getString("imageProfile");

                    } catch (Exception e) {
                        Log.d(TAG, "onSuccess: " + e.getMessage());
                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Error L" + e.getMessage());
                }
            });
        }

        binding.cancelCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                checker = "clicked";

                cancleCallingUser();
            }
        });

        binding.acceptCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mediaPlayer.stop();

                final HashMap<String, Object> callingPickupMap = new HashMap<>();
                callingPickupMap.put("picked","picked");

                reference.child("Calls").child(senderid).child("Ringing")
                        .updateChildren(callingPickupMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    startActivity(new Intent(CallsActivity.this, VideoChatActivity.class)
                                            .putExtra("id",receiverID)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                                    finish();
                                }
                            }
                        });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mediaPlayer.start();

        reference.child("Calls").child(receiverID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!checker.equals("clicked") && !snapshot.hasChild("Calling") && !snapshot.hasChild("Ringing")) {

//                            String ruserN

//                            firestore.collection("Users").document(receiverID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                @Override
//                                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                    Log.d(TAG, "onSuccess: ddd" + documentSnapshot.getString("userName"));
//
////                                    String userID = Objects.requireNonNull(dataSnapshot.child("callid").getValue()).toString();
////                                    Log.d(TAG, "onDataChange: userid " + userID);
//
////                                    call.setUserName(documentSnapshot.getString("userName"));
////                                    call.setUrlProfile(documentSnapshot.getString("urlProfile"));
//
//
//
//                                }
//
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d(TAG, "onFailure: Error L" + e.getMessage());
//                                }
//                            });
                            final HashMap<String, Object> callingInfo = new HashMap<>();
                            callingInfo.put("calling", receiverID);

                            reference.child("Calls").child(senderid)
                                    .child("Calling")
                                    .updateChildren(callingInfo)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                final HashMap<String, Object> ringingInfo = new HashMap<>();
                                                ringingInfo.put("ringing", senderid);

                                                reference.child("Calls").child(receiverID)
                                                        .child("Ringing")
                                                        .updateChildren(ringingInfo);

                                                DatabaseReference userMessageKeyRef = reference.child("CallList").push();

                                                messagePushID = userMessageKeyRef.getKey();

                                                Map messageTextBody = new HashMap();
                                                messageTextBody.put("dateTime", getCurrentDate());
                                                messageTextBody.put("callId", messagePushID);
                                                messageTextBody.put("receiver", receiverID);
                                                messageTextBody.put("sender", firebaseUser.getUid());
                                                messageTextBody.put("callType", "missed");
                                                messageTextBody.put("userName", userName);
                                                messageTextBody.put("urlProfile", userProfile);

                                                Map messageBodyDetails = new HashMap();
                                                messageBodyDetails.put( "CallList" + "/" + messagePushID, messageTextBody);

                                                reference.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task)
                                                    {
                                                        if (task.isSuccessful())
                                                        {
                                                            Log.d("Send", "onSuccess: ");
                                                        }
                                                        else
                                                        {
                                                            Log.d("Send", "onFailure: "+task.getException());
                                                        }
//                MessageInputText.setText("");
                                                    }
                                                });



//                                                final HashMap<String, Object> calllist = new HashMap<>();
//                                                calllist.put("callid", receiverID);
//                                                calllist.put("callType", "called");
//                                                calllist.put("date", getCurrentDate());
//                                                reference.child("CallList").child(senderid).child(receiverID)
//                                                        .updateChildren(calllist).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void aVoid) {
//                                                        final HashMap<String, Object> calllist1 = new HashMap<>();
//                                                        calllist1.put("callid", senderid);
//                                                        calllist1.put("callType", "missed");
//                                                        calllist1.put("date", getCurrentDate());
//                                                        reference.child("CallList").child(receiverID).child(senderid)
//                                                                .updateChildren(calllist1);
//                                                    }
//                                                });
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        reference.child("Calls").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(senderid).hasChild("Ringing") && !snapshot.child(senderid).hasChild("Calling")) {
                    binding.acceptCall.setVisibility(View.VISIBLE);
                }
                if (snapshot.child(receiverID).child("Ringing").hasChild("picked")){
                    mediaPlayer.stop();
                    final HashMap<String, Object> calllist2 = new HashMap<>();
                    calllist2.put("callType", "income");
                    reference.child("CallList").child(messagePushID)
                            .updateChildren(calllist2);
///////////////////////////////////////////////////
                    startActivity(new Intent(CallsActivity.this, VideoChatActivity.class)
                    .putExtra("id",receiverID)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        u = reference.child("Calls").child(senderid).getKey();

        reference.child("Calls").child(senderid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                mediaPlayer.stop();
                checker = "";
                startActivity(new Intent(CallsActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        reference.child("Calls").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child(senderid).hasChild("Ringing")){
//                    u="receiver";
//                }
//                if (snapshot.child(senderid).hasChild("Calling")){
//                    u="sender";
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//
//        new java.util.Timer().schedule(
//                new java.util.TimerTask() {
//                    @Override
//                    public void run() {
//
////                        final Toast toast = Toast.makeText(CallsActivity.this, u, Toast.LENGTH_SHORT);
////                        toast.show();
////                        Toast.makeText(CallsActivity.this, u, ).show();
//                        CallsActivity.this.runOnUiThread(new Runnable() {
//                            public void run() {
//                                Toast.makeText(CallsActivity.this, u, Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//
//
//                        // your code here
//                        if (u.equals("receiver")) {
//                            reference.child("Calls").addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    if (snapshot.exists() && !snapshot.child(senderid).hasChild("Ringing") && u.equals("Ringing")) {
//                                        mediaPlayer.stop();
//                                        checker = "";
//                                        startActivity(new Intent(CallsActivity.this, MainActivity.class)
//                                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//                        }else if (u.equals("sender")) {
//                            reference.child("Calls").addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    if (snapshot.exists() && !snapshot.child(senderid).hasChild("Calling") && u.equals("Calling")) {
//                                        mediaPlayer.stop();
//                                        checker = "";
//                                        startActivity(new Intent(CallsActivity.this, MainActivity.class)
//                                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//                        }
//                    }
//                },
//                3000
//        );










//        reference.child("Calls").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child(senderid).child("Ringing").hasChild("picked") || snapshot.child(receiverID).child("Ringing").hasChild("picked")){
//                    reference.child("Calls").child(senderid).child("Ringing").addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (snapshot.hasChild("ringing")){
//                                u="receiver";
//                                reference.child("Calls").child(senderid)
//                                        .child("Ringing")
//                                        .addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                        Toast.makeText(MainActivity.this, "received ok", Toast.LENGTH_SHORT).show();
//                                                if (!snapshot.hasChild("ringing") && u.equals("receiver")){
////                            calledBy = snapshot.child("ringing").getValue().toString();
////                            Toast.makeText(MainActivity.this, "received", Toast.LENGTH_SHORT).show();
////                            startActivity(new Intent(MainActivity.this, CallsActivity.class)
////                                    .putExtra("userID",calledBy));
//                                                    u="";
//                                                    checker="";
//                                                    startActivity(new Intent(CallsActivity.this, MainActivity.class)
//                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
////                            finish();
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                            }
//                                        });
//
//                            }else{
//                                u="sender";
//                                reference.child("Calls").child(senderid)
//                                        .child("Calling")
//                                        .addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                if (!snapshot.hasChild("calling") && u.equals("sender")){
//                                                    u="";
//                                                    checker="";
//                                                    startActivity(new Intent(CallsActivity.this, MainActivity.class)
//                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
////                            finish();
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                            }
//                                        });
//
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });



//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    private String getCurrentDate(){
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        //        HashMap<String, Object> offlineStateMap = new HashMap<>();
//        offlineStateMap.put("time", saveCurrentTime);
//        offlineStateMap.put("date", saveCurrentDate);

        return saveCurrentTime+" "+saveCurrentDate;
    }

    private void cancleCallingUser() {
        //from sender side
        reference.child("Calls").child(senderid)
                .child("Calling")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.hasChild("calling")) {
//                            callingId = snapshot.child("calling").getValue().toString();
////////////////////////////////////////////////////////////////////////////////////////////
                            reference.child("Calls").child(receiverID)
                                    .child("Ringing")
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                reference.child("Calls").child(senderid)
                                                        .child("Calling")
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                startActivity(new Intent(CallsActivity.this, MainActivity.class));
                                                                checker="";
                                                                startActivity(new Intent(CallsActivity.this, MainActivity.class)
                                                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                                                                finish();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        //from receiver side
        reference.child("Calls").child(senderid)
                .child("Ringing")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.hasChild("ringing")) {
//                            ringingId = snapshot.child("ringing").getValue().toString();
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            reference.child("Calls").child(receiverID)
                                    .child("Calling")
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                reference.child("Calls").child(senderid)
                                                        .child("Ringing")
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                startActivity(new Intent(CallsActivity.this, MainActivity.class));
                                                                checker="";
                                                                startActivity(new Intent(CallsActivity.this, MainActivity.class)
                                                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                                                                finish();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        } else {
//                            startActivity(new Intent(CallsActivity.this, MainActivity.class));
                            checker="";
                            startActivity(new Intent(CallsActivity.this, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
    }
}