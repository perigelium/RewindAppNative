package developer.alexangan.ru.rewindapp.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/*Created by user on 03.07.2017*/

public class ViewUtils
{
    public static int getScreenWidthAndroid(Activity activity)
    {
        try
        {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            //int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;

            return width;
        }
        catch (Exception ex)
        {
            //Utils.writeToDeviceLog(ex);
        }

        return 0;
    }

    public static void displayErrorAndroid(String errMsg, Context context, DialogInterface.OnClickListener listener)
    {
        try
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("Errore");

            builder.setMessage(errMsg);

            builder.setPositiveButton("OK", listener);

            builder.show();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void displayMessageAndroid(String title, String message, Context context, DialogInterface.OnClickListener listener)
    {
        try
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle(title);

            builder.setMessage(message);

            builder.setPositiveButton("OK", listener);

            builder.show();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static int calculateZoomLevel(int screenWidth)
    {
        int zoomLevel = 1;
        try {
            double equatorLength = 40075004; // in meters

            double metersPerPixel = equatorLength / 256;

            while ((metersPerPixel * screenWidth) > 165000) {
                metersPerPixel /= 2;
                ++zoomLevel;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return zoomLevel;
    }

    public static void showToastMessage(final Activity activity, final String msg)
    {
        activity.runOnUiThread(new Runnable()
        {
            public void run()
            {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void disableSoftKeyboard(Activity activity )
    {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();

        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
