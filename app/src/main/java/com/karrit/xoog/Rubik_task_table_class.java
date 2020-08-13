package com.karrit.xoog;

public class Rubik_task_table_class {
    private String task_type;
    private int rest;
    private int credits;
    public Rubik_task_table_class(String task_type,int rest, int credits){
        this.task_type=task_type;
        this.rest=rest;
        this.credits=credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public int getCredits() {
        return credits;
    }

    public int getRest() {
        return rest;
    }

    public String getTask_type() {
        return task_type;
    }
}

