
package com.yinhe.iptvsetting.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.view.View;

/**
 * 倒影生成
 * 
 * @author hqq
 */
public class ImageReflect {

    public static Bitmap convertViewToBitmap(View paramView) {
        paramView.measure(View.MeasureSpec.makeMeasureSpec(0, 0),
                View.MeasureSpec.makeMeasureSpec(0, 0));
        paramView.layout(0, 0, paramView.getMeasuredWidth(), paramView.getMeasuredHeight());
        paramView.buildDrawingCache();
        return paramView.getDrawingCache();

    }

    public static Bitmap createCutReflectedImage(Context context, Bitmap paramBitmap, int paramInt) {
        int i = paramBitmap.getWidth();
        int j = paramBitmap.getHeight();
        Bitmap localBitmap2 = null;
        int reflectImageHeight = j / 2;
        if (j <= paramInt + reflectImageHeight) {
            localBitmap2 = null;
        } else {
            Matrix localMatrix = new Matrix();
            localMatrix.preScale(1.0F, -1.0F);
            Bitmap localBitmap1 = null;
            if (paramInt < 0) {

                localBitmap1 = Bitmap.createBitmap(paramBitmap, 0, 0, i, j, localMatrix, true);
            } else {
                localBitmap1 = Bitmap.createBitmap(paramBitmap, 0, j - reflectImageHeight
                        - paramInt, i, reflectImageHeight, localMatrix, true);
            }
            localBitmap2 = Bitmap.createBitmap(i, reflectImageHeight,
                    Bitmap.Config.ARGB_8888);
            Canvas localCanvas = new Canvas(localBitmap2);
            localCanvas.drawBitmap(localBitmap1, 0.0F, 0.0F, null);
            LinearGradient localLinearGradient = new LinearGradient(0.0F, 0.0F, 0.0F,
                    localBitmap2.getHeight(), -2130706433, 16777215, TileMode.CLAMP);
            Paint localPaint = new Paint();
            localPaint.setShader(localLinearGradient);
            localPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
            localCanvas.drawRect(0.0F, 0.0F, i, localBitmap2.getHeight(), localPaint);
            if (!localBitmap1.isRecycled())
                localBitmap1.recycle();
            System.gc();
        }
        return localBitmap2;
    }

    public static Bitmap createReflectedImage(Bitmap paramBitmap, int paramInt) {
        int i = paramBitmap.getWidth();
        int j = paramBitmap.getHeight();
        Bitmap localBitmap2;
        if (j <= paramInt) {
            localBitmap2 = null;
        } else {
            Matrix localMatrix = new Matrix();
            localMatrix.preScale(1.0F, -1.0F);
            Bitmap localBitmap1 = Bitmap.createBitmap(paramBitmap, 0, j - paramInt, i, paramInt,
                    localMatrix, true);
            localBitmap2 = Bitmap.createBitmap(i, paramInt, Bitmap.Config.ARGB_8888);
            Canvas localCanvas = new Canvas(localBitmap2);
            localCanvas.drawBitmap(localBitmap1, 0.0F, 0.0F, null);
            LinearGradient localLinearGradient = new LinearGradient(0.0F, 0.0F, 0.0F,
                    localBitmap2.getHeight(), -2130706433, 16777215, TileMode.CLAMP);
            Paint localPaint = new Paint();
            localPaint.setShader(localLinearGradient);
            localPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
            localCanvas.drawRect(0.0F, 0.0F, i, localBitmap2.getHeight(), localPaint);
        }
        return localBitmap2;
    }
}
