package com.example.pam3.TasksRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pam3.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TaskRVAdapter extends RecyclerView.Adapter<TaskRVAdapter.ViewHolder> {
    Context context;
    ArrayList<TaskRVData> dataArrayList;
    OnItemListener onItemListener;

    public TaskRVAdapter(Context context, ArrayList<TaskRVData> dataArrayList, OnItemListener onItemListener) {
        this.context = context;
        this.dataArrayList = dataArrayList;
        this.onItemListener = onItemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_rv_item, parent, false);
        return new ViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = dataArrayList.get(position).getTitle();
        String category = dataArrayList.get(position).getCategory();
        String date = dataArrayList.get(position).getExecutionDate().substring(5) + " " + dataArrayList.get(position).getExecutionTime();

        int status = dataArrayList.get(position).getStatus();

        holder.dateTV.setText(date);
        holder.titleTV.setText(title);
        holder.categoryTV.setText(category);
        holder.statusCB.setChecked(status == 1);
        if (dataArrayList.get(position).getImgPath() == null) {
            holder.icon.setVisibility(View.INVISIBLE);
        }
        else {
            holder.icon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dateTV;
        private TextView titleTV;
        private TextView categoryTV;
        private CheckBox statusCB;
        private OnItemListener onItemListener;
        private ImageView icon;

        public ViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            this.onItemListener = onItemListener;

            dateTV = itemView.findViewById(R.id.dateTV);
            titleTV = itemView.findViewById(R.id.titleTV);
            categoryTV = itemView.findViewById(R.id.categoryTV);
            statusCB = itemView.findViewById(R.id.statusCB);
            icon = itemView.findViewById(R.id.attachmentIcon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemListener.onItemClick(getAdapterPosition());
                }
            });
            statusCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    onItemListener.onCheckBoxChanged(getAdapterPosition(), isChecked);
                }
            });
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
        void onCheckBoxChanged(int position, boolean isChecked);
    }
}
