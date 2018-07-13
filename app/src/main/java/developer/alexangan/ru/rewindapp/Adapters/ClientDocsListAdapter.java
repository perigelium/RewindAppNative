package developer.alexangan.ru.rewindapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import developer.alexangan.ru.rewindapp.Models.ClientDocsItem;

public class ClientDocsListAdapter extends BaseAdapter
{
    private Context mContext;
    private ArrayList<ClientDocsItem> totals;
    private int layout_id;
    //ViewHolder holder;

    public ClientDocsListAdapter(Context context, int layout_id, ArrayList<ClientDocsItem> totals)
    {
        //super(context, textViewResourceId, objects);
        mContext = context;
        this.totals = totals;
        this.layout_id = layout_id;
    }

    @Override
    public int getCount()
    {
        return totals.size();
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

/*        TextView tvTitleFromMonth = (TextView) row.findViewById(R.id.tvTitleFromMonth);
        TextView tvAmount = (TextView) row.findViewById(R.id.tvAmount);
        TextView tvChecked = (TextView) row.findViewById(R.id.tvChecked);

        String monthPeriod = String.valueOf(totals.get(position).getPeriod());
        int monthNumber = Integer.valueOf(monthPeriod.substring(4)) - 1;

        if (monthNumber == -1)
        {
            monthNumber = 11;
        }

        String periodDisplayed = "Fattura " + ItalianMonths.numToLongString(monthNumber) + " " + monthPeriod.substring(0, 4);

        tvTitleFromMonth.setText(periodDisplayed);

        String strAmount = String.valueOf(totals.get(position).getTotal());

        strAmount = MyTextUtils.reformatCurrencyString(strAmount);

        tvAmount.setText(strAmount);

        String strChecked = totals.get(position).getChecked();
        String strDisplayedChecked = "";

        if (strChecked != null)
        {
            strDisplayedChecked = "CONFERMATO";
        } else
        {
            strDisplayedChecked = "SOSPESO";
        }
        tvChecked.setText(strDisplayedChecked);*/

        return row;
    }
}
