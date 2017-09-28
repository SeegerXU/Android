package com.seeger.image.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.seeger.image.R;
import com.seeger.image.model.MediaBean;

import java.util.List;

/**
 * 预览页面
 *
 * @author Seeger
 */
public class PreviewAdapter extends BaseAdapter {

    private List<MediaBean> data;

    public PreviewAdapter(List<MediaBean> data) {
        this.data = data;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ContentViewHolder viewHolder = (ContentViewHolder) holder;
        viewHolder.bindData(data.get(position));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContentViewHolder(parent, R.layout.item_preview);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    private class ContentViewHolder extends BaseHolder<MediaBean> {
        private PhotoView img_item;

        public ContentViewHolder(ViewGroup parent, @LayoutRes int resId) {
            super(parent, resId);
            img_item = getView(R.id.img_item);
        }

        @Override
        public void bindData(MediaBean data) {
            Glide.with(getContext()).load(data.getPath()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    img_item.disenable();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    img_item.enable();
                    return false;
                }
            }).into(img_item);
        }
    }
}
