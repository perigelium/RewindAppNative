package developer.alexangan.ru.rewindapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import developer.alexangan.ru.rewindapp.Models.ProfileInfoItem;
import developer.alexangan.ru.rewindapp.Models.RaceRankItem;
import developer.alexangan.ru.rewindapp.R;
import io.realm.Realm;

public class RaceRankListAdapter extends BaseAdapter
{
    private final ProfileInfoItem profileInfoItem;
    private Context mContext;
    private List<RaceRankItem> l_raceRankItems;
    private int layout_id;
    //ViewHolder holder;

    public RaceRankListAdapter(Context context, int layout_id, List<RaceRankItem> l_raceRankItems, ProfileInfoItem profileInfoItem)
    {
        //super(context, textViewResourceId, objects);
        mContext = context;
        this.l_raceRankItems = l_raceRankItems;
        this.profileInfoItem = profileInfoItem;

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        if (this.profileInfoItem != null)
        {
            realm.copyFromRealm(this.profileInfoItem);
        }
        realm.commitTransaction();

        this.layout_id = layout_id;
    }

    @Override
    public int getCount()
    {
        return l_raceRankItems.size();
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

        TextView tvSerialNumber = (TextView) row.findViewById(R.id.tvSerialNumber);
        TextView tvAgentName = (TextView) row.findViewById(R.id.tvAgentName);
        TextView tvPointsEarned = (TextView) row.findViewById(R.id.tvPointsEarned);

        LinearLayout llAgentDetailsAllowedMark = (LinearLayout) row.findViewById(R.id.llAgentDetailsAllowedMark);


        tvSerialNumber.setText(String.valueOf(position + 1));

        String loggedAgentFullName = this.profileInfoItem.getName() + " " + this.profileInfoItem.getSurname();
        String agentName = l_raceRankItems.get(position).getName_agent();

        if(loggedAgentFullName.equals(agentName))
        {
            int blueDarkColor = mContext.getResources().getColor(R.color.blueDark);
            tvAgentName.setTextColor(blueDarkColor);
            tvPointsEarned.setTextColor(blueDarkColor);
        }
        else
        {
            llAgentDetailsAllowedMark.setVisibility(View.GONE);
        }

        tvAgentName.setText(l_raceRankItems.get(position).getName_agent());

        String pointsEarned = l_raceRankItems.get(position).getPunti();

        double dPointsEarned = 0;

        try
        {
            dPointsEarned = Double.parseDouble(pointsEarned);
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        if(dPointsEarned != 0)
        {
            String pointsEarnedFormatted = new DecimalFormat("##.##").format(dPointsEarned);
            tvPointsEarned.setText(pointsEarnedFormatted);
        }

        return row;
    }
}
