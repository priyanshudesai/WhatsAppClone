package com.pd.chatapp.menu;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pd.chatapp.R;
import com.pd.chatapp.adapter.CallListAdapter;
import com.pd.chatapp.databinding.FragmentCallsBinding;
import com.pd.chatapp.databinding.FragmentChatsBinding;
import com.pd.chatapp.model.CallList;
import com.pd.chatapp.model.Chatlist;
import com.pd.chatapp.view.activities.chats.CallsActivity;
import com.pd.chatapp.view.activities.chats.ChatsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class CallsFragment extends Fragment {

    private static final String TAG = "CallsFragment";

    public CallsFragment() {
        // Required empty public constructor
    }


    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private FirebaseFirestore firestore;
    private Handler handler = new Handler();

    private List<CallList> list;

    private FragmentCallsBinding binding;

    private ArrayList<String> allUserID;

    private CallListAdapter adapter;


    String calledBy="";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view =  inflater.inflate(R.layout.fragment_calls, container, false);
//
//        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        List<CallList> lists = new ArrayList<>();
//        //recyclerView.setAdapter(new CallListAdapter(lists,getContext()));
//        return view;


        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_calls, container, false);

        list = new ArrayList<>();
        allUserID = new ArrayList<>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CallListAdapter(list,getContext());
        binding.recyclerView.setAdapter(adapter);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();


        if (firebaseUser!=null) {
            getCallList();
        }else{
            Toast.makeText(getContext(), "hhh", Toast.LENGTH_SHORT).show();
        }


        return binding.getRoot();
    }

    private void getCallList() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        list.clear();
        allUserID.clear();
        reference.child("CallList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String sender = Objects.requireNonNull(snapshot.child("sender").getValue()).toString();
                    String rec = Objects.requireNonNull(snapshot.child("receiver").getValue()).toString();
//                    Log.d(TAG, "onDataChange: userid "+userID);

                    if (sender.equals(firebaseUser.getUid())){
                        allUserID.add(snapshot.child("callId").getValue().toString());
                    }else if(rec.equals(firebaseUser.getUid())){
                        allUserID.add(snapshot.child("callId").getValue().toString());
                    }

                    binding.progressCircular.setVisibility(View.GONE);
                    //allUserID.add(userID);
                }
                getUserInfo();
                if (allUserID.size()==0){
                    binding.progressCircular.setVisibility(View.GONE);
                    binding.lnInvite.setVisibility(View.VISIBLE);
                    binding.recyclerView.setVisibility(View.GONE);
                }else {
                    binding.progressCircular.setVisibility(View.GONE);
                    binding.lnInvite.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void getUserInfo(){

//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                for (final String userID : allUserID) {
//
//                    reference.child("CallList").addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
////                            call.setUrlProfile(dataSnapshot.child("callId").getValue().toString());
////                            call.setSender(dataSnapshot.child("sender").getValue().toString());
////                            call.setReceiver(dataSnapshot.child("receiver").getValue().toString());
////                            call.setDateTime(dataSnapshot.child("dateTime").getValue().toString());
////                            call.setCallType(dataSnapshot.child("callType").getValue().toString());
////
//                                    dataSnapshot.child("callId").getValue().toString(),
//                                    dataSnapshot.child("sender").getValue().toString(),
//                                    dataSnapshot.child("receiver").getValue().toString(),
//                                    userName,
//                                    // documentSnapshot.getString("userName"),
//                                    dataSnapshot.child("dateTime").getValue().toString(),
//                                    //documentSnapshot.getString("urlProfile"),
//                                    "https://firebasestorage.googleapis.com/v0/b/chatapp-ed8d7.appspot.com/o/ImagesProfile%2F1601112328575.jpg?alt=media&token=75445fb2-5440-40df-a44b-54862d9ce769",
//                                    dataSnapshot.child("callType").getValue().toString()
//
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//
//                    });
//
//                    list.add(call);
//
//
//
//                    if (adapter != null) {
//                        adapter.notifyItemInserted(0);
//                        adapter.notifyDataSetChanged();
//
//                        Log.d(TAG, "onSuccess: adapter " + adapter.getItemCount());
//                    }
//
//
//                }
//            }
//        });

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (String userID : allUserID){

                    reference.child("CallList").child(userID)
                            .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try {
                                CallList call = new CallList(
                                        snapshot.child("callId").getValue().toString(),
                                        snapshot.child("sender").getValue().toString(),
                                        snapshot.child("receiver").getValue().toString(),
                                        snapshot.child("userName").getValue().toString(),
                                        snapshot.child("dateTime").getValue().toString(),
                                        snapshot.child("urlProfile").getValue().toString(),
                                        snapshot.child("callType").getValue().toString()

                                );
                                list.add(0,call);
                            }catch (Exception e){
                                Log.d(TAG, "onSuccess: "+e.getMessage());
                            }
                            if (adapter!=null){

                                adapter.notifyItemInserted(0);
                                adapter.notifyDataSetChanged();

                                Log.d(TAG, "onSuccess: adapter "+adapter.getItemCount());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });




//                    firestore.collection("Users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                            Log.d(TAG, "onSuccess: ddd"+documentSnapshot.getString("userName"));
//                            try {
//                                CallList call = new CallList(
//                                        documentSnapshot.getString("userID"),
//                                        documentSnapshot.getString("userName"),
//                                        "this is description..",
//                                        "",
//                                        documentSnapshot.getString("imageProfile")
//                                );
//                                list.add(call);
//                            }catch (Exception e){
//                                Log.d(TAG, "onSuccess: "+e.getMessage());
//                            }
//                            if (adapter!=null){
//                                adapter.notifyItemInserted(0);
//                                adapter.notifyDataSetChanged();
//
//                                Log.d(TAG, "onSuccess: adapter "+adapter.getItemCount());
//                            }
//                        }
//
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d(TAG, "onFailure: Error L"+e.getMessage());
//                        }
//                    });
                }
            }
        });
    }
}
