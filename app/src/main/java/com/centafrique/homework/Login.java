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

public class Login extends AppCompatActivity {

    private EditText etEmail, etPassword;

    private GetText getStringText;
    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        getStringText = new GetText();

        progressDialog = new ProgressDialog(this);

    }

    public void Navigation(View view) {

        switch (view.getId()){

            case R.id.btnLogin:

                StartLogin();

                break;

            case R.id.tvGoToRegistrationPage:

                startActivity(new Intent(getApplicationContext(), Registration.class));

                break;

        }
    }

    private void StartLogin() {

        if (!TextUtils.isEmpty(getStringText.getText(etPassword)) && !TextUtils.isEmpty(getStringText.getText(etEmail))){

            if (getStringText.getText(etPassword).length() > 8){

                progressDialog.setTitle("Login on going..");
                progressDialog.setMessage("Please wait..");
                progressDialog.setCanceledOnTouchOutside(false);

                String txt = getStringText.getText(etEmail);

                if (Patterns.EMAIL_ADDRESS.matcher(txt).matches()){

                    progressDialog.show();

                    mAuth.signInWithEmailAndPassword(getStringText.getText(etEmail), getStringText.getText(etPassword))
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()){

                                        progressDialog.dismiss();

                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }else {

                                        progressDialog.dismiss();
                                        Toast.makeText(Login.this, "Authentication Error. Try again", Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });

                }else {

                    Toast.makeText(Login.this, "Email is incorrect..", Toast.LENGTH_SHORT).show();


                }
                
            }else{

                Toast.makeText(this, "Password must be 9 characters long", Toast.LENGTH_SHORT).show();
            }
            
    


        }


    }
}
