package com.shekglory.friends;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;


public class UsersActivity extends AppCompatActivity {


    private Toolbar mToolbar;

    private RecyclerView mUserList;

    private Query query;


    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);


        mToolbar = (Toolbar) findViewById(R.id.userAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");
        mUserList = (RecyclerView) findViewById(R.id.usersListId);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));

    }




    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();


        FirebaseRecyclerAdapter<User , UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(
               options
        ) {


            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(UsersViewHolder holder, int position, final User model) {

                holder.setName(model.getName());

                holder.setDisplayStatus(model.getStatus());
                holder.setUserImage(model.getImage(), getApplicationContext());
                final String userId = getRef(position).getKey();
                final String userName = model.getName();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id",userId);
                        startActivity(profileIntent);

                    }
                });
            }
        };


        mUserList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }



    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setName(String name){

            TextView mUserNameView = (TextView) mView.findViewById(R.id.userSingleName);
            mUserNameView.setText(name);

        }


        public void setDisplayStatus(String status){

            TextView mUserStatusView = (TextView) mView.findViewById(R.id.userSingleStatus);
            mUserStatusView.setText(status);
        }

        public void setUserImage(String image, Context context) {
            CircularImageView userImageView = (CircularImageView) mView.findViewById(R.id.userSingleImage);
            Picasso.with(context).load(image).placeholder(R.drawable.defaultimg).into(userImageView);

        }
    }

}
