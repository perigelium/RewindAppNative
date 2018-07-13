package developer.alexangan.ru.rewindapp.Models;

import io.realm.RealmObject;

public class RealmString extends RealmObject
{
    private String val;

    public RealmString()
    {}

    public RealmString(String val)
    {
        this.val = val;
    }

    public String Value()
    {
        return val;
    }
}
