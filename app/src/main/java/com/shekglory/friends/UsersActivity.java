package com.shekglory.friends;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;


public class UsersActivity extends AppCompatActivity {


    private Toolbar mToolbar;

    private RecyclerView mUserList;

    private Query query;

    private FirebaseAuth mAuth;
    private String mCurrent_user_id;


    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);


        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mToolbar = (Toolbar) findViewById(R.id.userAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Friend_req").child(mCurrent_user_id).orderByChild("request_type").startAt("received").endAt("received");
        mUserList = (RecyclerView) findViewById(R.id.usersListId);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));




        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() == null){
                    CharSequence options[] = new CharSequence[]{  "Go to chats"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(UsersActivity.this);

                    builder.setTitle("No new friend requests");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // click event for each item.
                            if (which == 0){
                                Intent chatIntent = new Intent(UsersActivity.this, MainActivity.class);
                                startActivity(chatIntent);
                            }
                        }
                    });

                    builder.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<FriendReq> options =
                new FirebaseRecyclerOptions.Builder<FriendReq>()
                        .setQuery(query, FriendReq.class)
                        .build();


        FirebaseRecyclerAdapter<FriendReq , UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FriendReq, UsersViewHolder>(
                options
        ) {


            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_req, parent, false);

                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final UsersViewHolder holder, final int position, final FriendReq model) {


                final String list_user_id = getRef(position).getKey();
                final Query getUsers = mUserDatabase.child(list_user_id);

                getUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()){
                            holder.setName(dataSnapshot.child("name").getValue().toString() + " ");
                            holder.setUserImage(dataSnapshot.child("image").getValue().toString(), getApplicationContext());
                            final String userId = getRef(position).getKey();

                            holder.checkOut.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                                    profileIntent.putExtra("user_id",userId);
                                    startActivity(profileIntent);
                                }
                            });


                            holder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                                    profileIntent.putExtra("user_id",userId);
                                    startActivity(profileIntent);

                                }
                            });
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };


        mUserList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }



    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        Button checkOut ;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            checkOut = (Button) mView.findViewById(R.id.checkOutBtnId);
        }



        public void setName(String name){

            TextView mUserNameView = (TextView) mView.findViewById(R.id.userNameRequestId);
            mUserNameView.setText(name);

        }


        public void setUserImage(String image, Context context) {
            CircularImageView userImageView = (CircularImageView) mView.findViewById(R.id.userSingleRequestImage);
            Picasso.with(context).load(image).placeholder(R.drawable.defaultimg).into(userImageView);

        }
    }

}
