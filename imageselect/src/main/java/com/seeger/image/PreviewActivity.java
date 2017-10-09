package com.seeger.image;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.seeger.image.adapter.BaseAdapter;
import com.seeger.image.adapter.PreviewAdapter;
import com.seeger.image.adapter.SelectAdapter;
import com.seeger.image.model.DataBean;
import com.seeger.image.model.MediaBean;
import com.seeger.image.utils.ImageOption;
import com.seeger.image.utils.ImageUtils;
import com.seeger.image.widget.CustomRecyclerView;
import com.soundcloud.android.crop.Crop;
import java.io.File;
import java.util.Collections;
import java.util.List;

import static com.seeger.image.utils.ImageOption.DATA_KEY;

/**
 * 预览界面
 *
 * @author Seeger
 */
public class PreviewActivity extends BaseActivity {

    private CustomRecyclerView rv_image;
    private RecyclerView rv_select;
    /**
     * 预览图片
     */
    private PreviewAdapter previewAdapter;
    /**
     * 选中
     */
    private SelectAdapter selectAdapter;
    private List<MediaBean> data;
    private List<MediaBean> selectImages;
    private LinearLayoutManager manager;
    private LinearLayoutManager sManager;
    private ItemTouchHelper itemTouchHelper;

    /**
     * 已选数量
     */
    private TextView tv_select;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        initToolbar();
        initUI();
        initData();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            refreshView();
        }
    }

    private void initData() {
        DataBean bean = (DataBean) getIntent().getSerializableExtra(DATA_KEY);
        if(bean.getType() == 0) {
            data = bean.getImages();
        }else{
            data = bean.getSelected();
        }
        selectImages = bean.getSelected();
        previewAdapter = new PreviewAdapter(data);
        rv_image.setAdapter(previewAdapter);
        setTitle((bean.getCurrentPosition() + 1) + "/" + previewAdapter.getItemCount());
        rv_image.scrollToPosition(bean.getCurrentPosition());
        selectAdapter = new SelectAdapter(selectImages);
        rv_select.setAdapter(selectAdapter);
        selectAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                manager.scrollToPosition(data.indexOf(selectImages.get(position)));
            }
        });
    }

    private void initUI() {
        tv_select = (TextView) findViewById(R.id.tv_select);
        rv_image = (CustomRecyclerView) findViewById(R.id.rv_image);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_image.setLayoutManager(manager);
        rv_select = (RecyclerView) findViewById(R.id.rv_select);
        sManager = new LinearLayoutManager(this);
        sManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_select.setLayoutManager(sManager);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rv_image);
        rv_image.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE://滑动停止
                        setTitle((manager.findFirstVisibleItemPosition() + 1) + "/" + previewAdapter.getItemCount());
                        refreshView();
                        break;
                }
            }
        });
        rv_right.setVisibility(View.VISIBLE);
        rv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!data.get(manager.findFirstVisibleItemPosition()).isSelect()) {
                    data.get(manager.findFirstVisibleItemPosition()).setSelect(true);
                    selectImages.add(data.get(manager.findFirstVisibleItemPosition()));
                } else {
                    selectImages.remove(data.get(manager.findFirstVisibleItemPosition()));
                    data.get(manager.findFirstVisibleItemPosition()).setSelect(false);
                    selectAdapter.setCurrentPosition(-1);
                }
                refreshView();
            }
        });
        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                // 如果是横向Linear布局，则能左右拖动，上下滑动
                int swipeFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //得到当拖拽的viewHolder的Position
                int fromPosition = viewHolder.getAdapterPosition();
                //拿到当前拖拽到的item的viewHolder
                int toPosition = target.getAdapterPosition();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(selectImages, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(selectImages, i, i - 1);
                    }
                }
                selectAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                selectAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        itemTouchHelper.attachToRecyclerView(rv_select);
    }

    private void refreshView() {
        if (data.get(manager.findFirstVisibleItemPosition()).isSelect()) {
            img_right.setVisibility(View.GONE);
            tv_right.setVisibility(View.VISIBLE);
            tv_right.setText("" + (selectImages.indexOf(data.get(manager.findFirstVisibleItemPosition())) + 1));
            selectAdapter.setCurrentPosition(selectImages.indexOf(data.get(manager.findFirstVisibleItemPosition())));
            selectAdapter.notifyDataSetChanged();
        } else {
            img_right.setVisibility(View.VISIBLE);
            img_right.setImageResource(R.mipmap.icon_normal);
            tv_right.setVisibility(View.GONE);
            selectAdapter.setCurrentPosition(-1);
            selectAdapter.notifyDataSetChanged();
        }
        tv_select.setText("完成(" + selectAdapter.getItemCount() + ")");
    }

    public void editAction(View view) {
        beginCrop(ImageUtils.getImageContentUri(this, data.get(manager.findFirstVisibleItemPosition()).getPath()));
    }

    public void finishAction(View view) {
        Intent intent = new Intent();
        Bundle mBundle = new Bundle();
        DataBean dataBean = new DataBean();
        dataBean.setSelected(selectImages);
        mBundle.putSerializable(DATA_KEY, dataBean);
        intent.putExtras(mBundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(ImageUtils.getExternalCacheDir(), "cropped" + System.currentTimeMillis() + ".jpg"));
        Crop.of(source, destination).start(this);
    }

    /**
     * 页面启动
     *
     * @param dataBean
     * @param activity
     */
    public static void startPreview(DataBean dataBean, Activity activity) {
        Intent intent = new Intent(activity, PreviewActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(ImageOption.DATA_KEY, dataBean);
        intent.putExtras(mBundle);
        activity.startActivityForResult(intent, ImageOption.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Log.v("ImageSelect", "path = " + ImageUtils.getImageAbsolutePath(this, Crop.getOutput(result)));
            if (selectAdapter.getCurrentPosition() != -1) {
                data.get(manager.findFirstVisibleItemPosition()).setCropPath(ImageUtils.getImageAbsolutePath(this, Crop.getOutput(result)));
                selectImages.set(selectAdapter.getCurrentPosition(), data.get(manager.findFirstVisibleItemPosition()));
            } else {
                data.get(manager.findFirstVisibleItemPosition()).setCropPath(ImageUtils.getImageAbsolutePath(this, Crop.getOutput(result)));
                data.get(manager.findFirstVisibleItemPosition()).setSelect(true);
                selectImages.add(data.get(manager.findFirstVisibleItemPosition()));
            }
            refreshView();
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
