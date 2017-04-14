package com.cantalou.android.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.media.ExifInterface;
import android.os.Build;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.cantalou.android.util.ReflectUtil.set;

public class BitmapUtils {

    public static BitmapFactory.Options createOptions() {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inDither = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            opts.inPreferredConfig = Config.RGB_565;
            set(opts, "inNativeAlloc", true);
        }
        return opts;
    }

    /**
     * 通过路径获取指定宽度的Bitmap
     *
     * @param path     文件路径
     * @param maxWidth 图片最大宽度
     * @return
     * @throws IOException
     */
    public static Bitmap getBitmap(String path, int maxWidth) throws IOException {
        return getBitmap(path, maxWidth, null);
    }

    /**
     * @param path
     * @param maxWidth
     * @param opts
     * @return
     * @throws IOException
     */
    public static Bitmap getBitmap(String path, int maxWidth, BitmapFactory.Options opts) throws IOException {

        Bitmap result = null;
        System.runFinalization();
        Runtime.getRuntime()
                .gc();

        if (opts == null) {
            opts = createOptions();
        }

        // 先测量图片的尺寸

        if (!opts.inJustDecodeBounds || opts.outWidth == 0 || opts.outHeight == 0) {
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);
        }

        // 小于指定的宽度直接返回
        if (opts.outWidth <= maxWidth) {
            opts.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(path, opts);
        }

        int degree = getBitmapDegree(path);
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = calculateInSampleSize(opts, maxWidth, degree / 90 == 0);
        result = BitmapFactory.decodeFile(path, opts);
        if (result == null) {
            return null;
        }

        //部分手机拍照横拍
        if (degree > 0) {
            Bitmap rotateBitmap = rotateBitmapByDegree(result, degree);
            if (result != rotateBitmap) {
                destroyBitmap(result);
            }
            result = rotateBitmap;
        }

        // 图片旋转后宽度小于指定的宽度直接放回返回
        if (result.getWidth() <= maxWidth) {
            return result;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Bitmap scaledBitmap = null;
            int targetHeight = (int) (1f * maxWidth / result.getWidth() * result.getHeight());
            scaledBitmap = Bitmap.createScaledBitmap(result, maxWidth, targetHeight, true);
            if (scaledBitmap != result) {
                destroyBitmap(result);
            }
            return scaledBitmap;
        } else {
            return result;
        }
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            Log.e(e);
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            destroyBitmap(bm);
        }
        return returnBm;
    }


    static String cacheExifInterfaceFile = null;
    static SoftReference<ExifInterface> cacheExifInterface;

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public synchronized static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = null;
            if (path.equals(cacheExifInterfaceFile) && cacheExifInterface != null) {
                exifInterface = cacheExifInterface.get();
            }

            if (exifInterface == null) {
                // 从指定路径下读取图片，并获取其EXIF信息
                exifInterface = new ExifInterface(path);
                cacheExifInterfaceFile = path;
                if (cacheExifInterface != null) {
                    cacheExifInterface.clear();
                }
                cacheExifInterface = new SoftReference<ExifInterface>(exifInterface);
            }

            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            Log.e(e);
        }
        return degree;
    }


    /**
     * Resize image.
     *
     * @param bmp       the bmp
     * @param newWidth  the new width
     * @param newHeight the new height
     * @return the bitmap
     */
    public static Bitmap resizeImage(Bitmap bmp, int newWidth, int newHeight) {
        if (bmp == null) {
            return null;
        }

        int originWidth = bmp.getWidth();
        int originHeight = bmp.getHeight();
        if (originWidth == newWidth && originHeight == newHeight) {
            return bmp;
        }

        float scaleWidth = ((float) newWidth) / originWidth;
        float scaleHeight = ((float) newHeight) / originHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizeBitmap = Bitmap.createBitmap(bmp, 0, 0, originWidth, originHeight, matrix, true);
        return resizeBitmap;
    }

    /**
     * Resize image.
     *
     * @param bmp   the bmp
     * @param scale the scale
     * @return the bitmap
     */
    public static Bitmap resizeImage(Bitmap bmp, float scale) {
        if (bmp == null) {
            return null;
        }

        int originWidth = bmp.getWidth();
        int originHeight = bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bmp, 0, 0, originWidth, originHeight, matrix, true);
    }

    /**
     * 获取图像的宽高.
     *
     * @param path the path
     * @return the image wh
     */
    public static int[] getImageWH(String path) {
        InputStream is = null;
        try {
            return getImageWH(is = new BufferedInputStream(new FileInputStream(path)));
        } catch (Exception e) {
            Log.e(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    Log.e(e);
                }
            }
        }
        return null;
    }

    /**
     * 描述:获取图像的宽高.
     *
     * @param is the is
     * @return the image wh
     */
    public static int[] getImageWH(InputStream is) {
        int[] wh = {-1, -1};
        if (is == null) {
            return wh;
        }
        try {
            BitmapFactory.Options options = createOptions();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(convert2BufStream(is), null, options);
            wh[0] = options.outWidth;
            wh[1] = options.outHeight;
        } catch (Throwable e) {
            Log.w("getImageWH Throwable.");
        }
        return wh;
    }

    public static InputStream convert2BufStream(InputStream is) {
        if (is instanceof BufferedInputStream) {
            return is;
        } else {
            return new BufferedInputStream(is);
        }
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, boolean isWidth) {
        // Raw height and width of image
        final int width = isWidth ? options.outWidth : options.outHeight;
        int inSampleSize = 1;

        if (width > reqWidth) {
            int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options}
     * object when decoding bitmaps using the decode* methods from
     * {@link BitmapFactory}. This implementation calculates the closest
     * inSampleSize that will result in the final decoded bitmap having a width
     * and height equal to or larger than the requested width and height. This
     * implementation does not ensure a power of 2 is returned for inSampleSize
     * which can be faster when decoding but results in a larger bitmap which
     * isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run
     *                  through a decode* method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down
            // further.
            // 任何超过2倍的请求像素，我们将品尝简化
            // 更多。
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }


    /**
     * drawable转换为bitmap.
     *
     * @param drawable the drawable
     * @return the bitmap
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (null == drawable) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        if (drawable.getIntrinsicWidth() < 1 || drawable.getIntrinsicHeight() < 1) {
            return null;
        }

        Bitmap bitmap = null;
        try {
            Rect rect = new Rect(drawable.getBounds());
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            drawable.setBounds(rect);
        } catch (OutOfMemoryError ooe) {
            Log.e(ooe);
            System.gc();
        }
        return bitmap;
    }


    /**
     * 回收资源.
     *
     * @param bmp the bmp
     */
    public static void destroyBitmap(Bitmap bmp) {
        if (null != bmp && !bmp.isRecycled()) {
            Log.d("recycle Bitmap {}", bmp);
            bmp.recycle();
        }
    }

    public static interface DestroyCallback {
        public void destroy(Drawable drawable);
    }

    /**
     * 回收Drawable资源.
     *
     * @param drawable
     */
    public static void destroyDrawable(Drawable drawable, DestroyCallback callback) {
        if (drawable == null) {
            return;
        }

        if (drawable instanceof BitmapDrawable) {
            callback.destroy(drawable);
            destroyBitmap(((BitmapDrawable) drawable).getBitmap());
        } else if (drawable instanceof DrawableContainer) {
            if (drawable instanceof AnimationDrawable) {
                ((AnimationDrawable) drawable).stop();
            }
            DrawableContainer container = ((DrawableContainer) drawable);
            DrawableContainer.DrawableContainerState state = ReflectUtil.get(container, "mDrawableContainerState");
            if (state != null) {
                for (Drawable d : state.getChildren()) {
                    destroyDrawable(d, callback);
                }
            }
        }
    }

    /**
     * 生成图片文件.
     *
     * @param bmp      the bmp
     * @param filePath the file path
     * @return true, if successful
     */
    public static boolean saveBitmap2file(Bitmap bmp, String filePath) {
        CompressFormat format = CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(filePath));
            return bmp.compress(format, quality, stream);
        } catch (Throwable e) {
            Log.e(e, "保存图片失败");
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
        return false;
    }

    /**
     * 生成图片文件.
     *
     * @param bmp      the bmp
     * @param filePath the file path
     * @return true, if successful
     */

    public static boolean saveBitmap2file(Bitmap bmp, String filePath, int quality, CompressFormat format) {

        if (format == null) {
            format = CompressFormat.JPEG;
        }
        OutputStream stream = null;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(filePath));
            return bmp.compress(format, quality, stream);
        } catch (Exception e) {
            Log.e(e, "保存图片失败:");
        } finally {
            FileUtil.close(stream);
        }
        return false;
    }

    /**
     * 生成图片文件.
     *
     * @param bmp
     * @param filePath
     * @param format
     * @return
     */
    public static boolean saveBitmap2file(Bitmap bmp, String filePath, CompressFormat format) {
        return saveBitmap2file(bmp, filePath, 100, format);
    }

    /**
     * 生成照片的路径
     *
     * @return
     */
    public static String generatePhotoPath(String dir, String suffix) {
        String photoName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + (suffix != null ? suffix : ".jpg");
        File file = new File(dir, photoName);
        Log.d("生成图片路径: {}", file.getAbsolutePath());
        return file.getAbsolutePath();
    }


    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, 100, baos);
            byte[] bitmapBytes = baos.toByteArray();
            result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
        }
        return result;
    }

    /**
     * 获取Bitmap占用内存大小
     *
     * @param bitmap
     * @return 内存大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
        }
    }
}
