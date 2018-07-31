package com.shekglory.friends;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private String name;
    private String username;


    public MessageAdapter(List<Messages> mMessageList, String name){
        this.name = name;
        this.mMessageList = mMessageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
        return new MessageViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        Messages c = mMessageList.get(position);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = mCurrentUser.getUid();
        String from_user = c.getFrom();
        String message_type = c.getType();
        String message_from = c.getFrom();





        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(message_from);

        String time = c.getTime() + "";

        Date date = new Date(c.getTime());

        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        String formattedTime = sdf.format(date);

        if(message_type.equals("text")){


            if (from_user.equals(currentUid)){
                holder.chat_left_layout.setVisibility(View.VISIBLE);
                holder.chat_right_layout.setVisibility(View.GONE);
                holder.messageImage.setVisibility(View.GONE);
                holder.messageText.setText(c.getMessages());
                holder.myTIme.setText(formattedTime);
            } else {
                holder.chat_left_layout.setVisibility(View.GONE);
                holder.messageImage.setVisibility(View.GONE);
                holder.chat_right_layout.setVisibility(View.VISIBLE);
                holder.myMessageText.setText(c.getMessages());
                holder.theirTime.setText(formattedTime);
            }


        } else {

            if(!from_user.equals(currentUid)){

                username = name;
                holder.imageFromCaption.setText(username);
                holder.imageFromCaption.setVisibility(View.VISIBLE);

            }

            holder.chat_left_layout.setVisibility(View.GONE);
            holder.chat_right_layout.setVisibility(View.GONE);

//             The images are loaded from here.
            Picasso.with(holder.messageImage.getContext()).load(c.getMessages())
                    .resize(260, 260)
                    .centerCrop()
                    .into(holder.messageImage);
        }
    }



    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public TextView imageFromCaption;
        public TextView theirTime;
        public TextView myTIme;
        public ImageView messageImage;
        public LinearLayout messageImageLayout;
        public LinearLayout chat_left_layout;
        private LinearLayout chat_right_layout;
        private  TextView myMessageText ;

        public MessageViewHolder(View view){
            super(view);
            messageText = (TextView) view.findViewById(R.id.messageTextLayout);
            imageFromCaption = (TextView) view.findViewById(R.id.imageFromId);
            myMessageText = (TextView) view.findViewById(R.id.myMessageTextLayout);
            chat_right_layout = (LinearLayout) view.findViewById(R.id.chat_right_msg_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            chat_left_layout = (LinearLayout) view.findViewById(R.id.chat_left_msg_layout);
            theirTime = (TextView) view.findViewById(R.id.their_message_time);
            myTIme = (TextView) view.findViewById(R.id.time_text_layout);
        }
    }
}
