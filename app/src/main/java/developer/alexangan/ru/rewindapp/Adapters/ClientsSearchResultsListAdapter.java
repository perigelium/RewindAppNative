package developer.alexangan.ru.rewindapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import developer.alexangan.ru.rewindapp.Models.ClientInfoItem;
import developer.alexangan.ru.rewindapp.R;

public class ClientsSearchResultsListAdapter extends BaseAdapter
{
    private Context mContext;
    List<ClientInfoItem> lClientInfoItems;
    private int layout_id;
    //ViewHolder holder;

    public ClientsSearchResultsListAdapter(Context context, int layout_id, List<ClientInfoItem> lClientInfoItems)
    {
        //super(context, textViewResourceId, objects);
        mContext = context;
        this.lClientInfoItems = lClientInfoItems;
        this.layout_id = layout_id;
    }

    @Override
    public int getCount()
    {
        return lClientInfoItems.size();
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

        tvSearchResultCompanyName.setText(lClientInfoItems.get(position).getRagione_sociale());
        tvSearchResultCompanyLocation.setText(lClientInfoItems.get(position).getAddress());

        return row;
    }
}
