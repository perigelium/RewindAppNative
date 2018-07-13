package developer.alexangan.ru.rewindapp.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ItalianMonths
{
    private static List<String> itMonths = new ArrayList<>(Arrays.asList("GEN", "FEB", "MAR", "APR", "MAG", "GIU", "LUG",
            "AGO", "SET", "OTT", "NOV", "DIC"));

    private static List<String> itMonthsLong = new ArrayList<>(Arrays.asList("GENAIO", "FEBRAIO", "MARZO", "APRILE",
            "MAGGIO", "GIUGNO", "LUGLIO", "AGOSTO", "SETTEMBRE", "OTTOBRE", "NOVEMBRE", "DICEMBRE"));

    public static String numToLongString(int value)
    {
        return itMonthsLong.get(value);
    }

    public static String numToString(int value)
    {
        return itMonths.get(value);
    }
}
