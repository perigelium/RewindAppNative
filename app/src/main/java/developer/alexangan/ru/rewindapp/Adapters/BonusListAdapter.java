package developer.alexangan.ru.rewindapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import developer.alexangan.ru.rewindapp.Models.BonusItem;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.MyTextUtils;

public class BonusListAdapter extends BaseAdapter
{
    private Context mContext;
    private List<BonusItem> l_BonusItems;
    private int layout_id;
    //ViewHolder holder;

    public BonusListAdapter(Context context, int layout_id, List<BonusItem> l_BonusItems)
    {
        //super(context, textViewResourceId, objects);
        mContext = context;
        this.l_BonusItems = l_BonusItems;
        this.layout_id = layout_id;
    }

    @Override
    public int getCount()
    {
        return l_BonusItems.size();
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

        TextView tvCompanyName = (TextView) row.findViewById(R.id.tvCompanyName);
        TextView tvProduct = (TextView) row.findViewById(R.id.tvProduct);
        TextView tvEarned = (TextView) row.findViewById(R.id.tvEarned);

        tvCompanyName.setText(l_BonusItems.get(position).getRagione_sociale());
        tvProduct.setText(l_BonusItems.get(position).getProdus());

        String strBonus = l_BonusItems.get(position).getBonus();

        strBonus = MyTextUtils.reformatCurrencyString(strBonus);

        tvEarned.setText(strBonus);

        return row;
    }
}
