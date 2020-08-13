package com.karrit.xoog;

public class shopItems {
    private String Title;
private int id;
private String description;
    private int Credits ;
    private int Thumbnail ;

    public shopItems() {
    }

    public shopItems(String title, int credits, int thumbnail,int Id) {
        Title = title;
       Credits = credits;
        Thumbnail = thumbnail;
       id=Id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return Title;
    }

    public int getCredits() {
        return Credits;
    }


    public int getThumbnail() {
        return Thumbnail;
    }


    public void setTitle(String title) {
        Title = title;
    }

    public void setCredits(int credits) {
        Credits=credits;
    }



    public void setThumbnail(int thumbnail) {
        Thumbnail = thumbnail;
    }
}


