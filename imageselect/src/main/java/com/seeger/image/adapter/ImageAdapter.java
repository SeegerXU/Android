package com.seeger.image.adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seeger.image.R;
import com.seeger.image.model.MediaBean;
import com.seeger.image.utils.ImageLoader;

import java.util.List;

/**
 * @author Seeger
 */
public class ImageAdapter extends BaseAdapter {

    private List<MediaBean> data;
    private List<MediaBean> select;

    public ImageAdapter(List<MediaBean> data) {
        this.data = data;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        ContentViewHolder viewHolder = (ContentViewHolder) holder;
        viewHolder.bindData(data.get(position));
        viewHolder.img_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, position);
                }
            }
        });
        viewHolder.tv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContentViewHolder(parent, R.layout.item_image);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    private class ContentViewHolder extends BaseHolder<MediaBean> {

        private ImageView img_item;
        private ImageView img_normal;
        private TextView tv_select;

        public ContentViewHolder(ViewGroup parent, @LayoutRes int resId) {
            super(parent, resId);
            img_item = getView(R.id.img_item);
            img_normal = getView(R.id.img_normal);
            tv_select = getView(R.id.tv_select);
        }

        @Override
        public void bindData(MediaBean data) {
            ImageLoader.loadImage(img_item, data.getPath());
            if (data.isSelect()) {
                tv_select.setVisibility(View.VISIBLE);
                tv_select.setText((select.indexOf(data) + 1) + "");
                img_normal.setVisibility(View.GONE);
            } else {
                tv_select.setVisibility(View.GONE);
                img_normal.setVisibility(View.VISIBLE);
            }
        }
    }

    public List<MediaBean> getSelect() {
        return select;
    }

    public void setSelect(List<MediaBean> select) {
        this.select = select;
    }

}
