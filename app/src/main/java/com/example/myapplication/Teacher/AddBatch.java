package com.example.myapplication.Teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.Adapter.MembersAdapter;
import com.example.myapplication.Adapter.TestAdapter;
import com.example.myapplication.CourseContent;
import com.example.myapplication.Models.MemberBatchModel;
import com.example.myapplication.Models.TestModel;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.myapplication.BasicFunctions.spinnerGenerator;

public class AddBatch extends AppCompatActivity {
    private Spinner branch, batch;
    private Button save;
    private RecyclerView recyclerView;
    private ArrayList<MemberBatchModel> membersList = new ArrayList<>();
    private MembersAdapter membersAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_batch);
        save=findViewById(R.id.addBatchBtn);
        batch=findViewById(R.id.addBatch);
        branch=findViewById(R.id.addBranch);
        recyclerView=findViewById(R.id.membersList);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Batch,R.layout.spinner_bg);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        batch.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,R.array.Branch,R.layout.spinner_bg);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branch.setAdapter(adapter1);
        readMembers();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBatch();
            }
        });


    }
    private void addBatch(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("course").child(getIntent().getStringExtra("course")).child("members").push();
        HashMap<String ,Object> usermap=new HashMap<>();
        usermap.put("batch", batch.getSelectedItem().toString());
        usermap.put("branch", branch.getSelectedItem().toString());
        usermap.put("nodeId", reference.getKey());
        reference.updateChildren(usermap);
    }
    private void readMembers(){
        FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("course").child(getIntent().getStringExtra("course")).child("members");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                membersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MemberBatchModel testData = snapshot.getValue(MemberBatchModel.class);
                    membersList.add(testData);
                }
                membersAdapter = new MembersAdapter(AddBatch.this, membersList, getIntent().getStringExtra("course"));
                LinearLayoutManager manager =new LinearLayoutManager(AddBatch.this);
                manager.setReverseLayout(true);
                manager.setStackFromEnd(true);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(membersAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddBatch.this, "check your network connection", Toast.LENGTH_SHORT).show();

            }
        });
    }
}