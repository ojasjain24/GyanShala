package com.example.myapplication.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CourseContent;
import com.example.myapplication.Models.CourseModel;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CourseDashboardAdapter extends RecyclerView.Adapter<CourseDashboardAdapter.Holder> {
    Context context;
    ArrayList<CourseModel> data;

    public CourseDashboardAdapter() {
    }

    public CourseDashboardAdapter(Context context, ArrayList<CourseModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.course_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        CourseModel courseModel = data.get(position);
        holder.name.setText(courseModel.getName());
        if(courseModel.getImage() != null) {
            Picasso.get().load(Uri.parse(courseModel.getImage())).fit().centerCrop().into(holder.image);
        }else{
            holder.image.setImageResource(R.drawable.ic_launcher_foreground);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, CourseContent.class);
                i.putExtra("teacher",courseModel.getTeacher());
                i.putExtra("course", courseModel.getName());
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
        ImageView image;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.courseName);
            image = itemView.findViewById(R.id.imageView2);
        }
    }

}
