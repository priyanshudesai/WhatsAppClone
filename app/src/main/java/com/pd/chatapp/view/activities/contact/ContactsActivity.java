package com.pd.chatapp.view.activities.contact;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pd.chatapp.Dao.ContactsDao;
import com.pd.chatapp.Database.ContactsDatabase;
import com.pd.chatapp.R;
import com.pd.chatapp.Repository.ContactsRepository;
import com.pd.chatapp.ViewModel.ContactsViewModel;
import com.pd.chatapp.adapter.ContactsAdapter;
import com.pd.chatapp.databinding.ActivityContactsBinding;
import com.pd.chatapp.model.user.Users;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private static final String TAG = "ContactsActivity";
    private ActivityContactsBinding binding;
    private List<Users> list = new ArrayList<>();
    private ContactsAdapter adapter;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    public static final int REQUEST_READ_CONTACTS = 79;
    private ListView contactlist;
    private ArrayList mobileArray;



    private ContactsViewModel contactsViewModel;
    //private static final String URL_DATA = "http://codingwithjks.tech/";
    //private RecyclerView recyclerView;
    //private List<Users> actorList;
    private ContactsRepository contactsRepository;
    //private ContactsAdapter contactsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding= DataBindingUtil.setContentView(this,R.layout.activity_contacts);
//
//        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        firestore = FirebaseFirestore.getInstance();
//
//        if (firebaseUser!=null){
//            getContactList();
//        }
//        binding.backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });

        binding= DataBindingUtil.setContentView(this,R.layout.activity_contacts);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        if (firebaseUser!=null){
            getContactFromPhone(); // If they using this app
            // getContactList();
        }

        if (mobileArray!=null) {
            getContactList();

        }

        contactsRepository = new ContactsRepository(getApplication());

        contactsViewModel = new ViewModelProvider(this).get(ContactsViewModel.class);
        adapter = new ContactsAdapter(list, this);
        contactsViewModel.getAllContact().observe(this, new Observer<List<Users>>() {
            @Override
            public void onChanged(List<Users> contactList) {
                adapter.getAllContacts(contactList);
                binding.recyclerView.setAdapter(adapter);
                Log.d("main", "onChanged: "+contactList);
            }
        });

    }

//    private void getContactList() {
//        firestore.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//                for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots){
//                    String userID = snapshots.getString("userID");
//                    String userName = snapshots.getString("userName");
//                    String imageUrl = snapshots.getString("imageProfile");
//                    String desc = snapshots.getString("bio");
//
//                    Users user = new Users();
//                    user.setUserID(userID);
//                    user.setBio(desc);
//                    user.setUserName(userName);
//                    user.setImageProfile(imageUrl);
//
//
//                    if (userID != null && !userID.equals(firebaseUser.getUid())) {
//                        list.add(user);
//                    }
//                }
//                adapter = new ContactsAdapter(list,ContactsActivity.this);
//                binding.recyclerView.setAdapter(adapter);
//            }
//
//        });
//    }

    private void getContactFromPhone() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            mobileArray = getAllPhoneContacts();
        } else {
            requestPermission();
        }

    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mobileArray = getAllPhoneContacts();
                } else {
                    finish();
                }
                return;
            }
        }
    }
    private ArrayList getAllPhoneContacts() {
        ArrayList<String> phoneList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
//                String name = cur.getString(cur.getColumnIndex(
//                        ContactsContract.Contacts.DISPLAY_NAME));
//                nameList.add(name);

                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneList.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return phoneList;
    }

    private void getContactList() {

        firestore.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots){
                    String userID = snapshots.getString("userID");
                    String userName = snapshots.getString("userName");
                    String imageUrl = snapshots.getString("imageProfile");
                    String desc = snapshots.getString("bio");
                    String phone = snapshots.getString("userPhone");

                    Users user = new Users(userID,
                            userName,
                            phone,
                            imageUrl,
                            "", "", "", "", "",
                            desc);
//                    user.setUserID(userID);
//                    user.setBio(desc);
//                    user.setUserName(userName);
//                    user.setImageProfile(imageUrl);
//                    user.setUserPhone(phone);

                    if (userID != null && !userID.equals(firebaseUser.getUid())) {
                        if (mobileArray.contains(user.getUserPhone()) || mobileArray.contains(user.getUserPhone().substring(3))){
                            list.add(user);
                        }
                    }
                }


//                for (Users user : list){
//                    if (mobileArray.contains(user.getUserPhone())){
//                        Log.d(TAG, "getContactList: true "+user.getUserPhone() );
//                    } else {
//                        Log.d(TAG, "getContactList: false"+user.getUserPhone());
//                    }
//                }


//                Toast.makeText(ContactsActivity.this, "Delete", Toast.LENGTH_SHORT).show();
//                contactsRepository.deleteAll();
                contactsRepository.insert(list);
//                binding.recyclerView.setAdapter(adapter);
            }

        });



    }

}
