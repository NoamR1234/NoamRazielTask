package com.example.homeworkplanner;


public class Task implements Rewardable {

    public static final String PRIORITY_LOW = "נמוכה";
    public static final String PRIORITY_MEDIUM = "בינונית";
    public static final String PRIORITY_HIGH = "גבוהה";

    private int id;
    private String title;
    private String subject;
    private String priority;
    private String dueDate;
    private boolean done;

    public Task(int id, String title, String subject, String priority, String dueDate) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.priority = priority;
        this.dueDate = dueDate;
        this.done = false;
    }

    public String getTypeName() {
        return "משימה";
    }

    protected int getPriorityBonus() {
        if (PRIORITY_HIGH.equals(priority)) {
            return 5;
        } else if (PRIORITY_MEDIUM.equals(priority)) {
            return 3;
        }
        return 1;
    }

    @Override
    public int getPoints() {
        return getPriorityBonus();
    }

    @Override
    public String toString() {
        String prefix = done ? "[בוצע] " : "";
        return prefix + title + "\n" + getTypeName() + " · " + subject + " · הגשה: " + dueDate;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getSubject() { return subject; }
    public String getPriority() { return priority; }
    public String getDueDate() { return dueDate; }
    public boolean isDone() { return done; }

    public void setDone(boolean done) { this.done = done; }
    public void setTitle(String title) { this.title = title; }
}
