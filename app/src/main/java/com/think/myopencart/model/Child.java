package com.think.myopencart.model;

/**
 * Created by Super on 25-08-2016.
 */

public class Child {

    private String Name;
    private int Image;
    private String Id;

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int Image) {
        this.Image = Image;
    }

    public void setId(String Id) {
        this.Id = Id;
    }
    public String getId(){
        return Id;
    }
}