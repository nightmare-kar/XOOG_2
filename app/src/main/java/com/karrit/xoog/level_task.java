package com.karrit.xoog;

public class level_task {
    int level,task;
    String task_type;
    int credits_earned;
    public level_task(int level,int task){
        this.level=level;
        this.task=task;
    }

    public void setCredits_earned(int credits_earned) {
        this.credits_earned = credits_earned;
    }

    public int getCredits_earned() {
        return credits_earned;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setTask(int task) {
        this.task = task;
    }

    public int getLevel() {
        return level;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getTask_type() {
        return task_type;
    }

    public int getTask() {
        return task;
    }
}
