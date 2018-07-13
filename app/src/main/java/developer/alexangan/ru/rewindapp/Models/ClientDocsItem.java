package developer.alexangan.ru.rewindapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 04.09.2017.
 */
public class ClientDocsItem implements Parcelable
{
    protected ClientDocsItem(Parcel in)
    {
    }

    public static final Creator<ClientDocsItem> CREATOR = new Creator<ClientDocsItem>()
    {
        @Override
        public ClientDocsItem createFromParcel(Parcel in)
        {
            return new ClientDocsItem(in);
        }

        @Override
        public ClientDocsItem[] newArray(int size)
        {
            return new ClientDocsItem[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
    }
}
