package com.example.pam3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.example.pam3.TasksRecyclerView.TaskRVAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements CategoryRVAdapter.OnItemListener {
    private RecyclerView categoryRV;
    private CategoryRVAdapter categoryRVAdapter;
    private ArrayList<String> categoryList;
    private Switch hideTasksSwitch;
    private EditText newCategoryET;
    private RadioGroup radioGroup;
    private RadioButton[] rb = new RadioButton[3];

    private SharedPreferences sPrefs;
    private SharedPreferences.Editor editor;

    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.Settings);
        setContentView(R.layout.activity_settings);
        db = new DatabaseHandler(this);
        db.openDatabase();

        sPrefs = getSharedPreferences("sPrefs", Context.MODE_PRIVATE);
        editor = sPrefs.edit();

        hideTasksSwitch = findViewById(R.id.hideTasksSwitch);
        if (sPrefs.getInt("hideCompleted", 0) == 1)
            hideTasksSwitch.setChecked(true);

        categoryRV = findViewById(R.id.categoryRV);
        categoryList = db.getCategories();
        categoryRVAdapter = new CategoryRVAdapter(this, categoryList, this);
        categoryRV.setAdapter(categoryRVAdapter);
        newCategoryET = findViewById(R.id.newCategoryET);

        radioGroup = findViewById(R.id.radioGroup);
        rb[0] = findViewById(R.id.fiveMinRB);
        rb[1] = findViewById(R.id.fifteenMinRB);
        rb[2] = findViewById(R.id.thirtyMinutesRB);
        int rbChoice = sPrefs.getInt("notifications", 1);
        rb[rbChoice].setChecked(true);
    }

    public void hideTasksS(View view) {
        if (((CompoundButton)view).isChecked())
            editor.putInt("hideCompleted", 1).commit();
        else
            editor.putInt("hideCompleted", 0).commit();
    }

    public void addCategoryBtn(View view) {
        String newCategory = newCategoryET.getText().toString();
        if (newCategory.equals("") || categoryList.contains(newCategory)) {
            Toast.makeText(this, "Couldn't add category", Toast.LENGTH_SHORT).show();
        }
        else {
            db.insertCategory(newCategory);
            categoryList = db.getCategories();
            categoryRVAdapter.update(categoryList);
            Toast.makeText(this, "Category added!", Toast.LENGTH_SHORT).show();
        }
    }

    public void radioBtns(View view) {
        switch(view.getId()) {
            case R.id.fiveMinRB:
                editor.putInt("notifications", 0).commit();
                break;
            case R.id.fifteenMinRB:
                editor.putInt("notifications", 1).commit();
                break;
            case R.id.thirtyMinutesRB:
                editor.putInt("notifications", 2).commit();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemClick(int position) {
        editor.putString("category", categoryList.get(position)).commit();
        categoryRVAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDelBtnClick(int position) {
        boolean isDeleted = db.deleteCategory(categoryList.get(position));
        if (isDeleted) {
            categoryList.remove(position);
            categoryRVAdapter.notifyItemRemoved(position);
        }
        else {
            Toast.makeText(this, R.string.toastCategoryDelError, Toast.LENGTH_SHORT).show();
        }
    }
}