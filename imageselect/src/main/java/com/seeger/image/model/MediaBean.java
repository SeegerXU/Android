package com.seeger.image.model;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.File;

/**
 * 图片构建工具类
 *
 * @author Seeger
 */
public class MediaBean extends BaseBean{

    /**
     * 图片名称
     */
    private String name;
    /**
     * 图片路径
     */
    private String path;
    /**
     * 原路径
     */
    private String cropPath;

    /**
     * 该图片的父路径名
     */
    private String parentPath;
    /**
     * 获取图片的详细信息
     */
    private String desc;
    /**
     * 获取图片的生成日期
     */
    private byte[] date;

    private boolean isSelect;

    public MediaBean(Cursor cursor) {
        //获取图片的名称
        name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
        //获取图片的生成日期
        date = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        //获取图片的详细信息
        desc = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION));
        //获取图片的路径
        path = cursor.getString(cursor
                .getColumnIndex(MediaStore.Images.Media.DATA));
        //获取该图片的父路径名
        parentPath = new File(path).getParentFile().getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public byte[] getDate() {
        return date;
    }

    public void setDate(byte[] date) {
        this.date = date;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getCropPath() {
        return cropPath;
    }

    public void setCropPath(String cropPath) {
        this.cropPath = cropPath;
    }

    public String toString() {
        return "name: " + name
                + "    parentPath：" + parentPath
                + "    path：" + path;
    }
}
