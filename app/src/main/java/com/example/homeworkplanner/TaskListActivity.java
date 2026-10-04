package com.example.homeworkplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class TaskListActivity extends AppCompatActivity {

    private TextView tvHello;
    private TextView tvStats;
    private TextView tvEmpty;
    private Spinner spFilter;
    private ListView lvTasks;
    private Button btnAdd;
    private Button btnLogout;

    private TaskStorage storage;
    private ArrayAdapter<Task> adapter;
    private final ArrayList<Task> shown = new ArrayList<>();
    private String[] filterOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        setTitle(R.string.title_list);

        storage = new TaskStorage(this);

        tvHello = findViewById(R.id.tvHello);
        tvStats = findViewById(R.id.tvStats);
        tvEmpty = findViewById(R.id.tvEmpty);
        spFilter = findViewById(R.id.spFilter);
        lvTasks = findViewById(R.id.lvTasks);
        btnAdd = findViewById(R.id.btnAdd);
        btnLogout = findViewById(R.id.btnLogout);

        String name = getIntent().getStringExtra("name");
        if (name == null) {
            name = getSharedPreferences(TaskStorage.PREFS_NAME, MODE_PRIVATE)
                    .getString(TaskStorage.KEY_STUDENT_NAME, "");
        }
        tvHello.setText(getString(R.string.hello_user, name));


        String[] subjects = getResources().getStringArray(R.array.subjects);
        filterOptions = new String[subjects.length + 1];
        filterOptions[0] = getString(R.string.all);
        System.arraycopy(subjects, 0, filterOptions, 1, subjects.length);
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, filterOptions);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilter.setAdapter(filterAdapter);


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shown);
        lvTasks.setAdapter(adapter);

        spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        lvTasks.setOnItemClickListener((parent, view, position, id) -> {
            Task task = shown.get(position);
            Intent intent = new Intent(TaskListActivity.this, TaskDetailsActivity.class);
            intent.putExtra("taskId", task.getId());
            startActivity(intent);
        });

        lvTasks.setOnItemLongClickListener((parent, view, position, id) -> {
            confirmDelete(shown.get(position));
            return true;
        });

        btnAdd.setOnClickListener(v ->
                startActivity(new Intent(TaskListActivity.this, AddTaskActivity.class)));

        btnLogout.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(R.string.logout_title)
                .setMessage(R.string.logout_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> finish())
                .setNegativeButton(R.string.no, null)
                .show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }


    private void refresh() {
        ArrayList<Task> all = storage.loadAll();

        int doneCount = 0;
        int points = 0;
        for (Task t : all) {
            if (t.isDone()) {
                doneCount++;
                points += t.getPoints();
            }
        }
        tvStats.setText(getString(R.string.stats, all.size(), doneCount, points));

        int pos = spFilter.getSelectedItemPosition();
        shown.clear();
        for (Task t : all) {
            if (pos <= 0 || t.getSubject().equals(filterOptions[pos])) {
                shown.add(t);
            }
        }
        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(shown.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void confirmDelete(final Task task) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_task_title)
                .setMessage(getString(R.string.delete_task_message, task.getTitle()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    storage.deleteById(task.getId());
                    refresh();
                    Toast.makeText(this, R.string.task_deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
