package com.example.ichat.tabItems;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ichat.ProfileModel;
import com.example.ichat.R;
import com.example.ichat.SearchUserAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link chatFrage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class chatFrage extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    SearchUserAdapter adapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    ArrayList<ProfileModel> list = new ArrayList<>();
    FirebaseFirestore db;
    LinearLayoutManager manager;
    EditText searchUsers;

    public chatFrage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment chatFrage.
     */
    // TODO: Rename and change types and number of parameters
    public static chatFrage newInstance(String param1, String param2) {
        chatFrage fragment = new chatFrage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_frage, container, false);
        recyclerView = view.findViewById(R.id.recycler_Main);
        searchUsers = view.findViewById(R.id.usersOnAppSearch);
        adapter = new SearchUserAdapter(getContext(), list);
        recyclerView.setHasFixedSize(true);

        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), manager.getOrientation());
        dividerItemDecoration.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);


        db = FirebaseFirestore.getInstance();

        if (list != null) {
            db.collection("UserList").orderBy("username").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> lists = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot ds : lists) {
                        ProfileModel model = ds.toObject(ProfileModel.class);
                        list.add(model);
                    }
                    adapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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

        return view;
    }

    void filter(String text){
        List<ProfileModel> temp = new ArrayList();
        for(ProfileModel d: list){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getUsername().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}