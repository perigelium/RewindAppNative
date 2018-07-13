package developer.alexangan.ru.rewindapp.Models;

/**
 * Created by user on 08.07.2017.
 */

public class RaceRankDetailsItem
{
    private int id_news;
    private int id_customer;
    private String name;
    private String product_type;
    private String product;
    private int quantity;
    private String points;
    private String status;
    private int new_cli;
    private String value;

    public int getId_news()
    {
        return id_news;
    }

    public int getId_customer()
    {
        return id_customer;
    }

    public String getName()
    {
        return name;
    }

    public String getProduct_type()
    {
        return product_type;
    }

    public String getProduct()
    {
        return product;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public String getPoints()
    {
        return points;
    }

    public String getStatus()
    {
        return status;
    }

    public String getValue()
    {
        return value;
    }

    public int getNew_cli()
    {
        return new_cli;
    }
}