package developer.alexangan.ru.rewindapp.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

// Created by Alexandr Angan on 06.02.2017.

public class ImageUtils
{
    public static String getMimeTypeOfUri(Context context, Uri uri)
    {
        BitmapFactory.Options opt = new BitmapFactory.Options();
    /* The doc says that if inJustDecodeBounds set to true, the decoder
     * will return null (no bitmap), but the out... fields will still be
     * set, allowing the caller to query the bitmap without having to
     * allocate the memory for its pixels. */
        opt.inJustDecodeBounds = true;

        InputStream istream = null;
        try
        {
            istream = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        BitmapFactory.decodeStream(istream, null, opt);

        try
        {
            if (istream != null)
            {
                istream.close();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return opt.outMimeType;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {
            if (width > height)
            {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else
            {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }

/*        if(inSampleSize > 1)
        {
            inSampleSize++;
        }*/

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight)
    {
        Bitmap bm = null;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        try
        {
            BitmapFactory.decodeFile(path, options);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        try
        {
            bm = BitmapFactory.decodeFile(path, options);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return bm;
    }

    public static Bitmap createProportionalBitmap(String pathFile)
    {
        int maxWidth = 2048;
        int maxHeight = 2048;
        Bitmap bm = null;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        try
        {
            BitmapFactory.decodeFile(pathFile, options);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        int width = options.outWidth;
        int height = options.outHeight;

        float ratioBitmap = (float) width / (float) height;
        //float ratioMax = (float) maxWidth / (float) maxHeight;

        int finalWidth = width;
        int finalHeight = height;

        if(width > maxWidth || height > maxHeight)
        {

            if (ratioBitmap > 1) // width > height
            {
                finalWidth = maxWidth;
                finalHeight = (int) ((float) maxWidth / ratioBitmap);

            } else
            {
                finalHeight = maxHeight;
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            }
        }
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, finalWidth, finalHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

        try
        {
            bm = BitmapFactory.decodeFile(pathFile, options);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return bm;
    }
}
