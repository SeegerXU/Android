package com.seeger.image;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seeger.image.adapter.BaseAdapter;
import com.seeger.image.adapter.ImageAdapter;
import com.seeger.image.adapter.PreviewAdapter;
import com.seeger.image.adapter.SelectAdapter;
import com.seeger.image.model.DataBean;
import com.seeger.image.model.MediaBean;
import com.seeger.image.utils.ImageUtils;
import com.seeger.image.widget.CustomDecoration;
import com.seeger.image.widget.CustomRecyclerView;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 图片选择
 */
public class ImageActivity extends BaseActivity {

    private List<MediaBean> data = new ArrayList<>();
    private ImageAdapter imageAdapter;
    private RecyclerView rv_image;
    private GridLayoutManager gManager;
    private List<MediaBean> selectImages = new ArrayList<>();
    private TextView tv_finish;
    /**
     * 图片预览
     */
    private CustomRecyclerView rv_image_preview;
    private RecyclerView rv_select;
    /**
     * 预览图片
     */
    private PreviewAdapter previewAdapter;
    /**
     * 选中
     */
    private SelectAdapter selectAdapter;
    private LinearLayoutManager manager;
    private LinearLayoutManager sManager;
    private ItemTouchHelper itemTouchHelper;

    /**
     * 已选数量
     */
    private TextView tv_select;

