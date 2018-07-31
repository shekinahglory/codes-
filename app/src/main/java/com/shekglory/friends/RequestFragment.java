package com.shekglory.friends;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mikhaellopez.circularimageview.CircularImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {


    private EditText mSearchField;
    private ImageView mSearchBtn;
    private RecyclerView mResultList;
    private DatabaseReference mUserDatabase;
    private Query query;
    private View mMainView;


    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_request, container, false);

        mSearchField = (EditText) mMainView.findViewById(R.id.searchFriendId);
        mSearchBtn = (ImageView) mMainView.findViewById(R.id.imageView);

        mResultList = (RecyclerView) mMainView.findViewById(R.id.result_id);

        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(getContext()));

        query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameToSearch = mSearchField.getText().toString();
                searchUser(nameToSearch);
            }
        });




        return mMainView;
    }

    private void searchUser(String nameToSearch) {

        Query search =  query.orderByChild("name").startAt(nameToSearch).endAt(nameToSearch + "\uf8ff");

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(search, User.class)
                        .build();

        FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(options) {
            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_layout, parent, false);

                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull User model) {

                holder.setDetails(getContext(), model.getName(), model.getStatus(), model.getImage());

                final String userId = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                        profileIntent.putExtra("user_id",userId);
                        startActivity(profileIntent);
                    }
                });
            }
        };

        mResultList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }


    // view holder class


    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDetails(Context ctx, String userName , String userStatus, String userImage){
            TextView user_name = (TextView) mView.findViewById(R.id.userSingleName);
            TextView user_status = (TextView) mView.findViewById(R.id.userSingleStatus);
            CircularImageView user_image = (CircularImageView) mView.findViewById(R.id.userSingleImage);
            Glide.with(ctx).load(userImage).into(user_image);
            user_name.setText(userName);
            user_status.setText(userStatus);

        }
    }
}
