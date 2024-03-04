package com.example.ichat.Post_Maintainence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ichat.ProfileModel;
import com.example.ichat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class followingList extends AppCompatActivity {
    followingAdapter adapter;
    ArrayList<followingModel> list;
    RecyclerView recyclerView;
    DatabaseReference reference;
    String receiverUid = "";
    LinearLayoutManager layoutManager;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    EditText searchUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_list);
        recyclerView = findViewById(R.id.following_followed_recyclerview);
        searchUsers = findViewById(R.id.searchFollowingUsers);

        reference = FirebaseDatabase.getInstance().getReference();
        receiverUid = getIntent().getStringExtra("RECEIVER_ID");
        list = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new followingAdapter(this, list,followingList.this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.custom_divider_2));
        recyclerView.addItemDecoration(dividerItemDecoration);

        if (list != null) {
            reference.child("Following").child(receiverUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot d : snapshot.getChildren()) {
                        followingModel followingModel = d.getValue(followingModel.class);
                        list.add(followingModel);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        searchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    void filter(String text){
        List<followingModel> temp = new ArrayList();
        for(followingModel d: list){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getUsername().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }
}