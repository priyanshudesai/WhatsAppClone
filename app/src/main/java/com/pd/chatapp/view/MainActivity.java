package com.pd.chatapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.pd.chatapp.R;
import com.pd.chatapp.databinding.ActivityMainBinding;
import com.pd.chatapp.menu.CallsFragment;
import com.pd.chatapp.menu.CameraFragment;
import com.pd.chatapp.menu.ChatsFragment;
import com.pd.chatapp.menu.StatusFragment;
import com.pd.chatapp.view.activities.chats.CallsActivity;
import com.pd.chatapp.view.activities.chats.ChatsActivity;
import com.pd.chatapp.view.activities.contact.ContactsActivity;
import com.pd.chatapp.view.activities.settings.SettingsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    FirebaseDatabase db;
    private String calledBy="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setUpWithViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        View tab1 = LayoutInflater.from(this).inflate(R.layout.custom_camera_tab, null);
        try {
            binding.tabLayout.getTabAt(0).setCustomView(tab1);
        }catch (Exception e){e.printStackTrace();}

        binding.viewPager.setCurrentItem(1); // Defualt display CHats tab

        setSupportActionBar(binding.toolbar);

        db = FirebaseDatabase.getInstance();


        mAuth = FirebaseAuth.getInstance();




        binding.fabAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ContactsActivity.class));
            }
        });

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeFabICon(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        updateUserStatus("online");
//        manageConnections();
    }

    private void setUpWithViewPager(ViewPager viewPager) {
        MainActivity.SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraFragment(), "");
        adapter.addFragment(new ChatsFragment(), "Chats");
        adapter.addFragment(new StatusFragment(), "Status");
        adapter.addFragment(new CallsFragment(), "Calls");
        viewPager.setAdapter(adapter);
    }

    //Add this code
    private static class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_search:
                Toast.makeText(MainActivity.this, "Action Search", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_new_group:
                Toast.makeText(MainActivity.this, "Action New Group", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_new_broadcast:
                Toast.makeText(MainActivity.this, "Action New Broadcast", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_wa_web:
                Toast.makeText(MainActivity.this, "Action Web", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_starred_message:
                Toast.makeText(MainActivity.this, "Action Starred Message", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUserStatus(String status) {
        final FirebaseUser currentUser = mAuth.getCurrentUser();

//        RootRef.child("userState").child(currentUser.getUid())
//                .updateChildren(onlineStateMap);

        if (status.equals("online")) {
            final DatabaseReference lastConnected = db.getReference().child("userState").child(currentUser.getUid());
            final DatabaseReference infoConnected = db.getReference(".info/connected");

            infoConnected.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);

                    String saveCurrentTime, saveCurrentDate;

                    Calendar calendar = Calendar.getInstance();

                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                    saveCurrentDate = currentDate.format(calendar.getTime());

                    SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
                    saveCurrentTime = currentTime.format(calendar.getTime());

                    HashMap<String, Object> onlineStateMap = new HashMap<>();
                    onlineStateMap.put("time", saveCurrentTime);
                    onlineStateMap.put("date", saveCurrentDate);
                    onlineStateMap.put("state", "online");

                    HashMap<String, Object> offlineStateMap = new HashMap<>();
                    offlineStateMap.put("time", saveCurrentTime);
                    offlineStateMap.put("date", saveCurrentDate);
                    offlineStateMap.put("state", "offline");

                    if (connected) {
                        DatabaseReference con = RootRef.child("userState").child(currentUser.getUid());
                        con.setValue(onlineStateMap);
                        con.onDisconnect().setValue(offlineStateMap);
                        lastConnected.onDisconnect().setValue(offlineStateMap);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Error", error.getMessage());
                }
            });

        }else if (status.equals("offline")){
            final DatabaseReference lastConnected = db.getReference().child("userState").child(currentUser.getUid());
            final DatabaseReference infoConnected = db.getReference(".info/connected");

            infoConnected.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    String saveCurrentTime, saveCurrentDate;

                    Calendar calendar = Calendar.getInstance();

                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                    saveCurrentDate = currentDate.format(calendar.getTime());

                    SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
                    saveCurrentTime = currentTime.format(calendar.getTime());

                    HashMap<String, Object> offlineStateMap = new HashMap<>();
                    offlineStateMap.put("time", saveCurrentTime);
                    offlineStateMap.put("date", saveCurrentDate);
                    offlineStateMap.put("state", "offline");
                    if (connected) {
                        DatabaseReference con = RootRef.child("userState").child(currentUser.getUid());
                        con.setValue(offlineStateMap);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Error", error.getMessage());
                }
            });

        }
    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser!=null){
//            updateUserStatus("online");
//        }
//    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser!=null){
//            updateUserStatus("offline");
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateUserStatus("offline");
    }

    private void changeFabICon(final int index) {
        binding.fabAction.hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (index) {
                    case 0:binding.fabAction.hide(); break;
                    case 1:
                        binding.fabAction.show();
                        binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_chat_black_24dp));
                        binding.fabAction.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(MainActivity.this, ContactsActivity.class));
                            }
                        });

                        break;
                    case 2:
                        binding.fabAction.show();
                        binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_camera_alt_black_24dp));
                        break;
                    case 3:
                        binding.fabAction.show();
                        binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_call_black_24dp));
                        break;
                }
//                binding.fabAction.show();
            }
        }, 400);

    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//
//    }

    //    private void manageConnections(){
//
//        final FirebaseUser currentUser = mAuth.getCurrentUser();
//
//        final DatabaseReference connectRefrence=db.getReference().child("connections");
//        final DatabaseReference lastConnected=db.getReference().child("lastConnected").child(currentUser.getUid());
//        final DatabaseReference infoConnected=db.getReference(".info/connected");
//
//        infoConnected.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                boolean connected = snapshot.getValue(Boolean.class);
//
//                if (connected){
//                    DatabaseReference con = connectRefrence.child(currentUser.getUid());
//                    con.setValue(ServerValue.TIMESTAMP);
//                    con.onDisconnect().setValue(false);
//                    lastConnected.onDisconnect().setValue(ServerValue.TIMESTAMP);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.d("Error",error.getMessage());
//            }
//        });
//
//    }

    @Override
    protected void onStart() {
        super.onStart();
        checkForReceivingCall();
    }

    private void checkForReceivingCall() {
//        Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_SHORT).show();
        RootRef.child("Calls").child(firebaseUser.getUid())
                .child("Ringing")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Toast.makeText(MainActivity.this, "received ok", Toast.LENGTH_SHORT).show();
                        if (snapshot.hasChild("ringing")){
                            calledBy = snapshot.child("ringing").getValue().toString();
//                            Toast.makeText(MainActivity.this, "received", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, CallsActivity.class)
                                    .putExtra("userID",calledBy));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
