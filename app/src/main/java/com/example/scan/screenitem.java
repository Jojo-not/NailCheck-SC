package com.example.scan;

public class screenitem {
    String Title;
    int ScreenImg;

    public screenitem(String title, int screenImg) {
        Title = title;
        ScreenImg = screenImg;
    }

    public void setTitle(String title) {
        Title = title;
    }


    public void setScreenImg(int screenImg) {
        ScreenImg = screenImg;
    }

    public String getTitle() {
        return Title;
    }

    public int getScreenImg() {
        return ScreenImg;
    }
}
