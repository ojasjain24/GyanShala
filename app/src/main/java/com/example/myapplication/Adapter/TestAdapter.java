package com.example.myapplication.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ContentView;
import com.example.myapplication.Models.TestModel;
import com.example.myapplication.R;

import java.util.ArrayList;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.Holder> {
    Context context;
    ArrayList<TestModel> data;
    String teacher;
    String course;
    public TestAdapter() {
    }

    public TestAdapter(Context context, ArrayList<TestModel> data, String teacher, String course) {
        this.context = context;
        this.data = data;
        this.teacher=teacher;
        this.course=course;
    }

    @NonNull
    @Override
    public TestAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TestAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.test_card, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TestAdapter.Holder holder, int position) {
        TestModel testModel = data.get(position);
        holder.name.setText(testModel.getTitle());
        holder.time.setText(testModel.getStartTime()+"\n"+testModel.getEndTime());
        holder.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ContentView.class);
                i.putExtra("teacher", teacher);
                i.putExtra("course", course);
                i.putExtra("link", testModel.getFile());
                i.putExtra("nodeId",testModel.getNodeId());
                context.startActivity(i);
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

        TextView name;
        TextView time;
        Button next;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView5);
            time = itemView.findViewById(R.id.textView4);
            next=itemView.findViewById(R.id.button3);
        }
    }

}
