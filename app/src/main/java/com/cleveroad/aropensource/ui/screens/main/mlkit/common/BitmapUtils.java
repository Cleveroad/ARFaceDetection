package com.cleveroad.aropensource.ui.screens.main.mlkit.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import androidx.camera.core.CameraX.LensFacing;
import androidx.core.content.ContextCompat;

import static androidx.camera.core.CameraX.LensFacing.FRONT;

/**
 * Utils functions for bitmap conversions.
 */
public class BitmapUtils {

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);

        if (drawable != null) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
        return null;
    }

    public static Bitmap rotateBitmap(Bitmap source, LensFacing facing, float angleY, float angleZ) {
        Matrix matrix = new Matrix();
        final Camera camera = new Camera();
        camera.save();
        if (facing == FRONT) {
            angleZ *= -1;
            angleY *= -1;
        }
        camera.rotateZ(angleZ);
        camera.rotateY(angleY);
        camera.getMatrix(matrix);
        camera.restore();

        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}