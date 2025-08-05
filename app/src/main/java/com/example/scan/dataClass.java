package com.example.scan;

public class dataClass {
    private String dataTitle;
    private String dataDes;
    private int dataImagee;
    private int dataDescInfo;
    private int dataDescImage;

    public dataClass(String dataTitle, String dataDes, int dataImagee, int dataDescInfo, int dataDescImage) {
        this.dataTitle = dataTitle;
        this.dataDes = dataDes;
        this.dataImagee = dataImagee;
        this.dataDescInfo = dataDescInfo;
        this.dataDescImage = dataDescImage;
    }

    public String getDataTitle() {
        return dataTitle;
    }

    public String getDataDes() {
        return dataDes;
    }

    public int getDataImagee() {
        return dataImagee;
    }

    public int getDataDescInfo() {
        return dataDescInfo;
    }

    public int getDataDescImage() {
        return dataDescImage;
    }

    public void setDataTitle(String dataTitle) {
        this.dataTitle = dataTitle;
    }

    public void setDataDes(String dataDes) {
        this.dataDes = dataDes;
    }

    public void setDataImagee(int dataImagee) {
        this.dataImagee = dataImagee;
    }

    public void setDataDescInfo(int dataDescInfo) {
        this.dataDescInfo = dataDescInfo;
    }

    public void setDataDescImage(int dataDescImage) {
        this.dataDescImage = dataDescImage;
    }
}