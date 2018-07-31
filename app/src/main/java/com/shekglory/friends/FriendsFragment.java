package com.shekglory.friends;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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



/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {


    private RecyclerView mFriendList;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private View mMainView;
    private Query query;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendList = (RecyclerView) mMainView.findViewById(R.id.friendsListId);
        mAuth = FirebaseAuth.getInstance();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        query= FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUserId);
        query.keepSynced(true);



        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment

        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(query, Friends.class)
                        .build();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                options
        ) {
            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new FriendsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull Friends model) {

                holder.setDate(model.getDate());


                final String listUserId = getRef(position).getKey();
                mUsersDatabase.child(listUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumbImage = dataSnapshot.child("image").getValue().toString();
//                        String userOnline = dataSnapshot.child("online").getValue().toString();

                        if(dataSnapshot.hasChild("online")){

                            String userOnline =  dataSnapshot.child("online").getValue().toString();

                            holder.setUserOnline(userOnline);
                        }

                        holder.setName(userName);

                        holder.setUserImage(userThumbImage, getContext());



                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{ "Open Profile", "Send message"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                         // click event for each item.

                                        if(which == 0){

                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                            profileIntent.putExtra("user_id",listUserId);
                                            startActivity(profileIntent);
                                        }

                                        if (which == 1){


                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("user_id",listUserId);
                                            chatIntent.putExtra("user_name", userName);
                                            startActivity(chatIntent);

                                        }
                                    }
                                });

                                builder.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }


        };


        mFriendList.setAdapter(friendsRecyclerViewAdapter);
        friendsRecyclerViewAdapter.startListening();

    }





    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FriendsViewHolder(View itemView){
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date){
            TextView userStatusView = (TextView) mView.findViewById(R.id.userSingleStatus);
            userStatusView.setText(date);
        }

        public void setName(String name){
            TextView userNameView = (TextView) mView.findViewById(R.id.userSingleName);
            userNameView.setText(name);
        }


        public void setUserImage(String image, Context context) {

            CircularImageView userImageView = (CircularImageView) mView.findViewById(R.id.userSingleImage);

            Picasso.with(context).load(image).placeholder(R.drawable.defaultimg).into(userImageView);

        }

        public void setUserOnline (String onlineStatus){
            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.imageSingleOnlineIcon);

            TextView userOnlineText = (TextView) mView.findViewById(R.id.userOnlineIcon);


            if(onlineStatus.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
                userOnlineText.setVisibility(View.VISIBLE);
            } else {

                userOnlineView.setVisibility(View.INVISIBLE);
                userOnlineText.setVisibility(View.INVISIBLE);

            }
        }
    }

}
