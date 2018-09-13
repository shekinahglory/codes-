package com.shekglory.friends;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {



    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;
    private TextInputEditText mUserName;
    private TextInputEditText mUserPassWord;
    private Button mLogin_button;
    private AppCompatButton goToRegisterBtn;
    private ProgressDialog mLoginProgress;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginEmail = (TextInputLayout) findViewById(R.id.logtextInputLayoutEmail);
        mLoginPassword = (TextInputLayout) findViewById(R.id.logtextInputLayoutPassword);
        mUserName = (TextInputEditText) findViewById(R.id.emailId);
        mUserPassWord = (TextInputEditText) findViewById(R.id.passwordId);
        mLoginPassword.setCounterEnabled(true);
        mLoginPassword.setCounterMaxLength(20);
        mLoginProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mLogin_button = (Button) findViewById(R.id.logcreateButton);
        goToRegisterBtn = (AppCompatButton) findViewById(R.id.goToRegisterFromLoing_id);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mUserName.getText().toString().isEmpty() && !hasFocus){
                    mLoginEmail.setErrorEnabled(true);
                    mLoginEmail.setError("Please enter your email.");
                } else {
                    mLoginEmail.setErrorEnabled(false);
                }
            }
        });

        mUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mUserName.getText().toString().isEmpty()){
                    mLoginEmail.setErrorEnabled(true);
                    mLoginEmail.setError("Please enter your email.");
                } else {
                    mLoginEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mUserPassWord.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(mUserPassWord.getText().toString().isEmpty() && !hasFocus){
                    mLoginPassword.setErrorEnabled(true);
                    mLoginPassword.setError("Please enter you password");
                } else {
                    mLoginPassword.setErrorEnabled(false);
                }
            }
        });

        mUserPassWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mUserPassWord.getText().toString().isEmpty()){
                    mLoginPassword.setErrorEnabled(true);
                    mLoginPassword.setError("Please enter you password");
                } else {
                    mLoginPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mLogin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mLoginEmail.getEditText().getText().toString();

                String password = mLoginPassword.getEditText().getText().toString();


                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please wait while we check your credentials");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    loginUser( email, password);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter your credentials", Toast.LENGTH_LONG).show();
                }
            }
        });

        goToRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });
    }


    private void loginUser(String email, String password) {



        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {

                    mLoginProgress.dismiss();

                    String current_user_Id = mAuth.getCurrentUser().getUid();

                    String tokenId = FirebaseInstanceId.getInstance().getToken();
                    mUserDatabase.child(current_user_Id).child("device_token").setValue(tokenId).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mainIntent);
                            finish();

                        }
                    });



                } else {
                    mLoginProgress.hide();
                    Toast.makeText(getApplicationContext(), "Cannot Sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}
