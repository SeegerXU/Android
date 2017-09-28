package com.seeger.image.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.seeger.image.R;

/**
 * 图片加载工具类
 *
 * @author Seeger
 */
public class ImageLoader {

    public static void loadImage(ImageView imageView, String path) {
        RequestOptions options = new RequestOptions().placeholder(R.drawable.icon_image).override(400, 400);
        Glide.with(imageView.getContext())
                .applyDefaultRequestOptions(options)
                .load(path)
                .into(imageView);
    }
}
