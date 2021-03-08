package com.jkkniu.vehicletracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jkkniu.vehicletracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Registration extends AppCompatActivity {
    EditText fullName, email, password, phone;
    Button registerBtn;
    TextView loginBtn;
    FirebaseAuth auth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        fullName = findViewById(R.id.name);
        email =findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        registerBtn= findViewById(R.id.but_register);
        loginBtn = findViewById(R.id.login);

        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);


        if (auth.getCurrentUser()!= null){
            startActivity(new Intent(getApplicationContext(), MapDriver.class));
            finish();
        }

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String memail = email.getText().toString().trim();
                String mpassword = password.getText().toString().trim();

                if (TextUtils.isEmpty(memail)){
                    email.setError("Email is Required.");
                    return;
                }
                if (TextUtils.isEmpty(mpassword)){
                    password.setError("Password is Required.");
                    return;
                }

                if(mpassword.length()<6){
                    password.setError("Password must be more than 5 characters ");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                // register the user in firebase;

                auth.createUserWithEmailAndPassword(memail, mpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Registration.this, "User created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MapDriver.class));
                        }
                        else{
                            Toast.makeText(Registration.this, "Error !"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }
}