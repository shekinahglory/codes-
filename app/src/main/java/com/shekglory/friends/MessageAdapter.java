package com.shekglory.friends;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    private List<Messages> mMessageList;


    private DatabaseReference mUserDatabase;

    private FirebaseUser mCurrentUser;


    public MessageAdapter(List<Messages> mMessageList){
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

//        if (from_user.equals(currentUid)){
//
//            Log.d("DEFAULT" , "working");
//
//        } else {
//
//            Log.d("DEFAULT" ,  from_user);
//
//        }


        String message_type = c.getType();

        String message_from = c.getFrom();



        if(message_type.equals("text")){




            if (from_user.equals(currentUid)){

                holder.messageTextLayoutLinearLayout.setVisibility(View.GONE);
                holder.myMessageTextLayout.setVisibility(View.VISIBLE);
                holder.myMessageText.setText(c.getMessages());


            } else {

                holder.messageTextLayoutLinearLayout.setVisibility(View.VISIBLE);
                holder.myMessageTextLayout.setVisibility(View.GONE);
                holder.messageText.setText(c.getMessages());
            }


//            holder.messageText.setPadding(50,0,0,0);




            Log.d("DEFAULT" ,  message_from);
//            holder.messageImage.setVisibility(View.INVISIBLE);

        } else {


            holder.messageTextLayoutLinearLayout.setVisibility(View.GONE);


//            Picasso.with(holder.messageImage.getContext()).load(c.getMessages())
//                    .placeholder(R.drawable.defaultimg).into(holder.messageImage);


            // The images are loaded from here.
//
//            Picasso.with(holder.messageImage.getContext()).load(c.getMessages()).fit().centerCrop()
//                    .placeholder(R.drawable.defaultimg)
//                    .into(holder.messageImage);


        }





    }



    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;

        public CircleImageView profileImage;

        public ImageView messageImage;

        public LinearLayout messageTextLayoutLinearLayout;

        private  TextView myMessageText ;

        private LinearLayout myMessageTextLayout;


        public MessageViewHolder(View view){
            super(view);

            messageText = (TextView) view.findViewById(R.id.messageTextLayout);

            myMessageText = (TextView) view.findViewById(R.id.myMessageTextLayout);

            myMessageTextLayout = (LinearLayout) view.findViewById(R.id.seconLayout);

//         messageImage = (ImageView) view.findViewById(R.id.message_image_layout);

            messageTextLayoutLinearLayout = (LinearLayout) view.findViewById(R.id.messageTextLayoutLinearLayout);

        }


    }

}
