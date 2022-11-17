package com.example.pam3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoryRVAdapter extends RecyclerView.Adapter<CategoryRVAdapter.ViewHolder>{
    Context context;
    ArrayList<String> categoryList;
    OnItemListener onItemListener;
    SharedPreferences sPrefs;

    public CategoryRVAdapter(Context context, ArrayList<String> categoryList, OnItemListener onItemListener) {
        this.context = context;
        this.categoryList = categoryList;
        this.onItemListener = onItemListener;
        this.sPrefs = context.getSharedPreferences("sPrefs", Context.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String categoryName = categoryList.get(position);

        holder.categoryTV.setText(categoryName);
        if (categoryName.equals("Any")) {
            holder.deleteBtn.setVisibility(View.INVISIBLE);
        }
        else
            holder.deleteBtn.setVisibility(View.VISIBLE);
        if (categoryName.equals(sPrefs.getString("category", "Any"))) {
            holder.constraintLayout.setBackgroundResource(R.color.orange2);
        }
        else {
            holder.constraintLayout.setBackgroundResource(R.color.white);
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void update(ArrayList<String> categoryList) {
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryTV;
        private ImageButton deleteBtn;
        private ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryTV = itemView.findViewById(R.id.categoryNameTV);
            deleteBtn = itemView.findViewById(R.id.categoryDeleteBtn);
            constraintLayout = itemView.findViewById(R.id.categoryItemLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemListener.onItemClick(getAdapterPosition());
                }
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemListener.onDelBtnClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
        void onDelBtnClick(int position);
    }
}
