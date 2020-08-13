package com.karrit.xoog;

public class upload_class {
private String description;
private int number_pictures;
private Boolean show_scramble;
public upload_class(String description, int number_pictures, boolean show_scramble){
    this.description=description;
    this.number_pictures=number_pictures;
    this.show_scramble=show_scramble;
}

    public Boolean getShow_scramble() {
        return show_scramble;
    }

    public int getNumber_pictures() {
        return number_pictures;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNumber_pictures(int number_pictures) {
        this.number_pictures = number_pictures;
    }

    public void setShow_scramble(Boolean show_scramble) {
        this.show_scramble = show_scramble;
    }
}
