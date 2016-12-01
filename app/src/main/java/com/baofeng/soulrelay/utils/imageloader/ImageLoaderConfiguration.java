package com.baofeng.soulrelay.utils.imageloader;

import android.widget.ImageView;

import com.baofeng.soulrelay.R;


/**
 * Created by soulrelay on 2016/10/11 13:44.
 * Class Note:
 * encapsulation of ImageView,Build Pattern used
 */
public class ImageLoaderConfiguration {
    private int type;  //图片加载类型，目前只有默认类型，以后可以扩展

    private String url; //需要解析的url

    private int placeHolder; //当没有成功加载的时候显示的图片

    private ImageView imgView; //ImageView的实例

    private int loadStrategy;//加载策略，目前只有默认加载策略，以后可以扩展

    private ImageLoaderConfiguration(Builder builder) {
        this.type = builder.type;
        this.url = builder.url;
        this.placeHolder = builder.placeHolder;
        this.imgView = builder.imgView;
        this.loadStrategy = builder.loadStrategy;
    }

    public int getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public int getPlaceHolder() {
        return placeHolder;
    }

    public ImageView getImgView() {
        return imgView;
    }

    public int getLoadStrategy() {
        return loadStrategy;
    }

    public static class Builder {
        private int type;
        private String url;
        private int placeHolder;
        private ImageView imgView;
        private int loadStrategy;

        public Builder() {
            this.type = ImageLoaderUtil.PIC_DEFAULT_TYPE;
            this.url = "";
            this.placeHolder = R.drawable.bg_default_video_common_small;
            this.imgView = null;
            this.loadStrategy = ImageLoaderUtil.LOAD_STRATEGY_DEFAULT;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder placeHolder(int placeHolder) {
            this.placeHolder = placeHolder;
            return this;
        }

        public Builder imgView(ImageView imgView) {
            this.imgView = imgView;
            return this;
        }

        public Builder strategy(int strategy) {
            this.loadStrategy = strategy;
            return this;
        }

        public ImageLoaderConfiguration build() {
            return new ImageLoaderConfiguration(this);
        }

    }
}
