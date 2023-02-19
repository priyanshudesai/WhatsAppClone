package com.pd.chatapp.view.activities.video;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.adapters.VideoViewBindingAdapter;

import android.Manifest;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.pd.chatapp.R;
import com.pd.chatapp.databinding.ActivityVideoChatBinding;
import com.pd.chatapp.view.MainActivity;
import com.pd.chatapp.view.activities.chats.CallsActivity;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoChatActivity extends AppCompatActivity
        implements Session.SessionListener, PublisherKit.PublisherListener {

    private static final String TAG = "VideoChatActivity";
    private ActivityVideoChatBinding binding;
    private static String API_Key = "46932914";
    private static String SESSION_ID = "1_MX40NjkzMjkxNH5-MTYwODU0MDk5MjY2OX5WRHF5U2s4VERuQ2pkdXJiNnVrallrZDd-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00NjkzMjkxNCZzaWc9ZTE1YjE2MWM4MThmNTFlZDdhYWM2NmM1YTIxZWNiYzE2NGM0N2YxMjpzZXNzaW9uX2lkPTFfTVg0ME5qa3pNamt4Tkg1LU1UWXdPRFUwTURrNU1qWTJPWDVXUkhGNVUyczRWRVJ1UTJwa2RYSmlOblZyYWxsclpEZC1mZyZjcmVhdGVfdGltZT0xNjA4NTQxMDA5Jm5vbmNlPTAuODgzNTcxMTQyODg3MjgyOCZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNjExMTMzMDA5JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
    private static final int RC_VIDEO_APP_PERM = 124;
    private String userId = "";
    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;
    private String otherid="";

    private DatabaseReference usersReference;
    private String u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_video_chat);


        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersReference = FirebaseDatabase.getInstance().getReference();

        otherid = getIntent().getStringExtra("id");

        binding.closeVideoChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usersReference.child("Calls").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(userId).hasChild("Ringing")){
                            usersReference.child("Calls").child(userId).child("Ringing").removeValue();
                            usersReference.child("Calls").child(otherid).child("Calling").removeValue();

                            if (mPublisher != null){
                                mPublisher.destroy();
                            }
                            if (mSubscriber != null){
                                mSubscriber.destroy();
                            }
                            startActivity(new Intent(VideoChatActivity.this, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                            finish();
                        }

                        if (snapshot.child(userId).hasChild("Calling")){
                            usersReference.child("Calls").child(userId).child("Calling").removeValue();
                            usersReference.child("Calls").child(otherid).child("Ringing").removeValue();
                            if (mPublisher != null){
                                mPublisher.destroy();
                            }
                            if (mSubscriber != null){
                                mSubscriber.destroy();
                            }
                            startActivity(new Intent(VideoChatActivity.this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                            finish();
                        }else {
                            if (mPublisher != null){
                                mPublisher.destroy();
                            }
                            if (mSubscriber != null){
                                mSubscriber.destroy();
                            }
                            startActivity(new Intent(VideoChatActivity.this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        requestPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, VideoChatActivity.this);

    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermission(){
        String[] perms = {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

        if (EasyPermissions.hasPermissions(this, perms)){
            //1.initialize and connect to the session
            mSession = new Session.Builder(this, API_Key, SESSION_ID).build();
            mSession.setSessionListener(VideoChatActivity.this);
            mSession.connect(TOKEN);
        }else {
            EasyPermissions.requestPermissions(this, "Hey, this app needs the Mic and Camera, Please allow.", RC_VIDEO_APP_PERM, perms);
        }
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

    //2. Publishing a stream to the session
    @Override
    public void onConnected(Session session) {
        Log.i(TAG, "Session Connected");

        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(VideoChatActivity.this);

        binding.publisherContainer.addView(mPublisher.getView());

        if (mPublisher.getView() instanceof GLSurfaceView){
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(TAG,"Stream Disconnected");
    }

    //3. Subscribing to the streams
    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(TAG,"Stream Received");

        if (mSubscriber == null){
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
            binding.subscriberContainer.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(TAG,"Stream Dropped");

        if (mSubscriber != null){
            mSubscriber = null;
            binding.subscriberContainer.removeAllViews();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.i(TAG,"Stream Error");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onStart() {
        super.onStart();


        usersReference.child("Calls").child(userId).child("Ringing").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("ringing")){
                    u="receiver";
                }else{
                    u="sender";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        usersReference.child("Calls").child(userId)
                .child("Ringing")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Toast.makeText(MainActivity.this, "received ok", Toast.LENGTH_SHORT).show();
                        if (!snapshot.hasChild("ringing") && u.equals("receiver")){
//                            calledBy = snapshot.child("ringing").getValue().toString();
//                            Toast.makeText(MainActivity.this, "received", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(MainActivity.this, CallsActivity.class)
//                                    .putExtra("userID",calledBy));
                            startActivity(new Intent(VideoChatActivity.this, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        usersReference.child("Calls").child(userId)
                .child("Calling")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.hasChild("calling") && u.equals("sender")){
                            startActivity(new Intent(VideoChatActivity.this, MainActivity.class)
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
    public void onBackPressed() {

    }
}