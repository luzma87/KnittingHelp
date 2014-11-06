package com.lzm.KnittingHelp.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import com.lzm.KnittingHelp.MainActivity;

import java.io.*;

/**
 * Created by svt on 7/29/2014.
 */
public class ImageUtils {


    /**
     * Use for decoding camera response data.
     *
     * @param data    : Intent
     * @param context : Context
     * @return : Bitmap
     */
    public static Bitmap getThumbnailFromCameraData(Intent data, MainActivity context) {
        return getBitmapFromCameraData(data, context, (int) (context.screenWidth / 3), (int) (context.screenHeight / 5));
    }

    public static Bitmap getThumbnail(String path, boolean force) {
        return decodeFile(path, 185, 185, force);
    }

    public static Bitmap getImage(String path) {
        return decodeFile(path, 1024, 768, false);
    }

    public static Bitmap getBitmapFromCameraData(Intent data, Context context) {
        return getBitmapFromCameraData(data, context, -1, -1);
    }

    public static Bitmap getBitmapFromCameraData(Intent data, Context context, int w, int h) {
        boolean resize = false;
        if (w > 0 && h > 0) {
            resize = true;
        }
        Uri selectedImage = data.getData();

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
        try {
            ExifInterface exif = new ExifInterface(picturePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            if (resize) {
                bitmap = decodeFile(picturePath, w, h);
                bitmap = rotateBitmap(bitmap, orientation);

                cursor.close();
            } else {
                bitmap = rotateBitmap(bitmap, orientation);
            }
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = BitmapFactory.decodeFile(picturePath);
        }
        return bitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeFile(String path, int w, int h) {
        return decodeFile(path, w, h, false);
    }

    public static Bitmap decodeFile(String path, int w, int h, boolean force) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, w, h);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);


        int imageW = bitmap.getWidth();
        int imageH = bitmap.getHeight();
        int newW = w;
        int newH = h;
        if (!force) {
            if (imageW > imageH) {
                newW = w;
                newH = (w * imageH) / imageW;
            } else {
                newH = h;
                newW = (h * imageW) / imageH;
            }
        }
        Bitmap photo = Bitmap.createScaledBitmap(bitmap, newW, newH, false);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        return photo;
    }

    public static Bitmap decodeFile(String path) {
        File f = new File(path);
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //The new size we want to scale to
            final int REQUIRED_W = 64;
            final int REQUIRED_H = 32;

            //Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_W && o.outHeight / scale / 2 >= REQUIRED_H)
                scale *= 2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

//            System.out.println("out");
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap decodeBitmap(String path, int w, int h) {
        return decodeFile(path, w, h);
    }

    public static Bitmap decodeBitmap(InputStream stream, int w, int h) {
        try {
            //Decode image size
            Bitmap b = BitmapFactory.decodeStream(stream);
            return Bitmap.createScaledBitmap(b, w, h, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap[] dobleBitmap(InputStream stream, int w, int h, int w2, int h2) {
        try {
            //Decode image size
            Bitmap[] res = new Bitmap[2];
            Bitmap b = BitmapFactory.decodeStream(stream);
            res[0] = Bitmap.createScaledBitmap(b, w, h, false);
            res[1] = Bitmap.createScaledBitmap(b, w2, h2, false);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * rotates image
     * http://stackoverflow.com/questions/20478765/how-to-get-the-correct-orientation-of-the-image-selected-from-the-default-image/20480741#20480741
     * answered Dec 9 '13 at 21:04 by ramaral
     *
     * @param bitmap:      Bitmap
     * @param orientation: orientation
     * @return: Bitmap
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        try {
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    return bitmap;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.setScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(-90);
                    break;
                default:
                    return bitmap;
            }
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Bitmap decodeBitmapPath(String path, int w, int h) {
        try {
            File f = new File(path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return Bitmap.createScaledBitmap(b, w, h, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
