package developer.alexangan.ru.rewindapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.TreeMap;

import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.MyTextUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

public class InvitationInvoiceAdapter extends BaseAdapter
{
    private Context mContext;
    private TreeMap<String, List<Pair<String, String>>> incomesOrExpenses;
    private int layout_id;
    //ViewHolder holder;

    public InvitationInvoiceAdapter(Context context, int layout_id, TreeMap<String, List<Pair<String, String>>> incomesOrExpenses)
    {
        //super(context, textViewResourceId, objects);
        mContext = context;
        this.incomesOrExpenses = incomesOrExpenses;
        this.layout_id = layout_id;
    }

    @Override
    public int getCount()
    {
        return incomesOrExpenses.size();
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

        TextView tvInvoiceGroup = (TextView) row.findViewById(R.id.tvInvoiceGroup);

        //String value =  (String) new Vector(incomesOrExpenses.keySet()).get(position);
        String strKey =  (String) incomesOrExpenses.keySet().toArray()[position];
        tvInvoiceGroup.setText(strKey);
        tvInvoiceGroup.setTextColor(Color.parseColor("#ff808080"));
        //tvInvoiceGroup.setTextColor(mContext.getResources().getColor(R.color.gray80));
        tvInvoiceGroup.setTextSize(17f);
        CalligraphyUtils.applyFontToTextView(mContext, tvInvoiceGroup, "fonts/SF_UI_Text_Medium.ttf");

        LinearLayout llGroupItems = (LinearLayout) row.findViewById(R.id.llGroupItems);
        List<Pair<String, String>> lGroupItems = incomesOrExpenses.get(strKey);

        for (int i = 0; i < lGroupItems.size(); i++)
        {
            FrameLayout frameLayout = new FrameLayout(mContext);
            FrameLayout.LayoutParams lpMW = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            FrameLayout.LayoutParams lpWWleft = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            FrameLayout.LayoutParams lpWWRight = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lpWWleft.gravity = Gravity.START;
            lpWWRight.gravity = Gravity.END;
            frameLayout.setLayoutParams(lpMW);

            TextView tvGroupItemText = new TextView(mContext);
            tvGroupItemText.setLayoutParams(lpWWleft);
            frameLayout.addView(tvGroupItemText);

            tvGroupItemText.setTextColor(Color.BLACK);
            tvGroupItemText.setText(lGroupItems.get(i).first);
            tvGroupItemText.setTextSize(14f);
            CalligraphyUtils.applyFontToTextView(mContext, tvGroupItemText, "fonts/SF_UI_Text_Regular.ttf");

            String strGroupItemTotal = lGroupItems.get(i).second;
            strGroupItemTotal = MyTextUtils.reformatCurrencyString(strGroupItemTotal);

            TextView tvGroupItemTotal = new TextView(mContext);
            tvGroupItemTotal.setLayoutParams(lpWWRight);
            frameLayout.addView(tvGroupItemTotal);

            tvGroupItemTotal.setTextColor(Color.BLACK);
            tvGroupItemTotal.setText(strGroupItemTotal);
            tvGroupItemTotal.setTextSize(14f);
            CalligraphyUtils.applyFontToTextView(mContext, tvGroupItemTotal, "fonts/SF_UI_Text_Medium.ttf");

            llGroupItems.addView(frameLayout);
        }


        //TextView tvAmount = (TextView) row.findViewById(R.id.tvAmount);
        //TextView tvChecked = (TextView) row.findViewById(R.id.tvChecked);

/*        String monthPeriod = String.valueOf(totals.get(position).getPeriod());
        int monthNumber = Integer.valueOf(monthPeriod.substring(4)) - 1;

        if (monthNumber == -1)
        {
            monthNumber = 11;
        }

        String periodDisplayed = "Fattura " + ItalianMonths.numToLongString(monthNumber) + " " + monthPeriod.substring(0, 4);

        tvTitleFromMonth.setText(periodDisplayed);

        String strAmount = String.valueOf(totals.get(position).getTotal());

        strAmount = TextUtils.reformatCurrencyString(strAmount);

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
