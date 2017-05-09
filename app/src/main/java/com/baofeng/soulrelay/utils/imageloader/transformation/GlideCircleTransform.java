package com.baofeng.soulrelay.utils.imageloader.transformation;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.baofeng.soulrelay.utils.CommonUtils;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * DES：自定义BitmapTransformation来实现圆形图片加载
 * Created by SuS on 2017/3/8.
 */
public class GlideCircleTransform extends BitmapTransformation {

    private Paint mBorderPaint;
    private float mBorderWidth;

    private int height = 0;
    private int width = 0;
    private int borderColor = 0;
    public GlideCircleTransform(Context context) {
        super(context);
    }

    public GlideCircleTransform(Context context, float borderWidth, int borderColor) {
        super(context);
        mBorderWidth = Resources.getSystem().getDisplayMetrics().density * borderWidth;
        mBorderWidth = CommonUtils.dip2px(context,borderWidth);
        mBorderPaint = new Paint();
        mBorderPaint.setDither(true);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(borderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }

    public GlideCircleTransform(Context context, float borderWidth, int borderColor, int heightPX, int widthPx) {
        super(context);
        mBorderWidth = Resources.getSystem().getDisplayMetrics().density * borderWidth;
        mBorderWidth = CommonUtils.dip2px(context,borderWidth);
        mBorderPaint = new Paint();
        mBorderPaint.setDither(true);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(borderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        width = widthPx;
        height = heightPX;
        this.borderColor = borderColor;
    }


    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        if(width>0){
            return circleCrop(pool, toTransform, outWidth, outHeight);
        }else{
            return circleCrop(pool, toTransform);
        }
    }

    private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        int size = (int) (Math.min(source.getWidth(), source.getHeight()) - (mBorderWidth / 2));
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        // TODO this could be acquired from the pool too
        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        if (mBorderPaint != null) {
            float borderRadius = r - mBorderWidth / 2;
            canvas.drawCircle(r, r, borderRadius, mBorderPaint);
        }
        return result;
    }

    private Bitmap circleCrop(BitmapPool pool, Bitmap source, int outWidth, int outHeight) {
        if (source == null) return null;
        Bitmap result = pool.get(width, width, Bitmap.Config.ARGB_8888);
        Bitmap output = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setColor(borderColor);
        paint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, width, width);
        RectF rectF = new RectF(rect);
        canvas.drawRoundRect(rectF, width, width, paint);
        output = makeRoundCorner(output,width,borderColor);
        result = makeRoundCorner(source,(int)(width-2*mBorderWidth),0);

        result = getAvatarInRoundBg(output,result);

        return  result;
    }

    //把头像保存成圆形图片
    public Bitmap makeRoundCorner(Bitmap bitmap,int px,int borderColor) {
        Bitmap output = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xffffffff;
        if(borderColor!=0){
            color = borderColor;
        }
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, px, px);
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, px, px, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

//        if(bitmap.getWidth()/2-67>0 && bitmap.getHeight()/2-67>0) {
        bitmap = Bitmap.createScaledBitmap(bitmap, px, px, true);
//        }

        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    //加上圆形背景环
    private Bitmap getAvatarInRoundBg(Bitmap bg, Bitmap avatar){
        // 生成画布图像
        Bitmap resultBitmap = Bitmap.createBitmap(width,
                width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);// 使用空白图片生成canvas

        // 将bmp1绘制在画布上
        bg = Bitmap.createScaledBitmap(bg, width, width, true);
        Rect srcRect = new Rect(0, 0, width,width);// 截取bmp1中的矩形区域
        Rect dstRect = new Rect(0, 0, width,width);// bmp1在目标画布中的位置
        canvas.drawBitmap(bg, srcRect, dstRect, null);

        avatar = Bitmap.createScaledBitmap(avatar, (int)(width-2*mBorderWidth), (int)(width-2*mBorderWidth), true);
        // 将bmp2绘制在画布上
        srcRect = new Rect(0, 0, (int)(width-2*mBorderWidth), (int)(width-2*mBorderWidth));// 截取bmp1中的矩形区域
        dstRect = new Rect((int)mBorderWidth ,(int)mBorderWidth,(int)(width-mBorderWidth),(int)(width-mBorderWidth));// bmp2在目标画布中的位置
        canvas.drawBitmap(avatar, srcRect, dstRect, null);
        // 将bmp1,bmp2合并显示
        return resultBitmap;
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
