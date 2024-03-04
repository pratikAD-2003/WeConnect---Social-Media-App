package com.example.ichat.UsersActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ichat.ProfileModel;
import com.example.ichat.R;
import com.example.ichat.SearchUserAdapter;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class search_users_onApp extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<ActivityModel> list;
    LinearLayoutManager layoutManager;
    ActivityAdapter adapter;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    DatabaseReference reference;
    ProgressBar progressBar;
    TextView empty_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users_on_app);
        recyclerView = findViewById(R.id.recyclerOnApp);
        progressBar = findViewById(R.id.users_activity_bar);
        empty_activity = findViewById(R.id.activities_text_empty);
        list = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ActivityAdapter(this, search_users_onApp.this, list);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.custom_divider_2));
        recyclerView.addItemDecoration(dividerItemDecoration);


        reference = FirebaseDatabase.getInstance().getReference();

        if (list != null) {
            reference.child("UsersActivities").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        progressBar.setVisibility(View.GONE);
                        empty_activity.setVisibility(View.VISIBLE);
                    } else {
                        for (DataSnapshot d : snapshot.getChildren()) {
                            progressBar.setVisibility(View.GONE);
                            empty_activity.setVisibility(View.GONE);
                            ActivityModel model = d.getValue(ActivityModel.class);
                            list.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}