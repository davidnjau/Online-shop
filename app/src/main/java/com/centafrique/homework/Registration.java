package com.centafrique.homework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etRetypePassword;

    private GetText getStringText;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etRetypePassword = findViewById(R.id.etRetypePassword);

        getStringText = new GetText();
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(this);

    }


    public void Navigation(View view) {

        switch (view.getId()){

            case R.id.btnRegister:

                StartRegistration();

                break;

            case R.id.tvGoToLoginPage:

                startActivity(new Intent(getApplicationContext(), Login.class));

                break;
        }

    }

    private void StartRegistration() {

        if (!TextUtils.isEmpty(getStringText.getText(etUsername)) && !TextUtils.isEmpty(getStringText.getText(etEmail))
                && !TextUtils.isEmpty(getStringText.getText(etPassword)) && !TextUtils.isEmpty(getStringText.getText(etRetypePassword))){



            progressDialog.setTitle("Registration on going..");
            progressDialog.setMessage("Please wait as we register you.");
            progressDialog.setCanceledOnTouchOutside(false);


            if (getStringText.getText(etPassword).equals(getStringText.getText(etRetypePassword))){

                if (getStringText.getText(etPassword).length() > 8 ){

                    progressDialog.show();

                    String txt = getStringText.getText(etEmail);

                    if (Patterns.EMAIL_ADDRESS.matcher(txt).matches()){

                        mAuth.createUserWithEmailAndPassword(getStringText.getText(etEmail), getStringText.getText(etPassword))
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()){

                                            DatabaseReference newPost = database.getReference().child("users").child(mAuth.getUid());
                                            newPost.child("email").setValue(getStringText.getText(etEmail));
                                            newPost.child("password").setValue(getStringText.getText(etPassword));
                                            newPost.child("username").setValue(getStringText.getText(etUsername));
                                            newPost.child("uid").setValue(mAuth.getUid());

                                            progressDialog.dismiss();

                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();

                                        }else {
                                            progressDialog.dismiss();

                                            Toast.makeText(Registration.this, "Authentication error.", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                });

                    }else {
                        progressDialog.dismiss();

                        Toast.makeText(Registration.this, "Email is incorrect..", Toast.LENGTH_SHORT).show();

                    }

                }else{

                    Toast.makeText(this, "Password must be 9 characters long", Toast.LENGTH_SHORT).show();
                }





            }else {

                etRetypePassword.setText("");
                etRetypePassword.setError("This should be the same as the password");

            }


        }else{

            if(TextUtils.isEmpty(getStringText.getText(etUsername))) etUsername.setError("Username cannot be empty..");
            if(TextUtils.isEmpty(getStringText.getText(etEmail))) etEmail.setError("Email Address cannot be empty..");
            if(TextUtils.isEmpty(getStringText.getText(etPassword))) etPassword.setError("Password cannot be empty..");
            if(TextUtils.isEmpty(getStringText.getText(etRetypePassword))) etRetypePassword.setError("Re-type password cannot be empty..");

        }


    }
}
