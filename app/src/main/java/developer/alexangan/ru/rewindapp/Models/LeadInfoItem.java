package developer.alexangan.ru.rewindapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 30.08.2017.
 */
public class LeadInfoItem implements Parcelable
{

    protected LeadInfoItem(Parcel in)
    {
    }

    public static final Creator<LeadInfoItem> CREATOR = new Creator<LeadInfoItem>()
    {
        @Override
        public LeadInfoItem createFromParcel(Parcel in)
        {
            return new LeadInfoItem(in);
        }

        @Override
        public LeadInfoItem[] newArray(int size)
        {
            return new LeadInfoItem[size];
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
