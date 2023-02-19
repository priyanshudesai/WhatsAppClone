package com.pd.chatapp.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pd.chatapp.Notifications.Client;
import com.pd.chatapp.Notifications.Data;
import com.pd.chatapp.Notifications.MyResponse;
import com.pd.chatapp.Notifications.Sender;
import com.pd.chatapp.Notifications.Token;
import com.pd.chatapp.R;
import com.pd.chatapp.interfaces.APIService;
import com.pd.chatapp.interfaces.OnReadChatCallBack;
import com.pd.chatapp.model.chat.Chats;
import com.pd.chatapp.view.activities.chats.ChatsActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatService {
    private Context context;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private String receiverID;

    APIService apiService;
    private FirebaseFirestore firestore;
    String userName;


    boolean notify = false;

    public ChatService(Context context, String receiverID) {
        this.context = context;
        this.receiverID = receiverID;
    }

    public void readChatData(final OnReadChatCallBack onCallBack){
        reference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Chats> list = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chats chats = snapshot.getValue(Chats.class);
                    try {
                        if (chats != null) {
                            if (chats.getSender().equals(firebaseUser.getUid()) && chats.getSendBoolean().equals("1")) {
                                if (chats.getSender().equals(firebaseUser.getUid()) && chats.getReceiver().equals(receiverID)
                                        || chats.getReceiver().equals(firebaseUser.getUid()) && chats.getSender().equals(receiverID)) {
                                    list.add(chats);

                                }
                            }
                            if (chats.getReceiver().equals(firebaseUser.getUid()) && chats.getRecBoolean().equals("1")) {
                                if (chats.getSender().equals(firebaseUser.getUid()) && chats.getReceiver().equals(receiverID)
                                        || chats.getReceiver().equals(firebaseUser.getUid()) && chats.getSender().equals(receiverID)) {
                                    list.add(chats);

                                }
                            }
                        }
                    }catch (Exception e){e.printStackTrace();}
                }
                onCallBack.onReadSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onCallBack.onReadFailed();
            }
        });
    }




    public void sendTextMsg(String text){

        notify=true;

        //reference.child("Chats").push();
//        reference.setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d("Send", "onSuccess: ");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d("Send", "onFailure: "+e.getMessage());
//            }
//        });


//        Chats chats = new Chats(
//                getCurrentDate(),
//                text,
//                "",
//                "TEXT",
//                firebaseUser.getUid(),
//                receiverID,
//                );

//        DatabaseReference userMessageKeyRef = reference.child("Chats").push();
        DatabaseReference userMessageKeyRef = reference.child("Chats").push();

        String messagePushID = userMessageKeyRef.getKey();

        Map messageTextBody = new HashMap();
        messageTextBody.put("dateTime", getCurrentDate());
        messageTextBody.put("messageId", messagePushID);
        messageTextBody.put("receiver", receiverID);
        messageTextBody.put("sender", firebaseUser.getUid());
        messageTextBody.put("textMessage", text);
        messageTextBody.put("type", "TEXT");
        messageTextBody.put("url", "");
        messageTextBody.put("sendBoolean", "1");
        messageTextBody.put("recBoolean", "1");
        messageTextBody.put("isseen", false);

        Map messageBodyDetails = new HashMap();
        messageBodyDetails.put( "Chats" + "/" + messagePushID, messageTextBody);

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


        //Add to ChatList
        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
        chatRef1.child("chatid").setValue(receiverID);

        //
        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
        chatRef2.child("chatid").setValue(firebaseUser.getUid());




        final String msg=text;

        reference1 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Users user = dataSnapshot.getValue(Users.class);
                if (notify) {
                    sendNotifiaction(receiverID, msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotifiaction(final String receiver, final String message1){
        final String[] username = new String[1];
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                username[0] = documentSnapshot.getString("userName");
//                String userPhone = documentSnapshot.getString("userPhone");
//                String imageProfile = documentSnapshot.getString("imageProfile");
//                String userAbout = documentSnapshot.getString("bio");

//                binding.tvUsername.setText(userName);
//                binding.tvAbout.setText(userAbout);
//                binding.tvPhone.setText(userPhone);
//                Glide.with(ProfileActivity.this).load(imageProfile).into(binding.imageProfile);



                apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

                DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
                Query query = tokens.orderByKey().equalTo(receiver);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Token token = snapshot.getValue(Token.class);
                            Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username[0] +": "+message1, "New Message",
                                    receiverID);

                            Sender sender = new Sender(data, token.getToken());

                            apiService.sendNotification(sender)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.code() != 200){
//                                        if (response.body().success != 1){
                                                Toast.makeText(context, "Failed!"+response, Toast.LENGTH_SHORT).show();
//                                        }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });



    }

    public void sendImage(String imageUrl){

//        Chats chats = new Chats(
//                getCurrentDate(),
//                "",
//                imageUrl,
//                "IMAGE",
//                firebaseUser.getUid(),
//                receiverID,
//                "hhh", "1", "1");

//        reference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d("Send", "onSuccess: ");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d("Send", "onFailure: "+e.getMessage());
//            }
//        });

        DatabaseReference userMessageKeyRef = reference.child("Chats").push();

        String messagePushID = userMessageKeyRef.getKey();

        Map messageTextBody = new HashMap();
        messageTextBody.put("dateTime", getCurrentDate());
        messageTextBody.put("textMessage", "");
        messageTextBody.put("url", imageUrl);
        messageTextBody.put("type", "IMAGE");
        messageTextBody.put("sender", firebaseUser.getUid());
        messageTextBody.put("receiver", receiverID);
        messageTextBody.put("messageId", messagePushID);
        messageTextBody.put("sendBoolean", "1");
        messageTextBody.put("recBoolean", "1");
        messageTextBody.put("isseen", false);

        Map messageBodyDetails = new HashMap();
        messageBodyDetails.put( "Chats" + "/" + messagePushID, messageTextBody);

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



        //Add to ChatList
        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
        chatRef1.child("chatid").setValue(receiverID);

        //
        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
        chatRef2.child("chatid").setValue(firebaseUser.getUid());
    }

    public String getCurrentDate(){
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String today = formatter.format(date);

        Calendar currentDateTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        String currentTime = df.format(currentDateTime.getTime());

        return today+", "+currentTime;
    }

    public void sendVoice(String audioPath){
        final Uri uriAudio = Uri.fromFile(new File(audioPath));
        final StorageReference audioRef = FirebaseStorage.getInstance().getReference().child("Chats/Voice/" + System.currentTimeMillis());
        audioRef.putFile(uriAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot audioSnapshot) {
                Task<Uri> urlTask = audioSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();
                String voiceUrl = String.valueOf(downloadUrl);

//                Chats chats = new Chats(
//                        getCurrentDate(),
//                        "",
//                        voiceUrl,
//                        "VOICE",
//                        firebaseUser.getUid(),
//                        receiverID,
//                        "hhhjj", "1", "1");

//                reference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d("Send", "onSuccess: ");
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d("Send", "onFailure: "+e.getMessage());
//                    }
//                });

                DatabaseReference userMessageKeyRef = reference.child("Chats").push();

                String messagePushID = userMessageKeyRef.getKey();

                Map messageTextBody = new HashMap();
                messageTextBody.put("dateTime", getCurrentDate());
                messageTextBody.put("textMessage", "");
                messageTextBody.put("url", voiceUrl);
                messageTextBody.put("type", "VOICE");
                messageTextBody.put("sender", firebaseUser.getUid());
                messageTextBody.put("receiver", receiverID);
                messageTextBody.put("messageId", messagePushID);
                messageTextBody.put("sendBoolean", "1");
                messageTextBody.put("recBoolean", "1");
                messageTextBody.put("isseen", false);

                Map messageBodyDetails = new HashMap();
                messageBodyDetails.put( "Chats" + "/" + messagePushID, messageTextBody);

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



                //Add to ChatList
                DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
                chatRef1.child("chatid").setValue(receiverID);

                //
                DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
                chatRef2.child("chatid").setValue(firebaseUser.getUid());
            }
        });
    }

}
