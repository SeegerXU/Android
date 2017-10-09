package com.seeger.image.adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
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

    public class ContentViewHolder extends BaseHolder<MediaBean> {

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

        public void onItemSelected() {
            startAnimation();
        }

        public void onItemClear() {
            clearAnimation();
        }

        private void startAnimation(){
            /** 设置缩放动画 */
            final ScaleAnimation animation =new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(300);//设置动画持续时间
            /** 常用方法 */
            //animation.setRepeatCount(int repeatCount);//设置重复次数
            animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
            //animation.setStartOffset(long startOffset);//执行前的等待时间
            itemView.startAnimation(animation);
        }

        private void clearAnimation(){
            /** 设置缩放动画 */
            final ScaleAnimation animation =new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(300);//设置动画持续时间
            /** 常用方法 */
            //animation.setRepeatCount(int repeatCount);//设置重复次数
            animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
            //animation.setStartOffset(long startOffset);//执行前的等待时间
            itemView.startAnimation(animation);
        }
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
}
