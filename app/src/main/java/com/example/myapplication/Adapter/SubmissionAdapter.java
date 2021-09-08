package com.example.myapplication.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.Models.SubmissionModel;
import com.example.myapplication.Models.userModel;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SubmissionAdapter extends RecyclerView.Adapter<SubmissionAdapter.Holder> {
    Context context;
    ArrayList<SubmissionModel> data;
    public SubmissionAdapter() {
    }

    public SubmissionAdapter(Context context, ArrayList<SubmissionModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public SubmissionAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubmissionAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.submission_card, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SubmissionAdapter.Holder holder, int position) {
        SubmissionModel testModel = data.get(position);
        Toast.makeText(context, "+"+testModel.getTime(), Toast.LENGTH_SHORT).show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(testModel.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel user = snapshot.getValue(userModel.class);
                holder.name.setText(user.getName());
                holder.email.setText(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(Long.parseLong(testModel.getTime()));
        holder.time.setText(sdf.format(resultdate));

        holder.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(Intent.ACTION_VIEW);
                I.setData(Uri.parse(testModel.getFile()));
                context.startActivity(I);
            }
        });
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView name, email;
        TextView time;
        Button next;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView5);
            time = itemView.findViewById(R.id.textView4);
            next=itemView.findViewById(R.id.button3);
            email=itemView.findViewById(R.id.textView14);
        }
    }
}
