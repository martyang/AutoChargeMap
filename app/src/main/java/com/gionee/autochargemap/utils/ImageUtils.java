package com.gionee.autochargemap.utils;

/*
 *  @项目名：  AutoChargeMap 
 *  @包名：    com.gionee.autochargemap.utils
 *  @文件名:   ImageUtils
 *  @创建者:   pbl
 *  @创建时间:  2017/3/28 10:24
 *  @描述：    view -> image
 */


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

public class ImageUtils {

    public static void viewSaveToImage(View view, String path, String filename) {
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheBackgroundColor(Color.WHITE);

        // 把一个View转换成图片
        Bitmap cachebmp = loadBitmapFromView(view);

        // 添加水印
        Bitmap bitmap = Bitmap.createBitmap(createWatermarkBitmap(cachebmp, Utils.getLeadTime()));

        FileOutputStream fos;
        try {

            File imgpath = new File(path);
            if (!imgpath.exists()){
                imgpath.mkdirs();
            }
            File file = new File(path, filename);
            fos = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);

            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(e.getMessage());
        }

        view.destroyDrawingCache();
    }

    private static Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c   = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /*如果不设置canvas画布为白色，则生成透明 */

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }

    // 为图片target添加水印
    private static Bitmap createWatermarkBitmap(Bitmap target, String str) {
        int w = target.getWidth();
        int h = target.getHeight();

        Bitmap bmp    = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        Paint p = new Paint();

        // 水印的颜色
        p.setColor(Color.argb(200,255,0,0));

        // 水印的字体大小
        p.setTextSize(DensityUtils.dp2px(ContextUtil.getInstance(),12f));

        p.setAntiAlias(true);// 去锯齿

        canvas.drawBitmap(target, 0, 0, p);

        // 在中间位置开始添加水印
        canvas.drawText(str, DensityUtils.dp2px(ContextUtil.getInstance(),10f),DensityUtils.dp2px(ContextUtil.getInstance(),13f), p);

        canvas.save(Canvas.ALL_SAVE_FLAG);

        canvas.restore();

        return bmp;
    }


}
