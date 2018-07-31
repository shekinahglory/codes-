package com.shekglory.friends;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;


public class RegisterProfImageActivity extends AppCompatActivity {



    private FloatingActionButton imageBtn;
    private Button skitpButton;
    private static final int PICK_IMAGE = 1;
    private Uri imageUri;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private StorageReference storageReference;
    private TextView wellcomeText;
    private CircularImageView imageToSave;
    private AppCompatButton continueToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_prof_image);

        imageBtn = (FloatingActionButton) findViewById(R.id.registerProfilePhoto_id);
        skitpButton = (Button) findViewById(R.id.skipButtonId);
        wellcomeText = (TextView) findViewById(R.id.welcomeText_id);
        imageToSave = (CircularImageView) findViewById(R.id.imageto_save_id);
        String username = getIntent().getStringExtra("username");
        wellcomeText.append(" " + username);
        storageReference = FirebaseStorage.getInstance().getReference();
        continueToMain = (AppCompatButton) findViewById(R.id.continue_to_main_id);
        continueToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTomainPage();
            }
        });

        imageUri = null;


        skitpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTomainPage();
            }
        });
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                skitpButton.setEnabled(false);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        if(imageUri != null){



        }




    }

    public void goTomainPage(){
        Intent goToMain = new Intent(RegisterProfImageActivity.this, MainActivity.class);
        goToMain.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(goToMain);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);
            imageToSave.setImageURI(imageUri);
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {

                    Uri resultUri = result.getUri();
                    File thum_filePath = new File(resultUri.getPath());
                    mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String current_user_id = mCurrentUser.getUid();
                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
                    mUserDatabase.keepSynced(true);
                    StorageReference filePath = storageReference.child("profile_images").child( current_user_id + ".jpg");
                    StorageReference thumb_filepath = storageReference.child("profile_images").child("thumbs").child(current_user_id + ".jpg");
                    filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                String download_url = task.getResult().getDownloadUrl().toString();
//                              UploadTask uploadTask = thumb_filepath.putBytes();
                                mUserDatabase.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(), "Profile image saved.", Toast.LENGTH_LONG).show();

                                        }
                                    }
                                });

                            } else {

                                Toast.makeText(getApplicationContext(), "Image not saved", Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }

    }
}
