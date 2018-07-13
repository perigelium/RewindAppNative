package developer.alexangan.ru.rewindapp.Models;

/**
 * Created by user on 03.07.2017.
 */

public class InvoiceTotalItem
{
    private String period;
    private float total;
    private String checked;


    public InvoiceTotalItem(String period, float total, String checked )
    {
        this.period = period;
        this.total = total;
        this.checked = checked;
    }

    public String getPeriod()
    {
        return period;
    }

    public float getTotal()
    {
        return total;
    }

    public String getChecked()
    {
        return checked;
    }
}
