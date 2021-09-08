package com.example.myapplication.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.Models.ClassesModel;
import com.example.myapplication.R;
import com.example.myapplication.meetingActivity;
import java.util.ArrayList;

public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.Holder> {
    Context context;
    ArrayList<ClassesModel> list;
    String key;
    public ClassesAdapter(Context context, ArrayList<ClassesModel> list, String key) {
        this.context = context;
        this.list=list;
        this.key=key;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ClassesAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.calander_card, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ClassesModel model = list.get(position);
        holder.up.setText(model.getName());
        holder.down.setText(model.getStart()+"-"+model.getEnd());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, meetingActivity.class);
                i.putExtra("key",key);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView down, up;

        public Holder(@NonNull View itemView) {
            super(itemView);
            down=itemView.findViewById(R.id.down);
            up=itemView.findViewById(R.id.up);
        }
    }
}
