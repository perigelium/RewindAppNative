package developer.alexangan.ru.rewindapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import developer.alexangan.ru.rewindapp.Models.AppointmentsSearchResultsListItem;
import developer.alexangan.ru.rewindapp.R;

public class AppointmentsSearchResultsListAdapter extends BaseAdapter
{
    private Context mContext;
    List<AppointmentsSearchResultsListItem> lAppointmentSearchResultsListItems;
    private int layout_id;
    //ViewHolder holder;

    public AppointmentsSearchResultsListAdapter(Context context, int layout_id, List<AppointmentsSearchResultsListItem> lAppointmentSearchResultsListItems)
    {
        //super(context, textViewResourceId, objects);
        mContext = context;
        this.lAppointmentSearchResultsListItems = lAppointmentSearchResultsListItems;
        this.layout_id = layout_id;
    }

    @Override
    public int getCount()
    {
        return lAppointmentSearchResultsListItems.size();
    }

    @Override
    public Object getItem(int i)
    {
        return i;
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(layout_id, parent, false);

        TextView tvSearchResultCompanyName = (TextView) row.findViewById(R.id.tvSearchResultCompanyName);
        TextView tvSearchResultCompanyLocation = (TextView) row.findViewById(R.id.tvSearchResultCompanyLocation);

/*        tvSearchResultCompanyName.setText(lAppointmentSearchResultsListItems.get(position).getRegSociale());
        tvSearchResultCompanyLocation.setText(lAppointmentSearchResultsListItems.get(position).getContattiInfo());*/

        return row;
    }
}