    /**
     * 列表页面
     */
    private LinearLayout ll_list;
    /**
     * 预览页面
     */
    private LinearLayout ll_preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initUI();
        getImage();
    }

    private void initUI() {
        ll_list = (LinearLayout) findViewById(R.id.ll_list);
        ll_preview = (LinearLayout) findViewById(R.id.ll_preview);
        //列表页面
        toolbar.setNavigationIcon(null);
        tv_finish = (TextView) findViewById(R.id.tv_finish);
        rv_image = (RecyclerView) findViewById(R.id.rv_image);
        gManager = new GridLayoutManager(this, 4);
        gManager.setOrientation(GridLayoutManager.VERTICAL);
        rv_image.setLayoutManager(gManager);
        rv_image.addItemDecoration(new CustomDecoration());

        //预览页面
        tv_select = (TextView) findViewById(R.id.tv_select);
        rv_image_preview = (CustomRecyclerView) findViewById(R.id.rv_image_preview);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_image_preview.setLayoutManager(manager);
        rv_select = (RecyclerView) findViewById(R.id.rv_select);
        sManager = new LinearLayoutManager(this);
        sManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_select.setLayoutManager(sManager);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rv_image_preview);
        rv_image_preview.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        rv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!data.get(manager.findFirstVisibleItemPosition()).isSelect()) {
                    if (selectImages.size() > 5) {
                        Toast.makeText(ImageActivity.this, "最多选择六张", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    data.get(manager.findFirstVisibleItemPosition()).setSelect(true);
                    selectImages.add(data.get(manager.findFirstVisibleItemPosition()));
                } else {
                    selectImages.remove(data.get(manager.findFirstVisibleItemPosition()));
                    data.get(manager.findFirstVisibleItemPosition()).setSelect(false);
                    selectAdapter.setCurrentPosition(-1);
                    for (MediaBean selectImage : selectImages) {
                        imageAdapter.notifyItemChanged(data.indexOf(selectImage));
                    }
                }
                imageAdapter.notifyItemChanged(manager.findFirstVisibleItemPosition());
                refreshView();
            }
        });
        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                // 如果是横向Linear布局，则能左右拖动，上下滑动
                int swipeFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN;
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
                if (viewHolder instanceof SelectAdapter.ContentViewHolder) {
                    SelectAdapter.ContentViewHolder holder = (SelectAdapter.ContentViewHolder) viewHolder;
                    holder.onItemClear();
                }
                selectAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (viewHolder instanceof SelectAdapter.ContentViewHolder) {
                    SelectAdapter.ContentViewHolder holder = (SelectAdapter.ContentViewHolder) viewHolder;
                    holder.onItemSelected();
                }
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        itemTouchHelper.attachToRecyclerView(rv_select);
        slide_in_left = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
        slide_out_right = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
        slide_in_right = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        slide_out_left = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
        slide_out_left.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ll_list.setVisibility(View.GONE);
                ll_preview.setVisibility(View.VISIBLE);
                rv_right.setVisibility(View.VISIBLE);
                toolbar.setNavigationIcon(R.mipmap.bg_back);
                // 设置 NavigationIcon 点击事件
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changerView(false, -1);
                    }
                });
                refreshView();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        slide_in_left.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                toolbar.setNavigationIcon(null);
                setTitle("图片选择");
                ll_list.setVisibility(View.VISIBLE);
                ll_preview.setVisibility(View.GONE);
                rv_right.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 初始化数据
     */
    private void getImage() {
        data = ImageUtils.getImage(this);
        imageAdapter = new ImageAdapter(data);
        imageAdapter.setSelect(selectImages);
        imageAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (view.getId()) {
                    case R.id.tv_select:
                        selectImages.remove(data.get(position));
                        data.get(position).setSelect(false);
                        imageAdapter.notifyItemChanged(position);
                        for (MediaBean selectImage : selectImages) {
                            imageAdapter.notifyItemChanged(data.indexOf(selectImage));
                        }
                        break;
                    case R.id.img_normal:
                        if (selectImages.size() > 5) {
                            Toast.makeText(ImageActivity.this, "最多选择六张", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        data.get(position).setSelect(true);
                        selectImages.add(data.get(position));
                        imageAdapter.notifyItemChanged(position);
                        break;
                    default:
                        changerView(true, position);
                        break;
                }
                refreshCount();
            }
        });
        rv_image.setAdapter(imageAdapter);
    }

    private Animation slide_in_left, slide_out_right, slide_in_right, slide_out_left;

    /**
     * 视图切换
     *
     * @param isPreview
     * @param currentPosition
     */
    private void changerView(boolean isPreview, int currentPosition) {
        if (isPreview) {
            initPreviewData(currentPosition);
            setTitle((currentPosition + 1) + "/" + previewAdapter.getItemCount());
            ll_list.setVisibility(View.VISIBLE);
            ll_preview.setVisibility(View.VISIBLE);
            ll_list.startAnimation(slide_out_left);
            ll_preview.startAnimation(slide_in_right);
        } else {
            ll_list.setVisibility(View.VISIBLE);
            ll_preview.setVisibility(View.VISIBLE);
            ll_list.startAnimation(slide_in_left);
            ll_preview.startAnimation(slide_out_right);
        }
    }

    private void initPreviewData(int currentPosition) {
        previewAdapter = new PreviewAdapter(data);
        rv_image_preview.setAdapter(previewAdapter);
        rv_image_preview.scrollToPosition(currentPosition);
        selectAdapter = new SelectAdapter(selectImages);
        rv_select.setAdapter(selectAdapter);
        selectAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                manager.scrollToPosition(data.indexOf(selectImages.get(position)));
            }
        });
    }

    /**
     * 刷新页面
     */
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
        refreshCount();
    }

    private void refreshCount() {
        tv_select.setText("完成(" + selectImages.size() + ")");
        tv_finish.setText("完成(" + selectImages.size() + ")");
    }

    public void previewAction(View view) {
        if (selectImages.size() == 0) {
            Toast.makeText(this, "请选择图片", Toast.LENGTH_SHORT).show();
            return;
        }
        DataBean dataBean = new DataBean();
        dataBean.setType(1);
        dataBean.setImages(data);
        dataBean.setSelected(selectImages);
        PreviewActivity.startPreview(dataBean, ImageActivity.this);
    }

    /**
     * 完成选择
     *
     * @param view
     */
    public void finishAction(View view) {
        if (selectImages.size() == 0) {
            Toast.makeText(this, "请选择图片", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "供选择图片" + selectImages.size() + "张", Toast.LENGTH_LONG).show();
    }

    /**
     * 编辑动作
     *
     * @param view
     */
    public void editAction(View view) {
        beginCrop(ImageUtils.getImageContentUri(this, data.get(manager.findFirstVisibleItemPosition()).getPath()));
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(ImageUtils.getExternalCacheDir(), "cropped" + System.currentTimeMillis() + ".jpg"));
        Crop.of(source, destination).start(this);
    }

    @Override
    public void onBackPressed() {
        if (ll_preview.getVisibility() == View.VISIBLE) {
            changerView(false, -1);
            return;
        }
        super.onBackPressed();
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

    public static void startSelect(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ImageActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

}
