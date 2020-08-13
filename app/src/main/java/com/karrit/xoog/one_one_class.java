package com.karrit.xoog;

public class one_one_class {
    private String desciption;
    private int time;
    private String link;
    public one_one_class(String desciption, int time){
        this.desciption=desciption;
        this.time=time;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getLink() {
        return link;
    }

    public String getDesciption() {
        return desciption;
    }

    public int getTime() {
        return time;
    }
}
