package developer.alexangan.ru.rewindapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import developer.alexangan.ru.rewindapp.Models.RaceRankDetailsItem;
import developer.alexangan.ru.rewindapp.R;

public class RaceRankDetailsListAdapter extends BaseAdapter
{
    private Context mContext;
    private List<RaceRankDetailsItem> lRaceRankDetailsItems;
    private int layout_id;
    //ViewHolder holder;

    public RaceRankDetailsListAdapter(Context context, int layout_id, List<RaceRankDetailsItem> lRaceRankDetailsItems)
    {
        //super(context, textViewResourceId, objects);
        mContext = context;
        this.lRaceRankDetailsItems = lRaceRankDetailsItems;
        this.layout_id = layout_id;
    }

    @Override
    public int getCount()
    {
        return lRaceRankDetailsItems.size();
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

    private class ViewHolder
    {
        TextView tvRaceNewsNumber;
        TextView tvNameCustomer;
        TextView tvProductType;
        TextView tvProduct;
        TextView tvProductQuantity;
        TextView tvProductValue;
        TextView tvProductPoints;
        ImageView ivDotColored;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row;
        ViewHolder holder;

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout_id, parent, false);
            holder = new ViewHolder();
            holder.tvRaceNewsNumber = (TextView) row.findViewById(R.id.tvRaceNewsNumber);
            holder.tvNameCustomer = (TextView) row.findViewById(R.id.tvNameCustomer);
            holder.tvProductType = (TextView) row.findViewById(R.id.tvProductType);
            holder.tvProduct = (TextView) row.findViewById(R.id.tvProduct);
            holder.tvProductQuantity = (TextView) row.findViewById(R.id.tvProductQuantity);
            holder.tvProductValue = (TextView) row.findViewById(R.id.tvProductValue);
            holder.tvProductPoints = (TextView) row.findViewById(R.id.tvProductPoints);
            holder.ivDotColored = (ImageView) row.findViewById(R.id.ivDotColored);
            row.setTag(holder);
        }
        else
        {
            row = convertView;
            holder = (ViewHolder) row.getTag();
        }

        //TextView tvRaceNewsNumber = (TextView) row.findViewById(R.id.tvRaceNewsNumber);
        String strRaceNewsNumber = "#" + String.valueOf(position + 1);
        holder.tvRaceNewsNumber.setText(strRaceNewsNumber);

        //TextView tvNameCustomer = (TextView) row.findViewById(R.id.tvNameCustomer);
        holder.tvNameCustomer.setText(lRaceRankDetailsItems.get(position).getName());

        //TextView tvProductType = (TextView) row.findViewById(R.id.tvProductType);
        holder.tvProductType.setText(lRaceRankDetailsItems.get(position).getProduct_type());

        //TextView tvProduct = (TextView) row.findViewById(R.id.tvProduct);
        holder.tvProduct.setText(lRaceRankDetailsItems.get(position).getProduct());

        //TextView tvProductQuantity = (TextView) row.findViewById(R.id.tvProductQuantity);
        holder.tvProductQuantity.setText(String.valueOf(lRaceRankDetailsItems.get(position).getQuantity()));

        //TextView tvProductValue = (TextView) row.findViewById(R.id.tvProductValue);
        holder.tvProductValue.setText(lRaceRankDetailsItems.get(position).getValue());

        //TextView tvProductPoints = (TextView) row.findViewById(R.id.tvProductPoints);
        holder.tvProductPoints.setText(lRaceRankDetailsItems.get(position).getPoints());

        //ImageView ivDotColored = (ImageView) row.findViewById(R.id.ivDotColored);

        String status = lRaceRankDetailsItems.get(position).getStatus();

        if(status != null)
        {
            if (status.equals("active"))
            {
                holder.ivDotColored.setImageResource(R.drawable.green_oval_shape);
            }
            else if (status.equals("close"))
            {
                holder.ivDotColored.setImageResource(R.drawable.red_oval_shape);
            }
            else
            {
                holder.ivDotColored.setImageResource(R.drawable.gray_oval_shape);
            }
        }
        else
        {
            int new_cli = lRaceRankDetailsItems.get(position).getNew_cli();

            if (new_cli == 1)
            {
                holder.ivDotColored.setImageResource(R.drawable.green_oval_shape);
            }
            else if (new_cli == 0)
            {
                holder.ivDotColored.setImageResource(R.drawable.red_oval_shape);
            }
            else
            {
                holder.ivDotColored.setImageResource(R.drawable.gray_oval_shape);
            }
        }

        return row;
    }


}
