package com.example.ichat.Post_Maintainence;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ichat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link comment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class comment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    EditText comment;
    ImageButton sendComment;
    CommentAdapter adapter;
    ArrayList<CommentModel> list;
    String typedComment = " ", userName = " ", profile = " ", postKey = " ", pushId = " ", key = " ";
    LinearLayoutManager layoutManager;
    DatabaseReference reference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    ProgressBar progressBar;
    TextView boxEmpty;

    public comment() {
        // Required empty public constructor
    }

    public static comment newInstance(String param1, String param2) {
        comment fragment = new comment();
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
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        recyclerView = view.findViewById(R.id.comment_recyclerview);
        comment = view.findViewById(R.id.commentToSend);
        sendComment = view.findViewById(R.id.sendComment);
        progressBar = view.findViewById(R.id.comment_box_progress_bar);
        boxEmpty = view.findViewById(R.id.comment_box_empty_indicator);


        list = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference();

        SharedPreferences sp = getActivity().getSharedPreferences("SendPostData", MODE_PRIVATE);
        key = sp.getString("share_post_comment", "not");

        SharedPreferences sp2 = getActivity().getSharedPreferences("UserProfileData", MODE_PRIVATE);
        userName = sp2.getString("username", "Please Set Username.");
        profile = sp2.getString("profileUri", "Invalid.");
        typedComment = comment.getText().toString();

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CommentAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.custom_divider_3));
        recyclerView.addItemDecoration(dividerItemDecoration);


        if (list != null) {
            reference.child("Post_Comments").child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        progressBar.setVisibility(View.GONE);
                        boxEmpty.setVisibility(View.VISIBLE);
                    } else {
                        for (DataSnapshot d : snapshot.getChildren()) {
                            progressBar.setVisibility(View.GONE);
                            boxEmpty.setVisibility(View.GONE);
                            CommentModel model = d.getValue(CommentModel.class);
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

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comment.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Blank Comment!...", Toast.LENGTH_SHORT).show();
                } else {
                    pushId = reference.push().getKey();
                    CommentModel model = new CommentModel(profile, userName, comment.getText().toString());
//                    reference.child("Post_Comments").child(key).child(uid).setValue(model)
                    reference.child("Post_Comments").child(key).push().setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            comment.setText(" ");
                        }
                    });
                }
            }
        });
        return view;
    }
}