package com.seeger.image.model;

import java.util.List;

/**
 * 图片内容
 *
 * @author Seeger
 */
public class DataBean extends BaseBean {

    /**
     * 展示类型
     */
    private int type;

    private int currentPosition;

    private List<MediaBean> images;

    private List<MediaBean> selected;

    public List<MediaBean> getImages() {
        return images;
    }

    public void setImages(List<MediaBean> images) {
        this.images = images;
    }

    public List<MediaBean> getSelected() {
        return selected;
    }

    public void setSelected(List<MediaBean> selected) {
        this.selected = selected;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
