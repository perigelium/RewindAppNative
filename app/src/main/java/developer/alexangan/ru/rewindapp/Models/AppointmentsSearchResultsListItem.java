package developer.alexangan.ru.rewindapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 14.09.2017.
 */
public class AppointmentsSearchResultsListItem implements Parcelable
{
    protected AppointmentsSearchResultsListItem(Parcel in)
    {
    }

    public static final Creator<AppointmentsSearchResultsListItem> CREATOR = new Creator<AppointmentsSearchResultsListItem>()
    {
        @Override
        public AppointmentsSearchResultsListItem createFromParcel(Parcel in)
        {
            return new AppointmentsSearchResultsListItem(in);
        }

        @Override
        public AppointmentsSearchResultsListItem[] newArray(int size)
        {
            return new AppointmentsSearchResultsListItem[size];
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
