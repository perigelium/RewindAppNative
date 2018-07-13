package developer.alexangan.ru.rewindapp.Models;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Created by user on 03.07.2017.
 */

public class InvoiceItem extends RealmObject
{
    private String name;

    @Index
    private String group;

    private int id;
    private String date;
    private String total;
    private String checked;
    private String period;
    private String notes;

    public String getName()
    {
        return name;
    }

    public String getGroup()
    {
        return group;
    }

    public int getId()
    {
        return id;
    }

    public String getDate()
    {
        return date;
    }

    public String getTotal()
    {
        return total;
    }

    public String getChecked()
    {
        return checked;
    }

    public String getPeriod()
    {
        return period;
    }

    public String getNotes()
    {
        return notes;
    }
}
