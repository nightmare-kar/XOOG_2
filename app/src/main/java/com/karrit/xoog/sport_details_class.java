package com.karrit.xoog;

public class sport_details_class {
    int credits,rest,task_number,ex_num,credits_earned;
    String ex_type;
    public sport_details_class(int credits,int rest,int ex_num,int task_number,String ex_type,int credits_earned){
        this.credits=credits;
        this.ex_num=ex_num;
        this.rest=rest;
        this.ex_type=ex_type;
        this.task_number=task_number;
        this.credits_earned=credits_earned;

    }

    public int getCredits_earned() {
        return credits_earned;
    }

    public void setCredits_earned(int credits_earned) {
        this.credits_earned = credits_earned;
    }

    public int getRest() {
        return rest;
    }

    public int getCredits() {
        return credits;
    }

    public int getEx_num() {
        return ex_num;
    }

    public int getTask_number() {
        return task_number;
    }

    public String getEx_type() {
        return ex_type;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setEx_num(int ex_num) {
        this.ex_num = ex_num;
    }

    public void setEx_type(String ex_type) {
        this.ex_type = ex_type;
    }

    public void setTask_number(int task_number) {
        this.task_number = task_number;
    }
}
