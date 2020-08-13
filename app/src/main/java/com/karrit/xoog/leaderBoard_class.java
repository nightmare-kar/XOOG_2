package com.karrit.xoog;

public class leaderBoard_class {
    String name;

    String rank;
    String credits;
    public leaderBoard_class(String name,String rank,String credits){
        this.credits=credits;

        this.name=name;
        this.rank=rank;
    }

    public String getCredits() {
        return credits;
    }



    public String getName() {
        return name;
    }

    public String getRank() {
        return rank;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
