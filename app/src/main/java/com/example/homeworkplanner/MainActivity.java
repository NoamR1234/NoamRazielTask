package com.example.homeworkplanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etName;
    private Button btnEnter;
    private Button btnReset;
    private TextView tvWelcome;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(TaskStorage.PREFS_NAME, MODE_PRIVATE);

        etName = findViewById(R.id.etName);
        btnEnter = findViewById(R.id.btnEnter);
        btnReset = findViewById(R.id.btnReset);
        tvWelcome = findViewById(R.id.tvWelcome);

        btnEnter.setOnClickListener(v -> enter());
        btnReset.setOnClickListener(v -> confirmReset());
    }

    @Override
    protected void onResume() {
        super.onResume();
        showWelcome();
    }

    private void showWelcome() {
        String saved = prefs.getString(TaskStorage.KEY_STUDENT_NAME, "");
        if (saved.isEmpty()) {
            tvWelcome.setText(R.string.welcome_new);
        } else {
            tvWelcome.setText(getString(R.string.welcome_back, saved));
            etName.setText(saved);
        }
    }

    private void enter() {
        String name = etName.getText().toString().trim();
        if (name.length() < 2) {
            Toast.makeText(this, R.string.name_too_short, Toast.LENGTH_SHORT).show();
            return;
        }
        prefs.edit().putString(TaskStorage.KEY_STUDENT_NAME, name).apply();

        Intent intent = new Intent(this, TaskListActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    private void confirmReset() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.reset_title)
                .setMessage(R.string.reset_message)
                .setPositiveButton(R.string.reset_confirm, (dialog, which) -> {
                    prefs.edit().clear().apply();
                    etName.setText("");
                    tvWelcome.setText(R.string.welcome_new);
                    Toast.makeText(this, R.string.reset_done, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
