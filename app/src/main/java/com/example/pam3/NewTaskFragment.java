package com.example.pam3;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pam3.TasksRecyclerView.TaskRVData;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewTaskFragment extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    private EditText titleET, descET;
    private Button addBtn, eDateBtn, eTimeBtn;
    private Spinner categorySpin;
    private DatabaseHandler db;

    int hour, minute;

    public static NewTaskFragment newInstance() {
        return new NewTaskFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        titleET = getView().findViewById(R.id.newTaskTitle);
        initCategorySpinner();
        initEDateBtn();
        initETimeBtn();
        descET = getView().findViewById(R.id.newTaskDesc);
        addBtn = getView().findViewById(R.id.addBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleET.getText().toString();
                String category = categorySpin.getSelectedItem().toString();
                String eDate = eDateBtn.getText().toString();
                String eTime = eTimeBtn.getText().toString();
                String desc = descET.getText().toString();
                if (title.equals("") || eDate.equals("") || desc.equals("") || category.equals("Category:"))
                    Toast.makeText(getContext(), R.string.toastValueError, Toast.LENGTH_SHORT).show();
                else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM HH:mm");
                    String cDate = sdf.format(new Date());
                    TaskRVData data = new TaskRVData(0, title, desc, category, cDate, eDate, eTime, 0, 0, null);
                    db.insertTask(data);
                    dismiss();
                }

            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof OnDialogListener)
            ((OnDialogListener)activity).onDialogClose(dialog);
    }

    public interface OnDialogListener {
        void onDialogClose(DialogInterface dialog);
    }

    private void initCategorySpinner() {
        categorySpin = getView().findViewById(R.id.newTaskCategory);

        ArrayList<String> list = db.getCategories();
        list.set(0, "Category:");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpin.setAdapter(adapter);
    }

    private void initEDateBtn() {
        eDateBtn = getView().findViewById(R.id.newTaskEDate);

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = year + "-" + String.format("%02d", month)  + "-" + String.format("%02d", day);
                eDateBtn.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(getContext(), dateSetListener, year, month, day);

        eDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
    }

    private void initETimeBtn() {
        eTimeBtn = getView().findViewById(R.id.newTaskETime);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                hour = h;
                minute = m;
                eTimeBtn.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };

        timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, hour, minute, true);

        eTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });
    }
}
