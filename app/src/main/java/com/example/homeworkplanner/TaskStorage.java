package com.example.homeworkplanner;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class TaskStorage {

    public static final String PREFS_NAME = "planner_prefs";
    public static final String KEY_STUDENT_NAME = "student_name";
    private static final String KEY_NEXT_ID = "next_id";
    private static final String KEY_HOMEWORK = "homework_list";
    private static final String KEY_EXAMS = "exam_list";

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public TaskStorage(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public ArrayList<Task> loadAll() {
        ArrayList<Task> all = new ArrayList<>();

        Type hwType = new TypeToken<ArrayList<HomeworkTask>>() {}.getType();
        ArrayList<HomeworkTask> hw = gson.fromJson(prefs.getString(KEY_HOMEWORK, "[]"), hwType);
        if (hw != null) {
            all.addAll(hw);
        }

        Type exType = new TypeToken<ArrayList<ExamTask>>() {}.getType();
        ArrayList<ExamTask> ex = gson.fromJson(prefs.getString(KEY_EXAMS, "[]"), exType);
        if (ex != null) {
            all.addAll(ex);
        }
        return all;
    }

    public void saveAll(ArrayList<Task> tasks) {
        ArrayList<HomeworkTask> hw = new ArrayList<>();
        ArrayList<ExamTask> ex = new ArrayList<>();
        for (Task t : tasks) {
            if (t instanceof HomeworkTask) {
                hw.add((HomeworkTask) t);
            } else if (t instanceof ExamTask) {
                ex.add((ExamTask) t);
            }
        }
        prefs.edit()
                .putString(KEY_HOMEWORK, gson.toJson(hw))
                .putString(KEY_EXAMS, gson.toJson(ex))
                .apply();
    }

    public void addTask(Task task) {
        ArrayList<Task> all = loadAll();
        all.add(task);
        saveAll(all);
    }

    public Task findById(int id) {
        for (Task t : loadAll()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public void updateTask(Task task) {
        ArrayList<Task> all = loadAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == task.getId()) {
                all.set(i, task);
                break;
            }
        }
        saveAll(all);
    }

    public void deleteById(int id) {
        ArrayList<Task> all = loadAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == id) {
                all.remove(i);
                break;
            }
        }
        saveAll(all);
    }

    public int nextId() {
        int id = prefs.getInt(KEY_NEXT_ID, 1);
        prefs.edit().putInt(KEY_NEXT_ID, id + 1).apply();
        return id;
    }
}
