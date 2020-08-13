package com.karrit.xoog;

public class show_video_class {
    private String description;
    private String link;
    public show_video_class(String description, String link){
        this.description=description;
        this.link=link;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
