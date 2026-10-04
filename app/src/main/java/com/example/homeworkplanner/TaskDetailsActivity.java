package com.example.homeworkplanner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class TaskDetailsActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvType;
    private TextView tvSubject;
    private TextView tvPriority;
    private TextView tvDueDate;
    private TextView tvAmount;
    private TextView tvStatus;
    private TextView tvPoints;
    private Button btnToggleDone;
    private Button btnDelete;
    private Button btnBack;

    private TaskStorage storage;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        setTitle(R.string.title_details);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        storage = new TaskStorage(this);

        tvTitle = findViewById(R.id.tvTitle);
        tvType = findViewById(R.id.tvType);
        tvSubject = findViewById(R.id.tvSubject);
        tvPriority = findViewById(R.id.tvPriority);
        tvDueDate = findViewById(R.id.tvDueDate);
        tvAmount = findViewById(R.id.tvAmount);
        tvStatus = findViewById(R.id.tvStatus);
        tvPoints = findViewById(R.id.tvPoints);
        btnToggleDone = findViewById(R.id.btnToggleDone);
        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);

        int taskId = getIntent().getIntExtra("taskId", -1);
        task = storage.findById(taskId);
        if (task == null) {
            Toast.makeText(this, R.string.not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        showTask();

        btnToggleDone.setOnClickListener(v -> {
            task.setDone(!task.isDone());
            storage.updateTask(task);
            showTask();
            if (task.isDone()) {
                Toast.makeText(this, getString(R.string.done_toast, task.getPoints()),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.undone_toast, Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(R.string.delete_task_title)
                .setMessage(getString(R.string.delete_task_message, task.getTitle()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    storage.deleteById(task.getId());
                    Toast.makeText(this, R.string.task_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.cancel, null)
                .show());

        btnBack.setOnClickListener(v -> finish());
    }

    private void showTask() {
        tvTitle.setText(task.getTitle());
        tvType.setText(getString(R.string.d_type, task.getTypeName()));
        tvSubject.setText(getString(R.string.d_subject, task.getSubject()));
        tvPriority.setText(getString(R.string.d_priority, task.getPriority()));
        tvDueDate.setText(getString(R.string.d_due, task.getDueDate()));

        if (task instanceof HomeworkTask) {
            tvAmount.setText(getString(R.string.d_exercises, ((HomeworkTask) task).getExercises()));
        } else if (task instanceof ExamTask) {
            tvAmount.setText(getString(R.string.d_topics, ((ExamTask) task).getTopics()));
        }

        tvStatus.setText(task.isDone() ? R.string.d_status_done : R.string.d_status_open);
        tvPoints.setText(getString(R.string.d_points, task.getPoints()));
        btnToggleDone.setText(task.isDone() ? R.string.unmark_done : R.string.mark_done);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
