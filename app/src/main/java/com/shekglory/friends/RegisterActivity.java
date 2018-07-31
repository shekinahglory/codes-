package com.shekglory.friends;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;



public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout  mPassword;
    private Button mCreateBtn;
    private Button goToLogin;
    private ProgressDialog mRegProgress;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
//    private DatabaseReference mUserFriendDatabase;
    private Toolbar mToolBar;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mRegProgress = new ProgressDialog(this);
        progressBar = (ProgressBar) findViewById(R.id.registerProgressBar);
        mAuth = FirebaseAuth.getInstance();
        mDisplayName = (TextInputLayout) findViewById(R.id.textInputLayoutDisplayName);
        mEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        mPassword  = (TextInputLayout)  findViewById(R.id.textInputLayoutPassword);
        mPassword.setCounterEnabled(true);
        mPassword.setCounterMaxLength(20);
        mCreateBtn = (Button) findViewById(R.id.createButton);
        goToLogin = (Button) findViewById(R.id.goToLoginButton);
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                goToLogin.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(goToLogin);
                finish();
            }
        });

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String displayName = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();
                if(!TextUtils.isEmpty(displayName) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(displayName, email, password);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void registerUser(final String displayName, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = currentUser.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
//                            mUserFriendDatabase = FirebaseDatabase.getInstance().getReference().child("UsersFindFriend");
                            String tokenId = FirebaseInstanceId.getInstance().getToken();
                            HashMap<String, String> userMap = new HashMap<>();
//                            HashMap<String, String> userFindFriendMap = new HashMap<>();
                            userMap.put("name", displayName);
                            userMap.put("status", "Hi there, I'm using mtbos chat app");
                            userMap.put("image", "default");
                            userMap.put("thumb_image", "default");
                            userMap.put("device_token", tokenId);
                            userMap.put("friend_number", "0");

//                            userFindFriendMap.put("name", displayName);
//                            userFindFriendMap.put("userUid", uid);
//                            userFindFriendMap.put("status", "Hi there, I'm using clean chat app");
//                            userFindFriendMap.put("image", "default");

//                            mUserFriendDatabase.setValue(userFindFriendMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//
//                                }
//                            });
                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Intent regImageIntent = new Intent(RegisterActivity.this, RegisterProfImageActivity.class);
                                        regImageIntent.putExtra("username", displayName);
                                        regImageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        regImageIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(regImageIntent );
                                        finish();
                                    }
                                }
                            });
//
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Cannot Sign in. PLease check the form and try again.", Toast.LENGTH_LONG ).show();
                        }
                        // ...
                    }
                });
    }


}
