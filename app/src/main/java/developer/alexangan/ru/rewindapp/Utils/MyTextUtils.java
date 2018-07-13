package developer.alexangan.ru.rewindapp.Utils;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyTextUtils
{
    public static String reformatDateString(String datePost)
    {
        SimpleDateFormat sdfSopralluogo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALIAN);
        Date dateNews;

        try
        {
            dateNews = sdfSopralluogo.parse(datePost);
            sdfSopralluogo = new SimpleDateFormat("dd.MM.yyyy", Locale.ITALIAN);
            String formattedDate = sdfSopralluogo.format(dateNews);

            return formattedDate;

        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    public static String reformatCurrencyString(String strBonus)
    {
        try
        {
            strBonus = strBonus.replace(".", ",");
            String strIntegerPart = "";

            if (strBonus.lastIndexOf(",") == -1)
            {
                strBonus += ",00";
            } else if (strBonus.lastIndexOf(",") == strBonus.length() - 2)
            {
                strBonus += "0";
            }

            int firstDigitIntegerPartPos = strBonus.startsWith("-") ? 1 : 0;
            strIntegerPart = strBonus.substring(firstDigitIntegerPartPos, strBonus.lastIndexOf(","));

            if (strIntegerPart.length() > 3)
            {
                String strDecimalPart = strBonus.substring(strBonus.lastIndexOf(","));
                String strIntFirstPart = strIntegerPart.substring(0, strIntegerPart.length() - 3);
                String strIntLastPart = strIntegerPart.substring(strIntFirstPart.length());
                strBonus = strIntFirstPart + "." + strIntLastPart + strDecimalPart;
            }
            strBonus = strBonus + " €";
            strBonus = strBonus.replace(",0 €", ",00 €");
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return strBonus;
    }

    public static String toDisplayCase(String s)
    {
        final String ACTIONABLE_DELIMITERS = " '-/."; // these cause the character following
        // to be capitalized

        StringBuilder sb = new StringBuilder();
        boolean capNext = true;

        for (char c : s.toCharArray())
        {
            c = (capNext)
                    ? Character.toUpperCase(c)
                    : Character.toLowerCase(c);
            sb.append(c);
            capNext = (ACTIONABLE_DELIMITERS.indexOf((int) c) >= 0); // explicit cast not needed
        }
        return sb.toString();
    }
}
