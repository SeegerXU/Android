package com.seeger.image;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.seeger.image.adapter.BaseAdapter;
import com.seeger.image.adapter.ImageAdapter;
import com.seeger.image.model.DataBean;
import com.seeger.image.model.MediaBean;
import com.seeger.image.utils.ImageOption;
import com.seeger.image.utils.ImageUtils;
import com.seeger.image.widget.CustomDecoration;

import java.util.ArrayList;
import java.util.List;

import static com.seeger.image.utils.ImageOption.DATA_KEY;

/**
 * 图片选择
 */
public class ImageActivity extends BaseActivity {

    private List<MediaBean> data = new ArrayList<>();
    private ImageAdapter imageAdapter;
    private RecyclerView rv_image;
    private GridLayoutManager manager;
    private List<MediaBean> selectImages = new ArrayList<>();
    private TextView tv_finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initUI();
        getImage();
    }

    private void initUI() {
        toolbar.setNavigationIcon(null);
        tv_finish = (TextView) findViewById(R.id.tv_finish);
        rv_image = (RecyclerView) findViewById(R.id.rv_image);
        manager = new GridLayoutManager(this, 4);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        rv_image.setLayoutManager(manager);
        rv_image.addItemDecoration(new CustomDecoration());
    }

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
                        break;
                    case R.id.img_normal:
                        data.get(position).setSelect(true);
                        selectImages.add(data.get(position));
                        imageAdapter.notifyItemChanged(position);
                        break;
                    default:
                        DataBean dataBean = new DataBean();
                        dataBean.setImages(data);
                        dataBean.setType(0);
                        dataBean.setSelected(selectImages);
                        dataBean.setCurrentPosition(position);
                        PreviewActivity.startPreview(dataBean, ImageActivity.this);
                        break;
                }
            }
        });
        rv_image.setAdapter(imageAdapter);
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

    public void finishAction(View view) {
        if (selectImages.size() == 0) {
            Toast.makeText(this, "请选择图片", Toast.LENGTH_SHORT).show();
            return;
        }
        for (MediaBean selectImage : selectImages) {
            Log.v("SelectImage", selectImage.toString());
        }
        Toast.makeText(this, "供选择图片" + selectImages.size() + "张", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageOption.REQUEST_CODE && resultCode == RESULT_OK) {
            DataBean databean = (DataBean) data.getSerializableExtra(DATA_KEY);
            Intent intent = new Intent();
            Bundle mBundle = new Bundle();
            DataBean dataBean = new DataBean();
            dataBean.setSelected(databean.getSelected());
            mBundle.putSerializable(DATA_KEY, dataBean);
            intent.putExtras(mBundle);
            setResult(RESULT_OK, intent);
            finish();
        }else if(requestCode == ImageOption.REQUEST_CODE){
            imageAdapter.notifyDataSetChanged();
        }
    }

    public static void startSelect(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ImageActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }
}
