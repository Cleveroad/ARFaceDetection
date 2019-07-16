package com.cleveroad.aropensource.ui.screens.main.mlkit.common;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

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

    // Convert NV21 format byte buffer to bitmap.
    @Nullable
    public static Bitmap getBitmap(ByteBuffer data, FrameMetadata metadata) {
        data.rewind();
        byte[] imageInBuffer = new byte[data.limit()];
        data.get(imageInBuffer, 0, imageInBuffer.length);
        try {
            YuvImage image =
                    new YuvImage(
                            imageInBuffer, ImageFormat.NV21, metadata.getWidth(), metadata.getHeight(), null);
            if (image != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, metadata.getWidth(), metadata.getHeight()), 80, stream);

                Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());

                stream.close();
                return rotateBitmap(bmp, metadata.getRotation(), metadata.getCameraFacing());
            }
        } catch (Exception e) {
            Log.e("VisionProcessorBase", "Error: " + e.getMessage());
        }
        return null;
    }

    // Rotates a bitmap if it is converted from a bytebuffer.
    private static Bitmap rotateBitmap(Bitmap bitmap, int rotation, int facing) {
        Matrix matrix = new Matrix();
        int rotationDegree = 0;
        switch (rotation) {
            case FirebaseVisionImageMetadata.ROTATION_90:
                rotationDegree = 90;
                break;
            case FirebaseVisionImageMetadata.ROTATION_180:
                rotationDegree = 180;
                break;
            case FirebaseVisionImageMetadata.ROTATION_270:
                rotationDegree = 270;
                break;
            default:
                break;
        }

        // Rotate the image back to straight.}
        matrix.postRotate(rotationDegree);
        if (facing == CameraInfo.CAMERA_FACING_BACK) {
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {
            // Mirror the image along X axis for front-facing camera image.
            matrix.postScale(-1.0f, 1.0f);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}

