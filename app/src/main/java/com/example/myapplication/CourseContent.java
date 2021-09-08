package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.Adapter.CourseDashboardAdapter;
import com.example.myapplication.Adapter.TestAdapter;
import com.example.myapplication.Models.CourseModel;
import com.example.myapplication.Models.TestModel;
import com.example.myapplication.Models.membersModel;
import com.example.myapplication.Teacher.AddBatch;
import com.example.myapplication.Teacher.AddContent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class CourseContent extends AppCompatActivity {
    private Button addBatch, addContent;
    private RecyclerView recyclerView1,recyclerView2;
    private ArrayList<TestModel> Assignments = new ArrayList<>();
    private ArrayList<TestModel> Tests = new ArrayList<>();
    private TestAdapter testAdapter,testAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_content);
        Intent i = getIntent();
        addBatch = findViewById(R.id.button);
        addContent = findViewById(R.id.button2);
        recyclerView1=findViewById(R.id.testList);
        recyclerView2=findViewById(R.id.assignmentList);

        if(i.getStringExtra("teacher").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            addBatch.setVisibility(View.VISIBLE);
            addContent.setVisibility(View.VISIBLE);
            addBatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i1 = new Intent(CourseContent.this, AddBatch.class);
                    i1.putExtra("course", i.getStringExtra("course"));
                    startActivity(i1);
                }
            });
            addContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i1 = new Intent(CourseContent.this, AddContent.class);
                    i1.putExtra("course", i.getStringExtra("course"));
                    startActivity(i1);                }
            });
        }
        readTests(recyclerView1,recyclerView2);

    }

    public void readTests(RecyclerView recyclerView1,RecyclerView recyclerView2) {
        FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("course").child(getIntent().getStringExtra("course")).child("content");
        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Tests.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TestModel testData = snapshot.getValue(TestModel.class);
                    if(testData.getType().equals("Test")) {
                        Tests.add(testData);
                    }else{
                        Assignments.add(testData);
                    }
                }
                testAdapter = new TestAdapter(CourseContent.this, Tests, getIntent().getStringExtra("teacher"),getIntent().getStringExtra("course"));
                LinearLayoutManager manager =new LinearLayoutManager(CourseContent.this);
                manager.setReverseLayout(true);
                manager.setStackFromEnd(true);
                recyclerView1.setLayoutManager(manager);
                recyclerView1.setAdapter(testAdapter);

                testAdapter2 = new TestAdapter(CourseContent.this, Assignments, getIntent().getStringExtra("teacher"),getIntent().getStringExtra("course"));
                LinearLayoutManager manager1 =new LinearLayoutManager(CourseContent.this);
                manager1.setReverseLayout(true);
                manager1.setStackFromEnd(true); recyclerView2.setLayoutManager(manager1);
                recyclerView2.setAdapter(testAdapter2);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CourseContent.this, "check your network connection", Toast.LENGTH_SHORT).show();

            }
        });
    }
}