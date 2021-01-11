package com.notes.test.ui.RecyclerView;

public class MyListData {

    private String  description, header , downloadableLink, previewImg, numberOfDownloads, id, type;

    public String getNumberOfDownloads() {
        return numberOfDownloads;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNumberOfDownloads(String numberOfDownloads) {
        this.numberOfDownloads = numberOfDownloads;
    }

    public MyListData(String description, String header, String downloadableLink, String previewImg) {
        this.description = description;
        this.header = header;
        this.downloadableLink = downloadableLink;
        this.previewImg = previewImg;
    }

    public String getPreviewImg() {
        return previewImg;
    }

    public void setPreviewImg(String previewImg) {
        this.previewImg = previewImg;
    }

    public MyListData() {
    }

    public String getDownloadableLink() {
        return downloadableLink;
    }

    public void setDownloadableLink(String downloadableLink) {
        this.downloadableLink = downloadableLink;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }

}
