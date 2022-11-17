package com.example.pam3;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.pam3.TasksRecyclerView.TaskRVAdapter;
import com.example.pam3.TasksRecyclerView.TaskRVData;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TaskRVAdapter.OnItemListener, NewTaskFragment.OnDialogListener {
    private RecyclerView taskRV;
    private TaskRVAdapter taskRVAdapter;
    private ArrayList<TaskRVData> dataArrayList;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        db = new DatabaseHandler(this);
        db.openDatabase();
        //db.setDb();
        taskRV = findViewById(R.id.taskRV);
        dataArrayList = new ArrayList<>();
        taskRVAdapter = new TaskRVAdapter(this, dataArrayList, this);
        taskRV.setAdapter(taskRVAdapter);

        getTasksData(null);
    }

    public void settingsBtn(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    public void searchBtn(View view) {
        EditText editText = findViewById(R.id.searchEditText);
        String searchText = editText.getText().toString();
        if (searchText.equals(""))
            getTasksData(null);
        else
            getTasksData(searchText);
    }

    public void addTaskBtn(View view) {
        NewTaskFragment.newInstance().show(getSupportFragmentManager(), NewTaskFragment.TAG);
    }

    @Override
    public void onItemClick(int position) {
        TaskRVData data = dataArrayList.get(position);
        Intent intent = new Intent(this, TaskDetailsActivity.class);

        intent.putExtra("id", data.getId());
        intent.putExtra("title", data.getTitle());
        intent.putExtra("desc", data.getDesc());
        intent.putExtra("category", data.getCategory());
        intent.putExtra("cDate", data.getCreationDate());
        intent.putExtra("eDate", data.getExecutionDate());
        intent.putExtra("eTime", data.getExecutionTime());
        intent.putExtra("status", data.getStatus());
        intent.putExtra("notification", data.getNotification());
        intent.putExtra("imgPath", data.getImgPath());

        startActivity(intent);
        finish();
    }

    @Override
    public void onCheckBoxChanged(int position, boolean isChecked) {
        if (isChecked) {
            db.updateStatus(dataArrayList.get(position).getId(), 1);
        }
        else {
            db.updateStatus(dataArrayList.get(position).getId(), 0);
        }
    }

    @Override
    public void onDialogClose(DialogInterface dialog) {
        getTasksData(null);
    }

    private void getTasksData(@Nullable String searchText) {
        String category;
        Integer status = null;
        SharedPreferences sPrefs = getSharedPreferences("sPrefs", Context.MODE_PRIVATE);
        if (sPrefs.getInt("hideCompleted", 0) == 1)
            status = 0;
        category = sPrefs.getString("category", "Any");

        db.getTasks(category, status, searchText, dataArrayList);
        taskRVAdapter.notifyDataSetChanged();
    }
}