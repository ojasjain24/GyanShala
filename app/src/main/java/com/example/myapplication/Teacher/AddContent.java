package com.example.myapplication.Teacher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.Models.CourseModel;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddContent extends AppCompatActivity {
    private ImageView upload, T1,T2,T3;
    private TextView uploadText;
    private String T1t,T2t,T3t;
    private EditText startTime, endTime, title;
    private DatabaseReference courseData;
    private Uri imageUri;
    private Spinner spinner;
    private static final int imageRequest = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_content);
        upload=findViewById(R.id.imageView);
        uploadText=findViewById(R.id.uploadFileText);
        startTime= findViewById(R.id.startTimeInput);
        endTime = findViewById(R.id.endTimeInput);
        spinner = findViewById(R.id.type);
        title = findViewById(R.id.titleInput);



        T1=findViewById(R.id.imageButtonT);
        T1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!title.getText().toString().trim().equals("")) {
                    title.setEnabled(false);
                    T1.setEnabled(false);
                    T1t = title.getText().toString();
                } else {
                    Toast.makeText(AddContent.this, "Please set a Title", Toast.LENGTH_SHORT).show();
                }
            }
        });
        T2=findViewById(R.id.imageButton2T);
        T2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!startTime.getText().toString().trim().equals("")) {
                    startTime.setEnabled(false);
                    T2.setEnabled(false);
                    T2t = startTime.getText().toString();
                } else {
                    Toast.makeText(AddContent.this, "Please add a Name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        T3=findViewById(R.id.imageButton3T);
        T3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!endTime.getText().toString().trim().equals("")) {
                    endTime.setEnabled(false);
                    T3.setEnabled(false);
                    T3t = endTime.getText().toString();
                } else {
                    Toast.makeText(AddContent.this, "Please add a Name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        uploadText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(T1t!=null && T2t!=null && T3t!=null) {
                    openImage();
                }else{
                    Toast.makeText(AddContent.this, "Fill all the data before uploading the file", Toast.LENGTH_SHORT).show();
                }
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.ContentType,R.layout.spinner_bg);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }


    private void openImage() {
        Intent intent=new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,imageRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==imageRequest && resultCode==RESULT_OK){
            assert data != null;
            imageUri=data.getData();
            uploadImage();
        }
    }
    private String getfilextention(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(){
        final ProgressDialog pd= new ProgressDialog(this);
        pd.setMessage("uploading");
        pd.show();
        if(imageUri != null){
            final StorageReference fileref = FirebaseStorage.getInstance().getReference().child("uploads").child(System.currentTimeMillis()+"."+getfilextention(imageUri));
            fileref.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                pd.dismiss();
                                courseData = FirebaseDatabase.getInstance().getReference().child("course").child(getIntent().getStringExtra("course")).child("content").push();
                                HashMap<String ,Object> usermap=new HashMap<>();
                                usermap.put("file", url);
                                usermap.put("endTime", endTime.getText().toString());
                                usermap.put("startTime",startTime.getText().toString());
                                usermap.put("type",spinner.getSelectedItem().toString());
                                usermap.put("title",title.getText().toString());
                                usermap.put("nodeId",courseData.getKey());
                                courseData.updateChildren(usermap);
                                startActivity(new Intent(AddContent.this,MainActivity.class));
                                Toast.makeText(AddContent.this, "upload successful", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddContent.this, "Error : " + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        Toast.makeText(AddContent.this, "Error : " + task.getException().toString(), Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

}