package developer.alexangan.ru.rewindapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import developer.alexangan.ru.rewindapp.Models.ClientPraticheInfoItem;
import developer.alexangan.ru.rewindapp.R;

public class ClientPracticesListAdapter extends BaseAdapter
{
    private Context mContext;
    private List<ClientPraticheInfoItem> l_clientPraticheInfoItems;
    private int layout_id;
    //ViewHolder holder;

    public ClientPracticesListAdapter(Context context, int layout_id, List<ClientPraticheInfoItem> l_clientPraticheInfoItems)
    {
        //super(context, textViewResourceId, objects);
        mContext = context;
        this.l_clientPraticheInfoItems = l_clientPraticheInfoItems;
        this.layout_id = layout_id;
    }

    @Override
    public int getCount()
    {
        return l_clientPraticheInfoItems.size();
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

        TextView tvCreationDate = (TextView) row.findViewById(R.id.tvCreationDate);
        tvCreationDate.setText(l_clientPraticheInfoItems.get(position).getData_creazione());

        TextView tvProductType = (TextView) row.findViewById(R.id.tvProductType);
        tvProductType.setText(l_clientPraticheInfoItems.get(position).getProduct_type());

        TextView tvProduct = (TextView) row.findViewById(R.id.tvProduct);
        tvProduct.setText(l_clientPraticheInfoItems.get(position).getProduct());

        TextView tvProductQuantity = (TextView) row.findViewById(R.id.tvProductQuantity);
        tvProductQuantity.setText(l_clientPraticheInfoItems.get(position).getQuantity());

        TextView tvProductQuantityActive = (TextView) row.findViewById(R.id.tvProductQuantityActive);

        String quantity_active = l_clientPraticheInfoItems.get(position).getQuantity_active();

        if (quantity_active == null)
        {
            quantity_active = "0";
        }

        tvProductQuantityActive.setText(quantity_active);

        return row;
    }


}
