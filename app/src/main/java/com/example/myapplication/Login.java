package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Models.userModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private TextView email;
    private TextView password;
    private FirebaseAuth auth;
    boolean doubleBackToExitPressedOnce = false;
    private TextView forgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email =findViewById(R.id.emailInput);
        password=findViewById(R.id.passwordInput);
        forgetPassword=findViewById(R.id.forgetPassword);

        Button login = findViewById(R.id.login);
        Button signup = findViewById(R.id.signupcheck);
        auth = FirebaseAuth.getInstance();
        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               startActivity(new Intent(Login.this,SignUp.class));
           }

        });
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgot_password(v);
            }
        });
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String usernameInput= email.getText().toString();
                String passwordInput=password.getText().toString();
                if(!(usernameInput.trim().equals("")||passwordInput.trim().equals(""))) {
                    loginUser(usernameInput.trim(), passwordInput.trim());
                }else{
                    Toast.makeText(Login.this, "please fill all the information", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void loginUser(String username,String password) {
        auth.signInWithEmailAndPassword(username , password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                if(auth.getCurrentUser().isEmailVerified()){
                    Toast.makeText(Login.this, "log-in successfully", Toast.LENGTH_SHORT).show();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            final userModel user = snapshot.getValue(userModel.class);
                            if (user.getBatch().equals("student") || user.getBranch().equals("student")) {
                                Intent i = new Intent(Login.this, profileActivity.class);
                                startActivity(i);
                            } else {
                                Intent i =new Intent(Login.this, MainActivity.class);
                                i.putExtra("batch", user.getBatch());
                                i.putExtra("branch", user.getBranch());
                                startActivity(i);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(),"Please Verify Your Email",Toast.LENGTH_SHORT).show();
                }
            }
        });
        auth.signInWithEmailAndPassword(username,password).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, "entered email or password not valid", Toast.LENGTH_SHORT).show();
            }
        });
    }
    protected void onStart(){
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            if(auth.getCurrentUser().isEmailVerified()) {
                Toast.makeText(Login.this, "log-in successfully", Toast.LENGTH_SHORT).show();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final userModel user = snapshot.getValue(userModel.class);
                        if (user.getBatch().equals("student") || user.getBranch().equals("student")) {
                            Intent i =new Intent(Login.this, profileActivity.class);
                            startActivity(i);

                        } else {
                            Intent i =new Intent(Login.this, MainActivity.class);
                            i.putExtra("batch", user.getBatch());
                            i.putExtra("branch", user.getBranch());
                            startActivity(i);
                            finish();
                            }
                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }
    private Boolean validateEmailDialogBox(EditText reset,String val) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";

        if (val.isEmpty()) {
            Toast.makeText(Login.this, "Email not be empty",Toast.LENGTH_SHORT).show();
            return false;
        } else if (!val.matches(emailPattern)) {
            Toast.makeText(Login.this, "Invalid Email",Toast.LENGTH_SHORT).show();
            return false;
        } else {
            reset.setError(null);
            return true;
        }
    }
    @SuppressLint("SetTextI18n")
    private void forgot_password(View v) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
        View view = getLayoutInflater().inflate(R.layout.change_password_dialog, null);
        Button yes = view.findViewById(R.id.yes);
        Button no = view.findViewById(R.id.no);
        final EditText enterEmail = view.findViewById(R.id.enterEmail);
        enterEmail.setHint("Enter Your Email...");
        TextView heading = view.findViewById(R.id.textchangepassword);
        heading.setText("Change Password");
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = enterEmail.getText().toString().trim();
                if (validateEmailDialogBox(enterEmail, mail)) {
                    auth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Login.this, "Reset Link Has been Sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login.this, "Error! Reset Link is Not Sent. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(Login.this, "Not Sent", Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            finishAffinity();
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
}