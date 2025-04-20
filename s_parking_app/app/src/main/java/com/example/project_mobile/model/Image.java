package com.example.project_mobile.model;

public class Image {
    private int imagesId;
    private String link;

    public int getImagesId() {
        return imagesId;
    }

    public void setImagesId(int imagesId) {
        this.imagesId = imagesId;
    }
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Image(int imagesId, String link) {
        this.imagesId = imagesId;
        this.link = link;
    }
}
