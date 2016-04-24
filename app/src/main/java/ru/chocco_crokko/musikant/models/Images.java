package ru.chocco_crokko.musikant.models;


/*
 * class, which contains two string - URL of images
 */
public class Images {
    private String small = null, big = null;

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getBig() {
        return big;
    }

    public void setBig(String big) {
        this.big = big;
    }
}