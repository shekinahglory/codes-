package com.shekglory.friends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button mRegister;

    private Button mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        mRegister = (Button) findViewById(R.id.regButton);

        mLogin = (Button) findViewById(R.id.newAcBut) ;

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regIntent = new Intent(StartActivity.this, RegisterActivity.class);
                regIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(regIntent);
                finish();
            }
        });


        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logInIntent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(logInIntent);
                finish();
            }
        });




    }
}
