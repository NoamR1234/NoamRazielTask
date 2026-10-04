package com.example.homeworkplanner;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddTaskActivity extends AppCompatActivity {

    private static final int TYPE_HOMEWORK = 0;

    private Spinner spType;
    private EditText etTitle;
    private Spinner spSubject;
    private Spinner spPriority;
    private EditText etDueDate;
    private EditText etAmount;
    private TextView tvAmountLabel;

    private TaskStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        setTitle(R.string.title_add);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        storage = new TaskStorage(this);

        spType = findViewById(R.id.spType);
        etTitle = findViewById(R.id.etTitle);
        spSubject = findViewById(R.id.spSubject);
        spPriority = findViewById(R.id.spPriority);
        etDueDate = findViewById(R.id.etDueDate);
        etAmount = findViewById(R.id.etAmount);
        tvAmountLabel = findViewById(R.id.tvAmountLabel);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        setupSpinner(spType, R.array.task_types);
        setupSpinner(spSubject, R.array.subjects);
        setupSpinner(spPriority, R.array.priorities);

        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == TYPE_HOMEWORK) {
                    tvAmountLabel.setText(R.string.amount_exercises);
                    etAmount.setHint(R.string.amount_hint_exercises);
                } else {
                    tvAmountLabel.setText(R.string.amount_topics);
                    etAmount.setHint(R.string.amount_hint_topics);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnSave.setOnClickListener(v -> saveTask());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void setupSpinner(Spinner spinner, int arrayRes) {
        ArrayAdapter<CharSequence> a = ArrayAdapter.createFromResource(
                this, arrayRes, android.R.layout.simple_spinner_item);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(a);
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        String due = etDueDate.getText().toString().trim();
        String amountText = etAmount.getText().toString().trim();

        if (title.isEmpty()) {
            toast(R.string.err_title);
            return;
        }
        if (!due.contains("/")) {
            toast(R.string.err_date);
            return;
        }
        if (amountText.isEmpty()) {
            if (spType.getSelectedItemPosition() == TYPE_HOMEWORK) {
                toast(R.string.err_empty_exercises);
            } else {
                toast(R.string.err_empty_topics);
            }
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(amountText);
        } catch (NumberFormatException e) {
            toast(R.string.err_amount_range);
            return;
        }
        if (amount < 1 || amount > 50) {
            toast(R.string.err_amount_range);
            return;
        }

        String subject = spSubject.getSelectedItem().toString();
        String priority = spPriority.getSelectedItem().toString();
        int id = storage.nextId();

        Task task;
        if (spType.getSelectedItemPosition() == TYPE_HOMEWORK) {
            task = new HomeworkTask(id, title, subject, priority, due, amount);
        } else {
            task = new ExamTask(id, title, subject, priority, due, amount);
        }
        storage.addTask(task);

        Toast.makeText(this, getString(R.string.saved_points, task.getPoints()),
                Toast.LENGTH_SHORT).show();
        finish();
    }

    private void toast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
