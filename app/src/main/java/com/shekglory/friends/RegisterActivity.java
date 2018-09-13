package com.shekglory.friends;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;



public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputEditText mUserName;
    private TextInputLayout mEmail;
    private TextInputEditText mUserEmail;
    private TextInputLayout  mPassword;
    private TextInputEditText mUserPassword;
    private Button mCreateBtn;
    private Button goToLogin;
    private ProgressDialog mRegProgress;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
//    private DatabaseReference mUserFriendDatabase;
    private Toolbar mToolBar;
    private ProgressBar progressBar;
    private Query query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mRegProgress = new ProgressDialog(this);
        progressBar = (ProgressBar) findViewById(R.id.registerProgressBar);
        mAuth = FirebaseAuth.getInstance();
        mDisplayName = (TextInputLayout) findViewById(R.id.textInputLayoutDisplayName);
        mUserName = (TextInputEditText) findViewById(R.id.displayName);
        mEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        mUserEmail = (TextInputEditText) findViewById(R.id.emailRegisterId);
        mPassword  = (TextInputLayout)  findViewById(R.id.textInputLayoutPassword);
        mUserPassword = (TextInputEditText) findViewById(R.id.passwordRegisterId);
        mPassword.setCounterEnabled(true);
        mPassword.setCounterMaxLength(20);
        mCreateBtn = (Button) findViewById(R.id.createButton);
        goToLogin = (Button) findViewById(R.id.goToLoginButton);


        mUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!mUserName.getText().toString().isEmpty()){
                    final String username = mUserName.getText().toString();
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    Query search =  mDatabase.child("Users").orderByChild("name").equalTo(username);

                    search.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                mDisplayName.setErrorEnabled(true);
                                mDisplayName.setError("This name already exists .");
                            } else {
                                mDisplayName.setErrorEnabled(false);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } if (mUserName.getText().toString().isEmpty() && !hasFocus){
                    mDisplayName.setErrorEnabled(true);
                    mDisplayName.setError("Please enter a username");
                } else {
                    mDisplayName.setErrorEnabled(false);
                }
            }
        });

        mUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mUserName.getText().toString().isEmpty()){
                    mDisplayName.setErrorEnabled(true);
                    mDisplayName.setError("Please enter a username");
                } else {
                    mDisplayName.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        mUserEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!mUserEmail.getText().toString().isEmpty()){
                    final String email = mUserEmail.getText().toString();
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    Query searchEmail = mDatabase.child("Users").orderByChild("email").equalTo(email);
                    searchEmail.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                mEmail.setErrorEnabled(true);
                                mEmail.setError("This email already exists");
                            } else {
                                mEmail.setErrorEnabled(false);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
                } if (mUserEmail.getText().toString().isEmpty()  && !hasFocus){
                    mEmail.setErrorEnabled(true);
                    mEmail.setError("Please enter your email");
                } else {
                    mEmail.setErrorEnabled(false);
                }
            }
        });

        mUserEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mUserEmail.getText().toString().isEmpty()){
                    mEmail.setErrorEnabled(true);
                    mEmail.setError("Please enter your email");
                } else {
                    mEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mUserPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mUserPassword.getText().toString().isEmpty()  && !hasFocus){
                    mPassword.setErrorEnabled(true);
                    mPassword.setError("Please enter a password");
                } else {
                    mPassword.setErrorEnabled(false);
                }
            }
        });
        mUserPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mUserPassword.getText().toString().isEmpty() ){
                    mPassword.setErrorEnabled(true);
                    mPassword.setError("Please enter a password");
                } else {
                    mPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
                String password = mUserPassword.getText().toString();
                if(!TextUtils.isEmpty(displayName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(displayName, email, password);
                } else {

                    Toast.makeText(getApplicationContext(), "Please enter a username, email and password.", Toast.LENGTH_LONG).show();
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

    private void registerUser(final String displayName, final String email, String password) {



        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = currentUser.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            String tokenId = FirebaseInstanceId.getInstance().getToken();
                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", displayName);
                            userMap.put("email", email);
                            userMap.put("status", "Hi there, I'm using mtbos chat app");
                            userMap.put("image", "default");
                            userMap.put("thumb_image", "default");
                            userMap.put("device_token", tokenId);
                            userMap.put("friend_number", "0");

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
                            Toast.makeText(getApplicationContext(), "Cannot Sign Up. PLease check the form and try again.", Toast.LENGTH_LONG ).show();
                        }
                        // ...
                    }
                });
    }


}
