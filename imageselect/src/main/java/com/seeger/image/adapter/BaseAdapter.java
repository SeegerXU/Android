package com.seeger.image.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * 列表Adapter基类
 *
 * @author Xuzj
 */
public abstract class BaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected OnItemClickListener onItemClickListener;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(view, position);
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 是否使用动画 默认显示加载动画
     *
     * @return
     */
    protected boolean isAnimation() {
        return true;
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    /**
     * 列表项点击事件
     *
     * @author Xuzj
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}
