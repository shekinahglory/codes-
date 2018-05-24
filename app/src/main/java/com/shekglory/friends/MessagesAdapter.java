package com.shekglory.friends;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends BaseAdapter {



    private List<Messages> mMessageList ;
    Context context;

    private DatabaseReference mUserDatabase;

    private FirebaseUser mCurrentUser;

    private LayoutInflater messageInflater;


    public MessagesAdapter(Context context, List<Messages> mMessageList) {

        this.context = context;
        this.mMessageList = mMessageList;
        messageInflater = LayoutInflater.from(this.context);

    }

    public void add(Messages message) {
        this.mMessageList.add(message);
        notifyDataSetChanged(); // to render the list we need to notify
    }




    @Override
    public int getCount() {
        return mMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MessageViewHolder holder = new MessageViewHolder();

//        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        Messages message = mMessageList.get(position);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = mCurrentUser.getUid();

        if( convertView == null){


            convertView = messageInflater.inflate(R.layout.message_single_layout, parent, false );
            holder = new MessageViewHolder();
            convertView.setTag(holder);
        }



        String from_user = message.getFrom();




        if (from_user.equals(currentUid)){

            convertView = messageInflater.inflate(R.layout.my_message, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getMessages());

        } else {
            convertView = messageInflater.inflate(R.layout.their_message, null);
            holder.avatar = (View) convertView.findViewById(R.id.avatar);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);

            holder.name.setText(message.getFrom());
            holder.messageBody.setText(message.getMessages());
            GradientDrawable drawable = (GradientDrawable) holder.avatar.getBackground();

        }





        return null;
    }

    class MessageViewHolder {
        public View avatar;
        public TextView name;
        public TextView messageBody;
    }
}
