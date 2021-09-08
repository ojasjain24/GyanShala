package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;

public class profileActivity extends AppCompatActivity {
    private CircleImageView profilePic;
    private Spinner branch, batch;
    private Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profilePic = findViewById(R.id.Img);
        batch=findViewById(R.id.Batch);
        branch=findViewById(R.id.Branch);
        save=findViewById(R.id.button4);
        if(getIntent().getStringExtra("menu")!=null){
            batch.setEnabled(false);
            branch.setEnabled(false);
            save.setEnabled(false);
        }
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Batch,R.layout.spinner_bg);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        batch.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,R.array.Branch,R.layout.spinner_bg);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branch.setAdapter(adapter1);

        //TODO profile image
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                HashMap<String ,Object> usermap=new HashMap<>();
                usermap.put("batch", batch.getSelectedItem().toString());
                usermap.put("branch", branch.getSelectedItem().toString());
                reference.updateChildren(usermap);
                Intent i =new Intent(profileActivity.this,MainActivity.class);
                i.putExtra("batch", batch.getSelectedItem().toString());
                i.putExtra("branch", branch.getSelectedItem().toString());
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}