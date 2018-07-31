package com.shekglory.friends;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {


    private Toolbar mToolbar;

    private TextInputLayout mStatus;

    private Button mSavebtn;


    //Firebase

    private DatabaseReference mStatusDatabase;
//    private DatabaseReference mUserFindFriendStatusDatabase;
    private FirebaseUser mCurrentUser;

    //Progress

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        String statusValue = getIntent().getStringExtra("statusValue");

        //Firebase

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = mCurrentUser.getUid();

        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUid);
//        mUserFindFriendStatusDatabase = FirebaseDatabase.getInstance().getReference().child("UsersFindFriend").child("status");

        mToolbar = (Toolbar) findViewById(R.id.statusAppBarId);

        mStatus = (TextInputLayout) findViewById(R.id.statusInputId);

        mSavebtn = (Button) findViewById(R.id.statusSaveButtonId);



        mStatus.getEditText().setText(statusValue);

        mStatus.getEditText().setText(statusValue);

        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait whle we save the changes");
                mProgress.show();

                String status = mStatus.getEditText().getText().toString();
//                mUserFindFriendStatusDatabase.setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//
//                    }
//                });
                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgress.dismiss();
                        } else {

                            Toast.makeText(getApplicationContext(), "There was some error in saving changes", Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





    }
}
