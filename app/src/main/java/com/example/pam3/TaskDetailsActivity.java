package com.example.pam3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.pam3.TasksRecyclerView.TaskRVData;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Locale;

public class TaskDetailsActivity extends AppCompatActivity {
    private final int CHOOSE_IMAGE_FROM_DEVICE = 1001;
    private final int EXTERNAL_STORAGE_REQUEST = 1002;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    private TextView cDateTV;
    private EditText titleET, categoryET, descET;
    private Button editBtn, eDateBtn, eTimeBtn, addBtn;
    private CheckBox checkBox;
    private Bundle extras;
    private DatabaseHandler db;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private ImageView image;

    private SharedPreferences sPrefs;

    int id, hour, minute;
    String imgPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_task_details);

        db = new DatabaseHandler(this);
        db.openDatabase();
        createNotificationChannel();

        sPrefs = getSharedPreferences("sPrefs", Context.MODE_PRIVATE);

        titleET = findViewById(R.id.titleET);
        categoryET = findViewById(R.id.categoryET);
        cDateTV = findViewById(R.id.cDateTDTV);

        initEDateBtn();
        initETimeBtn();
        descET = findViewById(R.id.descET);
        image = findViewById(R.id.attachmentImage);
        checkBox = findViewById(R.id.checkBox);
        editBtn = findViewById(R.id.editBtn);
        addBtn = findViewById(R.id.addAttachmentBtn);

        titleET.setEnabled(false);
        categoryET.setEnabled(false);
        eDateBtn.setEnabled(false);
        eTimeBtn.setEnabled(false);
        descET.setEnabled(false);
        addBtn.setVisibility(View.INVISIBLE);

        extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getInt("id");
            titleET.setText(extras.getString("title"));
            categoryET.setText(extras.getString("category"));
            cDateTV.setText(cDateTV.getText().toString() + " " + extras.getString("cDate"));
            eDateBtn.setText(extras.getString("eDate"));
            eTimeBtn.setText(extras.getString("eTime"));
            descET.setText(extras.getString("desc"));
            imgPath = extras.getString("imgPath");
            if (imgPath != null) {
                image.setImageURI(Uri.parse(imgPath));
                addBtn.setText(R.string.addBtn2);
            }

            if (extras.getInt("notification") == 1)
                checkBox.setChecked(true);
        }
    }

    public void chooseImageFromDevice(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQUEST);
        }
        else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, CHOOSE_IMAGE_FROM_DEVICE);
        }
    }

    public void editBtn(View view) {
        String btnValue = editBtn.getText().toString();
        if (btnValue.equals("Edit")) {
            editBtn.setText(R.string.saveBtn);
            titleET.setEnabled(true);
            eDateBtn.setEnabled(true);
            eTimeBtn.setEnabled(true);
            descET.setEnabled(true);
            addBtn.setVisibility(View.VISIBLE);
        }
        else {
            editBtn.setText(R.string.editBtn);
            titleET.setEnabled(false);
            eDateBtn.setEnabled(false);
            eTimeBtn.setEnabled(false);
            descET.setEnabled(false);
            addBtn.setVisibility(View.INVISIBLE);
            saveData();
        }
    }

    public void saveData() {
        String title = titleET.getText().toString();
        String category = categoryET.getText().toString();
        String desc = descET.getText().toString();
        String eDate = eDateBtn.getText().toString();
        String eTime = eTimeBtn.getText().toString();
        int id = extras.getInt("id");
        checkBox.setChecked(false);
        cancelAlarm();
        TaskRVData data = new TaskRVData(id, title, desc, category, null, eDate, eTime, 0, 0, imgPath);
        db.updateTask(data);
        db.updateNotification(extras.getInt("id"), 0);
        Toast.makeText(this, R.string.toastDataSaved, Toast.LENGTH_SHORT).show();
    }

    public void checkBox(View view) {
        if (((CompoundButton)view).isChecked()) {
            db.updateNotification(extras.getInt("id"), 1);
            setAlarm();
        }
        else {
            db.updateNotification(extras.getInt("id"), 0);
            cancelAlarm();
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE_FROM_DEVICE && resultCode == Activity.RESULT_OK) {
            image.setImageURI(data.getData());
            imgPath = data.getData().toString();
            addBtn.setText(R.string.addBtn2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case EXTERNAL_STORAGE_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.pGranted, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, R.string.pNotGranted, Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("channelID", name, importance);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void initEDateBtn() {
        eDateBtn = findViewById(R.id.eDateBtn);

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

        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);

        eDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
    }

    private void initETimeBtn() {
        eTimeBtn = findViewById(R.id.eTimeBtn);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                hour = h;
                minute = m;
                eTimeBtn.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };

        timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);

        eTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });
    }

    private void setAlarm() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);

        intent.putExtra("id", id);
        intent.putExtra("title", titleET.getText().toString());
        intent.putExtra("desc",  descET.getText().toString());
        intent.putExtra("category", categoryET.getText().toString());
        intent.putExtra("cDate", extras.getString("cDate"));
        intent.putExtra("eDate", eDateBtn.getText().toString());
        intent.putExtra("eTime", eTimeBtn.getText().toString());
        intent.putExtra("imgPath", extras.getString("imgPath"));

        pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String[] dateSplit = eDateBtn.getText().toString().split("-");
        String[] timeSplit = eTimeBtn.getText().toString().split(":");

        LocalDateTime dateTime = LocalDateTime.of(Integer.parseInt(dateSplit[0]),
                Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2]),
                Integer.parseInt(timeSplit[0]), Integer.parseInt(timeSplit[1]));
        long eDate = ZonedDateTime.of(dateTime, ZoneId.systemDefault()).toInstant().getEpochSecond();

        int min = 0;
        switch (sPrefs.getInt("notifications", 1)) {
            case 0:
                min = 5;
                break;
            case 1:
                min = 15;
                break;
            case 2:
                min = 30;
        }

        long trigger = eDate * 1000 - min * 60 * 1000;
        alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, pendingIntent);
    }
    private void cancelAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        }
        alarmManager.cancel(pendingIntent);
    }
}