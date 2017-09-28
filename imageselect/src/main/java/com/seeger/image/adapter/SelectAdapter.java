package com.seeger.image.adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.seeger.image.R;
import com.seeger.image.model.MediaBean;
import com.seeger.image.utils.ImageLoader;
import com.seeger.image.utils.StringUtils;

import java.util.List;

/**
 * @author Seeger
 */
public class SelectAdapter extends BaseAdapter {

    private List<MediaBean> data;
    private int currentPosition = -1;

    public SelectAdapter(List<MediaBean> data) {
        this.data = data;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ContentViewHolder viewHolder = (ContentViewHolder) holder;
        viewHolder.bindData(data.get(position));
        if (position == currentPosition) {
            viewHolder.ll_border.setBackgroundResource(R.drawable.bg_current);
        } else {
            viewHolder.ll_border.setBackground(null);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContentViewHolder(parent, R.layout.item_select);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    private class ContentViewHolder extends BaseHolder<MediaBean> {

        private LinearLayout ll_border;
        private ImageView img_select;
        private ImageView img_copy;

        public ContentViewHolder(ViewGroup parent, @LayoutRes int resId) {
            super(parent, resId);
            ll_border = getView(R.id.ll_border);
            img_select = getView(R.id.img_select);
            img_copy = getView(R.id.img_copy);
        }

        @Override
        public void bindData(MediaBean data) {
            if (StringUtils.isBlank(data.getCropPath())) {
                img_copy.setVisibility(View.GONE);
                ImageLoader.loadImage(img_select, data.getPath());
            } else {
                img_copy.setVisibility(View.VISIBLE);
                ImageLoader.loadImage(img_select, data.getCropPath());
            }
        }
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
}
