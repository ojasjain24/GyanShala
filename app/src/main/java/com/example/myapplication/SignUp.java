package com.example.myapplication;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.Voice;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.firebase.ui.auth.util.ExtraConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SignUp extends AppCompatActivity {
    private Spinner type;
    private Button create;
    private TextView email,username,password;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference userdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        type = findViewById(R.id.typeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Type, R.layout.spinner_bg);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter);

        email=findViewById(R.id.email);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        auth= FirebaseAuth.getInstance();
        create = findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = email.getText().toString();
                String passwordInput = password.getText().toString();
                String usernameInput = username.getText().toString();
                if (TextUtils.isEmpty(emailInput) || TextUtils.isEmpty(passwordInput) || TextUtils.isEmpty(usernameInput)) {
                    Toast.makeText(SignUp.this, "Please fill all the information", Toast.LENGTH_SHORT).show();
                } else if (passwordInput.length() < 6) {
                    Toast.makeText(SignUp.this, "Password must contain at least 6 characters", Toast.LENGTH_SHORT).show();
                }else {
                    registerUser(emailInput.trim(), passwordInput.trim(), type.getSelectedItem().toString());
                }
            }
        });
    }

    private void registerUser(final String email, String password, String type) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Verification Link sent successfully", Toast.LENGTH_SHORT).show();
                                user=FirebaseAuth.getInstance().getCurrentUser();
                                userdata= FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                                HashMap<String, String> usermap = new HashMap<>();
                                usermap.put("userId", user.getUid());
                                usermap.put("name", username.getText().toString().trim());
                                usermap.put("email", user.getEmail().trim());
                                usermap.put("type",type);
                                if(type.equals("Teacher")){
                                    usermap.put("batch","teacher");
                                    usermap.put("branch","teacher");
                                    usermap.put("image","");
                                }else{
                                    usermap.put("batch","student");
                                    usermap.put("branch","student");
                                    usermap.put("image","");
                                }
                                userdata.setValue(usermap);


                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                if (!email.contains("@")) {
                    Toast.makeText(SignUp.this, "Email address does not exist", Toast.LENGTH_SHORT).show();
                }
                if (!task.isSuccessful()) {
                    Toast.makeText(SignUp.this, "user already exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}