package com.centafrique.homework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UploadProduct extends AppCompatActivity {

    private String camera;
    private ImageView image_main;
    private EditText et_product_name, et_product_description, et_product_price;
    private Uri imageUri;

    private Uri mImageUri;
    private String txtChild;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_product);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("products");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);

        image_main = findViewById(R.id.image_main);

        et_product_name = findViewById(R.id.et_product_name);
        et_product_description = findViewById(R.id.et_product_description);
        et_product_price = findViewById(R.id.et_product_price);

        findViewById(R.id.BtnMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);

            }
        });

        findViewById(R.id.btn_add_seller).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String txtName = et_product_name.getText().toString();
                final String txtDescription = et_product_description.getText().toString();
                final String txtPrice = et_product_price.getText().toString();

                progressDialog.setTitle("Please wait");
                progressDialog.setMessage("Uploading..");
                progressDialog.setCanceledOnTouchOutside(false);

                if (!TextUtils.isEmpty(txtName) && !TextUtils.isEmpty(txtDescription) && !TextUtils.isEmpty(txtPrice) && imageUri != null) {

                    progressDialog.show();

                    final String newPostKey = mDatabase.push().getKey();

                    if (newPostKey != null) {

                        final DatabaseReference newPost = mDatabase.child(newPostKey);

                        if (imageUri != null){

                            final StorageReference filepath = mStorage.child("products_Images").child(imageUri.getLastPathSegment());
                            UploadTask uploadTask = filepath.putFile(imageUri);

                            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    return filepath.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    if (task.isSuccessful()) {

                                        Uri downloadUri = task.getResult();
                                        String ImageUrl = String.valueOf(downloadUri);

                                        newPost.child("price").setValue(txtPrice);
                                        newPost.child("desc").setValue(txtDescription);
                                        newPost.child("name").setValue(txtName);
                                        newPost.child("uid").setValue(mCurrentUser.getUid());
                                        newPost.child("main_image").setValue(ImageUrl);


                                        Toast.makeText(UploadProduct.this, "Upload Complete..", Toast.LENGTH_SHORT).show();

                                        progressDialog.dismiss();

                                        Intent intent = new Intent(UploadProduct.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);


                                    } else {

                                        progressDialog.dismiss();
                                        Toast.makeText(UploadProduct.this, "Please try again..", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }

                    } else {

                        if (TextUtils.isEmpty(txtName))
                            et_product_name.setError("Product name cannot be empty");
                        if (TextUtils.isEmpty(txtDescription))
                            et_product_description.setError("Product description cannot be empty");
                        if (TextUtils.isEmpty(txtPrice))
                            et_product_price.setError("Product price cannot be empty");

                        if (imageUri == null)
                            Toast.makeText(UploadProduct.this, "You must select at least one main image.", Toast.LENGTH_SHORT).show();
                    }

                }



            }
        });





    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {

                imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                image_main.setImageBitmap(selectedImage);



            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(UploadProduct.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {

            Toast.makeText(UploadProduct.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}
