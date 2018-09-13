package com.shekglory.friends;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.opentok.OpenTok;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ChatActivity extends AppCompatActivity {



    private String mCharUser;
    private Toolbar mChatToolbar;
    private DatabaseReference mRootRef;
    private TextView mTitleView;
    private TextView mLastSeenView;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private ImageButton mChatAddBtn;
    private ImageButton mChatSendBtn;
    private ImageView videoCallBtn;
    private CircularImageView mProfileImageView;
    private EditText mChatMessageView;
    private RecyclerView mMessageList;
//    private ListView mMessageList;
    private SwipeRefreshLayout mRefreshLayout;
    private List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLienearLayout;
    private MessageAdapter messageAdapter;
    private static final int TOTAL_ITMES_TO_LOAD = 10;
    private int mCurrentPage = 1;
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";
    private static final int GALLERY_PICK = 1;
    // Storage Firebase
    private StorageReference mImageStorage;
    private DatabaseReference mUserRef;
    private String userName = "";
    private String messageFromId;
    private int messageNumber = 1;
    private String image;

    private Boolean messageSeen = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference();
        mChatToolbar = (Toolbar) findViewById(R.id.chatAppBar);
        setSupportActionBar(mChatToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        mCharUser = getIntent().getStringExtra("user_id");
         userName = getIntent().getStringExtra("user_name");
         messageFromId = getIntent().getStringExtra("message_from");
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custome_bar, null);
        actionBar.setCustomView(action_bar_view);

        // ------ Custome Action bar items ----
        videoCallBtn = (ImageView) findViewById(R.id.videocallId);
        mTitleView = (TextView) findViewById(R.id.customeBartitle);
        mChatAddBtn = (ImageButton) findViewById(R.id.chatAddButton);
        mChatSendBtn = (ImageButton) findViewById(R.id.chatSendButton);
        mChatMessageView = (EditText) findViewById(R.id.chatMessageView);
        mLastSeenView = (TextView) findViewById(R.id.lastSeenId);
        mProfileImageView = (CircularImageView) findViewById(R.id.customBarImage);
        mLienearLayout = new LinearLayoutManager(this);
        mMessageList = (RecyclerView) findViewById(R.id.messagesList);
        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(mLienearLayout);
        mTitleView.setText(userName);
        messageAdapter = new MessageAdapter(messagesList, userName);
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mMessageList = (RecyclerView) findViewById(R.id.messagesList);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mMessageList.setAdapter(messageAdapter);
        loadMessages();

        int apiKey = 000000; // YOUR API KEY
        String apiSecret = "YOUR API SECRET";
        OpenTok opentok = new OpenTok(apiKey, apiSecret);

        mRootRef.child("Users").child(mCharUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String  online  = dataSnapshot.child("online").getValue().toString();
                    if(online.equals("true")){
                        mLastSeenView.setText("Online");
                    } else {
                        mLastSeenView.setText(online);
                        GetTimeAgo getTimeAgo = new GetTimeAgo();
                        long lastTime = Long.parseLong(online);
                        String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());
                        mLastSeenView.setText(lastSeenTime);
                    }
                String image = dataSnapshot.child("image").getValue().toString();
                Picasso.with(ChatActivity.this).load(image).placeholder(R.drawable.defaultimg).into(mProfileImageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mRootRef.child("Users").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                image = dataSnapshot.child("image").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // sending message

        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                mChatMessageView.setText("");
            }
        });

        // Buttong for sending images .. Remember that the button was removed from the layout.
        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                itemPos = 0;
                loadMoreMessages();
            }
        });


        videoCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent videoCall = new Intent(ChatActivity.this, VideoCallActivity.class);
                startActivity(videoCall);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUserId.equals(messageFromId)){
            Map chatAddMap = new HashMap();
            chatAddMap.put("seen", true);
            chatAddMap.put("inactivity", true);
            Map chatUserMap = new HashMap();
            chatUserMap.put("Chat/" + mCurrentUserId + "/" + mCharUser, chatAddMap);
            mRootRef.child("Chat").child(mCurrentUserId).child(mCharUser).setValue(chatAddMap);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mCurrentUserId.equals(messageFromId)){
            Map chatAddMap = new HashMap();
            chatAddMap.put("seen", true);
            chatAddMap.put("inactivity", false);
            chatAddMap.put("timestamp", ServerValue.TIMESTAMP);
            Map chatUserMap = new HashMap();
            chatUserMap.put("Chat/" + mCurrentUserId + "/" + mCharUser, chatAddMap);
            mRootRef.child("Chat").child(mCurrentUserId).child(mCharUser).setValue(chatAddMap);
        }
    }

    public void updateChat(){
        mRootRef.child("Chat").child(mCharUser).child(mCurrentUserId).push();
        mRootRef.child("Chat").child(mCharUser).child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String number = "1";
                Boolean seen = false;
                Boolean userInactivity;

                if (dataSnapshot.hasChildren()){
                    if (dataSnapshot.hasChild("number")){
                        number = dataSnapshot.child("number").getValue().toString();
                    }

                    seen =  (Boolean) dataSnapshot.child("seen").getValue();
                    userInactivity = (Boolean) dataSnapshot.child("inactivity").getValue();
                    if(userInactivity){
                        messageSeen = true;
                    }
                }
                messageNumber = Integer.parseInt(number);
                if(!seen){
                    messageNumber++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Map chatAddMap = new HashMap();
        Map chatAddMapSec = new HashMap();
        chatAddMap.put("seen", messageSeen);
        chatAddMap.put("inactivity", true);
        chatAddMap.put("timestamp", ServerValue.TIMESTAMP);
        chatAddMap.put("number", messageNumber +"" );
        chatAddMapSec.put("seen", messageSeen);
        chatAddMapSec.put("inactivity", messageSeen);
        chatAddMapSec.put("timestamp", ServerValue.TIMESTAMP);
        chatAddMapSec.put("number", messageNumber +"" );
        Map chatUserMap = new HashMap();
        chatUserMap.put("Chat/" + mCurrentUserId + "/" + mCharUser, chatAddMap);
        chatUserMap.put("Chat/" + mCharUser + "/" + mCurrentUserId, chatAddMap);
        mRootRef.child("Chat").child(mCurrentUserId).child(mCharUser).setValue(chatAddMap);
        mRootRef.child("Chat").child(mCharUser).child(mCurrentUserId).setValue(chatAddMapSec);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            final String current_user_ref = "messages/" + mCurrentUserId + "/" + mCharUser;
            final String chat_user_ref = "messages/" + mCharUser + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserId).child(mCharUser).push();

            final String push_id = user_message_push.getKey();
            StorageReference filepath = mImageStorage.child("message_images").child( push_id + ".jpg");
            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()){
                        String download_url = task.getResult().getDownloadUrl().toString();
                        Map messageMap = new HashMap();
                        messageMap.put("messages", download_url);
                        messageMap.put("seen", false);
                        messageMap.put("type", "image");
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("image", image);
                        messageMap.put("from", mCurrentUserId);
                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);
                        mChatMessageView.setText("");
                        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError != null){ Log.d("CHAT_LOG", databaseError.getMessage().toString()); }
                            }
                        });
                    }
                }
            });

            updateChat();


        }
    }

    private void loadMoreMessages(){

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mCharUser);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();
                if(!mPrevKey.equals(messageKey)){ messagesList.add(itemPos++, message); }
                else { mPrevKey = mLastKey; }
                if(itemPos == 1) { mLastKey = messageKey; }
                messageAdapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(false);
                mLienearLayout.scrollToPositionWithOffset(10, 0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadMessages() {

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mCharUser);
        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITMES_TO_LOAD);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

                itemPos++;
                if(itemPos == 1){
                    String messageKey = dataSnapshot.getKey();
                    mLastKey = messageKey;
                    mPrevKey = messageKey;
                }
                messagesList.add(message);
                mMessageList.scrollToPosition(messagesList.size() - 1);
                messageAdapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {

        String message = mChatMessageView.getText().toString();

        if (!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + mCurrentUserId + "/" + mCharUser;
            String chat_user_ref = "messages/" + mCharUser +"/" + mCurrentUserId;
            DatabaseReference usere_message_push = mRootRef.child("messages").child(mCurrentUserId).child(mCharUser).push();
            String push_id = usere_message_push.getKey();
            String message_to_ref = "messagesendto/" + mCharUser + "/" + push_id;

            Map messageMap = new HashMap();
            messageMap.put("messages" , message);
            messageMap.put("seen" , false);
            messageMap.put("type", "text");
            messageMap.put("name", userName);
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);

            Map messageUserMap = new HashMap();

            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(message_to_ref + "/" ,messageMap);
            messageUserMap.put(chat_user_ref+ "/" +push_id, messageMap);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError != null){
                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                    }
                }
            });

            updateChat();


        }
    }
}
