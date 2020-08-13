package com.karrit.xoog;

public class sport_exercise_class {
    int ex_id,relax,time,ex_order;
    String ex_name;
    public sport_exercise_class(int ex_id,int relax,int time,String ex_name,int ex_order){
        this.ex_id=ex_id;
        this.ex_order=ex_order;
        this.ex_name=ex_name;
        this.relax=relax;
        this.time=time;
    }

    public int getEx_order() {
        return ex_order;
    }

    public void setEx_order(int ex_order) {
        this.ex_order = ex_order;
    }

    public int getTime() {
        return time;
    }

    public int getEx_id() {
        return ex_id;
    }

    public int getRelax() {
        return relax;
    }

    public String getEx_name() {
        return ex_name;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setEx_id(int ex_id) {
        this.ex_id = ex_id;
    }

    public void setEx_name(String ex_name) {
        this.ex_name = ex_name;
    }

    public void setRelax(int relax) {
        this.relax = relax;
    }
}
