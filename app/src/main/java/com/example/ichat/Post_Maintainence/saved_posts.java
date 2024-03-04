package com.example.ichat.Post_Maintainence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ichat.Post_Details.postAdapter;
import com.example.ichat.Post_Details.postModel;
import com.example.ichat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class saved_posts extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<postModel> list;
    postAdapter postAdapter;
    DatabaseReference reference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    RecyclerView.LayoutManager layoutManager;
    ProgressBar progressBar;
    TextView is_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_posts);
        recyclerView = findViewById(R.id.saved_posts_recyclerview);
        progressBar = findViewById(R.id.saved_progressbar);
        is_empty = findViewById(R.id.saved_list_empty);
        list = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        postAdapter = new postAdapter(this, list, getParent());
        recyclerView.setAdapter(postAdapter);

        if (list != null) {
            reference.child("PersonalSaved").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        progressBar.setVisibility(View.GONE);
                        is_empty.setVisibility(View.VISIBLE);
                    } else {
                        for (DataSnapshot d : snapshot.getChildren()) {
                            progressBar.setVisibility(View.GONE);
                            is_empty.setVisibility(View.GONE);
                            postModel model = d.getValue(postModel.class);
                            list.add(model);
                        }
                        postAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}