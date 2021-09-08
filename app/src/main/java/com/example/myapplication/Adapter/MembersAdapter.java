package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.Models.MemberBatchModel;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.Holder> {
    Context context;
    ArrayList<MemberBatchModel> data;
    String course;
    public MembersAdapter(Context context, ArrayList<MemberBatchModel> data, String course) {
        this.context = context;
        this.data = data;
        this.course=course;
    }

    @NonNull
    @Override
    public MembersAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MembersAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.batch_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MembersAdapter.Holder holder, int position) {
        MemberBatchModel model = data.get(position);
        holder.batch.setText(model.getBatch());
        holder.branch.setText(model.getBranch());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("course").child(course).child("members").child(model.getNodeId()).setValue(null).
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, model.getBranch() + ", " + model.getBatch() + " removed", Toast.LENGTH_SHORT).show();
                            }
                        });
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

        TextView batch;
        TextView branch;
        ImageView delete;
        public Holder(@NonNull View itemView) {
            super(itemView);
            batch = itemView.findViewById(R.id.batch);
            branch = itemView.findViewById(R.id.branch);
            delete=itemView.findViewById(R.id.delete);
        }
    }
}
