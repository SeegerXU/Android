package com.seeger.image;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Seeger
 */
public class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    private TextView tvTitle;
    protected TextView tv_right;
    protected ImageView img_right;
    protected RelativeLayout rv_right;

    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        tv_right = (TextView) toolbar.findViewById(R.id.tv_right);
        img_right = (ImageView) toolbar.findViewById(R.id.img_right);
        rv_right = (RelativeLayout) toolbar.findViewById(R.id.rv_right);
        tvTitle.setText(getTitle());
        getSupportActionBar().setTitle("");
        toolbar.setNavigationIcon(R.mipmap.bg_back);
        // 设置 NavigationIcon 点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void setTitle(CharSequence title) {
        if (tvTitle != null) {
            tvTitle.setText(title);
        } else {
            super.setTitle(title);
        }
    }
}
