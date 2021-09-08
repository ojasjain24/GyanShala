package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.Adapter.CourseDashboardAdapter;
import com.example.myapplication.Models.CourseModel;
import com.example.myapplication.Models.MemberBatchModel;
import com.example.myapplication.Models.membersModel;
import com.example.myapplication.Models.userModel;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SubjectDashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubjectDashboard extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    String batch, branch;
    private ArrayList<CourseModel> courseList = new ArrayList<>();
    private ArrayList<membersModel> membersList = new ArrayList<>();
    private CourseDashboardAdapter CourseDashboardAdapter;

    public SubjectDashboard() {
        // Required empty public constructor
    }
    public SubjectDashboard(String batch, String branch) {
        this.batch=batch;
        this.branch=branch;
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubjectDashboard.
     */
    // TODO: Rename and change types and number of parameters
    public static SubjectDashboard newInstance(String param1, String param2) {
        SubjectDashboard fragment = new SubjectDashboard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subject_dashboard, container, false);
        recyclerView=view.findViewById(R.id.dashboardList);
        Toast.makeText(getContext(), ""+batch+branch, Toast.LENGTH_SHORT).show();
        //TODO Fix

        readCourses();
        return view;
    }

    public void readCourses() {
        FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("course");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                courseList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CourseModel courses = snapshot.getValue(CourseModel.class);
                    if(courses.getTeacher().equals(me.getUid())) {
                        courseList.add(courses);
                    }else{
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("course").child(courses.getName()).child("members");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                for (DataSnapshot dataSnapshot1 : snapshot1.getChildren()) {
                                    MemberBatchModel batchModel = dataSnapshot1.getValue(MemberBatchModel.class);
                                    if(batchModel.getBatch().equals(batch)&&batchModel.getBranch().equals(branch)){
                                        courseList.add(courses);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                CourseDashboardAdapter = new CourseDashboardAdapter(getContext(), courseList);
                LinearLayoutManager manager =new LinearLayoutManager(getContext());
                manager.setReverseLayout(true);
                manager.setStackFromEnd(true);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(CourseDashboardAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "check your network connection", Toast.LENGTH_SHORT).show();

            }
        });
    }
}