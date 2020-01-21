package com.huatu.handheld_huatu.helper;

import java.io.Serializable;

/**
 *

 */
public class Images implements Serializable {

    public int index;

    public String url;

    public String imageId;

    public Images(String url, int index) {
        this.url = url;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static Images getFirstImages(String url) {
        return new Images(url, 0);
    }


    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
