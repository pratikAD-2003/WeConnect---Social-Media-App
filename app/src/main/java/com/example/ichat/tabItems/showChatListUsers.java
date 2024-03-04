package com.example.ichat.tabItems;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ichat.MainAdapter;
import com.example.ichat.MainScreenModel;
import com.example.ichat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class showChatListUsers extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText search;
    FirebaseFirestore db;
    MainAdapter adapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    ArrayList<MainScreenModel> list = new ArrayList<>();
    LinearLayoutManager manager;
    DatabaseReference reference;
    ProgressBar progressBar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_chat_list_users);
        search = findViewById(R.id.chatUserSearch);
        recyclerView = findViewById(R.id.chat_list_recyclerview);
        progressBar = findViewById(R.id.chatList_progressbar);
        textView = findViewById(R.id.chatList_empty_indicator);
        recyclerView.setHasFixedSize(true);
        db = FirebaseFirestore.getInstance();

        reference = FirebaseDatabase.getInstance().getReference();

        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new MainAdapter(this, list);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), manager.getOrientation());
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.custom_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        list.clear();

        if (list != null) {
            db.collection("OnScreenUser").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> lists = queryDocumentSnapshots.getDocuments();
                    if (lists.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                    }
                    for (DocumentSnapshot ds : lists) {
                        String data = ds.getString("onScreenID");
                        if (data.contains(uid)) {
                            progressBar.setVisibility(View.GONE);
                            textView.setVisibility(View.GONE);
                            MainScreenModel model = ds.toObject(MainScreenModel.class);
                            list.add(model);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            textView.setVisibility(View.VISIBLE);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }

        search.addTextChangedListener(new TextWatcher() {
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

    void filter(String text) {
        List<MainScreenModel> temp = new ArrayList();
        for (MainScreenModel d : list) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getUsername().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }
}