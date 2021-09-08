package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.SubmissionAdapter;
import com.example.myapplication.Adapter.TestAdapter;
import com.example.myapplication.Models.SubmissionModel;
import com.example.myapplication.Models.TestModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ContentView extends AppCompatActivity {
    ImageView download, upload;
    TextView downloadText, uploadText, submissionText;
    private RecyclerView  submissionsList;
    private Uri imageUri;
    private static final int imageRequest = 1;
    private ArrayList<SubmissionModel> submissions = new ArrayList<>();
    private SubmissionAdapter submissionAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_view);
        download = findViewById(R.id.downloadTask);
        upload = findViewById(R.id.uploadTask);
        downloadText = findViewById(R.id.downloadText);
        uploadText = findViewById(R.id.UploadText);
        submissionText=findViewById(R.id.textView13);
        submissionsList=findViewById(R.id.submissions);
        if(getIntent().getStringExtra("teacher").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            submissionsList.setVisibility(View.VISIBLE);
            submissionText.setVisibility(View.VISIBLE);
            upload.setVisibility(View.GONE);
            uploadText.setVisibility(View.GONE);
            readSubmissions();
        }
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadImage(getIntent().getStringExtra("link"));
            }
        });
        downloadText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadImage(getIntent().getStringExtra("link"));
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
        uploadText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

    }
    private void readSubmissions(){

        FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("course").child(getIntent().getStringExtra("course")).child("content");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                submissions.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TestModel data = snapshot.getValue(TestModel.class);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("course").child(getIntent().getStringExtra("course")).child("content").child(data.getNodeId()).child("submissions");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                SubmissionModel model = dataSnapshot1.getValue(SubmissionModel.class);
                                submissions.add(model);

                            }
                            submissionAdapter = new SubmissionAdapter(ContentView.this, submissions);
                            LinearLayoutManager manager =new LinearLayoutManager(ContentView.this);
                            manager.setReverseLayout(true);
                            manager.setStackFromEnd(true);
                            submissionsList.setLayoutManager(manager);
                            submissionsList.setAdapter(submissionAdapter);
                            Toast.makeText(ContentView.this, "Done", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ContentView.this, "check your network connection", Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void openImage() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, imageRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == imageRequest && resultCode == RESULT_OK) {
            assert data != null;
            imageUri = data.getData();
            uploadImage();
        }
    }

    private String getfilextention(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("uploading");
        pd.show();
        if (imageUri != null) {
            final StorageReference fileref = FirebaseStorage.getInstance().getReference().child("uploads").child(System.currentTimeMillis() + "." + getfilextention(imageUri));
            fileref.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                pd.dismiss();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("course").child(getIntent().getStringExtra("course")).child("content").child(getIntent().getStringExtra("nodeId")).child("submissions").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                map.put("file", ""+url);
                                map.put("time", System.currentTimeMillis() + "");
                                reference.updateChildren(map);
                                startActivity(new Intent(ContentView.this, MainActivity.class));
                                Toast.makeText(ContentView.this, "upload successful", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ContentView.this, "Error : " + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(ContentView.this, "Error : " + task.getException().toString(), Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }


    void DownloadImage(String ImageUrl) {

        if (ContextCompat.checkSelfPermission(ContentView.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(ContentView.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ContentView.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            ActivityCompat.requestPermissions(ContentView.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            Toast.makeText(this, "Need Permission to access storage for Downloading Image", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Downloading Image", Toast.LENGTH_SHORT).show();
            //Asynctask to create a thread to downlaod image in the background
            new DownloadsImage().execute(ImageUrl);
        }
    }

    class DownloadsImage extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(strings[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap bm = null;
            try {
                bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Create Path to save Image
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/GyanShala Media"); //Creates app specific folder

            if (!path.exists()) {
                path.mkdirs();
            }

            File imageFile = new File(path, String.valueOf(System.currentTimeMillis()) + "." + getIntent().getStringExtra("Type"));
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(imageFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
                out.flush();
                out.close();
                // Tell the media scanner about the new file so that it is
                // immediately available to the user.
                MediaScannerConnection.scanFile(ContentView.this, new String[]{imageFile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        // Log.i("ExternalStorage", "Scanned " + path + ":");
                        //    Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(ContentView.this, "Downloaded", Toast.LENGTH_SHORT).show();
        }
    }
}
