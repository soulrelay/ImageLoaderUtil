package com.baofeng.soulrelay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.baofeng.soulrelay.utils.imageloader.ImageLoaderUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.iv_normal)
    ImageView ivNormal;
    @BindView(R.id.iv_gif)
    ImageView ivGif;
    @BindView(R.id.iv_gif1)
    ImageView ivGif1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        ImageLoaderUtil.getInstance().loadImage("http://image.sports.baofeng.com/25a3dbb0c99c5e48e52e60941ed230be", R.drawable.bg_default_video_common_small, ivNormal);
        ImageLoaderUtil.getInstance().loadImage("http://image.sports.baofeng.com/19ce5d6ac3b4fff255196f200b1d3079", R.drawable.bg_default_video_common_small, ivGif);
        ImageLoaderUtil.getInstance().loadGifImage("http://image.sports.baofeng.com/19ce5d6ac3b4fff255196f200b1d3079", R.drawable.bg_default_video_common_small, ivGif1);

    }

}
