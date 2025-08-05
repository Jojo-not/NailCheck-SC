package com.example.scan;

public class imageitem {
    String text,title;
    int image;

    public imageitem(String text, String title, int image) {
        this.text = text;
        this.title = title;
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
