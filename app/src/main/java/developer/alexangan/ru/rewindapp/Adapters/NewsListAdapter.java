package developer.alexangan.ru.rewindapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import developer.alexangan.ru.rewindapp.Models.NewsItem;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.MyTextUtils;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.mSettings;

public class NewsListAdapter extends BaseAdapter
{
    private Context mContext;
    private ArrayList<NewsItem> lNewsItems;
    private int layout_id;
    //ViewHolder holder;

    public NewsListAdapter(Context context, int layout_id, ArrayList<NewsItem> lNewsItems)
    {
        //super(context, textViewResourceId, objects);
        mContext = context;
        this.lNewsItems = lNewsItems;
        this.layout_id = layout_id;
    }

    @Override
    public int getCount()
    {
        return lNewsItems.size();
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

        TextView tvNewsTitle = (TextView) row.findViewById(R.id.tvNewsTitle);
        TextView tvNewsSubtitle = (TextView) row.findViewById(R.id.tvNewsSubtitle);
        TextView tvNewsDate = (TextView) row.findViewById(R.id.tvNewsDate);

        ImageView ivAttachmentIcon = (ImageView) row.findViewById(R.id.ivAttachmentIcon);

        if(lNewsItems.get(position).getIsAttach() == 0)
        {
            ivAttachmentIcon.setVisibility(View.GONE);
        }

        String datePost = lNewsItems.get(position).getDate_post();

        String formattedDate = MyTextUtils.reformatDateString(datePost);

        tvNewsDate.setText(formattedDate);

        String searchString = mSettings.getString("newsSearchLastQueryString", "");
        String searchStrLower = searchString.toLowerCase();

        String strNewsTitle = lNewsItems.get(position).getTitle_news();
        String strNewsSubtitle = lNewsItems.get(position).getSubtitle_news();


/*        if (strNewsTitle.toLowerCase().contains(searchStrLower))
        {
            SpannableString ssText = makeColoredString(strNewsTitle, searchStrLower);
            tvNewsTitle.setText(ssText);
        }
        else*/
        {
            tvNewsTitle.setText(strNewsTitle);
        }

/*        if (strNewsSubtitle.toLowerCase().contains(searchStrLower))
        {
            SpannableString ssText = makeColoredString(strNewsSubtitle, searchStrLower);
            tvNewsSubtitle.setText(ssText);
        }
        else*/
        {
            tvNewsSubtitle.setText(strNewsSubtitle);
        }

        return row;
    }

/*    @NonNull
    private SpannableString makeColoredString(String strSource, String searchString)
    {
        String sourceStrLower = strSource.toLowerCase();

        int iStart = sourceStrLower.indexOf(searchString);
        int iEnd = iStart + searchString.length();

        Spannable spannable = new SpannableString(strSource);
        SpannableString ssText = new SpannableString(spannable);
        ssText.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffd100")), iStart, iEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssText;
    }*/
}
